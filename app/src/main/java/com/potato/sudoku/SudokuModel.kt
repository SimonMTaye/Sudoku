package com.potato.sudoku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream

class SudokuModel(private val app: Application) : AndroidViewModel(app){

    enum class Diffculties (val id: Int) {
        EASY(R.raw.easy),
        MEDIUM(R.raw.medium),
        HARD(R.raw.hard)
    }

    private val cache = "CACHE"
    private val cacheFile = File(app.applicationContext.cacheDir, cache)

    val sudokuGame = MutableLiveData<SudokuGame>()
    val selected = MutableLiveData<Pair<Int, Int>>()
    var mistakes = MutableLiveData<MutableSet<Pair<Int, Int>>>()

    private fun getInputStream(difficulty: Diffculties): InputStream {
        return app.applicationContext.resources.openRawResource(difficulty.id)
    }

    private fun getRandomPuzzle(bufferedReader: BufferedReader): String{
        val r = (1..100).random()
        for (i in (1..100)){
            if (i == r){
                val s = bufferedReader.readLine()
                bufferedReader.close()
                return s
            }
            bufferedReader.readLine()
        }
        throw IllegalAccessError("File doesn't contain enough puzzles")
    }
    fun cachedGame(): Boolean{
        if (cacheFile.exists()) {
            val reader = cacheFile.bufferedReader()
            try {
                reader.readLine()
                reader.readLine()
                reader.readLine()
                reader.close()
                return true
            } catch (e: IOException) {
                cacheFile.delete()
                return false
            }
        }
        return false
    }


    fun saveGame(){
        if (sudokuGame.value != null){
            if (!cacheFile.exists()) {
                File.createTempFile(cache, null, app.applicationContext.cacheDir)
            }
            val writer = cacheFile.bufferedWriter()
            writer.write(
                SudokuGame.boardToString(sudokuGame.value!!.mSolution) + "\n" +
                SudokuGame.boardToString(sudokuGame.value!!.mPuzzle) + "\n" +
                SudokuGame.boardToString(sudokuGame.value!!.userEntries)
            )
            writer.close()
        }
    }

    fun initGame(){
        if (cacheFile.exists()){
            val reader = cacheFile.bufferedReader()
            try {
                val solBoardStr = reader.readLine()
                val puzBoardStr = reader.readLine()
                val usrBoardStr = reader.readLine()
                sudokuGame.value = SudokuGame(solBoardStr, puzBoardStr, usrBoardStr)
                reader.close()
            } catch (e: IOException){
                newGame(Diffculties.EASY)
                cacheFile.delete()
            }
        } else {
            newGame(Diffculties.EASY)
        }
    }

    fun newGame(difficulty: Diffculties){
        sudokuGame.value = SudokuGame(getRandomPuzzle(getInputStream(difficulty).bufferedReader()))
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
    private fun clearMistakes() {
        if (mistakes.value != null) {
            mistakes.value = null
        }
    }
}