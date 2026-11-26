package tech.trackium.cargo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/** The only public entry for device connection links. Local Core unlock is still required. */
class CoreConnectionActivity : Activity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val link = intent.takeIf { it.action == Intent.ACTION_VIEW }?.dataString
        if (link != null && OfficialDeviceAccess.card(link)?.first == "connect") {
            if (TrackiumCoreAccess.authorized()) {
                startActivity(Intent(this, MainActivity::class.java).putExtra(CONNECTION_LINK,link))
            } else {
                Toast.makeText(this,"Unlock Trackium Core, then reopen the device connection link.",Toast.LENGTH_LONG).show()
                packageManager.getLaunchIntentForPackage(packageName)?.let(::startActivity)
            }
        }
        finish()
    }

    companion object {
        internal const val CONNECTION_LINK = "tech.trackium.core.DEVICE_CONNECTION_LINK"
    }
}
