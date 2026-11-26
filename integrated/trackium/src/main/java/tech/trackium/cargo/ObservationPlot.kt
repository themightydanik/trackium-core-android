package tech.trackium.cargo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.DashPathEffect
import android.view.View
import kotlin.math.cos

/** Local coordinate plot. Gaps between reports are left undrawn. */
internal class ObservationPlot(
    context: Context,
    latest: PhysicalPositions.Fix?,
    private val history: List<PhysicalPositions.Fix>,
    private val plan: List<LocalFieldPlan.Stop>,
    private val planColor: Int,
    private val accent: Int,
    private val secondary: Int
) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private fun stop(fix: PhysicalPositions.Fix) = LocalFieldPlan.Stop(fix.latitude,fix.longitude)
    private val latest = latest
    private val all = plan + (history + listOfNotNull(latest)).map(::stop)
    private val anchor = latest?.let(::stop) ?: all.first()
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
        val minX = all.minOf(::x)
        val maxX = all.maxOf(::x)
        val minY = all.minOf { it.latitude }
        val maxY = all.maxOf { it.latitude }
        val unit = minOf((width-2*padding).coerceAtLeast(1f)/(maxX-minX).coerceAtLeast(.0003),
            (height-2*padding).coerceAtLeast(1f)/(maxY-minY).coerceAtLeast(.0003))
        fun plotX(stop: LocalFieldPlan.Stop) = (width/2 + (x(stop)-(minX+maxX)/2)*unit).toFloat()
        fun plotY(stop: LocalFieldPlan.Stop) = (height/2 - (stop.latitude-(minY+maxY)/2)*unit).toFloat()
        paint.color = planColor
        paint.strokeWidth = 2*density
        paint.pathEffect = DashPathEffect(floatArrayOf(6*density,5*density),0f)
        plan.zipWithNext().forEach { (from,to) ->
            canvas.drawLine(plotX(from),plotY(from),plotX(to),plotY(to),paint)
        }
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
