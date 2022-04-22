package co.atrasvida.avida2cars

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.atrasvida.avida2cars.databinding.ActivityMainBinding
import co.atrasvida.avida2cars.gameModels.Game
import co.atrasvida.avida2cars.gameModels.GameEvent
import kotlinx.coroutines.delay

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
    }

    private fun setUpGameView() {
        sp = getSharedPreferences("SP_GAME_CONFIG", MODE_PRIVATE)
        gameSharedPrefHelper = GameSharedPrefHelper(sp)
        game = Game(gameSharedPrefHelper)
        with(game) {
            roads = arrayListOf(binding.roadLeft, binding.roadRight)
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
            imgReset.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    game.restartOrPlayGame()
                }
                binding.cslMenu.visibility = View.GONE
                roadLeft.isEnabled = true
                roadRight.isEnabled = true
            }
        }
        lifecycleScope.launchWhenStarted {
            delay(1000)
            game.restartOrPlayGame()
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            game.restartOrPlayGame()
//        }, 1000)
    }

    private fun showGameOver(event: GameEvent.GameOver) {
        with(binding) {
            cslMenu.visibility = View.VISIBLE
            txtScoreMenu.text = event.currentScore.toString()
            binding.txtBestScoreMenu.text = event.bestScore.toString()
            roadLeft.isEnabled = false
            roadRight.isEnabled = false
        }
    }
}