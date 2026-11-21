package tech.trackium.cargo

import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.util.Locale

/** Display projections only. Missing evidence stays missing; no authorization/publication decisions. */
object CargoPresentation {
    data class Section(val title: String, val rows: List<Pair<String, String>>)
    fun value(j: JSONObject?, key: String, unit: String = ""): String {
        val v = j?.opt(key)
        return if (v == null || v == JSONObject.NULL || v is JSONObject || v is JSONArray || v.toString().isBlank())
            "Not reported" else v.toString().take(512) + unit
    }
    fun records(a: JSONArray?): List<JSONObject> = (0 until (a?.length() ?: 0)).mapNotNull { a?.optJSONObject(it) }
    fun matching(a: JSONArray?, query: String, filter: String): List<JSONObject> = records(a).filter { row ->
        listOf("id", "name", "label", "status", "mode", "identity_kind").any { value(row, it).lowercase(Locale.ROOT).contains(query.trim().lowercase(Locale.ROOT)) } &&
            (filter == "All" || listOf("status", "source_trust", "mode", "identity_kind").any { row.optString(it) == filter })
    }
    fun freshness(at: Long, now: Long = System.currentTimeMillis()): String = when {
        at == 0L -> "No authenticated observation"
        now < at -> "Observation time uncertain"
        now - at > 300_000 -> "Stale observation · refresh required"
        else -> "Recent observation · not a live stream"
    }
    fun session(config: JSONObject, observed: Boolean, at: Long): String {
        val s = config.optJSONObject("session") ?: return "Unpaired · owner approval required"
        val expiry = try { Instant.parse(s.optString("expires_at")).toEpochMilli() } catch (_: Exception) { return "Session expiry unknown · re-pair or refresh" }
        if (expiry <= System.currentTimeMillis()) return "Session expired · owner re-pairing required"
        return if (observed) "Authenticated observation · ${freshness(at)}" else "Session stored · connection not verified"
    }
    fun proofState(j: JSONObject): String = when(j.optString("state")) {
        "pending", "submitting" -> "Pending · no inclusion evidence"
        "submitted" -> "Submitted · inclusion not established"
        "included" -> "Inclusion observed · confirmation depth may change"
        "confirmed" -> "Gateway reports confirmed · independently recheck"
        "failed", "retryable_error", "blocked" -> "Failed / blocked · inspect reported error"
        else -> "Unknown · inspect Gateway evidence"
    }
    private fun section(title: String, j: JSONObject?, vararg fields: Pair<String,String>): Section =
        Section(title, fields.filter { (_,key) -> j?.has(key)==true && j.opt(key) !is JSONObject && j.opt(key) !is JSONArray }.map { (label,key) -> label to value(j,key) })
    private fun measurement(j: JSONObject?, key: String, unit: String = ""): String {
        val number = j?.opt(key) as? Number ?: return "Not reported"
        return if (number.toDouble().isFinite()) value(j, key, unit) else "Not reported"
    }

    private fun observation(event: JSONObject?): List<Pair<String, String>> {
        val payload = event?.optJSONObject("payload")
        val basis = payload?.optString("event_time_basis")
        val trust = payload?.optString("trust")
        val provenance = when (trust) {
            "unverified" -> "Physical observation · unverified"
            "synthetic" -> "Synthetic telemetry"
            else -> "Not reported"
        }
        val times = when {
            basis == "gateway_received_at" -> listOf(
                "Gateway received" to (value(event, "received_at").takeUnless { it == "Not reported" }
                    ?: value(event, "occurred_at")),
                "Device measurement time" to "Unavailable"
            )
            trust == "synthetic" -> listOf("Synthetic sample time" to value(event, "occurred_at"))
            else -> listOf(
                "Gateway received" to value(event, "received_at"),
                "Device measurement time" to "Unavailable"
            )
        }
        return listOf("Provenance" to provenance) + times + listOf(
            "Temperature" to measurement(payload, "temperature_c", " °C"),
            "Humidity" to measurement(payload, "humidity_pct", " %"),
            "Battery" to measurement(payload, "battery_pct", " %")
        )
    }

