package co.atrasvida.avida2cars

import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import co.atrasvida.avida2cars.databinding.ActivityMainBinding
import co.atrasvida.avida2cars.gameModels.Game
import co.atrasvida.avida2cars.gameModels.GameEvent


class MainActivity : AppCompatActivity() {
    private lateinit var animation: Animation
    private lateinit var loseAnimation: Animation
    private lateinit var binding: ActivityMainBinding
    private lateinit var game: Game
    private lateinit var gameSP: GameSharedPrefHelper
    private lateinit var mediaPlayerHelper: MediaPlayerHelper
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        setUpGameView()
        showWelcomeDialog()
    }

    private fun setUpGameView() {
        sp = getSharedPreferences("SP_GAME_CONFIG", MODE_PRIVATE)
        gameSP = GameSharedPrefHelper(sp)
        mediaPlayerHelper = MediaPlayerHelper(applicationContext, R.raw.score, R.raw.lose)
        game = Game(gameSP, mediaPlayerHelper, loseAnimation)
        with(game) {
            roads = arrayListOf(binding.roadLeft, binding.roadRight)
            prepareGame()
            onEvent { event ->
                when (event) {
                    is GameEvent.GameOver -> {
                        showGameOverDialog(event)
                    }
                    is GameEvent.UpdateScore -> {
                        binding.txtScore.startAnimation(animation)
                        binding.txtScore.text = event.score.toString()
                    }
                }
            }
        }
    }

    private fun setUpViews() {
        with(binding) {
            roadLeft.color = ContextCompat.getColor(this@MainActivity, R.color.red)
            roadRight.color = ContextCompat.getColor(this@MainActivity, R.color.blue)
        }
        animation = loadAnimation(this, R.anim.bounce)
        loseAnimation = loadAnimation(this, R.anim.bounce2)
     }

    private fun showGameOverDialog(event: GameEvent.GameOver) {
        GameOverDialogFragment.newInstance(false, event.currentScore, event.bestScore) {
            lifecycleScope.launchWhenStarted {
                game.restartOrPlayGame()
                binding.roadLeft.isEnabled = true
                binding.roadRight.isEnabled = true
            }
        }.show(supportFragmentManager, "GameMenuDialogFragment")
    }

    private fun showWelcomeDialog() {
        GameOverDialogFragment.newInstance(true, gameSP.score, gameSP.bestScore) {
            lifecycleScope.launchWhenStarted {
                game.restartOrPlayGame()
                binding.roadLeft.isEnabled = true
                binding.roadRight.isEnabled = true
            }
        }.show(supportFragmentManager, "GameMenuDialogFragment")
    }
}