package co.atrasvida.avida2cars

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.atrasvida.avida2cars.databinding.ActivityMainBinding
import co.atrasvida.avida2cars.gameModels.Game
import co.atrasvida.avida2cars.gameModels.GameEvent

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var game = Game()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.roadLeft.color = ContextCompat.getColor(this, R.color.red)
        binding.roadRight.color = ContextCompat.getColor(this, R.color.blue)
        with(game) {
            roads = arrayListOf(binding.roadLeft, binding.roadRight)
            onEvent { event ->
                when (event) {
                    GameEvent.GameOver -> {
                        showGameOver()
                    }
                    is GameEvent.OnScoreCallBack -> {
                        binding.txtScore.text = event.score.toString()
                    }
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            game.restartOrPlayGame()
        }, 1000)
        binding.imgReset.setOnClickListener {
            game.restartOrPlayGame()
            binding.cslMenu.visibility = View.GONE
        }
    }

    private fun showGameOver() {
        binding.cslMenu.visibility = View.VISIBLE
        binding.txtScoreMenu.text = game.getScore().toString()

        if (game.getScore() > getBestScore()) {
            saveScore(game.getScore())
        }

        binding.txtBestScoreMenu.text = getBestScore().toString()
    }

    private fun saveScore(score: Int) {
        val editor: SharedPreferences.Editor =
            getSharedPreferences("GAME_SCORE", MODE_PRIVATE).edit()
        editor.putInt("BEST_SCORE", score).apply()

    }

    private fun getBestScore(): Int {
        val prefs: SharedPreferences = getSharedPreferences("GAME_SCORE", MODE_PRIVATE)

        return prefs.getInt("BEST_SCORE", 0)
    }
}