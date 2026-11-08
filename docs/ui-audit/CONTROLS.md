# Forms, dialogs and action call-site census

Source-only register; these controls/call sites were not all executed. Conditional financial/restore actions stay protected and were not tested with a wallet. Generic helper functions are expanded by their source call sites, not assumed to be separate Activities. No raw user secrets/data appear here.

## Every XML form/control

| Source | Details |
|---|---|
| `app/src/main/res/layout/view_wallet_receive.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_receive_copy", "text": "Copy"}` |
| `app/src/main/res/layout/view_wallet_receive.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_receive_changeaddress", "text": "Get Address"}` |
| `app/src/main/res/layout/dialog_apps_info.xml` | `{"type": "CheckBox", "id": "@+id/app_info_enabled", "text": "Enabled"}` |
| `app/src/main/res/layout/dialog_apps_info.xml` | `{"type": "CheckBox", "id": "@+id/app_info_admin", "text": "Admin"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "EditText", "id": "@+id/chat_backup_password", "hint": "at least 8 characters", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "ImageButton", "id": "@+id/chat_backup_password_eye"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "EditText", "id": "@+id/chat_backup_confirm", "hint": "type it again", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "ImageButton", "id": "@+id/chat_backup_confirm_eye"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/chat_backup_save", "text": "Save chat backup"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "EditText", "id": "@+id/chat_backup_restore_password", "hint": "at least 8 characters", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "ImageButton", "id": "@+id/chat_backup_restore_password_eye"}` |
| `app/src/main/res/layout/backup_chat.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/chat_backup_restore", "text": "Choose backup file"}` |
| `app/src/main/res/layout/view_logs.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/logs_pause", "text": "Pause"}` |
| `app/src/main/res/layout/view_logs.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/logs_clear", "text": "Clear"}` |
| `app/src/main/res/layout/view_logs.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/logs_share", "text": "Share"}` |
| `app/src/main/res/layout/view_logs.xml` | `{"type": "EditText", "id": "@+id/logs_filter", "hint": "Filter"}` |
| `app/src/main/res/layout/activity_lock.xml` | `{"type": "EditText", "id": "@+id/lock_pin", "hint": "@string/l10n_lock_pin", "inputType": "numberPassword"}` |
| `app/src/main/res/layout/activity_lock.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/lock_unlock", "text": "@string/l10n_lock_unlock"}` |
| `app/src/main/res/layout/activity_lock.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/lock_biometric", "text": "@string/l10n_lock_use_biometric"}` |
| `app/src/main/res/layout/dialog_chat_identity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/identity_copy", "text": "Copy address"}` |
| `app/src/main/res/layout/dialog_chat_identity.xml` | `{"type": "EditText", "id": "@+id/identity_name", "hint": "Your display name", "inputType": "textCapWords"}` |
| `app/src/main/res/layout/seed_sync.xml` | `{"type": "EditText", "id": "@+id/seed_sync_host", "hint": "host:port", "inputType": "textUri"}` |
| `app/src/main/res/layout/seed_sync.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/seed_sync_proceed", "text": "Resync Node"}` |
| `app/src/main/res/layout/seed_sync.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/seed_sync_copy", "text": "Copy log"}` |
| `app/src/main/res/layout/seed_sync.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/seed_sync_restart", "text": "Restart node"}` |
| `app/src/main/res/layout/activity_lock_settings.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/lock_switch_enable", "text": "@string/l10n_lock_require_pin"}` |
| `app/src/main/res/layout/activity_lock_settings.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/lock_switch_bio", "text": "@string/l10n_lock_use_biometric"}` |
| `app/src/main/res/layout/activity_lock_settings.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/lock_autolock", "text": "@string/l10n_lock_one_minute"}` |
| `app/src/main/res/layout/activity_lock_settings.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/lock_change_pin", "text": "@string/l10n_lock_change_pin"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "EditText", "id": "@+id/backup_create_name", "hint": "minima-backup.bak", "inputType": "text"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "EditText", "id": "@+id/backup_create_password", "hint": "letters and numbers only", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "ImageButton", "id": "@+id/backup_create_password_eye"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "EditText", "id": "@+id/backup_create_confirm", "hint": "type it again", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "ImageButton", "id": "@+id/backup_create_confirm_eye"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_create_go", "text": "Create backup"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_create_copy", "text": "Copy full path"}` |
| `app/src/main/res/layout/backup_create.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_create_export", "text": "Save a copy to this phone"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "Spinner", "id": "@+id/wallet_send_tokens"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "EditText", "id": "@+id/wallet_send_amount", "hint": "0.0", "inputType": "numberDecimal"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_send_max", "text": "Max"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "EditText", "id": "@+id/wallet_send_address", "hint": "Mx… or 0x…", "inputType": "textNoSuggestions / textMultiLine"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_send_scan", "text": "Scan"}` |
| `app/src/main/res/layout/view_wallet_send.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_send_sendbutton", "text": "Send Funds"}` |
| `app/src/main/res/layout/launcher_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/launcher_language", "text": "@string/l10n_language_title"}` |
| `app/src/main/res/layout/launcher_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/launcher_button_newwallet", "text": "@string/l10n_launcher_new_wallet"}` |
| `app/src/main/res/layout/launcher_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/launcher_button_restore", "text": "@string/l10n_launcher_restore_wallet"}` |
| `app/src/main/res/layout/launcher_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/launcher_button_params", "text": "@string/l10n_menu_params"}` |
| `app/src/main/res/layout/activity_chat_thread.xml` | `{"type": "ImageButton", "id": "@+id/chat_attach"}` |
| `app/src/main/res/layout/activity_chat_thread.xml` | `{"type": "EditText", "id": "@+id/chat_input", "hint": "Message", "inputType": "textCapSentences / textMultiLine"}` |
| `app/src/main/res/layout/activity_chat_thread.xml` | `{"type": "com.google.android.material.floatingactionbutton.FloatingActionButton", "id": "@+id/chat_send"}` |
| `app/src/main/res/layout/dialog_chat_new.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/newchat_pick", "text": "Choose a saved contact"}` |
| `app/src/main/res/layout/dialog_chat_new.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/newchat_scan", "text": "Scan their QR code"}` |
| `app/src/main/res/layout/dialog_chat_new.xml` | `{"type": "EditText", "id": "@+id/newchat_address", "hint": "keets 0x… or Parlons MAX# / Mx…@host:port", "inputType": "text"}` |
| `app/src/main/res/layout/dialog_chat_new.xml` | `{"type": "EditText", "id": "@+id/newchat_name", "hint": "Name (optional)", "inputType": "textCapWords"}` |
| `app/src/main/res/layout/view_chat.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/chat_empty_start", "text": "Start a new chat"}` |
| `app/src/main/res/layout/view_chat.xml` | `{"type": "com.google.android.material.floatingactionbutton.FloatingActionButton", "id": "@+id/chat_fab"}` |
| `app/src/main/res/layout/view_apps_internal_row.xml` | `{"type": "com.google.android.material.materialswitch.MaterialSwitch", "id": "@+id/int_app_switch"}` |
| `app/src/main/res/layout/view_apps_internal_row.xml` | `{"type": "com.google.android.material.materialswitch.MaterialSwitch", "id": "@+id/int_app_freeze"}` |
| `app/src/main/res/layout/view_coin_detail_row.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/detail_copy", "text": "COPY"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_classic", "text": "@string/classic_mode_title"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_server", "text": "Server mode (-server)"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_megammr", "text": "MegaMMR (-megammr)"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_rpc", "text": "Enable RPC (-rpcenable)"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/params_rpc_password_copy", "text": "Copy RPC password"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_autoresync", "text": "Resync automatically when unhealthy"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.switchmaterial.SwitchMaterial", "id": "@+id/params_switch_keets", "text": "Keets transport (-keets)"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "EditText", "id": "@+id/params_extra", "hint": "-p2pnodes host:port -txpowdbstore 3 …", "inputType": "textMultiLine / textNoSuggestions"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/params_button_save", "text": "Save"}` |
| `app/src/main/res/layout/params_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/params_button_save_restart", "text": "Save & Restart Node"}` |
| `app/src/main/res/layout/vault_keys.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/vault_reveal", "text": "Show seed phrase"}` |
| `app/src/main/res/layout/vault_keys.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/vault_copy_phrase", "text": "Copy seed phrase"}` |
| `app/src/main/res/layout/restorewallet_activity.xml` | `{"type": "EditText", "id": "@+id/restorewallet_seed", "hint": "word one word two …", "inputType": "textMultiLine"}` |
| `app/src/main/res/layout/restorewallet_activity.xml` | `{"type": "EditText", "id": "@+id/restorewallet_keyuses", "hint": "0", "inputType": "number"}` |
| `app/src/main/res/layout/restorewallet_activity.xml` | `{"type": "EditText", "id": "@+id/restorewallet_megammr", "text": "spartacusrex.com:9001"}` |
| `app/src/main/res/layout/restorewallet_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/restorewallet_button_check", "text": "Check Word List"}` |
| `app/src/main/res/layout/restorewallet_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/restorewallet_button_proceed", "text": "Proceed"}` |
| `app/src/main/res/layout/view_password_field.xml` | `{"type": "EditText", "id": "@+id/password_input", "inputType": "textPassword"}` |
| `app/src/main/res/layout/view_password_field.xml` | `{"type": "ImageButton", "id": "@+id/password_toggle"}` |
| `app/src/main/res/layout/view_terminal.xml` | `{"type": "EditText", "id": "@+id/terminal_input", "hint": "Enter command"}` |
| `app/src/main/res/layout/view_terminal.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/terminal_send", "text": "Run"}` |
| `app/src/main/res/layout/newwallet_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/newwallet_button_proceed", "text": "I've saved it — Proceed"}` |
| `app/src/main/res/layout/dialog_chat_pay.xml` | `{"type": "RadioGroup", "id": "@+id/pay_currency"}` |
| `app/src/main/res/layout/dialog_chat_pay.xml` | `{"type": "RadioButton", "id": "@+id/pay_minima", "text": "Minima"}` |
| `app/src/main/res/layout/dialog_chat_pay.xml` | `{"type": "RadioButton", "id": "@+id/pay_mxusd", "text": "MxUSD"}` |
| `app/src/main/res/layout/dialog_chat_pay.xml` | `{"type": "EditText", "id": "@+id/pay_amount", "hint": "Amount", "inputType": "numberDecimal"}` |
| `app/src/main/res/layout/dialog_contact_pick.xml` | `{"type": "EditText", "id": "@+id/pick_search", "hint": "Search contacts", "inputType": "text"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_restore_import", "text": "Bring a file in from this phone"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "EditText", "id": "@+id/backup_restore_password", "hint": "the password used when it was made", "inputType": "textPassword"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "ImageButton", "id": "@+id/backup_restore_password_eye"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "RadioGroup", "id": "@+id/backup_restore_mode"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "RadioButton", "id": "@+id/backup_restore_mode_local", "text": "Use the backup as it is (it is recent)"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "RadioButton", "id": "@+id/backup_restore_mode_resync", "text": "Catch up to the chain tip from a MegaMMR node (it is old)"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "EditText", "id": "@+id/backup_restore_host", "hint": "host:port", "inputType": "textUri"}` |
| `app/src/main/res/layout/backup_restore.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_restore_go", "text": "Restore this node"}` |
| `app/src/main/res/layout/view_wallet_balance.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_btn_send", "text": "Send"}` |
| `app/src/main/res/layout/view_wallet_balance.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/wallet_btn_receive", "text": "Receive"}` |
| `app/src/main/res/layout/view_home.xml` | `{"type": "Button", "id": "@+id/open_trackium", "text": "Open Trackium"}` |
| `app/src/main/res/layout/view_home.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/button_tester", "text": "Tester"}` |
| `app/src/main/res/layout/keyboard_dismiss.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/keyboard_dismiss", "text": "@string/hide_keyboard"}` |
| `app/src/main/res/layout/activity_terminal.xml` | `{"type": "EditText", "id": "@+id/terminal_input", "hint": "Enter command"}` |
| `app/src/main/res/layout/activity_terminal.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/terminal_send", "text": "Run"}` |
| `app/src/main/res/layout/restorenewwallet_activity.xml` | `{"type": "EditText", "id": "@+id/restorewallet_seed", "hint": "word one word two …", "inputType": "textMultiLine"}` |
| `app/src/main/res/layout/restorenewwallet_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/restorewallet_button_check", "text": "Check Word List"}` |
| `app/src/main/res/layout/restorenewwallet_activity.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/restorewallet_button_proceed", "text": "Proceed"}` |
| `app/src/main/res/layout/view_backup_row.xml` | `{"type": "com.google.android.material.button.MaterialButton", "id": "@+id/backup_row_action"}` |
| `integrated/history/src/main/res/layout/pm_history_activity_main.xml` | `{"type": "Button", "id": "@+id/pm_history_openNodeBtn", "text": "@string/l10n_history_open_core"}` |
| `integrated/history/src/main/res/layout/pm_history_activity_main.xml` | `{"type": "EditText", "id": "@+id/pm_history_search", "hint": "@string/l10n_history_search", "inputType": "text"}` |

