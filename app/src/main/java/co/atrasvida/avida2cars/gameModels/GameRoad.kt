package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import java.util.*
import kotlin.math.abs

class GameRoad : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    var hurdles = arrayListOf<Hurdle>()

    private lateinit var car: Car

    // FIXME: detect position of car in road
    var widthFactor = 0f

    var place1 = 0f
    var place2 = 0f

    var color = 0

    var isRunning = false

    var step = 0f

    private var eventListener: (RoadEvent) -> Unit = {}

    fun onEvent(eventListener: (RoadEvent) -> Unit) {
        this.eventListener = eventListener
    }

    var threadSleep = 10L

    fun init() {
        car = Car(context)
        addView(car)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled) {
            if (event!!.action == MotionEvent.ACTION_DOWN)
                car.side = if (car.side == 0) 1 else 0
        }
        return super.onTouchEvent(event)

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        widthFactor = width / 4f

        place1 = widthFactor * 1
        place2 = widthFactor * 3

        step = height / 500f

        car.init(widthFactor, height - widthFactor * 2, color, 0)

    }

    var mainThread = createNewThread()
    var speedTread = createNewSpeedThread()

    private fun createNewThread() = Thread {

        while (isRunning) {

            handler.post { setNewState() }

            try {
                Thread.sleep(threadSleep)
            } catch (e: Exception) {
            }
        }

    }

    fun createNewSpeedThread() = Thread {
        while (isRunning) {
            try {
                Thread.sleep(20000)
                threadSleep--
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun setNewState() {
        for (hurdle in hurdles) {

            hurdle.refreshTop(hurdle.centerY + step)

            if (hurdle.centerY > height) {
                hurdle.refreshTop(hurdle.size * -1f)
                hurdle.refreshIsScore()

            }

            val verticalDistance = car.centerY - hurdle.centerY
            val horizontalDistance = car.centerX - hurdle.centerX

            val carHalfSize = car.size / 2
            val hurdleHalfSize = hurdle.size / 2

            if (verticalDistance < carHalfSize * -1) {
                if (hurdle.isScore && !hurdle.isUsed) {
                    ///  lose
                    eventListener.invoke(RoadEvent.GameOver)
                }
            } else if (abs(verticalDistance) < carHalfSize && abs(horizontalDistance) < hurdleHalfSize) {
                if (hurdle.isScore && !hurdle.isUsed) {
                    /// score
                    hurdle.setAsUsed()
                    eventListener.invoke(RoadEvent.UpdateScore)
                } else if (!hurdle.isScore) {
                    // lose
                    eventListener.invoke(RoadEvent.GameOver)
                }
            }

        }

    }


    fun restartOrPlayGame() {
        initHurdles()

        isRunning = true
        threadSleep = 10

        if (mainThread.isAlive) {
            mainThread.interrupt()
            speedTread.interrupt()
        }

        mainThread = createNewThread()
        mainThread.start()

        speedTread = createNewSpeedThread()
        speedTread.start()

    }


    /**
     * by calling this function we rest
     */
    fun stopGame() {
        isRunning = false
        mainThread.interrupt()
        speedTread.interrupt()
    }


    private fun initHurdles() {

        if (hurdles.size > 0)
            for (hurdle in hurdles) {
                removeView(hurdle)
            }

        hurdles = arrayListOf()

        val maxHurdles = 4
        val hurdleDistance = height / maxHurdles

        for (i in 0 until maxHurdles) {
            val hurdle = Hurdle(context)

            hurdles.add(hurdle)

            hurdle.color = color

            hurdle.widthFactor = widthFactor

            hurdle.side = if (Random().nextBoolean()) 0 else 1

            hurdle.centerY = (i * hurdleDistance - height / 2).toFloat()

            hurdle.size = widthFactor / 3

            hurdle.refreshIsScore()
            hurdle.refreshTop(hurdle.centerY)

            addView(hurdle)

        }

    }
}