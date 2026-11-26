package tech.trackium.cargo

import java.time.Instant
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class FieldRoadsTest {
    private val graph="a".repeat(64)
    private val snapshot="2026-11-24T21:21:23Z"
    private val provider=FieldRoads.Provider("https://roads.example","owner","random-test-passphrase",graph)
    private val stops=LocalFieldPlan.parse("0,0\n0,0.001")
    private val trip=JSONObject("""{"status":0,"units":"kilometers","summary":{"length":0.111,"time":20},"legs":[{"shape":"???o}@"}]}""")

    @Test fun explicitRoadGeometrySurvivesLocalCatalogAndSupportsDeviation() {
        val road=FieldRoads.fromTrip(trip,stops,provider,snapshot)
        val catalog=FieldRoads.catalog(listOf(FieldRoads.Saved("fixture",road)),"fixture")
        assertEquals(road,FieldRoads.saved(catalog).single().road)
        assertEquals(111L,road.meters)
        assertEquals(20L,road.seconds)
        val now=Instant.parse(snapshot)
        val fix=PhysicalPositions.Fix(0.001,0.0005,now,now,false)
        assertEquals(111L,FieldRoads.deviation(fix,road))
    }

    @Test fun rejectsInvalidGeometryAndUntrustedOriginConfiguration() {
        val broken=JSONObject(trip.toString())
        broken.getJSONArray("legs").getJSONObject(0).put("shape","~~~~")
        assertTrue(runCatching { FieldRoads.fromTrip(broken,stops,provider,snapshot) }.isFailure)
        assertTrue(runCatching {
            FieldRoads.fromTrip(trip,LocalFieldPlan.parse("20,20\n21,21"),provider,snapshot)
        }.isFailure)
        for (origin in listOf("http://roads.example","https://user@roads.example","https://roads.example/route","https://roads.example?q=secret"))
            assertTrue(runCatching { FieldRoads.Provider.read(provider.json().put("origin",origin)) }.isFailure)
        assertEquals(provider,FieldRoads.Provider.read(provider.json()))
    }

    @Test fun offlineBasemapAcceptsOnlyBoundedRoadLinework() {
        val map=JSONObject("""{"type":"FeatureCollection","features":[
            {"geometry":{"type":"LineString","coordinates":[[24,49],[22,50]]}}]}""")
        assertEquals(LocalFieldPlan.Stop(49.0,24.0),FieldRoads.offlineRoads(map).single().first())
        map.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").put("type","Point")
        assertTrue(runCatching { FieldRoads.offlineRoads(map) }.isFailure)
    }
}