## Dialog/title/confirmation source call sites

| Source | Details |
|---|---|
| `app/src/main/java/com/eurobuddha/minimacore/main/ParamsActivity.java` | `{"line": 138, "call": ".setTitle(Language.msg(l10n_params_restart_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/ParamsActivity.java` | `{"line": 141, "call": ".setPositiveButton(Language.msg(android.R.string.yes), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/ParamsActivity.java` | `{"line": 148, "call": ".setNegativeButton(Language.msg(android.R.string.no), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/ParamsActivity.java` | `{"line": 225, "call": ".setTitle(zTitle)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/ParamsActivity.java` | `{"line": 228, "call": ".setPositiveButton(Language.msg(android.R.string.yes), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/SeedSyncActivity.java` | `{"line": 117, "call": ".setTitle(Language.msg(l10n_resync_confirm_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/SeedSyncActivity.java` | `{"line": 119, "call": ".setPositiveButton(Language.msg(l10n_resync_operation), (d, w) -> startResync())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/SeedSyncActivity.java` | `{"line": 120, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 127, "call": "tb.setTitle(\"Trackium Core\");"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 242, "call": "getSupportActionBar().setTitle(BuildConfig.PANDAMONIUM ? \"\" : title);"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 370, "call": "menu.findItem(R.id.mainmenu_logs).setTitle(Language.text(this, l10n_menu_logs));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 371, "call": "menu.findItem(R.id.mainmenu_terminal).setTitle(Language.text(this, l10n_tab_terminal));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 372, "call": "menu.findItem(R.id.mainmenu_backup).setTitle(Language.text(this, l10n_menu_backup));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 373, "call": "menu.findItem(R.id.mainmenu_params).setTitle(Language.text(this, l10n_menu_params));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 374, "call": "menu.findItem(R.id.mainmenu_locknow).setTitle(Language.text(this, l10n_menu_lock_now));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 375, "call": "menu.findItem(R.id.mainmenu_applock).setTitle(Language.text(this, l10n_menu_app_lock));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 376, "call": "menu.findItem(R.id.mainmenu_shutdown).setTitle(Language.text(this, l10n_menu_shutdown));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 377, "call": "menu.findItem(R.id.mainmenu_wipeminima).setTitle(Language.text(this, l10n_menu_reset));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 378, "call": "menu.findItem(R.id.mainmenu_screenshots).setTitle(Language.text(this, l10n_menu_screenshots));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 379, "call": "menu.findItem(R.id.mainmenu_language).setTitle(Language.text(this, l10n_language_title));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 380, "call": "menu.findItem(R.id.mainmenu_battery).setTitle(Language.text(this, l10n_menu_battery));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 449, "call": ".setTitle(Language.text(this, l10n_battery_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 451, "call": ".setNegativeButton(Language.text(this, l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 452, "call": ".setPositiveButton(android.R.string.ok, (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 587, "call": "builder.setTitle(Language.msg(l10n_core_peer_title));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 593, "call": "builder.setPositiveButton(Language.msg(l10n_core_add_peer), new DialogInterface.OnClickListener() {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 603, "call": "builder.setNegativeButton(Language.msg(l10n_common_cancel), new DialogInterface.OnClickListener() {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 621, "call": "mShutdownDialog.setTitle(Language.text(this, l10n_core_shutting_down));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 685, "call": ".setTitle(Language.msg(l10n_core_reset_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 688, "call": ".setPositiveButton(Language.msg(android.R.string.yes), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/MainActivity.java` | `{"line": 697, "call": ".setNegativeButton(Language.msg(android.R.string.no), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFiles.java` | `{"line": 61, "call": "private void error(Exception e){post(()->track(new LanguageDialog(activity).setTitle(R.string.chat_files_title)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFiles.java` | `{"line": 62, "call": ".setMessage(e.getMessage()==null?text(R.string.chat_files_failed):e.getMessage()).setPositiveButton(android.R.string.ok,null).show()));}"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFiles.java` | `{"line": 122, "call": "track(new LanguageDialog(activity).setTitle(R.string.chat_files_title).setItems(names,(d,w)->show(list.get(w))).setNegativeButton(Language.msg(com.eurobuddha.minimacore.i18n.R.string.l10n_language_close),null).show());});"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFiles.java` | `{"line": 137, "call": "AlertDialog dialog=track(new LanguageDialog(activity).setTitle(t.file.name).setView(box).setNegativeButton(Language.msg(com.eurobuddha.minimacore.i18n.R.string.l10n_language_close),null).show());"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatImageActivity.java` | `{"line": 50, "call": "getSupportActionBar().setTitle(\"\");"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatImageActivity.java` | `{"line": 90, "call": "if (toolbar.getMenu().findItem(R.id.imgmenu_share) != null) toolbar.getMenu().findItem(R.id.imgmenu_share).setTitle(Language.text(this, l10n_logs_share));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatImageActivity.java` | `{"line": 91, "call": "if (toolbar.getMenu().findItem(R.id.imgmenu_save) != null) toolbar.getMenu().findItem(R.id.imgmenu_save).setTitle(Language.text(this, l10n_chat_save_photos));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/VoiceRecordDialog.java` | `{"line": 77, "call": ".setTitle(Language.msg(l10n_chat_voice_note))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/VoiceRecordDialog.java` | `{"line": 79, "call": ".setPositiveButton(Language.msg(l10n_wallet_send), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/VoiceRecordDialog.java` | `{"line": 80, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 130, "call": ".setTitle(displayName(t))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 139, "call": ".setTitle(displayName(t))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 253, "call": ".setTitle(Language.msg(l10n_chat_my_address))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 255, "call": ".setPositiveButton(Language.msg(l10n_chat_done), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 270, "call": ".setTitle(Language.msg(l10n_chat_new_chat))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 272, "call": ".setPositiveButton(Language.msg(l10n_chat_start_chat), null)   // set below so bad input doesn't dismiss"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 273, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 341, "call": ".setTitle(Language.msg(l10n_chat_contact_name))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 343, "call": ".setPositiveButton(Language.msg(l10n_common_save), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 347, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 360, "call": ".setTitle(Language.msg(l10n_chat_delete_chat))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 362, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_chat), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 366, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 373, "call": ".setTitle(Language.msg(l10n_chat_delete_contact))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 375, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_contact), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 379, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 386, "call": ".setTitle(Language.msg(l10n_chat_delete_chat))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 388, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_chat), (d, w) -> new Thread(() -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 393, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 400, "call": ".setTitle(Language.msg(l10n_chat_delete_contact))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 402, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_contact), (d, w) -> new Thread(() -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 407, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 442, "call": ".setTitle(Language.msg(l10n_chat_choose_contact))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatView.java` | `{"line": 444, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFailureDialog.java` | `{"line": 29, "call": "AlertDialog dialog = builder.setTitle(Language.msg(l10n_common_error))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatFailureDialog.java` | `{"line": 31, "call": ".setPositiveButton(Language.msg(l10n_language_close), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 152, "call": "if (menu.findItem(R.id.chatmenu_rename) != null) menu.findItem(R.id.chatmenu_rename).setTitle(Language.text(this, l10n_chat_rename_contact));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 153, "call": "if (menu.findItem(R.id.chatmenu_address) != null) menu.findItem(R.id.chatmenu_address).setTitle(Language.text(this, l10n_chat_view_address));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 154, "call": "if (menu.findItem(R.id.chatmenu_clear) != null) menu.findItem(R.id.chatmenu_clear).setTitle(Language.text(this, l10n_chat_delete_chat));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 155, "call": "if (menu.findItem(R.id.chatmenu_delete) != null) menu.findItem(R.id.chatmenu_delete).setTitle(Language.text(this, l10n_chat_delete_contact));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 161, "call": "getSupportActionBar().setTitle(title);"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 180, "call": ".setTitle(Language.msg(l10n_chat_contact_name))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 182, "call": ".setPositiveButton(Language.msg(l10n_common_save), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 186, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 204, "call": "dialog().setTitle(Language.msg(l10n_chat_contact_address)).setView(v)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 205, "call": ".setPositiveButton(Language.msg(l10n_chat_done), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 210, "call": ".setTitle(Language.msg(l10n_chat_delete_chat))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 212, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_chat), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 216, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 222, "call": ".setTitle(Language.msg(l10n_chat_delete_contact))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 224, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_contact), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 228, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 293, "call": ".setTitle(Language.msg(l10n_chat_send_payment))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 295, "call": ".setPositiveButton(Language.msg(l10n_wallet_send), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 304, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 728, "call": ".setTitle(Language.msg(l10n_chat_txid_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 730, "call": ".setPositiveButton(Language.msg(l10n_wallet_copy), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/chat/ChatThreadActivity.java` | `{"line": 735, "call": ".setNegativeButton(Language.msg(l10n_language_close), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/LauncherActivity.java` | `{"line": 53, "call": "tb.setTitle(\"\");"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 45, "call": ".setTitle(Language.msg(l10n_startup_starting))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 87, "call": "message = new LanguageDialog(this).setTitle(Language.msg(l10n_startup_error))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 90, "call": ".setPositiveButton(Language.msg(l10n_startup_open), (d, w) -> { finished = true; openMain(); })"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 91, "call": ".setNegativeButton(Language.msg(l10n_language_close), (d, w) -> finish())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 95, "call": "message = new LanguageDialog(this).setTitle(Language.msg(l10n_startup_slow))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 97, "call": ".setPositiveButton(Language.msg(l10n_startup_keep_waiting), (d, w) -> { message = null; waitForMinimaToStartUp(); })"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 98, "call": ".setNegativeButton(Language.msg(l10n_language_close), (d, w) -> finish())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 124, "call": "message = new LanguageDialog(this).setTitle(Language.msg(l10n_seed_sync_start_failed))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 126, "call": ".setPositiveButton(Language.msg(l10n_startup_retry), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/StartServiceActivity.java` | `{"line": 130, "call": ".setNegativeButton(Language.msg(l10n_language_close), (d, w) -> finish())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 46, "call": "tb.setTitle(Language.text(this, l10n_menu_app_lock));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 141, "call": ".setTitle(Language.msg(l10n_lock_auto_lock))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 147, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 160, "call": "getSupportActionBar().setTitle(Language.text(this, l10n_menu_app_lock));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 180, "call": ".setTitle(title)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 182, "call": ".setPositiveButton(positive, null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 183, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), (dd, w) -> render())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 213, "call": ".setTitle(Language.msg(l10n_lock_set_pin))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 215, "call": ".setPositiveButton(Language.msg(l10n_common_save), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/LockSettingsActivity.java` | `{"line": 216, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), (dd, w) -> onCancel.run())"}` |
| `app/src/main/java/com/eurobuddha/minimacore/lock/BiometricUnlock.java` | `{"line": 233, "call": ".setTitle(zTitle)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 99, "call": "getSupportActionBar().setTitle(title());"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 154, "call": "getSupportActionBar().setTitle(title());"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 160, "call": "if (menu.findItem(R.id.maxmenu_clear) != null) menu.findItem(R.id.maxmenu_clear).setTitle(Language.text(this, l10n_chat_delete_chat));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 161, "call": "if (menu.findItem(R.id.maxmenu_delete) != null) menu.findItem(R.id.maxmenu_delete).setTitle(Language.text(this, l10n_chat_delete_contact));"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 176, "call": ".setTitle(Language.msg(l10n_chat_delete_chat))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 178, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_chat), (d, w) -> new Thread(() -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 182, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 188, "call": ".setTitle(Language.msg(l10n_chat_delete_contact))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 190, "call": ".setPositiveButton(Language.msg(l10n_chat_delete_contact), (d, w) -> new Thread(() -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/maxchat/MaxThreadActivity.java` | `{"line": 194, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/utils/logger.java` | `{"line": 26, "call": ".setTitle(zTitle)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/utils/logger.java` | `{"line": 29, "call": ".setPositiveButton(Language.msg(com.eurobuddha.minimacore.i18n.R.string.l10n_common_ok), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/ChatBackupActivity.java` | `{"line": 187, "call": ".setTitle(Language.msg(l10n_chatbak_confirm_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/ChatBackupActivity.java` | `{"line": 189, "call": ".setPositiveButton(Language.msg(l10n_wallet_restore), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/ChatBackupActivity.java` | `{"line": 196, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 263, "call": ".setTitle(title)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 266, "call": ".setPositiveButton(confirmText, (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 275, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 293, "call": ".setTitle(title)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 296, "call": ".setPositiveButton(confirmText, (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 307, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 315, "call": ".setTitle(title)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 318, "call": ".setPositiveButton(confirmText, (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 326, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 339, "call": ".setTitle(Language.msg(l10n_vault_wipe_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 342, "call": ".setPositiveButton(Language.msg(l10n_vault_wipe_action), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 351, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 374, "call": ".setTitle(Language.msg(l10n_vault_rebuild_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 377, "call": ".setPositiveButton(Language.msg(l10n_vault_rebuild), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/VaultActivity.java` | `{"line": 400, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/BackupCreateActivity.java` | `{"line": 203, "call": ".setTitle(Language.msg(l10n_backup_export_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/BackupCreateActivity.java` | `{"line": 205, "call": ".setPositiveButton(Language.msg(l10n_backup_choose_location), (d, w) -> exportLauncher.launch(run.filename()))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/BackupCreateActivity.java` | `{"line": 206, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 159, "call": ".setTitle(Language.msg(l10n_backup_delete_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 161, "call": ".setPositiveButton(Language.msg(l10n_backup_delete), (d, w) -> deleteNow(file.getName()))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 162, "call": ".setNegativeButton(Language.msg(l10n_backup_keep), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 249, "call": ".setTitle(Language.msg(l10n_backup_replace_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 252, "call": ".setPositiveButton(Language.msg(l10n_wallet_restore), (d, w) -> {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/backup/RestoreFileActivity.java` | `{"line": 259, "call": ".setNegativeButton(Language.msg(l10n_common_cancel), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 127, "call": "new LanguageDialog(getActivity()).setTitle(Language.msg(l10n_apps_unverified_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 129, "call": ".setPositiveButton(Language.msg(android.R.string.ok), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 134, "call": "new LanguageDialog(getActivity()).setTitle(Language.msg(l10n_apps_unverified_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 136, "call": ".setPositiveButton(Language.msg(l10n_apps_enable_anyway), new DialogInterface.OnClickListener() {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 141, "call": ".setNegativeButton(Language.msg(android.R.string.cancel), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 158, "call": "new LanguageDialog(getActivity()).setTitle(Language.msg(l10n_apps_unverified_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 160, "call": ".setPositiveButton(Language.msg(android.R.string.ok), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 165, "call": "new LanguageDialog(getActivity()).setTitle(Language.msg(l10n_apps_unverified_title))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 167, "call": ".setPositiveButton(Language.msg(l10n_apps_enable_anyway), new DialogInterface.OnClickListener() {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 172, "call": ".setNegativeButton(Language.msg(android.R.string.cancel), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 196, "call": ".setTitle(pname)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 199, "call": ".setPositiveButton(Language.msg(android.R.string.ok), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/apps/AppsView.java` | `{"line": 203, "call": ".setNegativeButton(Language.msg(l10n_backup_delete), new DialogInterface.OnClickListener() {"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/send/SendView.java` | `{"line": 152, "call": ".setTitle(Language.msg(l10n_send_confirm))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/send/SendView.java` | `{"line": 155, "call": ".setPositiveButton(Language.msg(android.R.string.yes), new DialogInterface.OnClickListener(){"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/send/SendView.java` | `{"line": 159, "call": ".setNegativeButton(Language.msg(android.R.string.no), null).show();"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/balance/coins/CoinDetailDialog.java` | `{"line": 75, "call": ".setTitle(zSendable ? Language.msg(l10n_core_wallet_coin_sendable) : Language.msg(l10n_core_wallet_coin_locked))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/balance/coins/CoinDetailDialog.java` | `{"line": 77, "call": ".setPositiveButton(Language.msg(l10n_language_close), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/balance/coins/CoinsDialog.java` | `{"line": 118, "call": ".setTitle(zTokenName == null ? Language.msg(l10n_wallet_coins) : Language.msg(l10n_core_wallet_token_coins, zTokenName))"}` |
| `app/src/main/java/com/eurobuddha/minimacore/main/views/balance/coins/CoinsDialog.java` | `{"line": 121, "call": ".setPositiveButton(Language.msg(l10n_language_close), null)"}` |
| `app/src/main/java/com/eurobuddha/minimacore/launcher/restore/SeedSyncServiceActivity.java` | `{"line": 50, "call": ".setTitle(Language.msg(l10n_seed_sync_title))"}` |
| `integrated/history/src/main/java/com/eurobuddha/history/HistoryScreen.java` | `{"line": 337, "call": "new LanguageDialog(this).setTitle(Language.msg(l10n_history_transaction)).setView(sv).setPositiveButton(Language.msg(l10n_language_close), null).show();"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 135, "call": ".setTitle(title)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 137, "call": ".setPositiveButton(\"Close\", null)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 168, "call": ".setTitle(title)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 170, "call": ".setPositiveButton(\"Close\", null)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 549, "call": ".setTitle(\"Approve this immutable event\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 551, "call": ".setNegativeButton(\"Cancel\", null)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 552, "call": ".setPositiveButton(\"Publish / reconcile\") { _, _ ->"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 636, "call": ".setTitle(\"Verify through the trusted owner channel\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 640, "call": ".setNegativeButton(\"Cancel\", null)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 641, "call": ".setPositiveButton(\"Pin verified issuer\") { _, _ ->"}` |

