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
  val rows=sections.flatMap{it.rows};assertTrue(rows.contains("Temperature" to "12 °C"));assertTrue(rows.contains("Trust" to "synthetic"))
  val list=JSONArray().put(JSONObject().put("name","Cold cargo").put("status","active")).put(JSONObject().put("name","Other").put("status","draft"))
  assertEquals(1,CargoPresentation.matching(list,"COLD","active").size)
 }
}
