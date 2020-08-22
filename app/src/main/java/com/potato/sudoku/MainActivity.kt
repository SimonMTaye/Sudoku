package com.potato.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    val model: SudokuModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        model.initGame()
        supportFragmentManager.beginTransaction()
            .replace(R.id.board, BoardFragment.newInstance())
            .replace(R.id.inputFrame, InputFragment.newInstance())
            .commitNow()
    }

    override fun onPause() {
        super.onPause()
        model.saveGame()
    }

}