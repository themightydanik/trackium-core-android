package tech.trackium.cargo

import org.json.JSONArray
import org.json.JSONObject

internal object LocalFieldPlan {
    data class Stop(val latitude: Double, val longitude: Double)

    fun parse(text: String): List<Stop> {
        require(text.length <= 512) { "Use at most ten coordinate lines." }
        val lines = text.lineSequence().filter { it.isNotBlank() }.toList()
        require(lines.size in 2..10) { "Enter 2–10 stops in travel order." }
        return lines.mapIndexed { index, line ->
            val parts = line.split(',')
            val lat = parts.getOrNull(0)?.trim()?.toDoubleOrNull()
            val lon = parts.getOrNull(1)?.trim()?.toDoubleOrNull()
            require(parts.size == 2 && lat != null && lon != null &&
                lat in -90.0..90.0 && lon in -180.0..180.0) {
                "Stop ${index + 1}: enter latitude, longitude in decimal degrees."
            }
            Stop(lat,lon)
        }.also { stops ->
            require(stops.zipWithNext().none { (a,b) -> a == b }) { "Adjacent stops must be different." }
        }
    }

    fun text(stops: List<Stop>) = stops.joinToString("\n") { "${it.latitude}, ${it.longitude}" }

    fun read(store: SecureStore, key: String): List<Stop> {
        val text = store.read(key).optString("stops")
        return if (text.isEmpty()) emptyList() else parse(text)
    }

    fun save(store: SecureStore, key: String, stops: List<Stop>) =
        store.write(key, JSONObject().put("stops",text(stops)))

    fun key(origin: String, tenant: String, deviceId: String) =
        "field-plan-" + ProofProtocol.sha(JSONArray(listOf(origin,tenant,deviceId)).toString())
}
