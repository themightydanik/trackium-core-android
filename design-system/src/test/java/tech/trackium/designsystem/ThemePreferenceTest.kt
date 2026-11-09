package tech.trackium.designsystem
import android.app.Application
import android.content.res.Configuration
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class) @Config(sdk=[34],application=Application::class)
class ThemePreferenceTest {
 @Test fun manualModesAndSystemHaveSeparateValidatedPersistence() {
  val c=RuntimeEnvironment.getApplication()
  ThemePreference.set(c,"dark")
  assertEquals(Configuration.UI_MODE_NIGHT_YES,ThemePreference.wrap(c).resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
  ThemePreference.set(c,"light")
  assertEquals(Configuration.UI_MODE_NIGHT_NO,ThemePreference.wrap(c).resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
  ThemePreference.set(c,"system");assertSame(c,ThemePreference.wrap(c))
  assertFalse(c.getSharedPreferences("main_prefs",0).contains("theme"))
 }
 @Test(expected=IllegalArgumentException::class) fun invalidThemeIsRejected(){ThemePreference.set(RuntimeEnvironment.getApplication(),"invalid")}
}
