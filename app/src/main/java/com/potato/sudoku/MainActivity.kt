package com.potato.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.board, BoardFragment.newInstance())
                .replace(R.id.inputFrame, InputFragment.newInstance())
                .commitNow()
        }
    }
}