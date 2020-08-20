package com.potato.sudoku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.InputStream

class SudokuModel(val app: Application) : AndroidViewModel(app){
    enum class Diffculties (val id: Int) {
        EASY(R.raw.easy),
        MEDIUM(R.raw.medium),
        HARD(R.raw.hard)
    }

    val tag = "BOARD MODEL"
    val sudokuGame = MutableLiveData<SudokuGame>()
    val selected = MutableLiveData<Pair<Int, Int>>()
    var mistakes = MutableLiveData<MutableSet<Pair<Int, Int>>>()

    private fun getInputStream(difficulty: Diffculties): InputStream {
        return app.applicationContext.resources.openRawResource(difficulty.id)
    }

    fun newGame(difficulty: Diffculties){
        sudokuGame.value = SudokuGame(getInputStream(difficulty))
        sudokuGame.value!!.shufflePuzzle()
    }

    fun addNote(note: Int){
        clearMistakes()
        if (selected.value != null && sudokuGame.value!!.userEntries[selected.value!!.second][selected.value!!.first] == null){
            sudokuGame.value!!.addNote(note, selected.value!!.first, selected.value!!.second)
            sudokuGame.value = sudokuGame.value
        }
    }
    @ExperimentalStdlibApi
    fun addEntry(note: Int){
        clearMistakes()
        if (selected.value != null){
            sudokuGame.value!!.addEntry(note, selected.value!!.first, selected.value!!.second)
            sudokuGame.value!!.clearNote(selected.value!!.first, selected.value!!.second, 6)
            sudokuGame.value = sudokuGame.value
        }
    }
    @ExperimentalStdlibApi
    fun clearNote(count: Int = 1){
        clearMistakes()
        if (selected.value != null) {
            sudokuGame.value!!.clearNote(selected.value!!.first, selected.value!!.second, count)
            sudokuGame.value = sudokuGame.value
        }
    }
    fun clearEntry(){
        clearMistakes()
        if (selected.value != null) {
            sudokuGame.value!!.clearEntry(selected.value!!.first, selected.value!!.second)
            sudokuGame.value = sudokuGame.value
        }
    }
    fun solved(): Boolean {
        getMistakes()
        return sudokuGame.value?.solved() ?: false
    }
    private fun getMistakes(){
        if (mistakes.value == null){
            mistakes.value = mutableSetOf()
        }
        for (i in 0 until 9){
            for (j in 0 until 9){
                if (sudokuGame.value!!.userEntries[i][j] != null &&sudokuGame.value!!.userEntries[i][j] != sudokuGame.value!!.mSolution[i][j]) {
                    mistakes.value!!.add(Pair(j, i))
                }
            }
        }
    }
    private fun clearMistakes(){
        if (mistakes.value != null){
            mistakes.value = null
        }
    }



}