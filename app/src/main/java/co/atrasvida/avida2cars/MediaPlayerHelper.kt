package co.atrasvida.avida2cars

import android.content.Context
import android.media.MediaPlayer

class MediaPlayerHelper(applicationContext: Context, scoreResId: Int, loseResId: Int) {

    private val scorePlayerForLeft: MediaPlayer = MediaPlayer.create(applicationContext, scoreResId)
    private val scorePlayerForRight: MediaPlayer =
        MediaPlayer.create(applicationContext, scoreResId)
    private val losePlayer: MediaPlayer = MediaPlayer.create(applicationContext, loseResId)

    fun playScoreEffect(index: Int) {
        if (index == 0) playScoreEffectForLeft()
        else playScoreEffectForRight()
    }

    fun playScoreEffectForLeft() {
        scorePlayerForLeft.start()
    }

    fun playScoreEffectForRight() {
        scorePlayerForRight.start()
    }

    fun playLoseEffect() {
        losePlayer.start()
    }


}