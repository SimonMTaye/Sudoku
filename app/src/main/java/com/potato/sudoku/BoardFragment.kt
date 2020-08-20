package com.potato.sudoku

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class BoardFragment : Fragment() {


    private lateinit var gameView: SudokuGameView
    private val model: SudokuModel by activityViewModels()


    companion object {
        fun newInstance() = BoardFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameView = view.findViewById(R.id.game_display)
        gameView.cellClickedListener = {x, y -> select(x, y)}
        model.sudokuGame.observe(viewLifecycleOwner, {
            gameView.sudokuGame = it
            gameView.invalidate()
        })
        model.selected.observe(viewLifecycleOwner, {
            gameView.selected = it
            gameView.invalidate()
        })
        model.mistakes.observe(viewLifecycleOwner, {
            gameView.mistakes = it
            gameView.invalidate()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.board_fragment, container, false)
    }

    private fun select(x: Int, y: Int){
        Log.d("BOARD CLICKED", "($x , $y)")
        if (model.selected.value?.first ?: -1 != x && model.selected.value?.second ?: -1 != y) {
            model.selected.value = Pair(x, y)
        } else {
            model.selected.value = null
        }
    }

}