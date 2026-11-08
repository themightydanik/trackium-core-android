# Codex project instructions — Trackium Core Android

## Canonical repository and scope

Canonical Android repository: **https://github.com/themightydanik/trackium-core-android**.
This is the only current repository for the Trackium Core Android application.
These instructions apply throughout this repository; read any applicable nested
instructions before editing. Preserve existing instructions when updating them;
explicit user task/session instructions take precedence.

Develop here:

- The custom Minima Core Android host derived from Eurobuddha/BlackBear.
- The integrated native Trackium application and its Android UI/UX/navigation.
- Android branding, adaptive/themed launcher icons, themes and resources.
- Native Gateway client integration, Android security, tests and APK builds.

The Android project belongs at this repository's root. The host is in `app/`,
the native Trackium client in `integrated/trackium/`, and shared/integrated
modules remain part of this Android project.

**Do not create, maintain or move the current Android project into
`themightydanik/trackium`. Do not use its historical `apps/android` as the current
application. Never restore the project to `prototypes/trackium-core-android`.**
Historical copies may be read for provenance or compatibility analysis; their
presence does not change canonical ownership.

## Backend / web boundary

Canonical backend and MiniDapp repository:
**https://github.com/themightydanik/trackium**.
Its [project instructions](https://github.com/themightydanik/trackium/blob/main/AGENTS.md)
define that repository's scope.

Railway Gateway, PostgreSQL, REST API, devices/AOVX, shipments, telemetry, events,
Proof records/server policies, current web MiniDapp, Gateway authentication and
owner approval belong there. Do not copy the backend into this Android project
or duplicate server-owned business rules, authorization or publication policy in
the client. Native UI/client orchestration may consume the existing API; the
server remains authoritative for its domain and access checks.

The current native Android integration and the web MiniDapp are separate
implementations. Historical `legacy.html`, `local-core.html` and `apps/android`
in the backend repository are not the current Android Core architecture.
Do not automatically delete historical sources: first identify active consumers,
imports, serving/packaging paths, tests and compatibility dependencies.

## Cross-repository tasks

1. Identify the actual files, API contracts and consumers affected before editing.
2. Inspect the other repository when necessary, but leave it unchanged if reading
   its contracts or documentation is sufficient.
3. Avoid duplicated business logic and preserve clear client/server ownership.
4. For each repository that needs changes, use its own branch and pull request.
5. State cross-repository dependencies, compatibility assumptions and rollout
   order explicitly in the PR descriptions. Coordinate additive API changes
   before consumers when required; do not assume a fixed order for every task.
6. Never merge PRs automatically.

## Safety and compatibility

- Preserve existing Minima, wallet, one-time key-use/signing, node lifecycle and
  compatibility guarantees. Do not replace the reviewed engine or weaken
  checksum/security checks merely to make a build pass.
- Preserve authentication, Gateway owner-approved pairing, Proof publication
  guards and tenant/access isolation. Client presentation must not override
  server policy or imply privileges it does not have.
- Do not enable Mainnet publication without separate, explicit authorization.
  A UI change, successful build, test deployment or previous test approval is
  not authorization for real transactions/publications.
- Keep synthetic, simulated, unverified, stale and physically observed telemetry
  distinguishable. Never present fixtures as real device/AOVX data or claim
  chain inclusion proves physical sensor authenticity.
- Do not commit or expose seeds, private keys, production credentials, keystores
  or wallet data in logs, APK inputs or public artifacts. Use disposable test
  data for authorized tests; do not migrate working wallets implicitly.
- Preserve original licences, NOTICE, source pins, checksum manifests and
  documented release blockers. Green CI is not distribution clearance or
  physical-device/Mainnet acceptance.

## Validation and reporting

Use the existing project scripts and instructions: `README.md`,
`docs/CI_DEBUG_APK.md`, `docs/PHONE_ACCEPTANCE.md`, `sources.lock.json` and
`scripts/ci-debug.sh` as appropriate to the change. Run relevant checks; do not
invent tests for an instruction-only edit or disable lint/TLS/checksum checks.
Report exactly what ran and distinguish source review, mocks/emulators, physical
phones and live services. Keep runtime changes and historical-source removal
out of tasks that only ask for documentation or project instructions.
