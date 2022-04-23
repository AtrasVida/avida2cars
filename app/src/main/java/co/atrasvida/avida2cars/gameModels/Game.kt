package co.atrasvida.avida2cars.gameModels

import android.content.SharedPreferences
import android.view.animation.Animation
import co.atrasvida.avida2cars.GameSharedPrefHelper
import co.atrasvida.avida2cars.MediaPlayerHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Game(
    private val gameSP: GameSharedPrefHelper,
    private val mediaPlayerHelper: MediaPlayerHelper,
    private val loseAnimation: Animation
) {
    private val mainScope = MainScope()
    private val mainScope2 = MainScope()

    private var score = 0

    var isGameRunning = false
    var roads: ArrayList<GameRoad> = arrayListOf()

    private var gameSpeed = 1L
    var gameMaxSpeed = 50L


    fun onEvent(event: (GameEvent) -> Unit) {
        for ((index,road) in roads.withIndex()) {
            road.onEvent { roadEvent ->
                when (roadEvent) {
                    RoadEvent.GameOver -> {
                        mediaPlayerHelper.playLoseEffect()
                        saveScore(score)
                        event.invoke(
                            GameEvent.GameOver(
                                currentScore = score,
                                bestScore = getBestScore()
                            )
                        )
                        mainScope.launch { stopGame() }
                    }
                    is RoadEvent.UpdateScore -> {
                        mediaPlayerHelper.playScoreEffect(index)
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
        for ((index,road) in roads.withIndex()) {
            road.restartOrPlayGame(index)
        }
        gameEngine().start()
        speedChanger().start()
    }

    /**
     * by calling this function we stop game
     *
     * @see GameRoad.stopGame
     */
    private suspend fun stopGame() {
        isGameRunning = false
        for (road in roads) {
            road.stopGame()
        }
        speedChanger().cancel()
        gameEngine().cancel()
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

    private suspend fun speedChanger() = mainScope2.launch {
        while (isGameRunning) {
            gameSpeed++
            delay(20 * 100L)
        }
    }

    private suspend fun gameEngine() = mainScope.launch {
        while (isGameRunning) {
            for (road in roads) {
                road.setNewState()
            }
            delay(gameMaxSpeed - gameSpeed)
        }
    }

    fun prepareGame() {
        mainScope.launch {
            for (road in roads) {
                road.setMissAnimation(loseAnimation)
                road.setNewState()
            }
        }
    }
}