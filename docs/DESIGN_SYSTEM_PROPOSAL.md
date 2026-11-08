# Trackium design system — proposal 0.1

**Для обсуждения, не внедрено.** Цель — единый продукт Trackium Core с уважением к платформе Android и отдельной веб-реализации, без копирования Minima/Eurobuddha identity. [Visual board](ui-audit/proposals/concept-preview.png), [editable static HTML](ui-audit/proposals/preview.html), [tokens](ui-audit/proposals/tokens.json), [contrast checks](ui-audit/proposals/contrast-checks.json). Все примеры помечены synthetic/proposal; кнопки board ничего не выполняют.

## Иерархия токенов

Предлагаемый source of truth — versioned JSON: primitive palette → semantic roles → component states → Android/XML/Kotlin и CSS adapters. Draft JSON имеет собственную простую schema, **не объявляется готовым DTCG package**. Android mappings используют snake_case, CSS — kebab-case, semantic JSON — camelCase. Идентичность role важнее одинаковых номеров elevation/font metrics. Все файлы находятся под docs, не входят в source sets и не импортируются приложениями.

```text
palette.neutral / mint / amber / red / blue
  -> canvas, surface, text, textSecondary, accent, onAccent
  -> warning, danger, info, outline, outlineSubtle
  -> button.primary.enabled/focused/pressed/disabled
  -> device.sample.stale / proof.chain.included / input.invalid
```

Не допускать отдельной «четвёртой» палитры в History и dialog builders. Будущий generator обязан проверять формат, role coverage в light/dark, meaningful state contrast и отсутствие прямых brand hex в новых компонентах. Draft [CSS](ui-audit/proposals/tokens.css) и [XML](ui-audit/proposals/colors-draft.xml) показывают преобразование, но не заменяют существующие runtime resources. Версионирование: additions minor, удаления/изменение semantics — major + migration notes; generated adapters не править вручную.

| Role | Dark | Light | Назначение |
|---|---|---|---|
| canvas | #0B1220 | #F5F8FC | Основной фон |
| surface / raised | #132238 / #1C3048 | #FFFFFF / #EAF0F7 | Карточка / drawer/sheet |
| text | #E8EEF6 | #122238 | Основное чтение |
| textSecondary | #A7B8CA | #4C6077 | Метаданные, не disabled opacity |
| accent / onAccent | #4FE0C1 / #071B18 | #007E6D / #FFFFFF | Action/selected, не blanket «verified» |
| warning | #F6C56E | #805500 | Attention/stale и понятный label |
| danger | #FF8B8B | #AE2437 | Ошибка/опасное действие |
| info | #94B8FF | #285AB7 | Нейтральная информация |
| outline | #607894 | #63788F | Meaningful input/control edge, минимум 3:1 |
| outlineSubtle | #405771 | #D5DFEB | Только decorative dividers; не единственный input/focus indicator |

Контраст рассчитан стандартной WCAG relative luminance формулой, opaque sRGB. Dark text/canvas 16.04:1, secondary/surface 7.88:1, onAccent/accent 10.83:1; light onAccent/accent 4.99:1. Все draft functional pairs в JSON проходят пороги 4.5 для обычного текста и 3 для границы control. Это не аттестация целого UI: реальные pressed/disabled/translucent/icon/focus states требуют отдельного измерения. Изначальная draft decorative border не достигала3:1; поэтому выделены функциональная outline и outlineSubtle, а не занижен порог.

## Light/dark strategy

Dark — ведущая презентационная концепция, **не принудительная единственная тема**. Android: System default + Light/Dark preference, хранится отдельно от wallet config; существующие host light resources сохраняются до полноценного покрытия native Trackium/History. Web: system preference + explicit choice после согласования, не просто invert(). Иллюстрации/карта/семантические статусы проходят обе темы. Status/navigation bars используют правильные luminance flags, transparent/edge-to-edge policy зависит от API; secure flags, biometric surfaces и IME inset logic не менять ради темы. APK API28/29+/target35 и OEM должны быть проверены отдельно.

## Typography и layout

System sans как надёжный Android baseline; Manrope можно сохранить, если отдельная оценка читаемости/лицензии подтвердит единое семейство. Для web либо system stack, либо явно bundled/licensed font: сейчас CSS «Inter» не гарантирует реальный Inter. Не вводить удалённую загрузку font без privacy/CSP решения.

