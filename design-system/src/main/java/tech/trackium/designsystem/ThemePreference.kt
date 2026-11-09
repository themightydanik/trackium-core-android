package tech.trackium.designsystem

import android.content.Context
import android.content.res.Configuration

/** Presentation preferences only; never shares the wallet's settings store. */
object ThemePreference {
    const val STORE = "trackium_appearance"
    @JvmStatic fun get(context: Context): String =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE).getString("theme", "system")
            ?.takeIf { it in listOf("system", "light", "dark") } ?: "system"
    @JvmStatic fun set(context: Context, theme: String) {
        require(theme in listOf("system", "light", "dark"))
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().putString("theme", theme).apply()
    }
    @JvmStatic fun wrap(context: Context): Context {
        val mode = when (get(context)) {
            "light" -> Configuration.UI_MODE_NIGHT_NO
            "dark" -> Configuration.UI_MODE_NIGHT_YES
            else -> return context
        }
        val config = Configuration(context.resources.configuration)
        config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or mode
        return context.createConfigurationContext(config)
    }
}
