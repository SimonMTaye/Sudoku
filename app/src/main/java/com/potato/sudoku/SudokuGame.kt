package com.potato.sudoku

import java.io.BufferedReader
import java.io.InputStream

typealias Board = Array<Array<Int?>>

class SudokuGame {

    companion object BoardFactory {
        fun boardToString(board: Board): String{
            var boardstring = ""
            for (i in 0 until 81){
                val cell = board[i / 9][i % 9]
                if (cell == null){
                    boardstring += " "
                } else {
                    boardstring += cell.toString()
                }
            }
            return boardstring
        }
        fun boardFromString(string: String): Board {
            return Array(9) { y -> Array(9) { x ->  if (string[(y * 9) + x].toString() != " ") string[(y * 9) + x].toString().toInt() else null} }
        }
        fun boardInCols(board: Board): Board {
            return Array(9) { y -> Array(9) { x -> board[x][y] } }
        }
        fun copyBoard(board: Board): Board {
            return Array(9) { y -> Array(9) { x -> board[y][x] } }
        }
    }

    var mSolution: Board
    var mPuzzle: Board
    var userEntries: Board
    val userNotes: Array<Array<MutableList<Int>>> = Array(9) { Array(9) { mutableListOf<Int>() } }

    constructor(puzzleString: String){
        this.mSolution = solution(puzzleString)
        this.mPuzzle = puzzle(mSolution, puzzleString)
        this.userEntries = empty()
        this.shufflePuzzle()
    }

    constructor(solutionString: String, puzzleString: String, userNotesString: String){
        this.mSolution = boardFromString(solutionString)
        this.mPuzzle = boardFromString(puzzleString)
        this.userEntries = boardFromString(userNotesString)
    }


    private val translator = boardTranslator()


    // Neccessary becasue initilazing as completely null makes the compiler complain
    private fun empty(): Board{
        val c = copyBoard(mPuzzle)
        for (i in 0 until 9){
            for (j in 0 until 9){
                c[i][j] = null
            }
        }
        return c
    }

    private fun boardTranslator(): Map<Char, Int> {
        val nums = MutableList(9) { it + 1 }
        nums.shuffle()
        return mapOf(
            'a' to nums[0], 'b' to nums[1], 'c' to nums[2], 'd' to nums[3],
            'e' to nums[4], 'f' to nums[5], 'g' to nums[6], 'h' to nums[7], 'i' to nums[8]
        )
    }

    private fun solution(puzzleString: String): Board {
        return Array(9) { y -> Array(9) { x -> translator[puzzleString[(y * 9) + x]] } }
    }


    private fun shuffleBand(i: Int, solution: Board, puzzle: Board) {
        val key = mutableListOf(0, 1, 2)
        key.shuffle()
        val multiplier = ((i / 3) * 3)
        val sollines = Array(3) { lineNo -> Array(9) { solution[multiplier + lineNo][it] } }
        val puzzlelines = Array(3) { lineNo -> Array(9) { puzzle[multiplier + lineNo][it] } }
        for (x in (0..2)) {
            solution[multiplier + x] = sollines[key[x]]
            puzzle[multiplier + x] = puzzlelines[key[x]]
        }
    }


    private fun puzzle(solution: Board, puzzleString: String): Board {
        val copy = Array(9) { y -> Array(9) { x -> solution[y][x] } }
        for (i in 0 until 81) {
            if (puzzleString[81 + i] == '_') {
                copy[i / 9][i % 9] = null
            }
        }
        return copy
    }

    fun shufflePuzzle() {
        val boardCol = boardInCols(mSolution)
        val puzzleInCol = boardInCols(mPuzzle)
        for (i in 0 until 3) {
            shuffleBand(i, mSolution, mPuzzle)
            shuffleBand(i, boardCol, puzzleInCol)
        }
    }

    fun addNote(note: Int, x: Int, y: Int){
        if (mPuzzle[y][x] == null) {
            if (userNotes[y][x].count() < 6 && !userNotes[y][x].contains(note)) {
                userNotes[y][x].add(note)
            }
        }
    }

    fun addEntry(note: Int, x: Int, y: Int){
        if (mPuzzle[y][x] == null) {
            userEntries[y][x] = note
        }
    }

    fun clearEntry(x: Int, y: Int){
        userEntries[y][x] = null
    }

    @ExperimentalStdlibApi
    fun clearNote(x:Int, y: Int, count: Int){
        val c = userNotes[y][x].count()
        for (i in (1..count)) {
            if (c < i ){
                break
            }
            userNotes[y][x].removeLast()
        }

    }

    fun solved(): Boolean {
        for (i in 0 until 9){
            for (j in 0 until 9){
                if (mPuzzle[i][j] == null && userEntries[i][j] != mSolution[i][j]){
                    return false
                }
            }
        }
        return true
    }
}

