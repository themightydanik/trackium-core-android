package tech.trackium.cargo

import android.app.Application
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE, application = Application::class)
class EmbeddedActivityLockTest {
    @After fun reset() { TrackiumCoreAccess.install { false } }
    @Test fun lockedActivityNeverReadsEncryptedStateOrStartsNodeObservation() {
        TrackiumCoreAccess.install { false }
        // Generic Application has no host transport or AndroidKeyStore. Reaching
        // controller/renderSafe would fail; a locked entry must avoid both.
        val controller = Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
        val activity = controller.get()
        assertTrue(activity.isFinishing)
        val content = activity.findViewById<android.view.ViewGroup>(android.R.id.content)
        assertEquals("Unlock Trackium Core to continue", (content.getChildAt(0) as TextView).text)
        controller.pause().stop().destroy()
    }

    @Test fun evidenceCardsRenderReceiptAndUnavailablePhysicalPosition() {
        // Render detached evidence cards without opening the encrypted store or connecting to Core.
        val controller = Robolectric.buildActivity(MainActivity::class.java).create()
        val activity = controller.get()
        val container = LinearLayout(activity)
        val observation = JSONObject("""
            {"identity_kind":"physical","status":{"mode":"real"},
             "location":{"location":null},"history":{"events":[{
               "received_at":"2026-11-20T11:12:13Z","payload":{
                 "trust":"unverified","event_time_basis":"gateway_received_at","gps":null
               }
             }]}}
        """.trimIndent())
        activity.renderEvidenceSections(container, "Device", observation)
        fun labels(view: View): List<String> = when (view) {
            is TextView -> listOf(view.text.toString())
            is ViewGroup -> (0 until view.childCount).flatMap { labels(view.getChildAt(it)) }
            else -> emptyList()
        }
        val visible = labels(container)
        fun hasRow(label: String, value: String) =
            visible.windowed(2).any { it == listOf(label, value) }
        assertTrue(hasRow("Gateway received", "2026-11-20T11:12:13Z"))
        assertTrue(hasRow("Device measurement time", "Unavailable"))
        assertTrue(hasRow("Provenance", "Physical observation · unverified"))
        assertTrue(hasRow("Position", "Unavailable"))
        assertFalse("Latitude" in visible)
        controller.destroy()
    }
}
