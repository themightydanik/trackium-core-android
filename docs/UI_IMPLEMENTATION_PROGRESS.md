# UI implementation progress — Trackium Core Update 2

## Approved direction and sequencing

Trackium Core; owner gold interwoven PNG mark; navy/mint/gold, System Light/Dark, balanced density; root Overview / Trackium / Wallet / More. Routed T/Cargo C/Relay are rejected and are not installed. Original Minima/Eurobuddha attribution and protocol/currency symbols remain.

**Update 1 / Android PR #4 was merged** into main (`abf01be`). Update 2 combines the approved Android shell/Node and unified-navigation stages in one branch/PR. No Web/backend changes. Do not start Update 3 before owner review/merge of Update 2. No automatic merges.

| Stage | Status |
|---|---|
| Repository/instruction review | Current canonical mains used; Android PR #1 merged. Audit PR #2 was merged into the old phone-readiness branch, not main; its proposal is consumed by pinned source reference without reapplying its large audit assets. Web PR #14 is merged |
| 1 — canonical tokens | v1.0.0 JSON, Android shared library/XML/Kotlin, CSS custom properties, typography/spacing/radii/elevation/icon/motion/component states ready |
| 1 — owner brand | Exact transparent mark crop from existing owner wordmark; adaptive/themed launcher, notification, splash/onboarding resources and web icon derivatives ready; no AI/redrawn logo |
| 1 — visible changes | Android launcher identity and current web brand mark/favicon/app metadata only. No wallet/currency icon replacement. Notification and onboarding assets are prepared, not wired through privileged service/wallet code |
| 2 — chrome / Node | Implemented: gold startup/onboarding/header, DS1 host aliases, wrapping toolbar, Overview Node/Gateway evidence, More/tools and theme picker |
| 3 — unified navigation | Implemented: Overview / Trackium / Wallet / More; one shared root bar, native Trackium section selector, allowlisted state/Back/Activity recreation |
| 4 — native Trackium | Update 3 pending. Only shell navigation, DS1 colour roles and platform Light/Dark context are connected now; domain screens/dialog redesign is deferred |
| 5 — remaining modules | NOT STARTED; shared token dependency is available to History and native Trackium, no new independent palette |
| 6 — web redesign | NOT STARTED; existing UI/auth behavior preserved. New tc tokens support System/explicit light/dark but legacy component colour aliases are migrated only with complete theme coverage |
| 7 — acceptance | Limited foundation checks now; full physical-device/TalkBack/200%/lifecycle coverage remains future work |

## Source of truth and cross-repository order

Canonical `design-system/tokens.json` and `design-system/brand/` live in trackium-core-android. Web consumes a version/hash-pinned snapshot and generated assets, not an Android project or a backend copy. Review/merge Android foundation first, then the compatible Web snapshot; no server/API deployment is required. Both PRs remain independently testable and do not fetch the other repository during builds. Changes after review must update snapshot hashes explicitly.

Source audit reference: `2cf2cafc5e46ea44d0febfe99af69cd96ef1f09a`; prior proposals are not shipped styles. Owner PNG provenance: Trackium `ee384431f6e1d8c21240ee6cbcc1f9473b73555e`, `assets/img/logo.png` (transparent wordmark) and `icon.png` (opaque app icon). Hashes/crop box/output manifests are retained. Existing PNGs already provide the approved gold mark, so no replacement image is invented or requested.

## Theme and component boundary

The Android library resolves light/night roles from Context configuration; CSS supports prefers-color-scheme and explicit `data-tc-theme=light|dark`. Foundation provides these mappings in both schemes; it does **not** claim the old host/native/History or web UI has already been fully themed. Update 2 adds persisted System/Light/Dark in More → Appearance (`trackium_appearance`, separate from wallet settings), host semantic aliases and native Activity context/platform themes. Full internal component redesign remains Update 3 and later. This avoids a partly recoloured UI with broken contrast. Gold remains logo/brand accent; primary action role stays mint.

The approved light accent #007E6D measured 4.35:1 on elevated light surface; it is adjusted to #007667 to meet 4.5:1. Dark functional outline is #6D87A4. This is a contrast correction within the approved palette, not a new visual direction. Opaque semantic pairs are tested on canvas/surface/elevated; decorative borders/disabled states are not falsely treated as proof of entire-UI accessibility.

## Verification and limits

Targeted tests cover light/night Android resources and disabled selectors; Python checks exact source crop, gradient-preserving pixels, safe-circle containment, monochrome alpha equality, generated mappings/asset hashes and semantic contrast. Existing 327 JVM/Robolectric +33 Python guards and Web tests are preserved. Local verification: existing327 +4 new Android mapping/state tests pass (331 total); existing33 Python guards are retained, six new foundation tests pass; all Gradle assembly/lint tasks pass. Web existing16 +2 new browser tests pass (18 total). Final CI and any unrun scenarios are attached to the PR; these results do not imply physical-device acceptance.

Manual review: compare original PNG and cropped mark, adaptive masks and 24/32/48px readability (`docs/design-system-1/brand-comparison.png`), real Android launcher/themed icon/notifications, light/dark resource contexts, web favicon and brand sizing. No actual phone or themed Android13+ launcher acceptance is claimed. Raster derivatives are used because accurate vector reconstruction is not available; proportions/intersections/alpha/gradient are retained instead of approximated paths.

