package tech.trackium.cargo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import java.text.DateFormat
import java.util.Date
import org.json.JSONArray
import org.json.JSONObject

/** Native views only. No WebView or JavaScript-to-wallet bridge. Gateway owns business rules. */
class MainActivity : Activity() {
    private fun role(id: Int) = getColor(id)
    private val bg get() = role(tech.trackium.designsystem.R.color.tc_ds1_canvas)
    private val panel get() = role(tech.trackium.designsystem.R.color.tc_ds1_surface)
    private val mint get() = role(tech.trackium.designsystem.R.color.tc_ds1_accent)
    private val text get() = role(tech.trackium.designsystem.R.color.tc_ds1_text)
    private val muted get() = role(tech.trackium.designsystem.R.color.tc_ds1_text_secondary)
    private val amber get() = role(tech.trackium.designsystem.R.color.tc_ds1_warning)
    override fun attachBaseContext(base: android.content.Context) {
        super.attachBaseContext(tech.trackium.designsystem.ThemePreference.wrap(base))
    }

    private val c: AppController
        get() = TrackiumRuntime.get(application)

    private lateinit var shell: LinearLayout
    private var loading: ProgressBar? = null
    private lateinit var content: LinearLayout
    private var tab = "Dashboard"
    private var sessionStarted = false
    private var proofPage = 0
    private var detailTitle: String? = null
    private var detailValue: JSONObject? = null
    private var currentScroll: ScrollView? = null
    private var renderedKey: String? = null
    private val scrollPositions = mutableMapOf<String, Int>()
    private val searches = mutableMapOf<String, String>()
    private val filters = mutableMapOf<String, String>()
    private var focusId = 0
    private var cursor = 0
    private var draftGateway: String? = null
    private var draftTenant: String? = null

    private val fieldHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var fieldEpoch = 0
    private var officialDevices: JSONArray? = null
    private var pendingDeviceScan: String? = null
    private var fieldDialog: AlertDialog? = null
    private var roadPreview: Pair<String,FieldRoads.Road>? = null
    private var fieldViewportKey = ""
    private var fieldViewport = ObservationPlot.Viewport()
    private var offlineLinework: List<List<LocalFieldPlan.Stop>>? = null
    private val fieldPoll = object : Runnable {
        override fun run() {
            if (!sessionStarted || !TrackiumCoreAccess.authorized()) return
            val device = detailValue
            if (tab == "Devices" && device != null && PhysicalPositions.isPhysical(device) &&
                fieldDialog?.isShowing != true && !c.busy.get()) {
                if (device.has("official_installation")) loadOfficialObservation(device,replace=true)
                else loadDeviceObservation(device,replace=true)
            }
            fieldHandler.postDelayed(this,15000)
        }
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        tab = state?.getString("tab")?.takeIf { it in listOf("Dashboard", "Shipments", "Devices", "Proofs", "Settings") } ?: "Dashboard"
        for (key in listOf("Shipments", "Devices")) {
            searches[key] = state?.getString("query.$key") ?: ""
            filters[key] = state?.getString("filter.$key") ?: "All"
        }
        pendingDeviceScan = intent.getStringExtra(CoreConnectionActivity.CONNECTION_LINK)
        intent.removeExtra(CoreConnectionActivity.CONNECTION_LINK)
        if (pendingDeviceScan != null) tab = "Devices"
        proofPage = (state?.getInt("proof.page") ?: 0).coerceAtLeast(0)
        draftGateway = state?.getString("draft.origin")
        draftTenant = state?.getString("draft.tenant")
        for (key in listOf("Dashboard", "Shipments", "Devices", "Proofs", "Settings")) scrollPositions[key] = state?.getInt("scroll.$key") ?: 0
        render()
    }

    override fun onSaveInstanceState(out: Bundle) {
        out.putString("tab", tab)
        out.putInt("proof.page",proofPage)
        if(detailValue==null)currentScroll?.let { scrollPositions[tab]=it.scrollY }
        for(key in listOf("Shipments","Devices")) {out.putString("query.$key",searches[key]);out.putString("filter.$key",filters[key])}
        for(key in listOf("Dashboard","Shipments","Devices","Proofs","Settings"))out.putInt("scroll.$key",scrollPositions[key] ?: 0)
        val origin=draftGateway?.takeIf { v -> try { val u=java.net.URI(v);u.rawUserInfo==null && u.rawQuery==null && u.rawFragment==null }catch(_:Exception){false} }
        out.putString("draft.origin",origin);out.putString("draft.tenant",draftTenant?.takeIf { Regex("[0-9a-fA-F-]{0,36}").matches(it) })
        super.onSaveInstanceState(out)
    }

    override fun onResume() {
        super.onResume()
        if (!TrackiumCoreAccess.authorized()) {
            setContentView(TextView(this).apply { text = "Unlock Trackium Core to continue" })
            finish()
            return
        }
        sessionStarted = true
        fieldEpoch++
        fieldHandler.removeCallbacks(fieldPoll)
        fieldHandler.post(fieldPoll)
        c.lifecycle(true)
        c.listener = {
            if(c.busy.get()) {
                loading?.visibility=android.view.View.VISIBLE
                fun disable(view:android.view.View){if(view is Button)view.isEnabled=false;if(view is android.view.ViewGroup)for(i in 0 until view.childCount)disable(view.getChildAt(i))}
                if(::shell.isInitialized)disable(shell)
            } else render()
        }
        render()
        val scan = pendingDeviceScan
        pendingDeviceScan = null
        if (scan != null) confirmOfficialDevice(scan)
        else if (c.core == null && !c.busy.get()) c.connectCore()
    }

    override fun onPause() {
        fieldHandler.removeCallbacks(fieldPoll)
        fieldEpoch++
        fieldDialog?.dismiss()
        roadPreview = null
        detailValue?.takeIf { PhysicalPositions.isPhysical(it) }?.apply {
            remove("location"); remove("history"); remove("status")
        }
        if (sessionStarted) {
            c.listener = null
            c.lifecycle(false)
            sessionStarted = false
        }
        super.onPause()
    }

    private fun dp(n: Int) = (resources.displayMetrics.density * n).toInt()