| Token | Android sp / web rem | Line height | Use |
|---|---|---|---|
| caption | 12 / .75 | 1.4 | Вторичные подписи, не важные команды |
| label | 14 / .875 | 1.4 | Navigation/field labels |
| body | 16 / 1 | 1.5 | Основной текст |
| title | 20 / 1.25 | 1.3 | Card/sheet heading |
| headline | 28 / 1.75 | 1.2 | Page title |
| display | 36 / 2.25 | 1.15 | Только просторные desktop surfaces |

Tabular numerals для температур/батареи/сумм; units не крошечным suffix. IDs — monospace в Advanced, обычный короткий display + full-value copy; не менять содержимое подписываемых/exported данных. Grid 4/8/12/16/24/32/48/64 dp/CSS px. Phone gutters16, tablet24, desktop32; содержимое не бесконечно растягивать. Breakpoints сначала от контента: <600 compact, 600–959 medium, >=960 expanded — proposal, не shipping contract. Tables допускают локальную прокрутку с label, document horizontal overflow недопустим. Large text 200%: wrap/reflow, не обрезать ключевые labels и не фиксировать высоту поля/карточки.

Radii: control12, card16, sheet24, chip8/pill999. Elevation — роли base/card/overlay/modal; dark отделяется tonal surface + border, light тонкой тенью. Не добавлять blur/glass под мелким текстом/telemetry; contrast важнее decorative premium effect.

## Компоненты и состояния

| Компонент | Контракт |
|---|---|
| Primary button | Solid accent/onAccent, >=48dp Android; web primary >=44px. Одна доминирующая primary на область. Spinner + конкретное «Pairing…», guard от double-submit |
| Secondary / text | Outline или text; видимый keyboard focus, не только цвет. Disabled имеет label причины, не только opacity |
| Destructive | Отдельный danger treatment, объяснение последствий, прежние typed confirmations и security gates сохраняются. Не превращать финансовое подтверждение в обычный toast |
| Input | Постоянный label, hint/example/units, validation рядом с полем, сохранить draft/focus при ошибке, правильная keyboard/autofill policy. Secret не помещать в обычный SavedState/telemetry |
| Cargo card | Name/route/status, next checkpoint, last observation, provenance. IDs/time units с понятным drill-down; urgency не только mint/amber dot |
| Device card | Connectivity, sample age, battery, last coordinates and sensor authenticity — независимые поля. Missing/stale не рисовать 0°C/0% |
| Telemetry | Value+unit+sample time+receipt time+trust; chart показывает gaps/stale range и accessible table. Schematic/demo mark постоянен, не исчезает с toast |
| Node status | Starting / offline / connecting / observing / uncertain / stopped; высота и peers — observations, не автоматический «fully synced». Sync/health freshness независимы |
| Proof status | Not created / awaiting approval / submitted / inclusion observed / independently checked / failed / unknown. Отдельно bytes/signature integrity, chain inclusion и physical source trust; не объединять их в зелёный shield |
| Pairing | Origin + tenant + owner challenge + expiry + claim/revoke; ни owner token, ни Minima UID не становятся browser/native login. Scope показывать понятным языком без изменения auth |
| Loading | Initial load vs refresh vs partial; skeleton без фиктивных значений, `aria-busy`/TalkBack announcement, retry не создаёт повторное финансовое действие |
| Empty | Не настроено / ещё нет данных / фильтр пуст / нет разрешения / нет сети — разные тексты и разные next actions |
| Error | Human category + safe next step; safe technical code в Details. При неизвестном wallet outcome не советовать clear/reinstall. Error не стирает молча успешные cached data без stale label |
| Modal / sheet | Dialog title, close/Back/Escape, trapped/restored focus web; native IME/insets, sheet не скрывает mandatory consent. Длинные JSON только Advanced viewer |

## Accessibility и motion acceptance

WCAG 2.2 AA как web baseline; Android дополнительно TalkBack/Switch Access, 48dp touch, 200% font, keyboard/IME и magnification. Normal text4.5:1, large text3:1, meaningful nontext3:1; selection/errors/trust имеют текст/shape, не только colour. Focus не закрывается fixed banners/toasts. У canvas/SVG графика есть текстовая альтернатива, chart values доступны без hover. Reduced-motion обязателен: feedback120ms, navigation200ms, sheet240ms без spring overshoot/scroll-jacking; отключать декоративные transitions, сохранять мгновенный state feedback. Не анимировать здоровье/синхронизацию так, будто получено новое измерение.

## Граница решения владельца

Нужно выбрать brand direction/icon, default surface/theme, root IA и приоритет safety vs logistics entry. До этого допустимы только аудит/макеты/контракты. Следующие implementation PR идут по [roadmap](UI_REDESIGN_ROADMAP.md), с сохранением всех security/crypto/auth/publication gates.
