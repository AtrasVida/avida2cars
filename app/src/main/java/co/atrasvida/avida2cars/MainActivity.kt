package co.atrasvida.avida2cars

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import co.atrasvida.avida2cars.gameModels.Game
import co.atrasvida.avida2cars.gameModels.GameRoad

class MainActivity : AppCompatActivity() {


    lateinit var cslMenu: ConstraintLayout
    lateinit var txtScoreMenu: TextView
    lateinit var txtBestScoreMenu: TextView


    var game = Game()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val roadLeft: GameRoad = findViewById(R.id.roadLeft)
        val roadRight: GameRoad = findViewById(R.id.roadRight)
        txtBestScoreMenu = findViewById(R.id.txtBestScoreMenu)

        val txtScore: TextView = findViewById(R.id.txtScore)

        cslMenu = findViewById(R.id.cslMenu)
        txtScoreMenu = findViewById(R.id.txtScoreMenu)

        val imgReset: ImageView = findViewById(R.id.imgReset)

        roadLeft.color = resources.getColor(R.color.red)
        roadRight.color = resources.getColor(R.color.blue)

        game.roads = arrayListOf(roadLeft, roadRight)

        game.onLoseCallBack = {
            showGameOver()
        }

        game.onScoreCallBack = { totalScore ->
            txtScore.text = totalScore.toString()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            game.startGame()
        }, 1000)


        imgReset.setOnClickListener {
            game.startGame()
            cslMenu.visibility = View.GONE
        }
    }

    private fun showGameOver() {
        cslMenu.visibility = View.VISIBLE
        txtScoreMenu.text = game.getScore().toString()

        if (game.getScore() > getBestScore()) {
            saveScore(game.getScore())
        }

        txtBestScoreMenu.text = getBestScore().toString()
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