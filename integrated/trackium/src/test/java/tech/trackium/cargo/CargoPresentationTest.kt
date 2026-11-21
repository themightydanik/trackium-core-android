package tech.trackium.cargo
import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject
import org.json.JSONArray
class CargoPresentationTest {
 @Test fun missingMeasurementsStayMissingAndZeroRemainsAReportedValue(){
  assertEquals("Not reported",CargoPresentation.value(JSONObject(),"temperature_c"," °C"))
  assertEquals("0 °C",CargoPresentation.value(JSONObject().put("temperature_c",0),"temperature_c"," °C"))
 }
 @Test fun submittedAndConfirmedDoNotClaimIndependentVerification(){
  assertTrue(CargoPresentation.proofState(JSONObject().put("state","submitted")).contains("not established"))
  assertTrue(CargoPresentation.proofState(JSONObject().put("state","confirmed")).contains("independently recheck"))
  assertEquals("Stale observation · refresh required",CargoPresentation.freshness(1,600001))
 }
 @Test fun structuredTelemetryPreservesUnitsAndTrustAndSearchIsLocal(){
  val events=JSONArray().put(JSONObject().put("occurred_at","2026-01-01").put("payload",JSONObject().put("temperature_c",12).put("trust","synthetic")))
  val sections=CargoPresentation.sections("Device",JSONObject().put("history",JSONObject().put("events",events)))
  val rows=sections.flatMap{it.rows};assertTrue(rows.contains("Temperature" to "12 °C"));assertTrue(rows.contains("Provenance" to "Synthetic telemetry"))
  val list=JSONArray().put(JSONObject().put("name","Cold cargo").put("status","active")).put(JSONObject().put("name","Other").put("status","draft"))
  assertEquals(1,CargoPresentation.matching(list,"COLD","active").size)
 }

    @Test fun aovxReceiptUsesGatewayTimeAndDoesNotReadDeviceBcdAsAnInstant() {
        val device = JSONObject("""
            {"identity_kind":"physical","status":{"mode":"real"},
             "location":{"location":null},
             "history":{"events":[
               {"received_at":"2026-11-20T11:12:13.456Z","occurred_at":"2026-11-20T11:12:13.000Z",
                "payload":{"trust":"unverified","event_time_basis":"gateway_received_at",
                           "device_time_raw":"241101123456","temperature_c":null,
                           "humidity_pct":"12","battery_pct":null}}
             ]}}
        """.trimIndent())
        val sections = CargoPresentation.sections("Device", device)
        val latest = sections.single { it.title == "Latest device observation" }.rows.toMap()
        assertEquals("2026-11-20T11:12:13.456Z", latest["Gateway received"])
        assertEquals("Unavailable", latest["Device measurement time"])
        assertEquals("Physical observation · unverified", latest["Provenance"])
        assertEquals("Not reported", latest["Temperature"])
        assertEquals("Not reported", latest["Humidity"])
        assertEquals("Not reported", latest["Battery"])
        assertEquals(listOf("Position" to "Unavailable"), sections.single { it.title == "Current position" }.rows)
        assertFalse(sections.flatMap { it.rows }.any { it.second == "241101123456" })
    }

    @Test fun onlyEligibleSyntheticPositionsAreShownAndRealZeroCoordinatesArePreserved() {
        val response = JSONObject("""
            {"identity_kind":"simulator","location":{"location":{
              "trust":"synthetic","gps":{"latitude":0,"longitude":0}
            }}}
        """.trimIndent())
        fun position() = CargoPresentation.sections("Device", response)
            .single { it.title == "Current position" }.rows.toMap()
        assertEquals("Synthetic telemetry", position()["Provenance"])
        assertEquals("0", position()["Latitude"])
        assertEquals("0", position()["Longitude"])
        response.put("identity_kind", "physical")
        assertEquals(mapOf("Position" to "Unavailable"), position())
        response.put("identity_kind", "simulator")
            .getJSONObject("location").getJSONObject("location").getJSONObject("gps").put("longitude", 181)
        assertEquals(mapOf("Position" to "Unavailable"), position())
    }

    @Test fun missingTimeBasisDoesNotRelabelOccurredAtAsReceipt() {
        val device = JSONObject("""
            {"status":{"mode":"real"},"history":{"events":[
              {"occurred_at":"2026-11-20T11:12:13Z","payload":{"trust":"unverified"}}
            ]}}
        """.trimIndent())
        val rows = CargoPresentation.sections("Device", device)
            .single { it.title == "Latest device observation" }.rows.toMap()
        assertEquals("Not reported", rows["Gateway received"])
        assertEquals("Unavailable", rows["Device measurement time"])
    }
}
