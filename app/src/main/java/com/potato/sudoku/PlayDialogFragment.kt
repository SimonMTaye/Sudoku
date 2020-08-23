package com.potato.sudoku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment

class PlayDialogFragment: DialogFragment() {

    private lateinit var radioGroup: RadioGroup
    private var difficulty: SudokuModel.Diffculties = SudokuModel.Diffculties.EASY

    var playClickListner: ((difficulty: SudokuModel.Diffculties) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.dialog_fragment, container, false)
        dialogView.findViewById<Button>(R.id.play_button).setOnClickListener{onPlay()}
        radioGroup = dialogView.findViewById(R.id.difficulty_radio_group)
        return dialogView
    }

    private fun onPlay(){
        when (radioGroup.checkedRadioButtonId){
            R.id.easy_radio_button -> difficulty = SudokuModel.Diffculties.EASY
            R.id.medium_radio_button-> difficulty = SudokuModel.Diffculties.MEDIUM
            R.id.hard_radio_button -> difficulty = SudokuModel.Diffculties.HARD
        }
        playClickListner?.invoke(difficulty)
        this.dismiss()
    }
}