# Trackium Core — brand and icon concepts

**Три варианта на выбор; окончательная иконка не утверждена.** Ничего не заменено в app/res, Manifest, notification или favicon. [Concept sheet](ui-audit/proposals/icon-concepts.png), [editable SVG](ui-audit/proposals/icon-concepts.svg) и [mask/themed study](ui-audit/proposals/icon-mask-study.svg). Это оригинальные геометрические sketches, не новый официальный знак Minima/Eurobuddha.

## Позиционирование

Trackium Core — самостоятельная кастомная Android-сборка Minima Core с native logistics platform. Чёткий, спокойный и точный язык: «Recorded», «Last observed», «Needs review» вместо декоративных «secured/verified» без указания доказательства. Логистическое действие понятно раньше blockchain jargon; advanced details доступны, не удалены. В About/NOTICE остаются Eurobuddha, BlackBear/Pandamonium и original Minima attribution; никаких намёков на official endorsement. Имена технических пакетов, protocol IDs, pinning и storage не являются UI-brand strings для массового rename.

## Где сейчас живёт унаследованный бренд

Полный file/line список: [inventory brand references](ui-audit/INVENTORY.md#inherited-brand-references), JSON `brand_references` и graphics list.

| Поверхность | Ресурс / источник | Что учитывать при будущем approved change |
|---|---|---|
| Launcher / app switcher / app info / notification large icon | app Manifest @mipmap/ic_launcher, ic_launcher_round → adaptive foreground/background/monochrome | Сохранить adaptive/themed support, проверить все Android masks и package-manager metadata |
| Panda/BlackBear visual layer | drawable/ic_launcher_foreground.xml, ic_launcher_monochrome.xml, ic_launcher_background.xml; block launcher_pupil blue | Не перекрашивать панду и не выдавать её за новый Trackium logo |
| Onboarding | launcher_activity.xml @drawable/ic_minima, contentDescription Minima, MINIMA CORE title | Поменять approved presentation, но оставить legal origin в About/NOTICE |
| Host wallet tab и dialogs | MainActivity, SendView, Startup/restore/backup/Params/Chat dialogs, utils/logger используют ic_minima | Wallet token currency icon/Minima symbol семантически не то же, что app brand. Не заменить все Minima currency symbols Trackium знаком |
| Foreground service / Chat / health | MinimaService, ChatDrainer, NodeHealthMonitor и другие R.drawable.ic_minima references | Notification small icon требует отдельного простого monochrome silhouette и action semantics |
| App directories / integrated list | host application icon загружается PackageManager; PandamoniumHooks Trackium entry | Меняется неявно вместе с Manifest icon; проверить runtime registry/display |
| History | pm_history_ic_launcher*.png, pm_history theme/header, HistoryDesign dark/orange | Это заимствованная module identity; решить отдельный history glyph, не удалить upstream notices |
| Native Trackium | TRACKIUM / CARGO, собственная palette, host app icon | Подчинить общей hierarchy, не превратить в второе чужое приложение |
| Current web | index.html inline t-arrow wordmark и root icon.png favicon | Эти ресурсы принадлежат иной реализации; выбранный app icon не обязан стать единственным web glyph без legibility check |
| Legacy | legacy.html/assets/img/logo.png и старые assets | Исторические названия/compatibility предупреждения сохранить, не маскировать legacy как современный продукт |

Прямых references27; неявные usages PackageManager/manifest и runtime-theme inheritance описаны выше. Комментарии и package names BlackBear/Pandamonium не означают видимую панду; обратное тоже верно — картинка может быть через inherited resource без слова panda.

## Три исходных направления

| Concept | Форма / связь с продуктом | Достоинства | Риски / проверка |
|---|---|---|---|
| **A — Routed T** | T с ответвляющимся маршрутом/точкой | Сильная связь с Trackium, простая silhouette и small-size legibility; предпочтительный кандидат для теста | Не должен читаться как случайная стрелка/официальный network token. Сравнить с текущим web t-arrow и внешними marks, trademark clearance отдельно |
| **B — Cargo C** | Открытая коробка/объём и C через рёбра | Логистический смысл, понятная геометрия | Cube/blockchain рынок перегружен похожими формами; проверить distinctive gap и видимость в24dp |
| **C — Relay** | Три checkpoint nodes и связанный путь | Movement/network, работает monochrome | Может читаться как share/network icon; возможно слабее узнаваемость как app logo |

Каждый вариант имеет transparent foreground SVG и однокрасочный SVG. Board показывает большие и 24/48/72 previews; это предварительная оценка, не Android launcher certification. Нельзя считать выбранным A только потому, что он указан предпочтительным для тестирования.

## Adaptive / monochrome спецификация следующего шага

108×108 coordinate space; критическая геометрия нормализована внутрь центральной safe area (контроль: 66dp / circle radius33). Background — однотонный canvas, foreground — отдельный shape без baked rounded-square. Текущие sketches не используют clipping для скрытия лишних деталей; final geometry нужна оптическая доводка под circle/squircle/rounded square, motion/parallax masks и 24/32/48/72/108px. Themed icon — отдельный solid silhouette, не grayscale PNG. Notification small icon — тот же semantic mark упрощённый для24dp, alpha-only, без фона и wordmark.

После owner choice: настоящий Android adaptive XML + fallback densities, themed/monochrome с API33+, light/dark launcher surfaces, round mask, badge/work profile и disabled state. Colour alone не несёт единственную узнаваемость. Не использовать blockchain shield/checkmark как обещание доказанной физической доставки. Перед публичным применением — originality/trademark/licence review; в этом этапе юридическая уникальность не заявляется.
