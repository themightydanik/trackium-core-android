package com.eurobuddha.minimacore.shell;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=34,application=android.app.Application.class)
public class ShellStateTest {
 @Test public void restoreAndBackRetainOnlyNavigation(){
  ShellState s=new ShellState();s.root=3;s.tool=2;s.scroll=321;Bundle b=new Bundle();s.save(b);
  assertEquals(3,b.size());ShellState restored=new ShellState();restored.restore(b);
  assertEquals(3,restored.root);assertEquals(2,restored.tool);assertEquals(321,restored.scroll);
  assertTrue(restored.back());assertEquals(-1,restored.tool);assertEquals(3,restored.root);
  assertTrue(restored.back());assertEquals(0,restored.root);assertFalse(restored.back());
 }
 @Test public void untrustedDestinationsAndToolIndexesAreRejected(){
  Bundle b=new Bundle();b.putInt("shell.root",99);b.putInt("shell.tool",999);b.putInt("shell.scroll",-5);
  ShellState s=new ShellState();s.restore(b);assertEquals(0,s.root);assertEquals(-1,s.tool);assertEquals(0,s.scroll);
  b.putInt("shell.root",2);b.putInt("shell.tool",0);s.restore(b);assertEquals(-1,s.tool);
 }
}
