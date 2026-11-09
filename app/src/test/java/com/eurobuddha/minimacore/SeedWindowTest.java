package com.eurobuddha.minimacore;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
/** No screenshots or seed content are read, serialized, logged or asserted. */
@RunWith(RobolectricTestRunner.class) @Config(sdk=34,application=android.app.Application.class)
public class SeedWindowTest {
 private <T extends AppCompatActivity> void protectedScreen(Class<T> type,int view){
  var c=Robolectric.buildActivity(type);c.get().setTheme(R.style.Theme_MinimaCore);c.create().start().resume();
  assertNotEquals(0,c.get().getWindow().getAttributes().flags&WindowManager.LayoutParams.FLAG_SECURE);
  assertFalse(c.get().findViewById(view).isSaveEnabled());c.pause().stop().destroy();
 }
 @Test public void creation(){protectedScreen(com.eurobuddha.minimacore.launcher.newwallet.NewWalletActivity.class,R.id.newwallet_seed);}
 @Test public void newWalletRestore(){protectedScreen(com.eurobuddha.minimacore.launcher.newwallet.NewWalletRestoreActivity.class,R.id.restorewallet_seed);}
 @Test public void existingWalletRestore(){protectedScreen(com.eurobuddha.minimacore.launcher.restore.RestoreWalletSyncActivity.class,R.id.restorewallet_seed);}
}
