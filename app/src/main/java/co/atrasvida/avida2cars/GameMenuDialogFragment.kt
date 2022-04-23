package co.atrasvida.avida2cars

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import co.atrasvida.avida2cars.databinding.DialogGameMenuBinding

class GameMenuDialogFragment : DialogFragment() {

    private lateinit var mOnDialogDismissListener: OnDialogDismissListener
    private var currentScore: Int = 0
    private var bestScore: Int = 0
    private var isFirstLunch: Boolean = true


    var isOkPress = false

    companion object {
        fun newInstance(
            isFirstLunch: Boolean,
            currentScore: Int,
            bestScore: Int,
            onDialogDismissListener: OnDialogDismissListener
        ) =
            GameMenuDialogFragment().apply {
                this.mOnDialogDismissListener = onDialogDismissListener
                this.currentScore = currentScore
                this.bestScore = bestScore
            }
    }


    private lateinit var binding: DialogGameMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_AvidA2Cars)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogGameMenuBinding.inflate(inflater, container, false)

        with(binding) {
            if (isFirstLunch) {
                txtTitle.text = "WellCome"
                txtScoreMenu.visibility= View.INVISIBLE
            }
            txtScoreMenu.text = currentScore.toString()
            txtBestScoreMenu.text = bestScore.toString()

            imgReset.setOnClickListener {
                isOkPress = true
                dismiss()
            }
        }


        return binding.root

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mOnDialogDismissListener.onDialogDismissed(isOkPress)
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(width, height)

        }
    }


    interface OnDialogDismissListener {
        fun onDialogDismissed(isOkPress: Boolean)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

}
