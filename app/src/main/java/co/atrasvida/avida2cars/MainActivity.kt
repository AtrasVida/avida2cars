package co.atrasvida.avida2cars

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import co.atrasvida.avida2cars.databinding.ActivityMainBinding
import co.atrasvida.avida2cars.gameModels.Game
import co.atrasvida.avida2cars.gameModels.GameEvent

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var game: Game
    private lateinit var gameSharedPrefHelper: GameSharedPrefHelper
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        setUpGameView()

        showGameOver(null)
    }

    private fun setUpGameView() {
        sp = getSharedPreferences("SP_GAME_CONFIG", MODE_PRIVATE)
        gameSharedPrefHelper = GameSharedPrefHelper(sp)
        game = Game(gameSharedPrefHelper)
        with(game) {
            roads = arrayListOf(binding.roadLeft, binding.roadRight)
            prepareGame()
            onEvent { event ->
                when (event) {
                    is GameEvent.GameOver -> {
                        showGameOver(event)
                    }
                    is GameEvent.UpdateScore -> {
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
    }

    private fun showGameOver(event: GameEvent.GameOver?) {


        GameMenuDialogFragment.newInstance(
            event==null,
            event?.currentScore?:0,
            event?.bestScore?:0,
            object : GameMenuDialogFragment.OnDialogDismissListener {
                override fun onDialogDismissed(isOkPress: Boolean) {
                    lifecycleScope.launchWhenStarted {
                        game.restartOrPlayGame()
                        binding.roadLeft.isEnabled = true
                        binding.roadRight.isEnabled = true
                    }
                }
            }).show(supportFragmentManager, "GameMenuDialogFragment")
    }
}