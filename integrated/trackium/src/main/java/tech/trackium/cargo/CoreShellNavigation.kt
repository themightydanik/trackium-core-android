package tech.trackium.cargo

import android.app.Activity
import android.view.View

/** Host-owned presentation bridge. No wallet/command/Gateway capabilities. */
object CoreShellNavigation {
    fun interface Factory { fun create(activity: Activity): View }
    private var factory: Factory? = null
    @JvmStatic fun install(value: Factory) { factory = value }
    @JvmStatic fun create(activity: Activity): View? = factory?.create(activity)
}
