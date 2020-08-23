package com.potato.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val model: SudokuModel by viewModels()
    private lateinit var playDialog : PlayDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        playDialog = PlayDialogFragment()
        if (model.cachedGame()){
            model.initGame()
            createGameFragments()
        } else {
            playGameDialog()
        }
    }


    override fun onPause() {
        super.onPause()
        model.saveGame()
    }

    private fun createGameFragments(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.board, BoardFragment.newInstance())
            .replace(R.id.inputFrame, InputFragment.newInstance())
            .commitNow()
    }

    private fun playGameDialog(){
        Log.d("MAIN ACTIVITY", "SHOWING NEW GAME DIALOG")
        playDialog.playClickListner = {
            model.newGame(it)
            createGameFragments()
        }
        playDialog.show(supportFragmentManager, "DIFFICULTY")
    }

}