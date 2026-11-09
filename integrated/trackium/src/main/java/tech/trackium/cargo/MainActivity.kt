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

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        tab = state?.getString("tab")?.takeIf { it in listOf("Dashboard", "Shipments", "Devices", "Proofs", "Settings") } ?: "Dashboard"
        for (key in listOf("Shipments", "Devices")) {
            searches[key] = state?.getString("query.$key") ?: ""
            filters[key] = state?.getString("filter.$key") ?: "All"
        }
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
        for((key,y) in scrollPositions)out.putInt("scroll.$key",y)
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
        c.lifecycle(true)
        c.listener = {
            if(c.busy.get()) {
                loading?.visibility=android.view.View.VISIBLE
                fun disable(view:android.view.View){if(view is Button)view.isEnabled=false;if(view is android.view.ViewGroup)for(i in 0 until view.childCount)disable(view.getChildAt(i))}
                if(::shell.isInitialized)disable(shell)
            } else render()
        }
        render()
        if (c.core == null && !c.busy.get()) c.connectCore()
    }

    override fun onPause() {
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
        detailTitle = title
        detailValue = value
        render()
    }

    private fun renderDetail(value: JSONObject) {
        if(value.has("partial_observation"))content.addView(label("Partial observation · ${CargoPresentation.value(value,"partial_observation")}",16f,amber,true))
        button(content,"Back to $tab") { detailTitle=null;detailValue=null;currentScroll=null;render() }
        for(section in CargoPresentation.sections(detailTitle ?: "Details",value)) {
            if(section.rows.isEmpty())continue
            val card=box(content,section.title)
            for((name,v) in section.rows) {card.addView(label(name,12f,muted));card.addView(label(v,16f))}
        }
        content.addView(label("Reported evidence only. Read measurement/check timestamps and source trust; a recent fetch does not make an old sensor sample fresh.",14f,muted))
        val advanced=box(content,"Advanced / export")
        advanced.addView(label("This evidence may contain sensitive cargo data. Share only with authorized recipients.",14f,muted))
        button(advanced,"Advanced Raw Data") { dialog("Advanced Raw Data",value.toString(2)) }
        button(advanced,"Export reported data") { share(value) }
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
        if(focused!=null && focused.id in 20101..20104) {focusId=focused.id;cursor=focused.selectionStart}
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
        search.id=if(kind=="Shipments")20101 else 20102
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

    private fun devices() = browser("Devices",c.data?.optJSONArray("devices")) { parent,d ->
        val card=box(parent,d.optString("label").ifBlank{"Device"})
        card.addView(label("${CargoPresentation.value(d,"identity_kind")} · ${CargoPresentation.value(d,"mode")}",16f,mint,true))
        card.addView(label(CargoPresentation.value(d,"id"),12f,muted))
        button(card,"Open device observation"){
            val id=android.net.Uri.encode(d.getString("id"))
            async("Device · ${d.optString("label")}"){
                val observation=JSONObject(d.toString()).put("status",c.gateway.read("/v1/devices/$id/status"))
                val missing=mutableListOf<String>()
                try{observation.put("location",c.gateway.read("/v1/devices/$id/location"))}catch(_:Exception){missing.add("Location")}
                try{observation.put("history",c.gateway.read("/v1/devices/$id/events?limit=10"))}catch(_:Exception){missing.add("Telemetry history")}
                if(missing.isNotEmpty())observation.put("partial_observation",missing.joinToString(", ")+" unavailable; missing readings are not zeros")
                observation
            }
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
        url.id=20103;tenant.id=20104
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
