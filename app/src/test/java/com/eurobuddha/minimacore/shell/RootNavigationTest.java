package com.eurobuddha.minimacore.shell;
import android.app.Activity;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=34,application=android.app.Application.class)
public class RootNavigationTest {
 @Test public void allFourDestinationsRemainAccessibleAtLargeFonts(){
  Activity a=Robolectric.buildActivity(Activity.class).setup().get();
  a.getResources().getConfiguration().fontScale=2f;
  int[] selected={-1};LinearLayout nav=(LinearLayout)RootNavigation.create(a,0,target->selected[0]=target);
  assertEquals(4,nav.getChildCount());
  for(int i=0;i<4;i++){
   android.view.View item=nav.getChildAt(i);assertTrue(item.isFocusable());assertTrue(item.isClickable());
   assertTrue(item.getMinimumHeight()>=ShellViews.dp(a,48));assertNotNull(item.getContentDescription());
   item.performClick();assertEquals(i,selected[0]);
  }
  assertTrue(nav.getChildAt(0).isSelected());a.finish();
 }
}