    private fun location(device: JSONObject, status: JSONObject?): Section {
        val location = device.optJSONObject("location")?.optJSONObject("location")
        val gps = location?.optJSONObject("gps")
        val lat = (gps?.opt("latitude") as? Number)?.toDouble()
        val lon = (gps?.opt("longitude") as? Number)?.toDouble()
        val physical = device.optString("identity_kind") == "physical" ||
            device.optString("mode") == "real" || status?.optString("mode") == "real"
        val valid = !physical && location?.optString("trust") == "synthetic" &&
            lat != null && lat in -90.0..90.0 && lon != null && lon in -180.0..180.0
        return Section("Current position", if (valid) listOf(
            "Provenance" to "Synthetic telemetry",
            "Synthetic position time" to value(location, "occurred_at"),
            "Latitude" to measurement(gps, "latitude"),
            "Longitude" to measurement(gps, "longitude"),
            "Accuracy (m)" to measurement(gps, "accuracy_m")
        ) else listOf("Position" to "Unavailable"))
    }

    fun sections(title: String, j: JSONObject): List<Section> {
        val result = mutableListOf<Section>()
        result += section(title,j,"Name" to "name","Label" to "label","Identifier" to "id","State" to "status","Source trust" to "source_trust","Device" to "device_id","Observed / updated" to "updated_at")
        for (key in listOf("route","checkpoints")) for ((i,point) in records(j.optJSONArray(key)).withIndex()) {
            result += section("${if(key=="route") "Route stop" else "Checkpoint"} ${i+1}",point,"Name" to "name","Latitude" to "latitude","Longitude" to "longitude","Radius (m)" to "radius_m","Visited" to "visited_at","Trust" to "source_trust")
        }
        val status = j.optJSONObject("status")
        if (status != null)
            result += section("Device status", status, "Status" to "status", "Enabled" to "enabled",
                "Last observation timestamp" to "last_event_at", "Trust" to "trust", "Mode" to "mode")
        if (status != null || j.has("location") || j.has("history"))
            result += location(j, status)
        val history = records(j.optJSONObject("history")?.optJSONArray("events"))
        if (j.has("history") || status != null)
            result += Section("Latest device observation", observation(history.firstOrNull()))
        for (event in history)
            result += Section("Telemetry history", listOf("Identifier" to value(event, "id")) + observation(event))
        for (event in records(j.optJSONArray("events")))
            result += section("Shipment event", event, "Type" to "event_type", "Event time" to "event_time", "Identifier" to "id", "Source trust" to "source_trust")
        val device = j.optJSONObject("device_observation")
        if (device != null)
            result += section("Linked device status", device, "Status" to "status", "Trust" to "trust", "Mode" to "mode")
        for (sample in records(j.optJSONObject("telemetry")?.optJSONArray("events")))
            result += Section("Linked device observation", observation(sample))
        for(proof in records(j.optJSONArray("proofs")))result+=Section("Linked proof",listOf("Identifier" to value(proof,"id"),"State" to proofState(proof),"Checked" to value(proof,"last_checked_at")))
        for(token in records(j.optJSONArray("balance")))result+=section("Core-reported token",token,"Token" to "token","Token ID" to "tokenid","Confirmed" to "confirmed","Unconfirmed" to "unconfirmed","Sendable" to "sendable")
        if(j.has("verified"))result+=section("Independent reader evidence",j,"Independently verified at check time" to "verified","Chain observation" to "status","Checked" to "checked_at","Reason" to "reason","Source trust" to "source_trust")
        if(j.has("commitment")) {
            result+=Section("Proof evidence",listOf("State" to proofState(j),"Reported confirmations" to value(j,"confirmations"),"Required depth" to value(j,"required_depth"),"Included height" to value(j,"included_height"),"Last checked" to value(j,"last_checked_at"),"TXPoW" to value(j,"txpow_id"),"Error" to value(j,"last_error_code")))
            result+=section("Committed event",j.optJSONObject("commitment"),"Type" to "event_type","Source trust" to "source_trust","Shipment" to "shipment_id","Event" to "event_id")
        }
        if(j.has("verification"))result+=section("Independent Gateway reader result",j.optJSONObject("verification"),"Result" to "state","Reason" to "reason","Checked" to "checked_at","Confirmations" to "confirmations")
        if(j.has("version"))result+=section("Core observation",j,"Version" to "version","Peers" to "peers","Height" to "block","Address" to "address","Observed" to "observed_at")
        // A bounded, flat scalar section preserves extra API evidence without recursive JSON UI.
        val scalar=listOf("id","status","state","verified","reason","ready","checked_at","confirmations","required_depth","chain_id","mode","included_height","source_trust","identity_kind","enabled","last_event_at","created_at","updated_at","partial_observation","max_input_checked","hash_chain_valid","version","block","peers","observed_at").filter { j.has(it) && j.opt(it) !is JSONObject && j.opt(it) !is JSONArray }.map { it.replace('_',' ') to value(j,it) }.toList()
        if(scalar.isNotEmpty())result+=Section("Additional reported evidence",scalar)
        return result
    }
}