    private fun label(
        value: String,
        size: Float = 16f,
        color: Int = text,
        bold: Boolean = false,
    ): TextView =
        TextView(this).apply {
            this.text = value
            textSize = size
            setTextColor(color)
            setPadding(0, dp(5), 0, dp(5))
            if (bold) setTypeface(null, Typeface.BOLD)
            setTextIsSelectable(true)
            if(bold) isAccessibilityHeading = true
        }

    private fun box(parent: LinearLayout, title: String): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(14))
            background =
                GradientDrawable().apply {
                    setColor(panel)
                    cornerRadius = resources.getDimension(tech.trackium.designsystem.R.dimen.tc_ds1_radius_card)
                    setStroke(dp(1),role(tech.trackium.designsystem.R.color.tc_ds1_outline_subtle))
                }
            parent.addView(
                this,
                LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(8), 0, dp(8)) },
            )
            addView(label(title, 20f, text, true))
        }

    private fun button(
        parent: LinearLayout,
        title: String,
        enabled: Boolean = true,
        action: () -> Unit,
    ) =
        Button(this).apply {
            text = title
            isAllCaps = false
            setTextColor(getColorStateList(tech.trackium.designsystem.R.color.tc_ds1_primary_content))
            minHeight = dp(48)
            setPadding(dp(16),dp(10),dp(16),dp(10))
            backgroundTintList = getColorStateList(tech.trackium.designsystem.R.color.tc_ds1_primary_container)
            isEnabled = enabled && !c.busy.get()
            parent.addView(this, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8) })
            setOnClickListener { action() }
        }

    private fun input(parent: LinearLayout, title: String, value: String = ""): EditText {
        parent.addView(label(title, 13f, muted))
        return EditText(this).apply {
            setSingleLine()
            minHeight = dp(48)
            isSaveEnabled = false
            imeOptions = android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
            setTextColor(this@MainActivity.text)
            setHintTextColor(muted)
            setText(value)
            parent.addView(this, LinearLayout.LayoutParams(-1, -2))
        }
    }

    private fun dialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun detail(title: String, value: JSONObject) {
        currentScroll?.let { v -> renderedKey?.let { scrollPositions[it]=v.scrollY } }
        currentScroll=null
        roadPreview = null
        detailTitle = title
        detailValue = value
        render()
    }

    private fun renderDetail(value: JSONObject) {
        if(value.has("partial_observation"))content.addView(label("Partial observation · ${CargoPresentation.value(value,"partial_observation")}",16f,amber,true))
        button(content,"Back to $tab") { detailTitle=null;detailValue=null;currentScroll=null;render() }
        if (value.optBoolean("local_field_map") || (tab == "Devices" && PhysicalPositions.isPhysical(value))) {
            renderFieldMap(value)
            if (value.optBoolean("local_field_map")) return
        }
        renderEvidenceSections(content, detailTitle ?: "Details", value)
        content.addView(label("Check the time basis and provenance of each observation. Gateway receipt and device measurement are separate.",14f,muted))
        val advanced=box(content,"Advanced / export")
        advanced.addView(label("This evidence may contain sensitive cargo data. Share only with authorized recipients.",14f,muted))
        button(advanced,"Advanced Raw Data") { dialog("Advanced Raw Data",value.toString(2)) }
        button(advanced,"Export reported data") { share(value) }
    }

    private fun fieldKey(value: JSONObject): String? {
        if (value.optBoolean("local_field_map")) return "field-plan-local"
        if (value.has("official_installation")) return "field-official-" + ProofProtocol.sha(value.getString("id"))
        return value.optString("id").takeIf { it.isNotBlank() && value.has("status") }
            ?.let { LocalFieldPlan.key(c.gateway.origin,c.gateway.tenant,it) }
    }

    private fun renderFieldMap(value: JSONObject) {
        val standalone = value.optBoolean("local_field_map")
        val card = box(content,"Field map")
        val key = fieldKey(value)
        val latest = PhysicalPositions.current(value)
        val history = PhysicalPositions.history(value)
        val plan = key?.let { runCatching { LocalFieldPlan.read(c.secure,it) }.getOrNull() }
        val catalog = key?.let { runCatching { c.secure.read(it+"-road") }.getOrNull() }
        val savedRoads = catalog?.let { runCatching { FieldRoads.saved(it) }.getOrNull() }
        val active = savedRoads?.find { it.id == catalog?.optString("selected") }?.road
        val preview = roadPreview?.takeIf { it.first == key }?.second
        val road = preview ?: active
        val offline = offlineLinework ?: runCatching { FieldRoads.offlineRoads(c.secure.read("field-basemap")) }
            .getOrDefault(emptyList()).also { offlineLinework = it }
        if (latest != null || history.isNotEmpty() || !plan.isNullOrEmpty() || road != null || offline.isNotEmpty()) {
            val viewportKey = listOf(key,value.optString("id"),road?.encodedLegs?.hashCode(),offline.hashCode()).joinToString("/")
            if (fieldViewportKey != viewportKey) {
                fieldViewportKey = viewportKey
                fieldViewport = ObservationPlot.Viewport()
            }
            val plot = ObservationPlot(this,latest,history,plan.orEmpty(),road,offline,fieldViewport,amber,mint,muted)
            card.addView(plot, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(290)))
            val controls = LinearLayout(this)
            card.addView(controls)
            button(controls,"Zoom in") { plot.zoom(1.5f) }
            button(controls,"Zoom out") { plot.zoom(1/1.5f) }
            if (road != null) button(controls,"Fit road") { plot.fit(true) }
            button(controls,"Fit all") { plot.fit(false) }
        }
        card.addView(label(if (offline.isEmpty()) "No offline basemap loaded" else "Offline road linework · OpenStreetMap contributors",12f,muted))
        if (!standalone) {
            card.addView(label(if (latest == null) "Waiting for an eligible physical fix" else
                "Mint: last known unverified fix · rings: buffered reports",14f,muted))
            if (value.optBoolean("field_offline"))
                card.addView(label("Observation source unavailable. Rechecking while this screen is open.",14f,amber))
            button(card,"Refresh tracker observation") {
                if (value.has("official_installation")) loadOfficialObservation(value,replace=true)
                else loadDeviceObservation(value,replace=true)
            }
        }
        if (road == null && !plan.isNullOrEmpty())
            card.addView(label("Planned stops only. Calculate a road preview.",14f,muted))
        if (road != null) {
            card.addView(label(if (preview != null) "Road preview" else "Active local road",18f,mint,true))
            card.addView(label("${road.meters} m · ${road.seconds / 60} min estimated driving time · Passenger car",14f))
            card.addView(label("Valhalla ${road.engine} · ${road.snapshot}\nGraph ${road.graph}",12f,muted))
            road.stops.forEachIndexed { index, stop ->
                card.addView(label("Stop ${index+1}: ${stop.latitude}, ${stop.longitude}",12f,muted))
            }
            if (preview == null && latest != null && java.time.Instant.now().minusSeconds(900).isBefore(latest.measured)) {
                FieldRoads.deviation(latest,road)?.let { card.addView(label("Approximate distance to active road: ${it} m · advisory",14f,amber)) }
            }
        }
        if (key != null && plan != null) {
            button(card,"Edit local waypoint plan") { editLocalFieldPlan(key,plan.ifEmpty { active?.stops.orEmpty() }) }
            if (plan.size >= 2 && value.optString("permission") != "view") button(card,"Calculate road preview") { calculateFieldRoad(key,plan) }
            if (!savedRoads.isNullOrEmpty()) button(card,"Select saved local road") {
                val names = savedRoads.mapIndexed { index, saved ->
                    "${index+1} · ${saved.road.meters} m · ${saved.road.stops.size} stops · ${saved.road.snapshot}"
                }.toTypedArray()
                val epoch=fieldEpoch
                fieldDialog=AlertDialog.Builder(this).setTitle("Select active local road")
                    .setItems(names) { _, index ->
                        if (sessionStarted && fieldEpoch == epoch && TrackiumCoreAccess.authorized()) {
                            val selected=runCatching {
                                c.secure.write(key+"-road",FieldRoads.catalog(savedRoads,savedRoads[index].id))
                            }.isSuccess
                            if (selected) { roadPreview=null; render() }
                            else dialog("Local road unavailable","Could not update encrypted road storage.")
                        }
                    }.create().also { it.show() }
            }
            if (preview != null && savedRoads != null) {
                button(card,"Save and activate road locally") {
                    if (sessionStarted && TrackiumCoreAccess.authorized() && fieldKey(value) == key) {
                        val saved = runCatching {
                            val id=ProofProtocol.sha(preview.json().toString())
                            val rows=listOf(FieldRoads.Saved(id,preview))+savedRoads.filterNot{it.id==id}
                            check(rows.size <= 5)
                            c.secure.write(key+"-road",FieldRoads.catalog(rows,id))
                        }.isSuccess
                        if (saved) { roadPreview=null; render() }
                        else dialog("Local road unavailable","Could not save. Each local map has up to five saved routes.")
                    }
                }
                button(card,"Discard preview") { roadPreview=null; render() }
            }
        }
        button(card,"Configure independent road provider") { editFieldProvider() }
        button(card,"Import offline OSM road GeoJSON") {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*"),771)
        }
    }

    private fun calculateFieldRoad(key: String, stops: List<LocalFieldPlan.Stop>) {
        val provider = runCatching { FieldRoads.Provider.read(c.secure.read("field-router")) }.getOrNull()
        val current = detailValue
        val installation = current?.optString("official_installation")?.takeIf { it.isNotEmpty() }
        val origin = provider?.origin ?: installation?.let { OfficialDeviceAccess.origin }
        if (origin == null) {
            dialog("Road provider unavailable","Add an authorized Trackium tracker or configure an advanced road provider.")
            return
        }
        val epoch = fieldEpoch
        AlertDialog.Builder(this).setTitle("Calculate road preview")
            .setMessage("Send only these planned stops to $origin? Tracker locations and Minima data are kept local.")
            .setNegativeButton("Cancel",null).setPositiveButton("Calculate") { _,_ ->
                if (!sessionStarted || !TrackiumCoreAccess.authorized() || fieldEpoch != epoch) return@setPositiveButton
                roadPreview = null
                c.task {
                    val road = runCatching {
                        if (provider != null) FieldRoads.calculate(provider,stops)
                        else FieldRoads.calculateOfficial(requireNotNull(installation),stops)
                    }.getOrNull()
                    runOnUiThread {
                        if (sessionStarted && fieldEpoch == epoch && TrackiumCoreAccess.authorized() &&
                            current != null && detailValue?.let(::fieldKey) == key) {
                            if (road != null) roadPreview = key to road
                            else dialog("Road provider unavailable","The road service could not produce a route. Check your connection or try different stops.")
                        }
                    }
                    if (road == null) "Road provider unavailable." else "Road preview calculated."
                }
            }.create().also { fieldDialog=it; it.show() }
    }

    private fun editFieldProvider() {
        val existing = runCatching { c.secure.read("field-router") }.getOrElse { JSONObject() }
        val fields = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(18),0,dp(18),0) }
        fun field(key:String, name:String, secret:Boolean=false) = EditText(this).apply {
            hint=name; contentDescription=name
            inputType = android.text.InputType.TYPE_CLASS_TEXT or if (secret) android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD else 0
            if (!secret) setText(existing.optString(key))
            fields.addView(this)
        }
        val origin=field("origin","Provider HTTPS origin")
        val user=field("user","Provider username")
        val password=field("password","Provider password",true)
        val graph=field("graph","Pinned graph SHA-256")
        val dialog=AlertDialog.Builder(this).setTitle("Independent road provider").setView(fields)
            .setNegativeButton("Cancel",null).setPositiveButton("Save",null).create()
        fieldDialog=dialog
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!sessionStarted || !TrackiumCoreAccess.authorized()) { dialog.dismiss(); return@setOnClickListener }
            val saved=runCatching {
                val provider=FieldRoads.Provider.read(JSONObject().put("origin",origin.text.toString().trim())
                    .put("user",user.text.toString().trim()).put("password",password.text.toString())
                    .put("graph",graph.text.toString().trim()))
                c.secure.write("field-router",provider.json())
            }.isSuccess
            if (!saved) origin.error="Invalid configuration or encrypted storage unavailable."
            else { roadPreview=null; dialog.dismiss(); render() }
        }
    }

    @Deprecated("Uses the existing native Activity callback for the document picker")
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        val scan = com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if (scan != null) {
            if (TrackiumCoreAccess.authorized()) {
                if (sessionStarted) scan.contents?.let(::confirmOfficialDevice)
                else pendingDeviceScan = scan.contents
            }
            return
        }
        val uri=data?.data ?: return
        if (requestCode != 771 || resultCode != RESULT_OK || !TrackiumCoreAccess.authorized()) return
        c.task {
            val document=contentResolver.openInputStream(uri)?.use { FieldRoads.bounded(it).toString(Charsets.UTF_8) }
                ?: error("Offline basemap unavailable.")
            val parsed=JSONObject(document)
            val roads=FieldRoads.offlineRoads(parsed)
            c.secure.write("field-basemap",parsed)
            runOnUiThread { offlineLinework=roads }
            "Offline road linework imported."
        }
    }

    private fun editLocalFieldPlan(key: String, current: List<LocalFieldPlan.Stop>) {
        val editor = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            filters = arrayOf(android.text.InputFilter.LengthFilter(512))
            minLines = 4
            setText(LocalFieldPlan.text(current))
            contentDescription = "Coordinates in travel order"
        }
        val dialog = AlertDialog.Builder(this).setTitle("Edit local waypoint plan")
            .setMessage("Enter latitude, longitude on each line in travel order. This saves planned stops on this device.")
            .setView(editor).setNegativeButton("Cancel",null).setPositiveButton("Save",null).create()
        fieldDialog=dialog
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!sessionStarted || !TrackiumCoreAccess.authorized()) {
                dialog.dismiss()
                return@setOnClickListener
            }
            val stops = try { LocalFieldPlan.parse(editor.text.toString()) }
            catch (error: IllegalArgumentException) {
                editor.error = error.message
                return@setOnClickListener
            }
            try { LocalFieldPlan.save(c.secure,key,stops) }
            catch (_: Exception) {
                editor.error = "Could not write the encrypted waypoint plan."
                return@setOnClickListener
            }
            roadPreview=null
            dialog.dismiss()
            render()
        }
    }

    internal fun renderEvidenceSections(parent: LinearLayout, title: String, value: JSONObject) {
        for (section in CargoPresentation.sections(title, value)) {
            if (section.rows.isEmpty()) continue
            val card = box(parent, section.title)
            for ((name, reported) in section.rows) {
                card.addView(label(name, 12f, muted))
                card.addView(label(reported, 16f))
            }
        }
    }

    private fun async(title: String, call: () -> JSONObject) {
        c.task {
            val r = call()
            runOnUiThread {
                if (sessionStarted && TrackiumCoreAccess.authorized()) detail(title, r)
            }
            "$title observed now. Check timestamps and provenance."
        }
    }

    private fun render() {
        currentScroll?.let { v -> renderedKey?.let { scrollPositions[it]=v.scrollY } }
        val focused=currentFocus as? EditText
        if(focused!=null && focused.id in listOf(R.id.tc_search_shipments,R.id.tc_search_devices,R.id.tc_gateway_origin,R.id.tc_gateway_tenant)) {focusId=focused.id;cursor=focused.selectionStart}
        if (!TrackiumCoreAccess.authorized()) {
            setContentView(TextView(this).apply { text = "Unlock Trackium Core to continue" })
            return
        }
        try {
            renderSafe()
        } catch (_: Exception) {
            c.lifecycle(false)
            setContentView(
                LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(bg)
                    setPadding(dp(24), dp(32), dp(24), dp(24))
                    addView(label("Recovery review required", 25f, amber, true))
                    addView(
                        label(
                            "Encrypted local state is unavailable. Wallet actions are disabled. Preserve Gateway checkpoints and Core backups; do not clear/reinstall to bypass an unknown transaction. Restore trusted storage/session with the operator.",
                            16f,
                        )
                    )
                }
            )
        }
    }

    private fun renderSafe() {
        shell =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(bg)
                setPadding(dp(16), dp(16), dp(16), dp(8))
            }
        setContentView(shell)
        shell.setOnApplyWindowInsetsListener { v, insets ->
            v.setPadding(
                dp(16),
                dp(16) + insets.systemWindowInsetTop,
                dp(16),
                dp(8) + insets.systemWindowInsetBottom,
            )
            insets
        }
        shell.requestApplyInsets()
        shell.addView(label("Trackium Core · Trackium", 20f, text, true))
        val sections = listOf("Dashboard", "Shipments", "Devices", "Proofs", "Settings")
        val section = Spinner(this).apply {
            contentDescription = "Trackium section"
            minimumHeight = dp(48)
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, sections)
            setSelection(sections.indexOf(tab))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    if (sections[position] != tab) { currentScroll?.let { scrollPositions[tab]=it.scrollY };focusId=0;detailTitle=null;detailValue=null;currentScroll=null;tab = sections[position]; render() }
                }
            }
        }
        shell.addView(section)
        loading=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply {isIndeterminate=true;visibility=if(c.busy.get())android.view.View.VISIBLE else android.view.View.GONE;contentDescription="Loading authorized observation"}
        shell.addView(loading,LinearLayout.LayoutParams(-1,dp(4)))
        val scroll = ScrollView(this)
        currentScroll = scroll
        scroll.isSaveEnabled = false
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        // Status text scales and scrolls with content instead of squeezing navigation at 200%.
        content.addView(label(c.coreState, 12f, muted))
        content.addView(label(c.message, 14f, if (c.data == null) amber else muted).apply {accessibilityLiveRegion=android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE})
        scroll.addView(content)
        shell.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        if(detailValue!=null)renderDetail(detailValue!!) else when (tab) {
            "Dashboard" -> dashboard()
            "Shipments" -> shipments()
            "Devices" -> devices()
            "Proofs" -> proofs()
            "Settings" -> settings()
        }
        CoreShellNavigation.create(this)?.let { shell.addView(it) }
        renderedKey=if(detailValue==null)tab else "$tab/detail/$detailTitle"
        val y=scrollPositions[renderedKey ?: tab] ?: 0
        scroll.post { scroll.scrollTo(0,y)
            if(focusId!=0) (findViewById<android.view.View>(focusId) as? EditText)?.let { it.requestFocus();it.setSelection(cursor.coerceIn(0,it.text.length)) }
        }
    }

    @Deprecated("Uses the existing Activity back stack")
    override fun onBackPressed() {
        if(detailValue!=null){detailValue=null;detailTitle=null;currentScroll=null;render()}
        else if (tab != "Dashboard") { currentScroll=null;tab = "Dashboard";focusId=0;render() } else super.onBackPressed()
    }

    private fun freshness(parent: LinearLayout) {
        parent.addView(
            label(
                if (c.updated == 0L)
                    "No authenticated Gateway data loaded. Demo fixtures are not used."
                else
                    "Gateway observation: " +
                        DateFormat.getDateTimeInstance().format(Date(c.updated)) +
                        " · first 25 shipments/devices",
                12f,
                muted,
            )
        )
        button(parent, "Refresh Gateway", secureReady()) { c.refresh() }
    }

    private fun secureReady() = c.secure.config().has("session")

    private fun dashboard() {
        val profile=c.secure.config()
        val connection=box(content,"Gateway workspace")
        connection.addView(label(CargoPresentation.session(profile,c.data!=null,c.updated),18f,if(c.data!=null)mint else amber,true))
        connection.addView(label("Origin · ${CargoPresentation.value(profile,"gateway")}\nTenant · ${CargoPresentation.value(profile,"tenant")}",13f,muted))
        freshness(connection)
        val data=c.data
        if(data==null){box(content,"Your workspace is waiting").addView(label("No authenticated cargo observation is available. Open Settings to pair or restore the session. Missing data is not zero activity.",16f,muted))}
        else {
            val summary=box(content,"Loaded workspace · limited snapshot")
            summary.addView(label("${data.optJSONArray("shipments")?.length() ?: 0} shipments  ·  ${data.optJSONArray("devices")?.length() ?: 0} devices",22f,mint,true))
            summary.addView(label("${data.optJSONArray("proofs")?.length() ?: 0} proof records\nFirst 25 shipments/devices; this is not a tenant-wide total.",14f,muted))
            val recent=box(content,"Recent available proof events")
            val records=CargoPresentation.records(data.optJSONArray("proofs")).take(5)
            if(records.isEmpty())recent.addView(label("No proof events in the loaded snapshot.",14f,muted))
            for(proof in records){val event=proof.optJSONObject("commitment");recent.addView(label("${CargoPresentation.value(event,"event_type")} · ${CargoPresentation.proofState(proof)}",14f));recent.addView(label("Trust · ${CargoPresentation.value(event,"source_trust")}\n${CargoPresentation.value(proof,"last_checked_at")}",12f,muted))}
        }
        val safety=box(content,"Core & evidence")
        safety.addView(label("Chain inclusion verifies recorded bytes, not physical sensor authenticity. Connected peers do not establish full synchronization.",14f,muted))
        button(safety,"Observe embedded node") { c.connectCore() }
        button(safety,"Observe Core wallet & synchronization",c.core!=null){c.walletOverview()}
        c.wallet?.let { wallet ->
            safety.addView(label("Core ${CargoPresentation.value(wallet,"version")} · ${CargoPresentation.value(wallet,"peers")} peers · height ${CargoPresentation.value(wallet,"block")}",14f))
            button(safety,"Inspect Core-reported wallet balance"){detail("Core wallet observation",wallet)}
        }
        button(safety,"Return to Core"){finish()}
    }

    private fun browser(kind: String, array: JSONArray?, populate: (LinearLayout,JSONObject)->Unit) {
        freshness(content)
        val controls=box(content,"Find $kind · loaded snapshot")
        val search=input(controls,"Search name / ID",searches[kind] ?: "")
        search.id=if(kind=="Shipments")R.id.tc_search_shipments else R.id.tc_search_devices
        val choices=listOf("All")+CargoPresentation.records(array).flatMap { row -> listOf("status","source_trust","mode","identity_kind").mapNotNull { key -> row.optString(key).takeIf { it.isNotBlank() && it!="null" } } }.distinct().sorted()
        val filter=Spinner(this).apply {minimumHeight=dp(48);contentDescription="$kind filter";adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,choices);setSelection(choices.indexOf(filters[kind]).coerceAtLeast(0))}
        controls.addView(filter)
        val list=LinearLayout(this).apply {orientation=LinearLayout.VERTICAL};content.addView(list)
        fun update(){list.removeAllViews();val rows=CargoPresentation.matching(array,searches[kind] ?: "",filters[kind] ?: "All")
            if(rows.isEmpty())box(list,"No matching $kind").addView(label(if(array==null)"Pair and refresh to load authorized data." else "No records match the loaded snapshot and these filters.",14f,muted))
            for(row in rows)populate(list,row)
        }
        search.addTextChangedListener(watch{searches[kind]=it;update()})
        filter.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent:AdapterView<*>?)=Unit
            override fun onItemSelected(parent:AdapterView<*>?,view:android.view.View?,position:Int,id:Long){filters[kind]=choices[position];update()}
        };update()
    }

    private fun shipments() = browser("Shipments",c.data?.optJSONArray("shipments")) { parent,s ->
        val card=box(parent,s.optString("name").ifBlank{"Shipment"})
        card.addView(label("${CargoPresentation.value(s,"status")} · ${CargoPresentation.value(s,"source_trust")}",16f,mint,true))
        card.addView(label("${CargoPresentation.value(s,"id")}\nDevice · ${CargoPresentation.value(s,"device_id")}",12f,muted))
        button(card,"Open shipment details"){
            val id=android.net.Uri.encode(s.getString("id"))
            async("Shipment · ${s.optString("name")}"){
                val shipment=c.gateway.read("/v1/shipments/$id")
                shipment.put("events",c.gateway.read("/v1/shipments/$id/events?limit=25").optJSONArray("events") ?: JSONArray())
                shipment.put("proofs",JSONArray(CargoPresentation.records(c.data?.optJSONArray("proofs")).filter{it.optString("shipment_id")==s.getString("id")}))
                val device=shipment.optString("device_id")
                if(device.isNotBlank()){
                    val d=android.net.Uri.encode(device)
                    try {shipment.put("device_observation",c.gateway.read("/v1/devices/$d/status"))
                        shipment.put("telemetry",c.gateway.read("/v1/devices/$d/events?limit=10"))
                    }catch(_:Exception){shipment.put("partial_observation","Linked device observation unavailable; shipment evidence remains visible")}
                }
                shipment
            }
        }
    }

    private fun devices() {
        val access = box(content,"My devices")
        button(access,"Add Device") {
            com.google.zxing.integration.android.IntentIntegrator(this).apply {
                setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE)
                setPrompt("Scan your Trackium activation or connection QR")
                setBeepEnabled(false)
                setOrientationLocked(false)
                initiateScan()
            }
        }
        button(access,"Enter activation card or connection link") { enterOfficialDevice() }
        button(access,"Refresh my devices") {
            c.task {
                val result = runCatching { OfficialDeviceAccess.request("devices").getJSONArray("devices") }.getOrNull()
                runOnUiThread { officialDevices = result }
                if (result == null) "Trackium devices unavailable." else "Trackium devices updated."
            }
        }
        for (device in CargoPresentation.records(officialDevices)) {
            val card = box(content,device.getString("label"))
            card.addView(label(device.getString("serial"),14f,muted))
            button(card,"Open device observation") { loadOfficialObservation(device) }
        }
        browser("Devices",c.data?.optJSONArray("devices")) { parent,d ->
            val card=box(parent,d.optString("label").ifBlank{"Device"})
            card.addView(label("${CargoPresentation.value(d,"identity_kind")} · ${CargoPresentation.value(d,"mode")}",16f,mint,true))
            card.addView(label(CargoPresentation.value(d,"id"),12f,muted))
            button(card,"Open device observation"){ loadDeviceObservation(d) }
        }
    }

    private fun enterOfficialDevice() {
        val fields = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val serial = input(fields,"Serial number (activation card)")
        val secret = input(fields,"One-time code or connection link").apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        AlertDialog.Builder(this).setTitle("Add Device").setView(fields).setNegativeButton("Cancel",null)
            .setPositiveButton("Continue") { _,_ ->
                val value = secret.text.toString().trim()
                val id = serial.text.toString().trim().uppercase(java.util.Locale.ROOT)
                secret.text.clear()
                confirmOfficialDevice(if (id.isEmpty()) value else "trackium-activation:v1:$id:$value")
            }.show()
    }

    private fun confirmOfficialDevice(value: String) {
        val parsed = OfficialDeviceAccess.card(value)
        if (parsed == null) {
            dialog("Invalid device card","Scan a Trackium activation card or use an owner-issued connection link.")
            return
        }
        val (operation,payload) = parsed
        val confirmation = if (operation == "claim") "Activate this tracker for this installation?"
                else "Request access for this installation? The owner must confirm your fingerprint."
        AlertDialog.Builder(this).setTitle("Add Device").setMessage(confirmation)
            .setNegativeButton("Cancel",null).setPositiveButton("Continue") { _,_ ->
                if (!sessionStarted || !TrackiumCoreAccess.authorized()) return@setPositiveButton
                c.task {
                    val result = runCatching { OfficialDeviceAccess.request(operation,payload) }.getOrNull()
                    val devices = if (result != null && operation == "claim")
                        runCatching { OfficialDeviceAccess.request("devices").getJSONArray("devices") }.getOrNull() else null
                    runOnUiThread {
                        if (sessionStarted && TrackiumCoreAccess.authorized() && result != null) {
                            if (devices != null) officialDevices = devices
                            if (operation == "connect") dialog("Owner confirmation required",result.getString("fingerprint"))
                        }
                    }
                    if (result == null) "Device access unavailable. Check the code or refresh My devices."
                    else if (operation == "claim") "Device activated. Waiting for GPS." else "Waiting for owner confirmation."
                }
            }.show()
    }

    private fun loadOfficialObservation(device: JSONObject, replace: Boolean = false) {
        val installation = device.optString("official_installation").ifBlank { device.getString("id") }
        val id = device.getString("device_id")
        val epoch = fieldEpoch
        c.task {
            val record = JSONObject().put("id",id).put("device_id",id).put("label",device.getString("label"))
                .put("official_installation",installation).put("permission",device.getString("permission"))
                .put("identity_kind","physical").put("enabled",device.optBoolean("enabled",true))
            val result = runCatching {
                OfficialDeviceAccess.request("observations",JSONObject().put("installation_id",installation))
            }.getOrNull()
            if (result != null) {
                record.put("status",JSONObject().put("enabled",true))
                record.put("location",JSONObject().put("device_id",id).put("location",result.opt("location")))
                record.put("history",JSONObject().put("events",result.getJSONArray("history")))
            } else record.put("field_offline",true)
            runOnUiThread {
                if (sessionStarted && TrackiumCoreAccess.authorized() && epoch == fieldEpoch && tab == "Devices" &&
                    (!replace || detailValue?.optString("official_installation") == installation)) {
                    if (replace) { detailValue = record; render() }
                    else detail("Device · ${device.getString("label")}",record)
                }
            }
            if (result == null) "Tracker observations unavailable." else "Tracker observation checked."
        }
    }

    private fun loadDeviceObservation(device: JSONObject, replace: Boolean = false) {
        val deviceId = device.getString("id")
        val id = android.net.Uri.encode(deviceId)
        val origin = runCatching { c.gateway.origin }.getOrNull() ?: return
        val tenant = runCatching { c.gateway.tenant }.getOrNull() ?: return
        val epoch = fieldEpoch
        c.task {
            val registry = JSONObject()
            for (key in listOf("id","label","identity_kind","mode","enabled")) {
                if (device.has(key)) registry.put(key,device.get(key))
            }
            val result = runCatching {
                registry.put("status",c.gateway.read("/v1/devices/$id/status"))
                registry.put("location",c.gateway.read("/v1/devices/$id/location"))
                registry.put("history",c.gateway.read("/v1/devices/$id/events?limit=20"))
            }.getOrElse {
                registry.remove("status"); registry.remove("location"); registry.remove("history")
                registry.put("field_offline",true)
            }
            runOnUiThread {
                if (sessionStarted && TrackiumCoreAccess.authorized() && epoch == fieldEpoch &&
                    tab == "Devices" && runCatching { c.gateway.origin == origin && c.gateway.tenant == tenant }.getOrDefault(false) &&
                    (!replace || detailValue?.optString("id") == deviceId)) {
                    if (replace) { detailValue=result; render() }
                    else detail("Device · ${device.optString("label")}",result)
                }
            }
            if (result.optBoolean("field_offline")) "Tracker observation unavailable." else "Tracker observation checked."
        }
    }

    private fun proofs() {
        freshness(content)
        val controls = box(content, "Proof evidence · publication unavailable")
        controls.addView(
            label(
                "Embedded Core · read-only prototype. Publication is unavailable; private keys and key-use counters belong only to the node.",
                14f,
                muted,
            )
        )
        val config = c.secure.config()
        controls.addView(
            label(
                "Local live: ${config.optBoolean("live")}  /  Mainnet: ${config.optBoolean("mainnet")}\nLocal reservation limit: ${config.optInt("local_limit",1)}",
                14f,
                amber,
            )
        )
        val p = c.data?.optJSONObject("publisher")
        val pending = p?.optJSONArray("pending_events") ?: JSONArray()
        if (pending.length() > 0) {
            val waiting = box(content, "Awaiting Gateway owner approval")
            for (i in 0 until minOf(pending.length(), 5)) {
                val e = pending.getJSONObject(i)
                waiting.addView(
                    label(
                        "${e.getString("event_type")} · ${e.getString("source_trust")}\n${e.getString("id")}",
                        12f,
                        muted,
                    )
                )
            }
            waiting.addView(
                label(
                    "Owner approves one event on the trusted Gateway host. Native sessions cannot approve events.",
                    12f,
                    amber,
                )
            )
        }
        val list = c.data?.optJSONArray("proofs") ?: JSONArray()
        if(list.length()==0)box(content,"No proof records").addView(label("Refresh an authorized session. Pending and submitted records are not independently verified.",14f,muted))
        proofPage=proofPage.coerceIn(0,((list.length()-1).coerceAtLeast(0)/20))
        val pages=box(content,"Loaded proof records · page ${proofPage+1}")
        pages.addView(label("${list.length()} records in this snapshot; up to 20 on this page.",14f,muted))
        if(proofPage>0)button(pages,"Previous proofs"){proofPage--;currentScroll=null;scrollPositions["Proofs"]=0;render()}
        if((proofPage+1)*20<list.length())button(pages,"Next proofs"){proofPage++;currentScroll=null;scrollPositions["Proofs"]=0;render()}
        for (i in proofPage*20 until minOf(list.length(),(proofPage+1)*20)) {
            val proof = list.getJSONObject(i)
            val card = box(content, proof.getJSONObject("commitment").getString("event_type"))
            card.addView(
                label(
                    proof.getJSONObject("commitment").getString("source_trust") +
                        "  ·  " +
                        proof.getString("mode"),
                    14f,
                    amber,
                    true,
                )
            )
            card.addView(
                label(
                    "${CargoPresentation.proofState(proof)}\nReported depth ${CargoPresentation.value(proof,"confirmations")}/${CargoPresentation.value(proof,"required_depth")}\nChecked: ${proof.optString("last_checked_at","never")}",
                    13f,
                )
            )
            card.addView(
                label(
                    if (proof.isNull("txpow_id")) "No blockchain TXID"
                    else proof.getString("txpow_id"),
                    11f,
                    muted,
                )
            )
            button(card,"Open evidence details"){detail("Proof evidence",proof)}
            val id = proof.getString("id")
            button(card, "Verify with independent Gateway reader") {
                async("Fresh verification") { c.gateway.verify(id) }
            }
            button(card, "Export proof bundle") {
                c.task {
                    val bundle = c.gateway.bundle(id)
                    runOnUiThread { share(bundle) }
                    "Export contains cargo data; share only with authorized verifiers."
                }
            }
            if (
                proof.optString("publisher_transport") == "android_minimaapi" &&
                    proof.getString("mode") == "live"
            )
                button(card, "Review approved Native Core job") {
                    c.loadJob(id) { packet -> jobDialog(packet) }
                }
        }
        val saved = config.optJSONArray("proof_ids") ?: JSONArray()
        if (saved.length() > 0) {
            val recovery = box(content, "Device recovery checkpoints")
            for (i in 0 until saved.length()) {
                val id = saved.getString(i)
                val s = c.store.load(id) ?: continue
                recovery.addView(label(s.optString("phase") + " · " + id, 12f, muted))
                if (s.has("job"))
                    button(recovery, "Review saved job ${id.take(8)}") {
                        jobDialog(s.getJSONObject("job"))
                    }
            }
        }
    }

    private fun share(value: JSONObject) {
        val id = "trackium-proof-${System.currentTimeMillis()}.json"
        val file = java.io.File(cacheDir, id)
        file.writeText(value.toString(2))
        val uri = android.net.Uri.parse("content://$packageName.trackium.exports/$id")
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND)
                    .setType("application/json")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                "Share sensitive proof bundle",
            )
        )
    }

    private fun jobDialog(packet: JSONObject) {
        try {
            val job = ProofProtocol.job(packet, c.secure.config().getString("gateway_pin"))
            val parent =
                LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(16), dp(8), dp(16), dp(8))
                    setBackgroundColor(panel)
                }
            val view = ScrollView(this).apply { addView(parent) }
            parent.addView(
                label(
                    "Mainnet · ${job.proof.getJSONObject("commitment").getString("source_trust")}",
                    16f,
                    amber,
                    true,
                )
            )
            parent.addView(
                label(
                    "Event: ${job.event}\nShipment: ${job.proof.getString("shipment_id")}\nWallet: ${job.address}\nInput cap: ${job.maxInput} MIN\n0.001 self-transfer, zero requested burn. PoW/signing-key uses and temporary input locking apply.",
                    13f,
                )
            )
            parent.addView(label("STATE: ${job.wire}", 11f, muted))
            button(parent, "Check Core wallet & Mainnet readiness") {
                c.readiness(packet) { r -> detail("Core readiness observation", r) }
            }
            val agree =
                CheckBox(this).apply {
                    text =
                        "I checked Core sync, wallet, provenance and input cap; all enabled Core apps are trusted and no wallet clone is running. I authorize this Mainnet event."
                    setTextColor(textColor())
                    parent.addView(this)
                }
            val typed = input(parent, "Type PUBLISH MAINNET ${job.event}")
            AlertDialog.Builder(this)
                .setTitle("Approve this immutable event")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Publish / reconcile") { _, _ ->
                    if (!agree.isChecked)
                        dialog("Approval required", "Explicit Mainnet consent is required.")
                    else c.publish(packet, typed.text.toString().trim())
                }
                .show()
        } catch (e: Exception) {
            dialog("Job unavailable", NativePublisher.safeError(e))
        }
    }

    private fun textColor() = text

    private fun settings() {
        button(content,"Open independent field map") {
            detail("Field map",JSONObject().put("local_field_map",true))
        }
        val core = box(content, "embedded Core · your built-in wallet")
        core.addView(label(c.coreState, 14f, muted))
        core.addView(
            label(
                "In-process Pandamonium DirectNodeApi adapter. Only exact status/balance/network/peers/block reads are allowed. No external APK discovery, wallet write, seed import or WebView bridge.",
                12f,
            )
        )
        button(core, "Observe embedded node") { c.connectCore() }
        button(core, "Return to node settings") { finish() }
        val config = c.secure.config()
        val state=box(content,"Gateway session")
        state.addView(label(CargoPresentation.session(config,c.data!=null,c.updated),16f,amber,true))
        val session=config.optJSONObject("session")
        state.addView(label("Expires · ${CargoPresentation.value(session,"expires_at")}\nScopes · ${(session?.optJSONArray("scopes")?.let { (0 until it.length()).map { i -> it.optString(i) }.joinToString(", ") }) ?: "Not reported"}",14f,muted))
        val gateway = box(content, "1 · Connection profile")
        val url =
            input(gateway, "HTTPS Gateway origin", draftGateway ?: config.optString("gateway"))
        val tenant =
            input(gateway, "Tenant UUID (public)", draftTenant ?: config.optString("tenant"))
        url.id=R.id.tc_gateway_origin;tenant.id=R.id.tc_gateway_tenant
        url.addTextChangedListener(watch { draftGateway = it })
        tenant.addTextChangedListener(watch { draftTenant = it })
        button(gateway, "Save connection") {
            val u = url.text.toString()
            val t = tenant.text.toString()
            c.task {
                val base = NativeAuth.origin(u)
                require(Regex("[0-9a-f-]{36}").matches(t))
                c.secure.updateConfig {
                    if (it.optString("gateway") != base || it.optString("tenant") != t) {
                        c.clearGatewayObservation()
                        it.remove("session")
                        it.remove("challenge")
                        it.remove("gateway_pin")
                    }
                    it.put("gateway", base).put("tenant", t)
                }
                "Gateway profile saved. No owner key or browser cookie imported."
            }
        }
        val pairing=box(content,"2 · Request owner approval")
        pairing.addView(label("The trusted owner approves this device on the Gateway host. This app cannot approve itself or import an owner credential.",14f,muted))
        button(pairing, "Create owner approval challenge") {
            c.task {
                val r = c.gateway.challenge()
                "Owner approval required: ${r.getString("id")} · expires ${r.getString("expires_at")}. Use the native-approve CLI on the trusted Gateway host."
            }
        }
        val challenge = config.optJSONObject("challenge")
        if (challenge != null)
            pairing.addView(
                label(
                    "Challenge UUID:\n${challenge.getString("id")}\nApprove on Gateway with read/publish scope. Device proof stays in Android Keystore-protected storage.",
                    12f,
                    amber,
                )
            )
        pairing.addView(label("Challenge expiry · ${CargoPresentation.value(challenge,"expires_at")}",12f,muted))
        val connected=box(content,"3 · Claim, refresh or revoke")
        button(connected, "Complete owner pairing") {
            c.task {
                val s = c.gateway.claim()
                "Native read/publish session active until ${s.getString("expires_at")}. Refresh data."
            }
        }
        button(connected, "Refresh authenticated data", secureReady()) { c.refresh() }
        button(connected, "Disconnect / revoke session", secureReady()) {
            c.task {
                c.clearGatewayObservation()
                c.gateway.disconnect()
                "Native session revoked and removed from device."
            }
        }
        val issuer = c.data?.optJSONObject("publisher")?.optString("signer_fingerprint")
        gateway.addView(
            label("Job signer pin: ${config.optString("gateway_pin","not pinned")}", 11f, muted)
        )
        if (!issuer.isNullOrBlank() && issuer != "null")
            button(gateway, "Verify & pin Gateway issuer") {
                AlertDialog.Builder(this)
                    .setTitle("Verify through the trusted owner channel")
                    .setMessage(
                        "Fingerprint:\n$issuer\n\nConfirm it independently. Unknown job files cannot select their own trust key."
                    )
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Pin verified issuer") { _, _ ->
                        c.task {
                            c.secure.updateConfig { it.put("gateway_pin", issuer) }
                            "Gateway issuer pinned. Key rotation requires explicit owner review."
                        }
                    }
                    .show()
            }
        val safety = box(content, "Wallet publication unavailable · prototype #1")
        for ((key, title) in
            listOf(
                "live" to "Allow live wallet preparation",
                "mainnet" to "Allow Mainnet publication",
                "exclusive_core" to "Only trusted Core apps (including READ); one wallet copy",
            )) {
            val check =
                CheckBox(this).apply {
                    text = title
                    setTextColor(textColor())
                    isChecked = config.optBoolean(key)
                }
            check.isEnabled = false
            safety.addView(check)
            check.setOnCheckedChangeListener { _, value ->
                c.task {
                    c.secure.updateConfig { it.put(key, value) }
                    "$title = $value. Each event still needs typed confirmation."
                }
            }
        }
        val cap =
            input(
                safety,
                "Local lifetime reservation cap (first transaction: 1)",
                config.optInt("local_limit", 1).toString(),
            )
        cap.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        button(safety, "Set explicit local cap") {
            val value = cap.text.toString()
            c.task {
                val n = value.toInt()
                require(n in 1..100)
                c.secure.updateConfig { it.put("local_limit", n) }
                "Local cap set to $n; Gateway limits and owner event approval also apply."
            }
        }
        safety.addView(
            label(
                "No automatic backlog or background publishing. In-flight commands accepted by Core may finish after the app closes. Keep one copy of this Core wallet running; Trackium never manages one-time indices.",
                12f,
                amber,
            )
        )
    }

    private fun watch(change: (String) -> Unit) =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                change(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
}
