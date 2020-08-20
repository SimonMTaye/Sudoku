package com.potato.sudoku

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class InputFragment : Fragment(){

    companion object {
        fun newInstance() = InputFragment()
    }

    val model: SudokuModel by activityViewModels()
    var noteMode = false

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val gridView = inflater.inflate(R.layout.input_layout, container, false)
        for (view in gridView as ViewGroup){
            when {
                view.tag == "numButton" -> {
                    view.setOnClickListener { numClicked(it) }
                }
                view.id == R.id.clear_button -> {
                    view.setOnClickListener {  clearClicked() }
                }
                view.id == R.id.note_button -> {
                    view.setOnClickListener { noteClicked(it) }
                }
                view.id == R.id.check_button -> {
                    view.setOnClickListener { solveClicked() }
                }
            }
        }
        return gridView
    }

    override fun onStart() {
        super.onStart()
        if (model.sudokuGame.value == null) {
            model.newGame(SudokuModel.Diffculties.EASY)
        }

    }

    @ExperimentalStdlibApi
    private fun numClicked(v: View){
        val button = v as Button
        Log.d("INPUT BUTTON PRESSED", button.text[0].toString())
        inputNum(button.text.toString().toInt())
    }
    @ExperimentalStdlibApi
    private fun inputNum(num: Int){
        if (noteMode){
            model.addNote(num)
        } else {
            model.addEntry(num)
        }
    }
    @ExperimentalStdlibApi
    private fun clearClicked(){
        Log.d("INPUT BUTTON PRESSED", "CLEAR BUTTON")
        model.clearNote(1)
        model.clearEntry()
    }

    private fun noteClicked(view: View){
        noteMode = !noteMode
        if (noteMode){
            view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorLightGrey, requireContext().theme))
        }else {
            view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorTransparent, requireContext().theme))
        }
    }
    private fun solveClicked(){
        if(model.solved()){
            Toast.makeText(requireContext(), "You win!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Check your solution again", Toast.LENGTH_SHORT).show()
        }
    }

}