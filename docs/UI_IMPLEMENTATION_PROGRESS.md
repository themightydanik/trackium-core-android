# UI implementation progress — Design System 1.0

## Approved direction and sequencing

Trackium Core; owner gold interwoven PNG mark; navy/mint/gold, System Light/Dark, balanced density; future root Overview / Trackium / Wallet / More. Routed T/Cargo C/Relay are rejected and are not installed. Original Minima/Eurobuddha attribution and protocol/currency symbols remain.

Only **stage 1 (tokens and brand resources)** is implemented in this PR. Do not begin Android chrome/Node, navigation, native content, remaining modules or the web redesign until the owner has reviewed/merged the foundation PRs. No automatic merges.

| Stage | Status |
|---|---|
| Repository/instruction review | Current canonical mains used; Android PR #1 merged. Audit PR #2 was merged into the old phone-readiness branch, not main; its proposal is consumed by pinned source reference without reapplying its large audit assets. Web PR #14 is merged |
| 1 — canonical tokens | v1.0.0 JSON, Android shared library/XML/Kotlin, CSS custom properties, typography/spacing/radii/elevation/icon/motion/component states ready |
| 1 — owner brand | Exact transparent mark crop from existing owner wordmark; adaptive/themed launcher, notification, splash/onboarding resources and web icon derivatives ready; no AI/redrawn logo |
| 1 — visible changes | Android launcher identity and current web brand mark/favicon/app metadata only. No wallet/currency icon replacement. Notification and onboarding assets are prepared, not wired through privileged service/wallet code |
| 2 — chrome / Node | NOT STARTED; toolbar clipping and dashboard redesign remain next |
| 3 — unified navigation | NOT STARTED; no root or Back/lifecycle changes in this PR |
| 4 — native Trackium | NOT STARTED; ordinary JSON dialogs and hardcoded old theme remain for the approved later stage |
| 5 — remaining modules | NOT STARTED; shared token dependency is available to History and native Trackium, no new independent palette |
| 6 — web redesign | NOT STARTED; existing UI/auth behavior preserved. New tc tokens support System/explicit light/dark but legacy component colour aliases are migrated only with complete theme coverage |
| 7 — acceptance | Limited foundation checks now; full physical-device/TalkBack/200%/lifecycle coverage remains future work |

## Source of truth and cross-repository order

Canonical `design-system/tokens.json` and `design-system/brand/` live in trackium-core-android. Web consumes a version/hash-pinned snapshot and generated assets, not an Android project or a backend copy. Review/merge Android foundation first, then the compatible Web snapshot; no server/API deployment is required. Both PRs remain independently testable and do not fetch the other repository during builds. Changes after review must update snapshot hashes explicitly.

Source audit reference: `2cf2cafc5e46ea44d0febfe99af69cd96ef1f09a`; prior proposals are not shipped styles. Owner PNG provenance: Trackium `ee384431f6e1d8c21240ee6cbcc1f9473b73555e`, `assets/img/logo.png` (transparent wordmark) and `icon.png` (opaque app icon). Hashes/crop box/output manifests are retained. Existing PNGs already provide the approved gold mark, so no replacement image is invented or requested.

## Theme and component boundary

The Android library resolves light/night roles from Context configuration; CSS supports prefers-color-scheme and explicit `data-tc-theme=light|dark`. Foundation provides these mappings in both schemes; it does **not** claim the old host/native/History or web UI has already been fully themed. Settings switches and migration of existing hardcoded colours are stages 2/4/5/6. This avoids a partly recoloured UI with broken contrast. Gold remains logo/brand accent; primary action role stays mint.

The approved light accent #007E6D measured 4.35:1 on elevated light surface; it is adjusted to #007667 to meet 4.5:1. Dark functional outline is #6D87A4. This is a contrast correction within the approved palette, not a new visual direction. Opaque semantic pairs are tested on canvas/surface/elevated; decorative borders/disabled states are not falsely treated as proof of entire-UI accessibility.

## Verification and limits

Targeted tests cover light/night Android resources and disabled selectors; Python checks exact source crop, gradient-preserving pixels, safe-circle containment, monochrome alpha equality, generated mappings/asset hashes and semantic contrast. Existing 327 JVM/Robolectric +33 Python guards and Web tests are preserved. Local verification: existing327 +4 new Android mapping/state tests pass (331 total); existing33 Python guards are retained, six new foundation tests pass; all Gradle assembly/lint tasks pass. Web existing16 +2 new browser tests pass (18 total). Final CI and any unrun scenarios are attached to the PR; these results do not imply physical-device acceptance.

Manual review: compare original PNG and cropped mark, adaptive masks and 24/32/48px readability (`docs/design-system-1/brand-comparison.png`), real Android launcher/themed icon/notifications, light/dark resource contexts, web favicon and brand sizing. No actual phone or themed Android13+ launcher acceptance is claimed. Raster derivatives are used because accurate vector reconstruction is not available; proportions/intersections/alpha/gradient are retained instead of approximated paths.

No Minima JAR, consensus, signing/seed/key-use, network defaults, privileged permissions, direct transport, Gateway auth/pairing/policy, database migration or Mainnet publication changes. Stage-2 licence/NOTICE and 16-KiB/native compatibility blockers remain open. No public Release. The debug version is 0.2.1 (code3), not a production 1.0 release; DS version1.0.0 refers only to the foundation.
