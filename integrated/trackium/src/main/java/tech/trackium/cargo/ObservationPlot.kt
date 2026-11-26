package tech.trackium.cargo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.DashPathEffect
import android.view.View
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.GestureDetector
import kotlin.math.cos

/** Native offline linework and route geometry. Gaps between observations are left undrawn. */
internal class ObservationPlot(
    context: Context,
    latest: PhysicalPositions.Fix?,
    private val history: List<PhysicalPositions.Fix>,
    private val plan: List<LocalFieldPlan.Stop>,
    private val road: FieldRoads.Road?,
    private val offline: List<List<LocalFieldPlan.Stop>>,
    private val viewport: Viewport,
    private val planColor: Int,
    private val accent: Int,
    private val secondary: Int
) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private fun stop(fix: PhysicalPositions.Fix) = LocalFieldPlan.Stop(fix.latitude,fix.longitude)
    private val latest = latest
    data class Viewport(var zoom: Float=1f,var x: Float=0f,var y: Float=0f,var fitRoute: Boolean=false)
    private val all = plan + (history + listOfNotNull(latest)).map(::stop) +
        road?.legs.orEmpty().flatten()
    private val anchorPoints = all.ifEmpty { offline.flatten() }
    private val fitPoints get() = if (viewport.fitRoute && road != null) road.legs.flatten() else anchorPoints
    private val scaleGesture = ScaleGestureDetector(context,object:ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            zoom(detector.scaleFactor)
            return true
        }
    })
    private val dragGesture = GestureDetector(context,object:GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e:MotionEvent) = true
        override fun onScroll(first:MotionEvent?, current:MotionEvent, dx:Float, dy:Float): Boolean {
            if (!scaleGesture.isInProgress) {
                viewport.x -= dx; viewport.y -= dy
                invalidate()
            }
            return true
        }
    })
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) parent.requestDisallowInterceptTouchEvent(true)
        scaleGesture.onTouchEvent(event)
        dragGesture.onTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL)
            parent.requestDisallowInterceptTouchEvent(false)
        return true
    }
    fun zoom(factor: Float) {
        viewport.zoom = (viewport.zoom*factor).coerceIn(.25f,32f)
        invalidate()
    }
    fun fit(route: Boolean) {
        viewport.zoom=1f; viewport.x=0f; viewport.y=0f; viewport.fitRoute=route
        invalidate()
    }
    private val anchor = latest?.let(::stop) ?: anchorPoints.first()
    private val scale = cos(Math.toRadians(anchor.latitude)).coerceAtLeast(.05)
    private fun x(stop: LocalFieldPlan.Stop) = (((stop.longitude-anchor.longitude+540) % 360)-180) * scale

    init {
        contentDescription = "Local plan: ${plan.size} stops. Physical GPS observations: ${history.size} history points. " +
            if (latest == null) "Last known position unavailable." else
                "Last known fix ${latest.latitude}, ${latest.longitude} at ${latest.measured}."
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        val padding = 16 * density
        val points = fitPoints
        val minX = points.minOf(::x)
        val maxX = points.maxOf(::x)
        val minY = points.minOf { it.latitude }
        val maxY = points.maxOf { it.latitude }
        val unit = minOf((width-2*padding).coerceAtLeast(1f)/(maxX-minX).coerceAtLeast(.0003),
            (height-2*padding).coerceAtLeast(1f)/(maxY-minY).coerceAtLeast(.0003))*viewport.zoom
        fun plotX(stop: LocalFieldPlan.Stop) = (width/2 + viewport.x + (x(stop)-(minX+maxX)/2)*unit).toFloat()
        fun plotY(stop: LocalFieldPlan.Stop) = (height/2 + viewport.y - (stop.latitude-(minY+maxY)/2)*unit).toFloat()
        fun line(points: List<LocalFieldPlan.Stop>) {
            for (i in 1 until points.size) {
                val a = points[i-1]; val b = points[i]
                if (kotlin.math.abs(x(a)-x(b)) < 180*scale)
                    canvas.drawLine(plotX(a),plotY(a),plotX(b),plotY(b),paint)
            }
        }
        paint.color = secondary
        paint.alpha = 95
        paint.strokeWidth = density
        offline.forEach(::line)
        paint.color = accent
        paint.strokeWidth = 4*density
        road?.legs?.forEach(::line)
        paint.color = planColor
        paint.strokeWidth = 2*density
        paint.pathEffect = DashPathEffect(floatArrayOf(6*density,5*density),0f)
        (road?.stops ?: plan).forEach { canvas.drawCircle(plotX(it),plotY(it),4*density,paint) }
        paint.pathEffect = null
        fun point(fix: PhysicalPositions.Fix, color: Int, radius: Float, ring: Boolean) {
            paint.color = color
            paint.style = if (ring) Paint.Style.STROKE else Paint.Style.FILL
            paint.strokeWidth = 2*density
            canvas.drawCircle(plotX(stop(fix)),plotY(stop(fix)),radius*density,paint)
        }
        history.forEach { point(it,secondary,4f,it.buffered) }
        latest?.let { point(it,accent,7f,false) }
    }
}
