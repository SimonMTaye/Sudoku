package com.potato.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val model: SudokuModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViewById<Button>(R.id.restart_button).setOnClickListener{playGameDialog("NEW GAME")}
        createGameFragments()
        model.winCallback = {playGameDialog("YOU WIN! NEW GAME")}
        if (model.cachedGame()){
            model.initGame()
        } else {
            playGameDialog("NEW GAME")
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

    private fun playGameDialog(msg : String){
        val playDialog = PlayDialogFragment(msg)
        playDialog.playClickListner = {
            model.newGame(it)
            playDialog.dismiss()
        }
        playDialog.show(supportFragmentManager, "DIFFICULTY")
    }

}