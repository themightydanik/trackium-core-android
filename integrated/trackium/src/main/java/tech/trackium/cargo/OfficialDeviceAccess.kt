package tech.trackium.cargo

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.net.URL
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.Base64
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject

internal object OfficialDeviceAccess {
    const val origin = "https://trackium-gateway-production.up.railway.app"
    private const val alias = "trackium.core.device.p256.v1"
    private val code = Regex("[A-Za-z0-9_-]{43}")
    private val activation = Regex("trackium-activation:v1:([A-Z0-9][A-Z0-9._-]{3,63}):([A-Za-z0-9_-]{43})")

    fun card(value: String): Pair<String, JSONObject>? {
        activation.matchEntire(value)?.let {
            val (serial, secret) = it.destructured
            return "claim" to JSONObject().put("serial",serial).put("code",secret).put("label",serial)
        }
        val prefix = "$origin/app/connect-core#"
        val secret = value.removePrefix(prefix)
        return if (value.startsWith(prefix) && code.matches(secret))
            "connect" to JSONObject().put("code",secret) else null
    }

    @Synchronized
    private fun key(): KeyStore.PrivateKeyEntry {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!store.containsAlias(alias)) {
            val spec = KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build()
            KeyPairGenerator.getInstance("EC","AndroidKeyStore").apply { initialize(spec) }.generateKeyPair()
        }
        return store.getEntry(alias,null) as KeyStore.PrivateKeyEntry
    }

    fun request(operation: String, input: JSONObject = JSONObject()): JSONObject {
        require(operation in setOf("claim","connect","devices","observations","calculate"))
        val key = key()
        val payload = input.toString()
        val nonce = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val data = signingMessage(operation,nonce,timestamp,payload)
        val signature = Signature.getInstance("SHA256withECDSA").run {
            initSign(key.privateKey)
            update(data)
            sign()
        }
        val base64 = Base64.getUrlEncoder().withoutPadding()
        val body = JSONObject().put("public_key",base64.encodeToString(key.certificate.publicKey.encoded))
            .put("nonce",nonce).put("timestamp",timestamp).put("payload",payload)
            .put("signature",base64.encodeToString(signature)).toString().toByteArray(Charsets.UTF_8)
        val connection = URL("$origin/v1/core-devices/$operation").openConnection() as HttpsURLConnection
        try {
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 5000
            connection.readTimeout = 12000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type","application/json")
            connection.setFixedLengthStreamingMode(body.size)
            connection.outputStream.use { it.write(body) }
            check(connection.responseCode in 200..299) { "Trackium service rejected this operation." }
            return connection.inputStream.use { JSONObject(FieldRoads.bounded(it,2_000_000).toString(Charsets.UTF_8)) }
        } finally { connection.disconnect() }
    }

    fun signingMessage(operation: String, nonce: String, timestamp: Long, payload: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return listOf("trackium-core-device-v1",origin,operation,nonce,timestamp.toString(),digest)
            .joinToString("\n").toByteArray(Charsets.UTF_8)
    }
}
