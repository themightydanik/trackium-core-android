package tech.trackium.cargo

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.Base64
import javax.net.ssl.HttpsURLConnection
import kotlin.math.*
import org.json.JSONArray
import org.json.JSONObject

internal object FieldRoads {
    data class Provider(val origin: String, val user: String, val password: String, val graph: String) {
        override fun toString() = "Field road provider ($origin)"
        fun json() = JSONObject().put("origin", origin).put("user", user).put("password", password).put("graph", graph)
        companion object {
            fun read(j: JSONObject): Provider {
                val origin = NativeAuth.origin(j.getString("origin"))
                val user = j.getString("user")
                val password = j.getString("password")
                val graph = j.getString("graph")
                require(user.matches(Regex("[a-zA-Z0-9._-]{1,64}")) &&
                    password.matches(Regex("[!-~]{16,128}")) && graph.matches(Regex("[0-9a-f]{64}"))) {
                    "Use an HTTPS origin, valid provider account and 64-character graph ID."
                }
                return Provider(origin,user,password,graph)
            }
        }
    }

    data class Road(val stops: List<LocalFieldPlan.Stop>, val legs: List<List<LocalFieldPlan.Stop>>,
                    val meters: Long, val seconds: Long, val graph: String, val engine: String,
                    val snapshot: String, val origin: String, val encodedLegs: List<String>) {
        fun json() = JSONObject().put("stops",LocalFieldPlan.text(stops)).put("meters",meters)
            .put("seconds",seconds).put("graph",graph).put("engine",engine)
            .put("snapshot",snapshot).put("origin",origin).put("encoded",JSONArray(encodedLegs))
    }

    data class Saved(val id: String, val road: Road)

    fun saved(catalog: JSONObject): List<Saved> {
        val rows = catalog.optJSONArray("routes") ?: return emptyList()
        require(rows.length() <= 5)
        return (0 until rows.length()).map { i ->
            val row = rows.getJSONObject(i)
            Saved(row.getString("id"),readRoad(row.getJSONObject("road")))
        }
    }

    fun catalog(rows: List<Saved>, selected: String) = JSONObject().put("selected",selected)
        .put("routes",JSONArray(rows.map { JSONObject().put("id",it.id).put("road",it.road.json()) }))

    fun bounded(input: InputStream, limit: Int = 1024 * 1024): ByteArray {
        val out = ByteArrayOutputStream()
        val chunk = ByteArray(8192)
        val deadline = System.nanoTime() + 12_000_000_000L
        while (true) {
            check(System.nanoTime() < deadline) { "Road response or map timed out." }
            val size = input.read(chunk)
            if (size < 0) return out.toByteArray()
            check(out.size() + size <= limit) { "Road response or map is too large." }
            out.write(chunk,0,size)
        }
    }

    private fun point(lat: Double, lon: Double): LocalFieldPlan.Stop {
        require(lat in -90.0..90.0 && lon in -180.0..180.0) { "Invalid map coordinates." }
        return LocalFieldPlan.Stop(lat,lon)
    }

    private fun legs(shapes: List<String>): List<List<LocalFieldPlan.Stop>> {
        var remaining = 20000
        return shapes.map { shape ->
            require(shape.length in 4..240000)
            var index = 0
            var lat = 0L
            var lon = 0L
            fun delta(): Long {
                var value = 0L
                for (shift in 0..30 step 5) {
                    require(index < shape.length)
                    val char = shape[index++].code - 63
                    require(char in 0..63)
                    value = value or ((char and 31).toLong() shl shift)
                    if (char < 32) return if ((value and 1L) == 0L) value shr 1 else (value shr 1).inv()
                }
                error("Invalid road geometry.")
            }
            buildList {
                while (index < shape.length) {
                    require(remaining-- > 0)
                    lat += delta()
                    lon += delta()
                    add(point(lat/1e6,lon/1e6))
                }
                require(size >= 2)
            }
        }
    }

    fun readRoad(j: JSONObject): Road {
        val stops = LocalFieldPlan.parse(j.getString("stops"))
        val shapes = j.getJSONArray("encoded")
        require(shapes.length() == stops.size-1)
        val encoded = (0 until shapes.length()).map(shapes::getString)
        val graph = j.getString("graph")
        val engine = j.getString("engine")
        val snapshot = j.getString("snapshot")
        val meters = j.getLong("meters")
        val seconds = j.getLong("seconds")
        require(graph.matches(Regex("[0-9a-f]{64}")) && engine == "3.6.3" &&
            java.time.Instant.parse(snapshot).toString() == snapshot && meters in 1..5_000_000 && seconds in 1..604800)
        val geometry = legs(encoded)
        geometry.forEachIndexed { i, leg ->
            require(distance(leg.first(),stops[i]) <= 1000 && distance(leg.last(),stops[i+1]) <= 1000) {
                "Road geometry does not match its stops."
            }
        }
        return Road(stops,geometry,meters,seconds,graph,engine,snapshot,NativeAuth.origin(j.getString("origin")),encoded)
    }

