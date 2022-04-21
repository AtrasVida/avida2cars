package co.atrasvida.avida2cars

import android.content.SharedPreferences

/**
 * With this class, we store game information in [SharedPreferences] and read it from [SharedPreferences] when needed
 * @property score In this variable we save the user's current score
 * @property bestScore In this variable we save the best user score
 *
 */
class GameSharedPrefHelper constructor(
    private val sp: SharedPreferences
) {
    /**
     * In this variable we save the user's current score
     */
    var score: Int
        get() = sp.getInt("SP_SCORE", 0)
        set(value) = sp.edit().putInt("SP_SCORE", value).apply()

    /**
     * In this variable we save the best user score
     */
    var bestScore: Int
        get() = sp.getInt("SP_BEST_SCORE", 0)
        set(value) = sp.edit().putInt("SP_BEST_SCORE", value).apply()
}