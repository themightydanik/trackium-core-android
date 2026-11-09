package com.eurobuddha.minimacore.shell;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.eurobuddha.minimacore.R;
import com.eurobuddha.minimacore.main.MainActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
@RunWith(RobolectricTestRunner.class) @Config(sdk=34,application=Application.class)
public class ShellControllerTest {
 public static class Host extends MainActivity {
  @Override public void startMinimaService() {} // UI-only: never start a real node or contact peers.
  @Override public void checkBattery() {}
  @Override public void checkPermission(String[] p,int code){}
 }
 private org.robolectric.android.controller.ActivityController<Host> create(Bundle saved){
  RuntimeEnvironment.getApplication().getSharedPreferences("main_prefs",0).edit().putBoolean("keets_enabled",false).commit();
  org.robolectric.android.controller.ActivityController<Host> c=Robolectric.buildActivity(Host.class);
  c.get().setTheme(R.style.Theme_MinimaCore);return c.create(saved).start().resume().visible();
 }
 private void click(Host a,int target){((LinearLayout)a.findViewById(R.id.shell_navigation)).getChildAt(target).performClick();}
 @Test public void overviewMoreWalletAndNativeTrackiumKeepOneRootBar(){
  var c=create(null);Host a=c.get();assertEquals(View.GONE,a.findViewById(R.id.tabs).getVisibility());
  assertEquals(View.GONE,a.findViewById(R.id.main_pager).getVisibility());
  click(a,2);assertEquals(View.VISIBLE,a.findViewById(R.id.main_pager).getVisibility());
  click(a,3);assertEquals(View.GONE,a.findViewById(R.id.main_pager).getVisibility());
  click(a,1);Intent launched=shadowOf(a).getNextStartedActivity();assertEquals("tech.trackium.cargo.MainActivity",launched.getComponent().getClassName());
  a.getOnBackPressedDispatcher().onBackPressed();assertTrue(((LinearLayout)a.findViewById(R.id.shell_navigation)).getChildAt(0).isSelected());
  c.pause().stop().destroy();
 }
 @Test public void recreatedHostRestoresMoreAndBackReturnsOverview(){
  var c=create(null);click(c.get(),3);Bundle b=new Bundle();c.saveInstanceState(b).pause().stop().destroy();
  var restored=create(b);assertTrue(((LinearLayout)restored.get().findViewById(R.id.shell_navigation)).getChildAt(3).isSelected());
  restored.get().getOnBackPressedDispatcher().onBackPressed();
  assertTrue(((LinearLayout)restored.get().findViewById(R.id.shell_navigation)).getChildAt(0).isSelected());restored.pause().stop().destroy();
 }
}
