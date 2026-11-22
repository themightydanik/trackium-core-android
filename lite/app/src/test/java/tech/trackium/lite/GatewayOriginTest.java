package tech.trackium.lite;

import org.junit.Test;
import static org.junit.Assert.*;

public class GatewayOriginTest {
    @Test public void httpsOriginAndWorkspaceBoundaries() {
        GatewayOrigin gateway = GatewayOrigin.parse("HTTPS://Gateway.Example:443/");
        assertEquals("https://gateway.example/app/", gateway.workspace());
        assertTrue(gateway.permits("https://gateway.example/app/#proofs", true));
        assertTrue(gateway.permits("https://gateway.example/v1/session", false));
        assertFalse(gateway.permits("https://gateway.example/v1/session", true));
        assertFalse(gateway.permits("https://gateway.example:444/app/", true));
        assertFalse(gateway.permits("https://gateway.example.evil/app/", true));
        assertFalse(gateway.permits("https://gateway.example/app/../v1/session", true));
        assertFalse(gateway.permits("https://gateway.example/app/%2e%2e/v1/session", true));
        assertFalse(gateway.permits("https://gateway.example/app/?token=secret", true));
        assertFalse(gateway.permits("file:///app/index.html", true));
    }

    @Test public void rejectsCredentialsAndNonOriginInputs() {
        for (String value : new String[]{"http://gateway.example", "https://owner@gateway.example",
                "https://gateway.example/?token=secret", "https://gateway.example/app/",
                "https://gateway.example/#fragment", "https://gateway.example:65536"}) {
            assertThrows(IllegalArgumentException.class, () -> GatewayOrigin.parse(value));
        }
    }
}
