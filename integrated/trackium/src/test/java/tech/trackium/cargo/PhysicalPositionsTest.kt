package tech.trackium.cargo

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class PhysicalPositionsTest {
    @Test fun localWaypointPlanPreservesOrderAndRejectsInvalidStops() {
        val stops = LocalFieldPlan.parse("49.81, 24.02\n50.04, 22.00\n49.99, 22.21")
        assertEquals(50.04, stops[1].latitude, 0.0)
        assertEquals(stops, LocalFieldPlan.parse(LocalFieldPlan.text(stops)))
        for (invalid in listOf("0,0", "91,0\n0,0", "NaN,1\n0,0", "0,0\n0,0"))
            assertTrue(runCatching { LocalFieldPlan.parse(invalid) }.isFailure)
        assertNotEquals(LocalFieldPlan.key("origin","one","device"),
            LocalFieldPlan.key("origin","two","device"))
    }

    @Test fun currentProjectionAndBufferedHistoryStaySeparate() {
        val device = JSONObject("""
            {"id":"own","identity_kind":"physical","status":{"enabled":true},
             "location":{"device_id":"own","location":{"trust":"unverified",
               "gps":{"latitude":49.8,"longitude":24},
               "occurred_at":"2026-11-25T12:00:00Z","received_at":"2026-11-25T12:00:03Z"}},
             "history":{"events":[
               {"received_at":"2026-11-25T12:00:04Z","payload":{
                 "source":"aovx-gal-v1","trust":"unverified","device_id":"own",
                 "gps":{"latitude":49.9,"longitude":24},"device_measured_at":"2026-11-25T11:59:00Z",
                 "event_time_basis":"gateway_received_at","extra":{"gnss_fix":true,"buffered":true}}},
               {"received_at":"2026-11-25T12:00:04Z","payload":{
                 "source":"aovx-gal-v1","trust":"unverified","device_id":"other",
                 "gps":{"latitude":49.9,"longitude":24},"device_measured_at":"2026-11-25T11:59:00Z",
                 "extra":{"gnss_fix":true}}}
             ]}}
        """.trimIndent())
        val fix = PhysicalPositions.current(device)!!
        assertEquals(49.8, fix.latitude, 0.0)
        assertEquals("2026-11-25T12:00:03Z", fix.received.toString())
        assertTrue(PhysicalPositions.history(device).single().buffered)
        val rows = CargoPresentation.sections("Device",device)
            .single { it.title == "Latest device observation" }.rows.toMap()
        assertEquals("2026-11-25T11:59:00Z",rows["Device measurement time (UTC)"])
        device.getJSONObject("location").put("device_id","other")
        assertNull(PhysicalPositions.current(device))
        device.getJSONObject("status").put("enabled",false)
        assertTrue(PhysicalPositions.history(device).isEmpty())
    }
}
