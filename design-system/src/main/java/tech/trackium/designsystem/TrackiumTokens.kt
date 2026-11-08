package tech.trackium.designsystem
import android.content.Context
/** Generated DS1 semantic roles. Resource qualifiers follow the supplied Context's UI mode. */
object TrackiumTokens {
    const val VERSION = "1.0.0"
    enum class ColorRole(val resource: Int) {
        CANVAS(R.color.tc_ds1_canvas),
        SURFACE(R.color.tc_ds1_surface),
        SURFACE_RAISED(R.color.tc_ds1_surface_raised),
        OUTLINE(R.color.tc_ds1_outline),
        TEXT(R.color.tc_ds1_text),
        TEXT_SECONDARY(R.color.tc_ds1_text_secondary),
        ACCENT(R.color.tc_ds1_accent),
        ON_ACCENT(R.color.tc_ds1_on_accent),
        WARNING(R.color.tc_ds1_warning),
        DANGER(R.color.tc_ds1_danger),
        INFO(R.color.tc_ds1_info),
        OUTLINE_SUBTLE(R.color.tc_ds1_outline_subtle),
        GOLD(R.color.tc_ds1_gold),
        DISABLED_CONTAINER(R.color.tc_ds1_disabled_container),
        DISABLED_TEXT(R.color.tc_ds1_disabled_text),
        FOCUS(R.color.tc_ds1_focus)
    }
    @JvmStatic fun color(context: Context, role: ColorRole): Int = context.resources.getColor(role.resource, context.theme)
    const val TOUCH_TARGET_DP = 48
}
