# Evidence register and reproduction limits

## Source snapshots and integrity

Android source: `190afb8b22babeb19be9e1ba07af7e03c2c8074a`, v0.2.0. Web source: Trackium main `196b3dd5848e35844acfeffccf8f984030d7bee3`. Read-only inventory covers app/src/main, block, pandaShell, integrated/history and integrated/trackium. Runtime UI/engine/auth source sets are unchanged by the audit. The temporary Android screenshot instrumentation was used only in a disposable test APK; its reproducibility appendix is a `.java.txt` under tools, not a compiled source set. It is removed from app/src/androidTest before the PR.

Original PNGs are unretouched. `screenshots-sha256.json` records their hashes. Synthetic data in screenshots are the product's explicitly selected STATIC DEMO, not a real Gateway session. Proposals are under ../proposals, separate from this evidence directory and marked NOT IMPLEMENTED.

## Browser captures — visually reviewed

Real headless Chromium / Linux, reduced-motion, local static origin only. External requests blocked. No MDS wallet, no production Gateway, no owner credentials, no tenant writes. Shared desktop tools were not exposed (environment remote_desktop enabled/unknown); headless Chromium was available and actually used, not reported as unavailable.

| Viewport CSS px | Routes | Evidence |
|---|---|---|
| 1440×1000 | dashboard, shipments, devices, map, events, proofs, settings | web-desktop-*-preview.png |
| 320×640 | same seven | web-compact-*-preview.png |
| 390×844 | same seven | web-phone-*-preview.png |
| 768×1024 | same seven | web-tablet-*-preview.png |

28 route/viewport combinations, plus desktop/phone unpaired, phone shipment dialog and root-font200 stress screenshot. `web-capture-metadata.json` records viewport, page title, Preview label, errors and measured document overflow. All 28 have horizontalOverflow=0 and no captured pageerror. Non-zero-bounds interactive elements below44px are **candidates**, not a complete visible-control audit or automatic WCAG verdict (inline exceptions/offscreen elements need manual classification).

The root-font200 screenshot changes html font-size only. Existing CSS uses many fixed px values, so this is **not** proof of true browser/OS 200% text scaling. Light mode is not currently offered by the web source; it was not invented for screenshots. Full screenshot files can be very tall, especially Events; no pagination/virtualisation capability is inferred from their existence.

Core/browser regression tests: 16 pass (`tests/minidapp/core.test.cjs`, browser.test.cjs). No authenticated server visual fixture or production backend is represented. Live auth/expired cookie/scope errors have source review only unless explicitly covered by existing tests; tests are not live operational acceptance.

## Android captures — limited, explicit

Separate disposable API28 x86_64 software AVD `trackium-ui-audit-api28`, emulator-5556, no KVM; the pre-existing emulator/other apps were not reset. Fresh empty test wallet generated only in this new fixture to allow the actual host to open offline (`-nop2p -noconnect -host 127.0.0.1 -hashtest 0`, Keets off). No imports, signatures, mining/publication, real funds or Gateway pairing. These are fixture preferences, not production network changes. Screenshots use the existing debug default screenshot policy; no FLAG_SECURE/app-lock bypass.

| Configuration | What is actually verified | Limits |
|---|---|---|
| Standard 390×844/mdpi, light, font100 | First-launch language chooser; runtime Activity/secure-flag records | First screenshot pass included transitional black frames and an unselected language chooser over host views. Those frames are discarded; do not count their filenames as visual coverage of Node/Wallet/etc. First router harness attempt also waited for transient StartActivity and failed; fixed as capture tooling, not a product bug |
| Compact 320×640/mdpi, dark, font130 | Real Node, Wallet/offline, History/empty, Apps, Startup Params, App Lock settings, Terminal screenshots, individually inspected | Toolbar/subtitle clipping observed. Log pixels excluded for privacy. Backup window black/incomplete, not visually verified. Later capture harness stalled around Backup/idle; no full instrumented PASS claimed for this pass |
| Expanded 800×1280/mdpi, light, font100 | Actual Minima starting dialog (`android-expanded-light-starting.png`) | Other expanded host pages did not complete within practical software-emulation runtime. Not visually verified. No claim of tablet-ready layouts |
| Native Trackium Dashboard/Shipments/Devices/Proofs/Settings | All five Activity/tab states reached in the initial runtime probe, with secure=true | `android-standard-protected-window-metadata.json`: forced FLAG_SECURE preserved; no images captured. Source/runtime inspection only, **not** visual verification |

An initial screenshot pass completed its instrumentation (1 test), but modal/transition interference makes it insufficient for visual acceptance. Capture-tool completion is not a statement that product UX passed. The compact images retained here show content, not only system bars; black/transitional images and any raw logs were excluded. No seed creation, private key, populated restore form or wallet backup is pictured.

Send, Receive, Vault, secret-bearing backup/create/restore, PIN/biometric unlock, populated recovery, Chat/media, authenticated native tenant cards and all financial confirmations remain source-only/not run. Initial blank restore was opened but not retained as verified evidence because the first language prompt interfered. The audit still inventories/reviews all these screens; it does not silently omit them or claim they were visually inspected.

## Reproduce read-only browser evidence

Serve a clean checkout of the pinned main Trackium source on loopback. In another shell, use an existing development Playwright install (capture tooling is not an app dependency):

```sh
python3 -m http.server 8765 --bind 127.0.0.1 --directory /path/to/trackium
TRACKIUM_WEB_COMMIT=$(git -C /path/to/trackium rev-parse HEAD) \
NODE_PATH=/path/to/devtools/node_modules \
node docs/ui-audit/tools/capture-web.cjs
python3 docs/ui-audit/tools/inventory.py
```

The helper refuses non-local origins and permits network requests only to that origin. It clicks the actual Preview switch, waits for transient toast expiry and captures current rendered routes. It does not inject fake authenticated sessions or call private APIs.

## Reproduce Android safely

The tools/UiAuditCaptureTest.java.txt appendix describes the **audit-only** harness, not a maintained product test or phone acceptance script. Use only a new disposable emulator with no valuable wallet and an app/test APK signed with the same local debug key. Copy it temporarily into the androidTest package, assembleBlockDebugAndroidTest, explicitly pass `-e auditDisposable true -e auditConfig LABEL`; remove the temporary file afterward. It checks ranchu hardware, exact package and an existing fixture marker before reuse. Never run on a physical phone or user wallet, never import keys, and never disable secure windows to obtain pictures. Select English through the normal locale API in the fixture; display size/theme/font changes apply only to the disposable AVD. Wait for real frame stability and manually verify pixels; Activity names alone are not enough, as the initial failed/interfered runs demonstrate.

For protected screens, use on-device human review without exporting secrets, or a separately approved sanitized UI fixture that is clearly labelled mock data. Do not use View.draw or security-flag removal to present protected captures as normal screenshots. Actual phone/system-inset, API29+Keets, 200% Android font and TalkBack/biometric acceptance remain separate stages.
