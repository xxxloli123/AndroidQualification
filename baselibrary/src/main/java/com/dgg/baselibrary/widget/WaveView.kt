package com.madconch.study.baser

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import java.util.*

/**
 * 功能描述:@TODO 填写功能描述
 * Created by LuoHaifeng on 2017/6/19.
 * Email:496349136@qq.com
 */
class WaveView : ImageView {
    val step: Float = 1000.0f
    val amplitude: Float = 80.0f
    var startX: Float = 0.0f
    var startMaskX: Float = 0.0f
    var paintMask: Paint? = null
        get() {
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.style = Paint.Style.FILL
            p.color = Color.parseColor("#55ffffff")
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            return p
        }
    var paint: Paint? = null
        get() {
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.style = Paint.Style.FILL
            p.color = Color.BLUE
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            return p
        }
    var path: Path = Path()
    var thread: Thread? = null

    var isPause : Boolean = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        if (thread != null && (thread as Thread).isAlive) {
            (thread as Thread).interrupt()
        }

        //初始化线程
        thread = Thread({
            while (true) {
               if(!isPause){
                   startX -= 20//移动波浪层
                   startMaskX -= 10 //移动mask层 和波浪层速度不一致产生轻微的视差
                   if (Math.abs(startX) >= step * 2) { //如果超出步长 重新开始
                       startX = 0f
                   }

                   if (Math.abs(startMaskX) >= step * 2) {//如果超出步长 重新开始
                       startMaskX = 0f
                   }

                   postInvalidate()
               }
                Thread.sleep(16)
            }
        })

        (thread as Thread).start()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            canvas.saveLayer(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
            super.onDraw(canvas)
            drawWaveMask(canvas, startMaskX)
            drawWave(canvas, startX)
        }
    }

    private fun drawWaveMask(canvas: Canvas, startX: Float) {
        val yUp = canvas.height - 2 * amplitude
        val yDown = canvas.height.toFloat()
        val startY = canvas.height - amplitude

        path.reset()
        path.moveTo(startX, startY)

        var tempX = startX
        var tempCenterY = yUp
        while (true) {
            var x0 = tempX + step / 2
            var y0: Float
            if (tempCenterY == yDown) {
                tempCenterY = yUp
                y0 = tempCenterY
            } else {
                tempCenterY = yDown
                y0 = tempCenterY
            }

            var x1 = tempX + step
            var y1 = startY

            tempX = x1

            path.quadTo(x0, y0, x1, y1)

            if (x1 > canvas.width - startX) {
                path.lineTo(x1, canvas.height.toFloat())
                path.lineTo(0f, canvas.height.toFloat())
                path.close()
                break
            }
        }

        canvas.drawPath(path, paintMask)
    }

    private fun drawWave(canvas: Canvas, startX: Float) {
        val yUp = canvas.height - 2 * amplitude
        val yDown = canvas.height.toFloat()
        val startY = canvas.height - amplitude

        path.reset()
        path.moveTo(startX, startY)

        var tempX = startX
        var tempCenterY = yDown
        while (true) {
            var x0 = tempX + step / 2
            var y0: Float
            if (tempCenterY == yDown) {
                tempCenterY = yUp
                y0 = tempCenterY
            } else {
                tempCenterY = yDown
                y0 = tempCenterY
            }

            var x1 = tempX + step
            var y1 = startY

            tempX = x1

            path.quadTo(x0, y0, x1, y1)

            if (x1 > canvas.width - startX) {
                path.lineTo(x1, canvas.height.toFloat())
                path.lineTo(0f, canvas.height.toFloat())
                path.close()
                break
            }
        }

        canvas.drawPath(path, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isPause = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPause = true
    }
}