No Minima JAR, consensus, signing/seed/key-use, network defaults, privileged permissions, direct transport, Gateway auth/pairing/policy, database migration or Mainnet publication changes. Stage-2 licence/NOTICE and 16-KiB/native compatibility blockers remain open. No public Release. The Update 1 debug version was 0.2.1 (code3); Update 2 is 0.2.2 (code4), not a production 1.0 release; DS version1.0.0 refers only to the foundation.

## Update 2 — implementation and acceptance

- Startup/onboarding and root header use the original gold PNG derivatives. Attribution is available from More → About and original licence/NOTICE files remain untouched. Minima currency/protocol glyphs remain.
- Overview reads only local `status`, on the existing bounded read executor, coalesced and gated to the foreground/unlocked Overview. It clears missing/failed observations; peer count and block height are evidence, never a synchronization claim. It reads only a non-secret “native session stored / absent / storage unavailable” summary; stored credentials do not imply connectivity or unexpired authorization. No logistics records/fixtures are fabricated.
- More retains Node details, Apps, History, conditional Chat, Terminal, Logs, Node settings, Backup/Restore and App Lock. Original language, battery, screenshot, lock-now, shutdown and reset actions remain in the overflow with existing confirmations. Wallet reuses the complete existing BalanceView and financial Activities.
- The existing pager is an internal legacy-screen container with swipe disabled; its old tabs/footer are hidden. The root bar is the same presentation factory in host and native Trackium. Trackium has a top section selector, no competing bottom bar. Selecting another root destination returns to the existing host using an allowlisted destination; Back from nested Trackium returns to Dashboard, then to the previous host section.
- Saved shell state contains only root/tool enums and scroll offset. No credential/config/domain objects are copied into SavedState. The previous root/tool is restored after host recreation/process restart. Existing AppLock gates, native authorization callbacks and foreground service lifetime remain authoritative.
- Separate security commits cover an observed seed-window issue: creation and both onboarding restore screens now always require FLAG_SECURE and disable automatic seed-view state. Neither seed generation nor restore/key-use procedure changed. No protected window is captured or FLAG_SECURE bypassed.

Local validation: **346 JVM/Robolectric tests** (345 full-suite pass plus the additional passing tool-title/Back test; final CI repeats the full suite), **39 Python tests**, full assembly, host/native/design-system lint, source/engine/Keets dependency checks, debug APK signature and release-blocker policy pass. New tests cover root selection, native launch target, More recreation/Back, sanitized state, unavailable observations, theme persistence/context, 48dp+ root controls and actual seed-window secure/state flags. The clean committed-source packaging check passes; debug identity is development-only. CI/run/artifact links are attached to the PR.

Lint has no errors; host retains 7053 warnings (including dependency analysis, legacy hardcoded/localization/style/API and accessibility warnings), native module 3 and design-system 2. Warnings are not suppressed. New shell strings include ru/uk/zh translations; native domain-screen localization remains later work. Automated semantic checks do not substitute for a TalkBack user session.

Remaining acceptance: physical phone, Android 29+/35 OEM/edge-to-edge behavior, actual TalkBack exploration, real owner-approved Gateway/expiry flow and financial UI interaction without transactions require controlled manual acceptance. No real wallet, physical AOVX, live Gateway or Mainnet publication is tested. Existing Keets/provenance/licensing and 16-KiB native-library blockers remain. Update 3 will redesign native Trackium content, ordinary detail/JSON interactions, loading/error/empty states, list/filter retention and complete per-screen accessibility/theme acceptance; no Update 3 work begins here.

### Update 2 visual evidence and emulator boundary

Controlled Android API 28 emulator, 160 dpi; no owner session, real wallet credentials or live Gateway configured. Compact 320×640, standard 360×800 and large 800×1280 viewports were checked; these are emulator configurations, not three physical devices. The direct private host Activity was opened under this debug app’s own UID to inspect unknown/no-node shell state without wallet provisioning. This does not test successful wallet onboarding or chain synchronization. Android Wi-Fi/mobile data were disabled. Temporary system animation settings were disabled for deterministic captures.

Manual theme selection through More → Appearance was exercised for Dark and Light, including persisted preference across process restart. The 200% font capture demonstrates wrapping/scrolling instead of ellipsis; compact root labels wrap at this scale and should receive owner/TalkBack review. Native Trackium opened in the same APK; its window had FLAG_SECURE, and Back returned to the host. No native/seed protected screenshot was attempted. Captures use runtime commit `e54de0b`; subsequent changes add tool context titles/Back coverage and live locale relabelling without changing these English root-screen layouts.

- [Before: inherited Node, compact 130%](update2/before-node-compact-font130.png) — preserved audit evidence at `2cf2caf`, not a pixel-matched screenshot of the new APK.
- [After: Overview Light](update2/after-overview-light-compact.png) / [Dark](update2/after-overview-dark-compact.png).
- [After: More Dark](update2/after-more-dark-compact.png) / [Overview Dark at 200%](update2/after-overview-dark-font200.png).
- [After: standard viewport](update2/after-overview-light-standard.png) / [large viewport](update2/after-overview-light-large.png).
- [After: onboarding Light](update2/after-onboarding-light-compact.png).

Actual TalkBack gestures, landscape/IME on physical OEM devices, biometric lock/relock, reboot/foreground node recovery and all privileged wallet flows still require controlled phone acceptance. Existing automated lock/permission/wallet/node regression guards remain mandatory and passing; these emulator images are not financial, Mainnet or release acceptance. See `docs/PHONE_ACCEPTANCE.md` for the controlled-device protocol.
