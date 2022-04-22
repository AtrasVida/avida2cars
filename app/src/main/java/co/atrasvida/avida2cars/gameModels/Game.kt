package co.atrasvida.avida2cars.gameModels

import android.content.SharedPreferences
import android.os.Build
import co.atrasvida.avida2cars.GameSharedPrefHelper
import kotlinx.coroutines.*
import java.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Game(private val gameSP: GameSharedPrefHelper) {
    private var score = 0

    var isGameRunning = false
    var roads: ArrayList<GameRoad> = arrayListOf()
    private val uiScope = MainScope()

    fun onEvent(event: (GameEvent) -> Unit) {
        for (road in roads) {
            road.onEvent { roadEvent ->
                when (roadEvent) {
                    RoadEvent.GameOver -> {
                        saveScore(score)
                        event.invoke(
                            GameEvent.GameOver(
                                currentScore = score,
                                bestScore = getBestScore()
                            )
                        )
                        stopGame()
                    }
                    is RoadEvent.UpdateScore -> {
                        event.invoke(GameEvent.UpdateScore(++score))
                    }
                }
            }
        }
    }

       suspend fun restartOrPlayGame() {
        score = 0
        gameSpeed = 0
        isGameRunning = true
        for (road in roads) {
            road.restartOrPlayGame()
        }
        speedChanger.join()
        gameEngine.join()
    }

    /**
     * by calling this function we stop game
     *
     * @see GameRoad.stopGame
     */
    private fun stopGame() {
        isGameRunning = false
        for (road in roads) {
            road.stopGame()
        }
        speedChanger.cancel()
        gameEngine.cancel()
    }

    /**
     * In this method
     * We always store the user's current score in [SharedPreferences] using [GameSharedPrefHelper]
     * Then, by checking the following condition,
     * ## if (gameSP.score > gameSP.bestScore) {
     * ##      gameSP.bestScore = score
     * ## }
     * if the current score is greater than the previous user's previous best score, we will save it in [SharedPreferences]
     */
    private fun saveScore(score: Int) {
        gameSP.score = score
        if (gameSP.score > gameSP.bestScore) {
            gameSP.bestScore = score
        }
    }

    /**
     * return user best score
     *
     * @see GameSharedPrefHelper.bestScore
     */
    private fun getBestScore(): Int {
        return gameSP.bestScore
    }

    var gameSpeed = 1L
    var gameMaxSpeed = 20L

    var speedChanger: Job = uiScope.launch {
        var rsult = withContext(Dispatchers.Main) {
            while (isGameRunning) {
                gameSpeed++
                delay(20 * 1000L)
                //delay(20.toDuration(DurationUnit.SECONDS))
            }
        }
    }

    var gameEngine: Job = uiScope.launch {
        var rsult =  withContext(Dispatchers.Main) {
            while (isGameRunning) {

                for (road in roads) {
                    road.setNewState()
                }
                delay(gameSpeed - gameSpeed)
            }
        }
    }


}