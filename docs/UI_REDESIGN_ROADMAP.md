# UI redesign roadmap — small reviewable PRs

**Stage 3 результат — аудит и предложения. Массовое внедрение не авторизовано.** Runtime Android/Gateway/web UI в этом PR не изменяется. Не merge автоматически. Все дальнейшие шаги требуют выбранной владельцем концепции и обычного review; зелёный UI test не разрешает Mainnet publication.

| PR / этап | Scope | Вне scope / gates | Критерий готовности |
|---|---|---|---|
| 0. Owner review | Inventory, findings, 3 icons, tokens, navigation tasks | Не выбирать final icon молча | Выбраны visual direction, theme strategy, IA и 2–3 representative screens; feedback записан |
| 1. Privacy/source-state fixes | Точные route labels, capability unavailable wording, seed/secret screenshot review и tests | Не меняются auth, key derivation, network/signature checks; отдельный security review для sensitive flags | Матрица screenshot opt-in/secure states, честный Return destination, no automatic wallet commands |
| 2. Design token adapters | Versioned semantic JSON, validated Android XML/Kotlin/CSS mappings, existing-colour migration | Никакого включения финансовых функций и массового layout rewrite | Light/dark functional contrast, generated diff review; existing tests/lint unchanged |
| 3. Android chrome + approved icon | Launcher/toolbar/system bars/notifications/About с approved original icon и notices | Не заменять Minima currency glyph или protocol identifiers; не трогать signer/package identity | Masks/themed icon24/48/108, accessibility labels, insets API28/29+/target35, original provenance сохранена |
| 4. Node/onboarding | Node state hierarchy, startup/slow/error, language/brand и guided safe onboarding | Не упрощать seed/mode/key-use warnings; не фотографировать seed | Compact/standard/tablet + 200% font, TalkBack, dark/light; no false synchronized state |
| 5. Native Trackium cards | Human cargo/device summary, telemetry age/units/trust, identifier component, Advanced raw data | Не удалять JSON export/verification; no fake live tracker | Missing/stale/synthetic/unverified/partial states, no raw JSON required for ordinary task |
| 6. Native navigation/state | Approved shared root chrome, internal Trackium nav, saved UI state/Back | Сохранить private Activity, service lifecycle, foreground/unlocked adapter guard | Back/list filter/scroll/drafts, process death, lock/relock, IME, external share tested |
| 7. History/Apps/Chat/Backup | Apply tokens/domain hierarchy по одному module/flow | Не удалять conditional features, доверие Apps/backup/privacy checks | Реальные доступные screens плюс explicit conditional coverage; empty/error states, large text |
| 8. Current web parity | Tokens, readable typography/targets, honest map labels, authenticated view parity, no preview inflation | Legacy остаётся отдельно, auth cookie/PoP/owner approval неизменны | Desktop/mobile keyboard/dialog focus/reduced motion, authenticated dedicated test tenant; current-vs-preview checks |
| 9. Evidence UX | Independent inclusion/signature/source labels, proof details and error recovery | Publication hard gates в prototype остаются; никаких real transactions | Labels отражают точно API evidence contract, unknown/pending/submitted не превращаются в verified |
| 10. Phone acceptance | Выделенный пустой телефон/tenant, измерения memory/battery/storage, OEM lifecycle | Нет настоящих средств/seed migration; не запускать две ноды на21001 | PHONE_ACCEPTANCE.md, no regression vs baseline, documented NOT RUN/BLOCKED остаются явными |

## Safety/non-regression gates каждого implementation PR

Сначала base source/engine/native pins и исходные licences; затем 327 существующих JVM/Robolectric tests +33 Python guard tests для затронутых invariant contracts и новый targeted UI coverage. Сам по себе documentation PR не нуждается в новых тестах, зеркалящих текст. Для UI изменений проверять meaningful routes/state, не snapshots каждого пикселя без поведения. Lint не отключать; contrast и accessibility проверять по реальным компонентам и состояниям, а не только по token pair.

Постоянные запреты: изменения Minima JAR/консенсуса/криптографии/key-use safety/seed migration, owner-auth bypass, public admin endpoint, remote JS node bridge, fixture под видом физической телеметрии, Mainnet publication для демонстрации. Существующие runtime adapters и Gateway policy остаются authoritative.

## Feature gaps до promise parity

| Capability | Current native | Current web | Решение |
|---|---|---|---|
| Map | Координаты в detail; нет полноценной карты | Preview SVG и authenticated last-coordinate cards | Сначала честные labels/time; real map отдельная capability, не обещание на макете |
| Telemetry chart | Raw structured detail | Static preview chart; не эквивалент live API chart | Общий sample schema + source age, accessible values; integration отдельно |
| Shipment authoring / transitions | В основном чтение | Authenticated server forms / scope-dependent mutations | Не переносить в native автоматически; owner decides incremental API/UI tasks |
| Device provisioning/simulation | Read/pairing | Server simulator identity/run controls при explicit scope/enablement | Отдельные development tools; никогда не physical status |
| Events | Shipment detail tree | Dedicated route/history filter; loaded limits | Human native timeline и cursor UI по реальному API contract |
| Proof publication | Прототип hard-disabled, UI code может содержать financial copy | Legacy/local/server exchange branches с собственными guards | Не выравнивать путём включения транзакций; унифицировать только status/evidence semantics |
| Auth | Native challenge/PoP + Keystore | Same-origin owner challenge/cookie; MDS UID не identity | Платформенно разные безопасные реализации; не делать общий token storage |
| Accessibility/theme | Native fixed navy, host dual theme, History own dark | Dark-only CSS, compact utility text | Theme/token/component fixes отдельными PR с действительной проверкой |

## Readiness, rollout и rollback

Внедрять по экрану/компоненту за PR, с before/after и source refs. Контрольная группа: Node, pairing, shipment detail, locked state. Прежде чем расширять: owner review → limited test build → device accessibility/lifecycle → review measured regressions → следующий flow. Rollback UI commit возвращает UI, **не** очищает wallet/Keystore/database и не повторяет публикацию. Не делать миграцию секретов частью theme rollout.

Текущие blockers до публичного релиза сохраняются: Keets/native licence/NOTICE inventory, explicit Trackium distribution terms, 16-KiB native compatibility, API29+Keets и real phone/OEM acceptance. UI polish их не устраняет. Старые PR #12/#13 основного Trackium не закрываются этим аудитом; новая stage3 ветка зависит от phone-readiness PR #1, пока он не объединён.