## Native Trackium card/action/form/dialog call sites

| Source | Details |
|---|---|
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 178, "call": "if (sessionStarted && TrackiumCoreAccess.authorized()) detail(title, r)"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 281, "call": "button(parent, \"Refresh Gateway\", secureReady()) { c.refresh() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 287, "call": "val card = box(content, \"Your cargo workspace\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 313, "call": "val safety = box(content, \"Built for accountable delivery\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 321, "call": "button(safety, \"Observe Core wallet & synchronization\", c.core != null) {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 332, "call": "button(safety, \"Inspect reported wallet balance\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 333, "call": "dialog(\"Core-reported balance (not reserved funds)\", w.get(\"balance\").toString())"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 336, "call": "button(safety, \"Observe embedded node\") { c.connectCore() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 337, "call": "button(safety, \"Return to node\") { finish() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 345, "call": "box(content, \"No shipments loaded\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 353, "call": "val card = box(content, s.optString(\"name\", \"Shipment\"))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 358, "call": "button(card, \"Route & checkpoints\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 368, "call": "box(content, \"No devices loaded\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 372, "call": "val card = box(content, d.optString(\"label\", \"Device\"))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 377, "call": "button(card, \"Coordinates, sensors & status\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 396, "call": "val controls = box(content, \"Native Mainnet Publisher\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 415, "call": "val waiting = box(content, \"Awaiting Gateway owner approval\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 437, "call": "val card = box(content, proof.getJSONObject(\"commitment\").getString(\"event_type\"))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 463, "call": "button(card, \"Verify with independent Gateway reader\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 466, "call": "button(card, \"Export proof bundle\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 477, "call": "button(card, \"Review approved Native Core job\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 483, "call": "val recovery = box(content, \"Device recovery checkpoints\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 489, "call": "button(recovery, \"Review saved job ${id.take(8)}\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 537, "call": "button(parent, \"Check Core wallet & Mainnet readiness\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 538, "call": "c.readiness(packet) { r -> detail(\"Core readiness observation\", r) }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 547, "call": "val typed = input(parent, \"Type PUBLISH MAINNET ${job.event}\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 554, "call": "dialog(\"Approval required\", \"Explicit Mainnet consent is required.\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 559, "call": "dialog(\"Job unavailable\", NativePublisher.safeError(e))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 566, "call": "val core = box(content, \"embedded Core · your built-in wallet\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 574, "call": "button(core, \"Observe embedded node\") { c.connectCore() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 575, "call": "button(core, \"Return to node settings\") { finish() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 577, "call": "val gateway = box(content, \"Gateway · limited native session\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 579, "call": "input(gateway, \"HTTPS Gateway origin\", draftGateway ?: config.optString(\"gateway\"))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 581, "call": "input(gateway, \"Tenant UUID (public)\", draftTenant ?: config.optString(\"tenant\"))"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 584, "call": "button(gateway, \"Save connection\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 601, "call": "button(gateway, \"Create owner approval challenge\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 616, "call": "button(gateway, \"Complete owner pairing\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 622, "call": "button(gateway, \"Refresh authenticated data\", secureReady()) { c.refresh() }"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 623, "call": "button(gateway, \"Disconnect / revoke session\", secureReady()) {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 634, "call": "button(gateway, \"Verify & pin Gateway issuer\") {"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 649, "call": "val safety = box(content, \"Wallet publication unavailable · prototype #1\")"}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 672, "call": "input("}` |
| `integrated/trackium/src/main/java/tech/trackium/cargo/MainActivity.kt` | `{"line": 678, "call": "button(safety, \"Set explicit local cap\") {"}` |
