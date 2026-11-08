# Trackium Design System 1.0

Canonical owner-approved semantic tokens and original owner brand resources for Android and Web. This module contains no node, wallet, Gateway business logic or credentials.

- `tokens.json`: versioned semantic roles, complete light/dark maps, typography, spacing, radii, elevation, icon sizing, motion and component-state contracts.
- `tools/generate_tokens.py`: deterministic XML/Kotlin/CSS mappings; `--check` rejects drift.
- `brand/source`: byte-identical owner PNG sources with provenance in `brand/manifest.json`.
- `tools/brand_assets.py`: exact alpha-preserving crop, safe adaptive padding, monochrome alpha and raster sizes. No AI drawing, inaccurate SVG tracing or gradient replacement.
- `requirements.txt`: development-only Pillow13, official release hashes locked; never packaged in APK.

Reproduce: install the hash-locked requirements into a local development venv; run the two generation scripts, then `python3 design-system/tools/test_foundation.py`. Generated outputs are tracked; they may not be hand-edited. Runtime library `tech.trackium.designsystem.TrackiumTokens` reads Context resources so Android system/manual UI-mode contexts can select values. Actual theme selection/settings UI is a later stage.

Web receives a checked snapshot, version and token/asset hashes in its own repository. Canonical changes start here; neither build requires a live checkout of the other repository. Domain/business logic is not shared through this presentation package.

Original Minima currency/protocol resources and legal notices are retained. These assets are not a public-release clearance. See docs/UI_IMPLEMENTATION_PROGRESS.md for stage boundaries, manual checks and unresolved blockers.
