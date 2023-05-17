package com.example.clockapp

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f.sp
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val paintMinutes = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.px
        color = Color.LTGRAY
        style = Paint.Style.STROKE
    }
    private val paintHour = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3f.px
        color = Color.WHITE
        style = Paint.Style.STROKE
    }
    private val paintXLeft = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.px
        color = ContextCompat.getColor(context, R.color.purple)
        style = Paint.Style.FILL
    }
    private val paintXRight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.px
        color = ContextCompat.getColor(context, R.color.beige)
        style = Paint.Style.FILL
    }
    private val paintVertical = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.px
        color = ContextCompat.getColor(context, R.color.dark_gray)
        style = Paint.Style.FILL
    }
    private val paintCenter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f.px
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val paintArrowSecond = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f.px
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val paintArrowMinute = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 7f.px
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintArrowHour = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f.px
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintInfo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f.sp
        color = Color.WHITE
        style = Paint.Style.FILL
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
    }

    private var cX = 0f
    private var cY = 0f
    private var outerRadius = 0f
    private var innerRadius = 0f
    private var textRadius = 0f
    private var hourLineRadius = 0f
    private var minutesLineRadius = 0f
    private val pathMinutes = Path()
    private val pathHour = Path()
    private val textRect = Rect()
    private val numbersMap = hashMapOf<String, Pair<Float, Float>>()
    private val heartBeatBitmap: Bitmap
    private var weatherBitmap: Bitmap? = null
    var heartBeat: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var weather: Weather = Weather.Sunny(10)
        set(value) {
            field = value
            weatherBitmap = when (value) {
                is Weather.Windy -> {
                    AppCompatResources.getDrawable(context, R.drawable.ic_windy)!!
                        .toBitmap()
                }

                is Weather.Rain -> {
                    AppCompatResources.getDrawable(context, R.drawable.ic_rain)!!
                        .toBitmap()
                }

                is Weather.Snowy -> {
                    AppCompatResources.getDrawable(context, R.drawable.ic_snow)!!
                        .toBitmap()
                }

                is Weather.Sunny -> {
                    AppCompatResources.getDrawable(context, R.drawable.ic_sunny)!!
                        .toBitmap()
                }

                is Weather.Cloud -> {
                    AppCompatResources.getDrawable(context, R.drawable.ic_cloud)!!
                        .toBitmap()
                }
            }
            invalidate()
        }

    init {
        heartBeatBitmap =
            AppCompatResources.getDrawable(context, R.drawable.ic_heart_beat)!!.toBitmap()
    }

    var timeSecond = 0
        set(value) {
            field = value
            invalidate()
        }

    var timeMinutes = 0
        set(value) {
            field = value
            invalidate()
        }

    var timeHours = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cX = width / 2f
        cY = height / 2f
        outerRadius = cX
        innerRadius = outerRadius - 16f.px
        textRadius = innerRadius - 16f.px
        hourLineRadius = textRadius / 2f
        minutesLineRadius = textRadius - 16f.px

        numbersMap.clear()
        for (angle in 0 until 360 step 6) {
            val (startX, startY) = calculatePoint(innerRadius, angle.toFloat())
            val (stopX, stopY) = calculatePoint(outerRadius, angle.toFloat())
            val hour = (angle + 90) / 30
            if (angle % 30 == 0) {
                val text = when (hour) {
                    in 0..12 -> hour.toString()
                    else -> (hour - 12).toString()
                }
                paintNumber.getTextBounds(text, 0, text.length, textRect)
                val (x, y) = calculatePoint(textRadius, angle.toFloat())
                numbersMap[text] = (x - textRect.width() / 2f) to (y + textRect.height() / 2f)
                pathHour.moveTo(startX, startY)
                pathHour.lineTo(stopX, stopY)
            } else {
                pathMinutes.moveTo(startX, startY)
                pathMinutes.lineTo(stopX, stopY)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircles(canvas)
        showDegree(canvas)
        showHeartbeat(canvas)
        canvas?.drawPath(pathMinutes, paintMinutes)
        canvas?.drawPath(pathHour, paintHour)
        numbersMap.forEach { (text, coordinates) ->
            val (x, y) = coordinates
            canvas?.drawText(text, x, y, paintNumber)
        }
        drawHourArrow(canvas)
        drawMinuteArrow(canvas)
        drawSecondArrow(canvas)
        canvas?.drawCircle(cX, cY, 10f.px, paintArrowSecond)
        canvas?.drawCircle(cX, cY, 2f.px, paintCenter)
    }

    private fun calculatePoint(radius: Float, angle: Float): Pair<Float, Float> {
        val x = (cX + radius * cos(Math.toRadians(angle.toDouble())).toFloat())
        val y = (cY + radius * sin(Math.toRadians(angle.toDouble())).toFloat())
        return x to y
    }

    private fun drawCircles(canvas: Canvas?) {
        canvas?.drawCircle(cX + innerRadius / 2, cY, innerRadius / 3, paintXRight)
        canvas?.drawCircle(cX - innerRadius / 2, cY, innerRadius / 3, paintXLeft)
        canvas?.drawCircle(cX, cY - innerRadius / 2, innerRadius / 3, paintVertical)
        canvas?.drawCircle(cX, cY + innerRadius / 2, innerRadius / 3, paintVertical)
    }

    private fun showDegree(canvas: Canvas?) {
        val temp = "${weather.temperature}°"
        paintInfo.getTextBounds(temp, 0, temp.length, textRect)
        canvas?.drawText(
            temp,
            cX - textRect.width() / 2,
            cY - innerRadius / 2 + 16f.px,
            paintInfo
        )
        val bitmapWidth = weatherBitmap!!.width / 2
        canvas?.drawBitmap(
            weatherBitmap!!,
            cX - bitmapWidth,
            cY - innerRadius / 2 - textRect.height() * 2,
            null
        )
    }

    private fun showHeartbeat(canvas: Canvas?) {
        paintInfo.getTextBounds(heartBeat.toString(), 0, heartBeat.toString().length, textRect)
        canvas?.drawText(
            heartBeat.toString(),
            cX - textRect.width() / 2,
            cY + innerRadius / 2 + 16f.px,
            paintInfo
        )
        val bitmapWidth = heartBeatBitmap.width / 2
        canvas?.drawBitmap(
            heartBeatBitmap,
            cX - bitmapWidth,
            cY + innerRadius / 2 - textRect.height() * 2,
            null
        )
    }

    private fun drawHourArrow(canvas: Canvas?) {
        val angelHour = (((timeHours % 12) * 30f) - 90) + (timeMinutes * 0.5f)
        val (xH, yH) = calculatePoint(hourLineRadius, angelHour)
        canvas?.drawLine(cX, cY, xH, yH, paintArrowHour)
    }

    private fun drawMinuteArrow(canvas: Canvas?) {
        val angelMinute = (timeMinutes * 6f) - 90
        val (xM, yM) = calculatePoint(minutesLineRadius, angelMinute)
        canvas?.drawLine(cX, cY, xM, yM, paintArrowMinute)
    }

    private fun drawSecondArrow(canvas: Canvas?) {
        val angelSecond = (timeSecond * 6f) - 90
        val (xS, yS) = calculatePoint(innerRadius, angelSecond)
        canvas?.drawLine(cY, cY, xS, yS, paintArrowSecond)
    }
}

val Float.px: Float get() = (this * getSystem().displayMetrics.density)
val Float.sp: Float get() = (this * getSystem().displayMetrics.scaledDensity)

data class ClockAngel(val hours: Int, val minutes: Int, val seconds: Int)


