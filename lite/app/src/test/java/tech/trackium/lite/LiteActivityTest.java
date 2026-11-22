package tech.trackium.lite;

import android.content.Context;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class LiteActivityTest {
    private LinearLayout content(LiteActivity activity) {
        return (LinearLayout) ((android.view.ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    @Test public void firstLaunchShowsConnectionAndProtectsScreen() {
        try (var controller = Robolectric.buildActivity(LiteActivity.class).setup()) {
            LiteActivity activity = controller.get();
            assertTrue(((TextView) content(activity).getChildAt(0)).getText().toString().contains("Gateway"));
            assertNotEquals(0, activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Test public void restartOpensConfiguredHttpsWorkspaceWithoutFileAccess() {
        RuntimeEnvironment.getApplication().getSharedPreferences("gateway-profile", Context.MODE_PRIVATE)
                .edit().putString("origin", "https://gateway.example").commit();
        try (var controller = Robolectric.buildActivity(LiteActivity.class).setup()) {
            WebView view = (WebView) content(controller.get()).getChildAt(1);
            assertEquals("https://gateway.example/app/", view.getUrl());
            assertFalse(view.getSettings().getAllowFileAccess());
            assertFalse(view.getSettings().getAllowContentAccess());
            assertEquals(WebSettings.MIXED_CONTENT_NEVER_ALLOW, view.getSettings().getMixedContentMode());
        }
    }
}
