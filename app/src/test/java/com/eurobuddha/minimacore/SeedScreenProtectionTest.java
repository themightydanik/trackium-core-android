package com.eurobuddha.minimacore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
public class SeedScreenProtectionTest {
    @Test public void seedCreationAndBothRestorePathsAlwaysRequirePrivateWindows() {
        assertTrue(MinimaApplication.requiresPrivateWindow(mock(com.eurobuddha.minimacore.launcher.newwallet.NewWalletActivity.class)));
        assertTrue(MinimaApplication.requiresPrivateWindow(mock(com.eurobuddha.minimacore.launcher.newwallet.NewWalletRestoreActivity.class)));
        assertTrue(MinimaApplication.requiresPrivateWindow(mock(com.eurobuddha.minimacore.launcher.restore.RestoreWalletSyncActivity.class)));
    }
    @Test public void workspaceAndExistingVaultRemainPrivate() {
        assertTrue(MinimaApplication.requiresPrivateWindow(mock(tech.trackium.cargo.MainActivity.class)));
        assertTrue(MinimaApplication.requiresPrivateWindow(mock(com.eurobuddha.minimacore.main.backup.VaultActivity.class)));
        assertFalse(MinimaApplication.requiresPrivateWindow(mock(com.eurobuddha.minimacore.launcher.LauncherActivity.class)));
    }
}
