package tech.trackium.cargo

import java.time.Instant
import org.json.JSONObject

internal object PhysicalPositions {
    data class Fix(val latitude: Double, val longitude: Double, val measured: Instant,
                   val received: Instant, val buffered: Boolean)

    fun isPhysical(device: JSONObject) =
        device.optString("identity_kind") == "physical" || device.optString("mode") == "real"

    fun instant(raw: String): Instant? = runCatching { Instant.parse(raw) }.getOrNull()

    private fun fix(gps: JSONObject?, measured: String, received: String, buffered: Boolean): Fix? {
        if (gps == null) return null
        val lat = (gps.opt("latitude") as? Number)?.toDouble() ?: return null
        val lon = (gps.opt("longitude") as? Number)?.toDouble() ?: return null
        val time = instant(measured) ?: return null
        val receipt = instant(received) ?: return null
        if (lat !in -90.0..90.0 || lon !in -180.0..180.0) return null
        return Fix(lat, lon, time, receipt, buffered)
    }

    private fun disabled(device: JSONObject) =
        !device.optBoolean("enabled",true) || device.optJSONObject("status")?.optBoolean("enabled",true) == false

    fun current(device: JSONObject): Fix? {
        if (!isPhysical(device) || disabled(device))
            return null
        val projection = device.optJSONObject("location") ?: return null
        if (projection.optString("device_id") != device.optString("id") || device.optString("id").isEmpty())
            return null
        val observation = projection.optJSONObject("location") ?: return null
        if (observation.optString("trust") != "unverified") return null
        return fix(observation.optJSONObject("gps"), observation.optString("occurred_at"),
            observation.optString("received_at"), false)
    }

    fun history(device: JSONObject): List<Fix> {
        if (!isPhysical(device) || disabled(device) ||
            device.optString("id").isBlank()) return emptyList()
        return CargoPresentation.records(device.optJSONObject("history")?.optJSONArray("events")).take(20)
            .mapNotNull { event ->
                val payload = event.optJSONObject("payload") ?: return@mapNotNull null
                val extra = payload.optJSONObject("extra") ?: return@mapNotNull null
                if (payload.optString("source") != "aovx-gal-v1" || payload.optString("trust") != "unverified" ||
                    payload.optString("device_id") != device.optString("id") || extra.opt("gnss_fix") != true)
                    return@mapNotNull null
                fix(payload.optJSONObject("gps"), payload.optString("device_measured_at"),
                    event.optString("received_at"), extra.opt("buffered") == true)
            }
    }
}
