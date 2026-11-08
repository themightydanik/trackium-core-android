package tech.trackium.designsystem
import android.app.Application
import android.content.res.Configuration
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[28], application=Application::class)
class TrackiumTokensTest {
    private fun context(night: Boolean) = RuntimeEnvironment.getApplication().let { app ->
        app.createConfigurationContext(Configuration(app.resources.configuration).apply {
            uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                if(night) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
        })
    }
    @Test fun darkRolesResolveThroughNightResources() {
        assertEquals(0xFF0B1220.toInt(), TrackiumTokens.color(context(true), TrackiumTokens.ColorRole.CANVAS))
        assertEquals(0xFF4FE0C1.toInt(), TrackiumTokens.color(context(true), TrackiumTokens.ColorRole.ACCENT))
    }
    @Test fun lightRolesRemainAvailableRegardlessOfHostDarkPalette() {
        assertEquals(0xFFF5F8FC.toInt(), TrackiumTokens.color(context(false), TrackiumTokens.ColorRole.CANVAS))
        assertEquals(0xFF007667.toInt(), TrackiumTokens.color(context(false), TrackiumTokens.ColorRole.ACCENT))
    }
    @Test fun disabledPrimaryUsesSemanticStateInBothThemes() {
        for(night in listOf(false,true)) {
            val ctx = context(night)
            val colors = ctx.getColorStateList(R.color.tc_ds1_primary_container)
            assertEquals(TrackiumTokens.color(ctx, TrackiumTokens.ColorRole.DISABLED_CONTAINER),
                colors.getColorForState(intArrayOf(-android.R.attr.state_enabled), 0))
            assertEquals(TrackiumTokens.color(ctx, TrackiumTokens.ColorRole.ACCENT), colors.defaultColor)
        }
    }
    @Test fun everyRoleResolvesInBothModes() {
        for(night in listOf(false,true)) for(role in TrackiumTokens.ColorRole.values())
            assertNotEquals(0, TrackiumTokens.color(context(night),role))
    }
}
