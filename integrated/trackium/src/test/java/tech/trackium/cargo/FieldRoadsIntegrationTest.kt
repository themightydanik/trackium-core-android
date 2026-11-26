package tech.trackium.cargo

import java.io.File
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

class FieldRoadsIntegrationTest {
    @Test fun independentHttpsProxyReturnsRealCorridorGeometry() {
        val ca = System.getenv("TRACKIUM_FIELD_TEST_CA")
        assumeTrue("Requires disposable loopback HTTPS Valhalla fixture", !ca.isNullOrBlank())
        val certificate = File(ca!!).inputStream().use { CertificateFactory.getInstance("X.509").generateCertificate(it) }
        val store = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null,null)
            setCertificateEntry("fixture-only",certificate)
        }
        val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(store) }
        val context = SSLContext.getInstance("TLS").apply { init(null,factory.trustManagers,null) }
        val previous = HttpsURLConnection.getDefaultSSLSocketFactory()
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
        try {
            val provider = FieldRoads.Provider("https://127.0.0.1:8443","owner","fixture-pass-not-production",
                "6a707e47f877bd471dce66baa5fb0e1e54d4a1bfe6a0f2c49156eb1f9d992f29")
            val stops = LocalFieldPlan.parse("49.84112,24.02999\n50.03741,22.00709")
            val road = FieldRoads.calculate(provider,stops)
            assertEquals(169710L,road.meters)
            assertEquals(7916L,road.seconds)
            assertTrue(road.legs.single().size > 100)
            assertTrue(runCatching { FieldRoads.calculate(provider.copy(password="invalid-test-password"),stops) }.isFailure)
            assertTrue(runCatching { FieldRoads.calculate(provider.copy(graph="a".repeat(64)),stops) }.isFailure)
        } finally {
            HttpsURLConnection.setDefaultSSLSocketFactory(previous)
        }
    }
}
