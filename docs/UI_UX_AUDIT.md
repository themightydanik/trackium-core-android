# Trackium Core — UI/UX audit, stage 3

Аудит и предложения для выбора владельцем. **Редизайн не реализован.** Продуктовые исходники, APK permissions, lifecycle, app-lock, аутентификация, Minima JAR/консенсус/ключи/сеть, Gateway и публикация Proofs не изменены.

Web-specific appendix в основном репозитории: [Trackium MiniDapp audit](https://github.com/themightydanik/trackium/blob/docs/ui-ux-audit/docs/UI_UX_AUDIT.md).

## Область и основания

| Реализация | Зафиксированная версия | Назначение |
|---|---|---|
| Trackium Core Android | `190afb8b22babeb19be9e1ba07af7e03c2c8074a`, stage-2 branch, v0.2.0 | BlackBear-derived host + native Trackium + History, один APK |
| Актуальный MiniDapp | Trackium main `196b3dd5848e35844acfeffccf8f984030d7bee3` | `index.html` → assets/v2/cargo.*, server-workspace.*; static Preview и отдельный authenticated same-origin Gateway режим |
| Исторические реализации | тот же Trackium commit | legacy.html + assets/js/*; local-core.html — явно обозначенный legacy classic MDS publisher. Не native Trackium и не текущий Cargo workspace |

Обозначения: **S** — исходники; **V** — реально снятый и просмотренный UI; **R** — runtime Activity/проверка, но не визуальная аттестация; **N** — не запущено. Полная воспроизводимая опись: [INVENTORY](ui-audit/INVENTORY.md), [JSON](ui-audit/inventory.json), [полный control/dialog census](ui-audit/CONTROLS.md), [evidence](ui-audit/EVIDENCE.md). Опись включает 26 manifest Activity, 34 source screen/View класса, 53 XML layout, 95 style/color/dimension declarations, 52 drawable/mipmap/font файла и 27 прямых ссылок на унаследованные брендовые ресурсы. Наличие layout не доказывает его достижимость. Fragment-классы в проверенных shipping source sets не найдены. Пять debug harnesses перечислены отдельно, не как продуктовые страницы.

Скриншоты веба — реальные Chromium-renderings текущего кода при 1440×1000, 320×640, 390×844 и 768×1024 CSS px; это эмуляция viewport, не физические устройства. Preview включён через штатную кнопку и всюду обозначен STATIC DEMO. Authenticated Gateway, production данные, live wallet/финансовые действия не запускались. Native Trackium всегда FLAG_SECURE: защита не отключалась, отсутствие снимков защищённых Activity не подменяется макетами. Подробные фактические результаты Android captures приведены в EVIDENCE.md; все остальные экраны ниже имеют только S/N.

## Полный маршрутный каталог Android

Каждая объявленная Activity и каждая группа унаследованных страниц учтена; ссылки на файлы и XML — в INVENTORY.

| Экран / Activity или View | Сценарий и состояния, проверенные в source | Что требуется улучшить |
|---|---|---|
| StartActivity | Router: seed absent → Launcher, configured → service startup | Не называть состояние router экраном dashboard |
| LauncherActivity | Язык, Create/Restore/Startup params; inherited MINIMA CORE и Minima icon | Первый экран должен представлять Trackium Core и безопасный onboarding, не партнёрское/официальное приложение |
| NewWalletActivity | Новая seed-фраза, proceed; секретный материал | Отдельный privacy gate; никакого публичного screenshot. Source: Activity не входит в sensitive-list MinimaApplication; общий screenshot opt-in/debug default требует отдельной проверки защиты seed |
| NewWalletRestoreActivity | Подтверждение/восстановление новой seed-сессии | Долгий текст → последовательные шаги, сохранить предупреждения и все key-use ограничения |
| RestoreWalletSyncActivity | Seed, mode/key-use floor, MegaMMR URL и restore | Нельзя превращать safety floor в красивый непонятный numeric input. Режим и происхождение кошелька показывать до ввода |
| SeedSyncServiceActivity | Диалог ожидания восстановления | Прогресс, время, cancel semantics и явное «node может продолжать» |
| StartServiceActivity | Waiting/ready/failed/slow; Keep waiting/close/open | Различать старт ноды и network sync; не создавать ложный таймер завершения |
| MainActivity / HomeView (Node) | Toolbar, brand subtitle, pager, block/peers/health, Open Trackium, footer | Главная иерархия: node status, last observation, sync uncertainty, workspace entry; технические поля в diagnostics |
| BalanceView / Wallet | Баланс/токены/coins, loading, reconnect, token details, drag ordering в отдельных flavor paths | Не объединять spendable balance, observed balance и reserved inputs; длинные token IDs, пустой/несинхронизированный кошелёк |
| SendActivity / SendView | Адрес, токен, amount, QR, confirmation/error | Денежные flow только source-аудит. Предложение не меняет confirmation, fee/PoW/key-use controls |
| ReceiveActivity / ReceiveView | Адрес/QR/copy и error | Нельзя автоматически получать новый address ради обновления карточки/скриншота |
| HistoryTabView / HistoryScreen / history.MainActivity | Встроенная History и отдельная private Activity; filters/details/sync | Ещё одна hardcoded dark/orange система; убрать визуальный разрыв, не менять историю и ownership filtering |
| AppsView | External/integrated apps, permissions, info и unverified warning | Подчеркнуть trusted-code границу и scope; сохранить проверку доверия, не заменить её красивой галочкой |
| ChatView | Контакты, identity, scan, поиск/threads | Условная доступность Keets: API28 fixture не подтверждает Chat; показать зависимость, не удалить feature |
| ChatThreadActivity | Сообщения, media, address/contact actions, payment dialog | Развести коммуникацию и финансовые действия; payment не исполнялся |
| MaxThreadActivity | Native Maxima thread, media/contact actions | Визуальная общность с Chat при сохранении transport/identity различий |
| ChatImageActivity | Media viewer/share | Согласовать Back/insets; не публиковать приватные media/contacts |
| ParamsActivity | Параметры запуска, node mode, Keets, RPC, restart/save/error | Секции Basic/Advanced; не скрывать последствия restart и RPC authentication; не менять значения ради UI |
| LockActivity | PIN, неправильный ввод, biometric/cancel/unlock | Нельзя ослаблять lock ради согласованных цветов; TalkBack, focus, lockout и secure windows — отдельные acceptance gates |
| LockSettingsActivity | Включение PIN/biometric, timeout/change/disable | Различать app-open lock и шифрование seed at rest; не обещать второе |
| BackupActivity | Hub/list backup, empty/failed | Секретные операции отдельно от нейтрального списка, подтверждать безопасное место хранения |
| BackupCreateActivity | Export/encrypted backup/passphrase | Защищённый экран; никаких настоящих backup во время аудита |
| RestoreFileActivity | File picker/list, replace/delete/restore confirmations | Не оптимизировать до one-tap destructive action; сохранить warnings и single-wallet-copy semantics |
| VaultActivity | Keys/seed/private material | Только S/N. Никогда не включать в обычные diagnostics screenshots/exports |
| ChatBackupActivity | Export/import Chat backup и подтверждение | Сохранить отдельную privacy модель и FLAG_SECURE |
| SeedSyncActivity | Pending recovery/resync и подтверждение | Отделить восстановление кошелька от безопасного refresh сети |
| TerminalActivity / TerminalView | Команда, structured/text output, history, error | Advanced tool, не пользовательский dashboard; никаких команд подписи/публикации в аудите |
| LogsActivity / LogsView | Log buffer, filter/share, empty | Перед share предупреждать о sensitive data; raw logcat/seed не собирать автоматически |
| Native tech.trackium.cargo.MainActivity | Пять tab states и вложенные формы/диалоги ниже | Одна Activity того же APK, не MiniDapp/WebView; отдельная forced secure policy |

`app/src/debug` harnesses — test-only. Отключённые classic/pandamonium Gradle variants и ветки BuildConfig не считать shipped полноценным Pandamonium. BlockDebug реально использует pandaShell, History и Trackium; Chat зависит от Keets. PendingView, старые wallet composite layouts и LogsView остаются в инвентаризации, но их наличие не означает отдельный текущий tab. Перечень каждого XML, включая dialog/row/keyboard/progress layouts, сохранён без исключений.

## Native Trackium: страницы, формы и диалоги

| Поверхность | Текущее поведение (S) | Gap / предложение |
|---|---|---|
| Dashboard | Core observation, Gateway freshness, первые 25 грузов/устройств, proofs/publisher overview | Две независимые связи Node/Gateway, expired/stale/unknown отдельно; counts не выдавать за полный fleet total |
| Shipments | Карточки, Route & conditions, event history, proof-related details | Нет полноценного map/chart/editor parity; технические JSON keys в generic detail() заменить понятной summary + Advanced raw data |
| Devices | Tenant-scoped карточки, Coordinates/sensors/status | Единицы, timestamps, stale state, missing GPS/sensor, заряды и provenance в отдельных компонентах, а не JSON-tree |
| Proofs | Pending owner approval, records, verify/export, saved jobs | Published/submitted/included/verified и physical authenticity не синонимы. Publication в prototype hard-disabled |
| Settings/Gateway | Origin + tenant, save, challenge/claim/revoke, issuer pin, disabled safety switches и cap | Stepper pairing, срок, scope и следующая безопасная операция; «Return to node settings» сейчас finish(), не переход в ParamsActivity |
| Generic dialog/detail() | JSON object/array recursion depth 5, до 50 элементов, technical field names | Читаемые доменные summary, collapsible IDs + copy, raw JSON только Advanced. Не удалять export/проверку |
| jobDialog() | Consent/typed Mainnet text/readiness/publish UI сохранены в коде | Controller loadJob/readiness/publish заранее запрещены. Наличие текстов не означает разрешённую публикацию. Понятная read-only capability notice вместо активноподобных обещаний |
| Recovery/error | «Recovery review required», safeError, данные сбрасываются при task exception | Дать error category, retry/re-pair/operator action; не советовать clear/reinstall неизвестного wallet/checkpoint |
| Navigation/form state | Bottom labels 10sp, setContentView при render(), tab сохранён Bundle; gateway drafts — поля Activity | Scroll/focus/drafts уязвимы к rerender/process recreation. Не класть токены/seed в SavedState; безопасный UI state хранить отдельно от SecureStore credentials |

## MiniDapp: актуальные страницы и режимы

В `index.html` семь маршрутов: Dashboard, Shipments, Devices, Live Map, Events, Proofs, Settings/Gateway. Каждая страница имеет **unpaired**, **explicit static Preview**, и отдельную authenticated same-origin server ветку. Это разные data/render contracts, а не просто тема.

| Маршрут | Что реально видно в Preview | Authenticated source branch / ограничения |
|---|---|---|
| Dashboard | Синтетические summary, schematic map, static chart, cargo focus/activity | server-workspace.render: fleet/shipments, свежесть polling; не считать preview chart доказательством live telemetry |
| Shipments | Search, 3 demo routes, shipment detail dialog, linked device | Draft/create form, transitions, route points/conditions и simulation controls при scopes; source-only, не отправлялись write-запросы |
| Devices | Search, fixture rows/cards, device dialog | Зарегистрированные устройства; simulator identity требует simulate scope и server enablement. Это не физический tracker |
| Live Map | SVG-схема Европы, fixture позиции, не navigation-grade карта | Server branch показывает последние координаты/timestamps по устройствам; реальной basemap/live-track parity нет |
| Events | Filters и длинная demo timeline/table | Shipment event history; pagination/loaded-prefix видимость требует явного UX; нельзя выдавать отсутствие поздних событий за доказанный факт |
| Proofs | Honest empty state «No blockchain proofs published» | Pending approvals, policy, preflight, Local/Server Core exchange, export/independent verification; source-only, финансовые действия не исполнялись |
| Settings/Gateway | Public origin/deployment profile, support check, runtime info, legacy links | Owner challenge pairing, short-lived HttpOnly same-origin session, disconnect/revoke; не credential textbox и не Minima UID identity |
| Дополнительные состояния | Menu/scrim, preview switch/banner/toast, shipment/device dialogs, no data/search results | Loading/session expiry/scope errors/forms/checkpoints. Private tenant/runtime claims остаются N без тестовых owner credentials |
| legacy.html | Отдельная старая local DB/simulator/blockchain UI | Не мигрирована в v2 и не считается актуальным набором features; не запускалась для финансовых действий |
| local-core.html | Явно legacy classic MDS Local Core Publisher | Prepare/sign/mine/checkpoint/publish flow — только source-аудит, не открывался с wallet и не является встроенным Android Trackium |

## Сравнительная матрица

| Ось | Android host | Native Trackium | Current MiniDapp | Решение |
|---|---|---|---|---|
| Palette | light #F4F5F7 / dark #0F1114, orange #FF4A2B | #0A1423 / #111F31, mint #43D7BD | #0D1727 / #131F32, same mint | Общие semantic colours; native light/dark adapters, не перекрасить все статусы в mint |
| Typography | Manrope resources, Material appearances | system sans, 10sp nav/12sp metadata/29sp title | Inter declaration без bundled font, OS fallback; 7–16px utility text | Общая scale, системные font metrics допустимы; не считать Inter реально загруженным |
| Components | Material3/AppCompat XML + programmatic History | Native Activity + programmatic LinearLayout/buttons | semantic HTML, CSS panels, SVG | Общие states/contracts, platform-native реализации |
| Navigation | Pager Wallet/History/Apps/(Chat)/Node + overflow | 5 labels внутри отдельной Activity; finish returns host | Sidebar → mobile drawer, hash routes/dialogs | Одна продуктовая IA, не два конкурирующих bottom bars |
| Spacing/radii | Buttons min48dp, 13dp corners/cards16 | margins8, padding16/18, radius16 | radius14, many compact controls | 4pt grid, 48dp Android / ≥44px web primary targets |
| Forms | XML/EditText, mode/danger confirmations | labels отдельными TextView, full rerender | native inputs; pairing/security copy dense | labels/errors/autofill/focus/state по платформе, единые сообщения |
| Icons | Minima/panda launcher, notification/dialog references | host icon + text-only bottom nav | own t-arrow/SVG line set; root favicon PNG separate | Original Trackium Core icon, line weight/sizing shared, не переименовывать protocol IDs |
| Accessibility | Framework assists; faint text/orange button contrast risks | tiny nav, no explicit selected/tab semantics/heading roles | skip link, aria-current, dialogs, reduced-motion tests; small targets/px typography | Contrast/TalkBack/keyboard/font-scale gates для каждого PR |
| Errors | node startup/slow/service and destructive dialogs | safeError + generic recovery/detail tree | unpaired/support/scope/loading/Preview separation | Domain error + next action; technical trace behind Advanced |
| Lifecycle/insets | KeyboardInsets/SystemBars, app-lock, service independent | fixed system bar colours, insets listener, close adapter onPause | browser history/dialog focus and drawer | Общий visual chrome, нативный Back/lifecycle/security остаются платформенными |
| Data/evidence | Chain/wallet observations | Gateway reads + read-only node | richer Preview, thinner authenticated views | Не выводить parity по демо; три независимых trust indicators |

## Findings и приоритеты

P0 — риск потери/раскрытия секретов или ложного финансового подтверждения, блокирует соответствующий flow; P1 — основная задача/доступность/identity; P2 — консистентность и удобство; P3 — polish. Severity не означает, что денежный инцидент воспроизведён.

| ID | Priority / evidence | Finding и критерий решения |
|---|---|---|
| UX-01 | P1 / S,V | Launcher MINIMA CORE/icon и Trackium Core toolbar не одна identity. Approved Trackium brand должен охватить launcher/onboarding/notifications/dialogs, original attribution сохранить в About/NOTICE |
| UX-02 | P1 / S | Seed-creation screen не в forced-sensitive list; debug default screenshot allowed. Отдельный security review + test матрицы screenshot opt-in для всех seed/PIN/backup экранов; seed не снимался и защита не менялась |
| UX-03 | P1 / S | Generic JSON в обычных native shipment/device dialogs. Human summary, units/time/trust; raw export/advanced remain |
| UX-04 | P1 / S | Native tab10sp/flat labels и два navigation shells. Semantic tabs/selection, root hierarchy, ≥48dp targets; verify TalkBack, не только цвет |
| UX-05 | P1 / S | Full rerender/setContentView может терять focus, scroll и несохранённые формы; process-death state не полон. Tests для typed drafts, errors, Back, network refresh |
| UX-06 | P1 / S,V | «Live Map» vs schematic/static preview/last coordinates; разделить labels, age и capability. Не имитировать real GPS/live-map готовность |
| UX-07 | P1 / S | Financial-sounding UI тексты существуют при hard gates. Показывать unavailable capability заранее, не активировать публикацию ради mockup |
| UX-08 | P1 / calculated | White on host orange #FF4A2B ≈3.35:1, ниже normal-text4.5. Ввести onAccent/размерные правила и реальные state checks |
| UX-09 | P1 / S | «Return to node settings» просто finish; process recreation default Dashboard. Правильное имя/route contract и проверка Back/state |
| UX-10 | P1 / S,V | Native manual first25/50 vs web polling/loaded subsets; counts не global. Loading/partial/stale/empty/session expired не смешивать |
| UX-11 | P2 / V | В 28 web route/viewport captures document overflow=0, но много compact link/button candidates <44px; это не WCAG compliance verdict. Визуально/клавиатурой проверить конкретные controls |
| UX-12 | P2 / S,V | Host supports themes, History/Trackium/web fixed dark; status/navigation bar переход создаёт чужой-app эффект. Token adapters и согласованный chrome, secure flags retain |
| UX-13 | P2 / S | Одинаковое слово «verified» может смешать API session, payload signature, chain inclusion и sensor authenticity. Раздельные labels/statuses/timestamps, не только зелёная иконка |
| UX-14 | P2 / S,V | Очень длинные IDs, expiry/scopes и technical prose мешают основным задачам. Short display + copy/full detail; никогда сокращать криптографическое значение в export/check |
| UX-15 | P2 / S | Native English literals обходят host localisation. Централизовать тексты, ru/uk/zh и 200% font/RTL checks до новой оболочки |
| UX-17 | P1 / V | Compact dark Android/font130: toolbar title/subtitle обрезаны при видимом Open Trackium action. Header hierarchy, responsive action placement и large-text acceptance нужны до rollout |
| UX-16 | P2 / S | Legacy ссылки рядом с современным продуктом ведут к иному runtime/кошельку. Явная совместимость, отдельное предупреждение/выход; не считать feature parity |

Пока нет воспроизведённого P0 инцидента. Seed/privacy риск UX-02 и Mainnet/evidence semantics требуют обязательных safety acceptance gates, а не косметического одобрения.

## Направление и границы

[Design system](DESIGN_SYSTEM_PROPOSAL.md), [brand/icon concepts](BRAND_AND_ICON_CONCEPTS.md), [navigation](UNIFIED_NAVIGATION.md), [roadmap](UI_REDESIGN_ROADMAP.md) и [editable visual proposals](ui-audit/proposals/preview.html) — для обсуждения. Ни одна иконка не выбрана и не установлена. Existing features не удалены. Исторические package names, storage keys, protocol names, cert pinning, authentication scopes и event/state contracts не являются брендингом для автоматического rename.
