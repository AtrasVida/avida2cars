package co.atrasvida.avida2cars

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import co.atrasvida.avida2cars.databinding.DialogGameMenuBinding
import co.atrasvida.avida2cars.gameModels.ResultDialogEvent

class GameOverDialogFragment : DialogFragment() {
    private lateinit var binding: DialogGameMenuBinding
    private lateinit var listener: (ResultDialogEvent) -> Unit
    private var currentScore: Int = 0
    private var bestScore: Int = 0
    private var isFirstLunch: Boolean = true
    var mustResetGame = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.Theme_AvidA2Cars)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGameMenuBinding.inflate(inflater, container, false)
        with(binding) {
            if (isFirstLunch) {
                txtTitle.text = getString(R.string.welcome)
            }
            txtScoreMenu.text = currentScore.toString()
            txtBestScoreMenu.text = bestScore.toString()
            imgReset.setOnClickListener {
                mustResetGame = true
                dismiss()
            }
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.invoke(ResultDialogEvent.MustRestGame(mustResetGame))
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(width, height)
            }
        }
    }

    companion object {
        fun newInstance(
            isFirstLaunch: Boolean,
            currentScore: Int,
            bestScore: Int,
            listener: (ResultDialogEvent) -> Unit
        ): GameOverDialogFragment {
            return GameOverDialogFragment().apply {
                this.isFirstLunch = isFirstLaunch
                this.currentScore = currentScore
                this.bestScore = bestScore
                this.listener = listener
            }
        }
    }
}