    fun calculate(provider: Provider, stops: List<LocalFieldPlan.Stop>): Road {
        val body = JSONObject().put("locations", JSONArray(stops.map {
            JSONObject().put("lat",it.latitude).put("lon",it.longitude).put("type","break")
        })).put("costing","auto").put("units","kilometers").put("shape_format","polyline6")
            .toString().toByteArray(Charsets.UTF_8)
        val conn = URL(provider.origin + "/route").openConnection(java.net.Proxy.NO_PROXY) as HttpsURLConnection
        try {
            conn.instanceFollowRedirects = false
            conn.connectTimeout = 4000
            conn.readTimeout = 9000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setFixedLengthStreamingMode(body.size)
            conn.setRequestProperty("Content-Type","application/json")
            conn.setRequestProperty("Accept","application/json")
            conn.setRequestProperty("Accept-Encoding","identity")
            conn.setRequestProperty("Authorization","Basic " + Base64.getEncoder()
                .encodeToString((provider.user + ":" + provider.password).toByteArray(Charsets.UTF_8)))
            conn.outputStream.use { it.write(body) }
            check(conn.responseCode == 200) { "Road provider unavailable or route not found." }
            check(conn.getHeaderField("X-Trackium-Graph-Id") == provider.graph &&
                conn.getHeaderField("X-Trackium-Engine-Version") == "3.6.3") { "Road graph identity mismatch." }
            check(conn.contentType?.substringBefore(';') == "application/json" &&
                conn.contentEncoding.let { it == null || it == "identity" })
            val trip = JSONObject(conn.inputStream.use { bounded(it).toString(Charsets.UTF_8) }).getJSONObject("trip")
            return fromTrip(trip,stops,provider,conn.getHeaderField("X-Trackium-OSM-Snapshot").orEmpty())
        } finally { conn.disconnect() }
    }

    fun fromTrip(trip: JSONObject, stops: List<LocalFieldPlan.Stop>, provider: Provider, snapshot: String): Road {
        check(trip.getInt("status") == 0 && trip.getString("units") == "kilometers")
        val summary = trip.getJSONObject("summary")
        val rawLegs = trip.getJSONArray("legs")
        val km = summary.getDouble("length")
        val seconds = summary.getDouble("time")
        require(km.isFinite() && seconds.isFinite() && rawLegs.length() == stops.size-1)
        val shapes = (0 until rawLegs.length()).map { rawLegs.getJSONObject(it).getString("shape") }
        return readRoad(JSONObject().put("stops",LocalFieldPlan.text(stops)).put("meters",(km*1000).roundToLong())
            .put("seconds",seconds.roundToLong()).put("graph",provider.graph).put("engine","3.6.3")
            .put("snapshot",snapshot).put("origin",provider.origin).put("encoded",JSONArray(shapes)))
    }

    private fun wrapped(delta: Double) = ((delta+540)%360)-180

    fun deviation(fix: PhysicalPositions.Fix, road: Road): Long? {
        val cosLat = cos(Math.toRadians(fix.latitude))
        if (cosLat < .05) return null
        val position = point(fix.latitude,fix.longitude)
        return road.legs.minOf { leg ->
            leg.zipWithNext().minOf { (a,b) ->
                val x = wrapped(a.longitude-fix.longitude)*cosLat
                val y = a.latitude-fix.latitude
                val dx = wrapped(b.longitude-a.longitude)*cosLat
                val dy = b.latitude-a.latitude
                val length = dx*dx+dy*dy
                val ratio = if (length == 0.0) 0.0 else (-(x*dx+y*dy)/length).coerceIn(0.0,1.0)
                distance(position,LocalFieldPlan.Stop(a.latitude+ratio*dy,a.longitude+ratio*dx/cosLat))
            }
        }.roundToLong()
    }

    private fun distance(a: LocalFieldPlan.Stop, b: LocalFieldPlan.Stop): Double {
        val h = sin(Math.toRadians(b.latitude-a.latitude)/2).pow(2) +
            cos(Math.toRadians(a.latitude))*cos(Math.toRadians(b.latitude))*sin(Math.toRadians(b.longitude-a.longitude)/2).pow(2)
        return 12742000 * asin(sqrt(h.coerceIn(0.0,1.0)))
    }

    fun offlineRoads(document: JSONObject): List<List<LocalFieldPlan.Stop>> {
        require(document.optString("type") == "FeatureCollection")
        val features = document.getJSONArray("features")
        require(features.length() in 1..1000)
        var remaining = 20000
        return (0 until features.length()).map { i ->
            val geometry = features.getJSONObject(i).getJSONObject("geometry")
            require(geometry.getString("type") == "LineString")
            val positions = geometry.getJSONArray("coordinates")
            require(positions.length() in 2..remaining)
            remaining -= positions.length()
            (0 until positions.length()).map { j ->
                val pair = positions.getJSONArray(j)
                point(pair.getDouble(1),pair.getDouble(0))
            }
        }
    }
}
