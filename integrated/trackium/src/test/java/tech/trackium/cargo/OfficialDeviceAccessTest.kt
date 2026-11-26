package tech.trackium.cargo

import org.junit.Assert.*
import org.junit.Test

class OfficialDeviceAccessTest {
    @Test fun onlyOfficialLinksAndWellFormedActivationCardsAreAccepted() {
        val code = "a".repeat(43)
        val parsed = OfficialDeviceAccess.card("trackium-activation:v1:TRACKER-123:$code")!!
        assertEquals("claim",parsed.first)
        assertEquals("TRACKER-123",parsed.second.getString("serial"))
        assertEquals(code,parsed.second.getString("code"))
        assertEquals("connect",OfficialDeviceAccess.card(OfficialDeviceAccess.origin + "/app/connect-core#$code")?.first)
        assertNull(OfficialDeviceAccess.card("https://other.example/app/connect-core#$code"))
        assertNull(OfficialDeviceAccess.card(OfficialDeviceAccess.origin + "/app/connect-core?code=$code"))
        assertNull(OfficialDeviceAccess.card("trackium-activation:v1:TRACKER-123:short"))
    }

    @Test fun requestSignatureUsesTheProtocolTranscript() {
        val nonce = "b13cc6b8-7d2a-4f2f-bdec-efad0e3a8e6e"
        val transcript = OfficialDeviceAccess.signingMessage("devices",nonce,1200,"{}").toString(Charsets.UTF_8)
        assertEquals(listOf("trackium-core-device-v1",OfficialDeviceAccess.origin,"devices",nonce,"1200",
            "44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a").joinToString("\n"),transcript)
    }
}
