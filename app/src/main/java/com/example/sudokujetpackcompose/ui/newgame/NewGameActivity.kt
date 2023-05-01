package com.example.sudokujetpackcompose.ui.newgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.sudokujetpackcompose.R
import com.example.sudokujetpackcompose.common.makeToast
import com.example.sudokujetpackcompose.ActiveGameActivity
import com.example.sudokujetpackcompose.ui.newgame.buildlogic.buildNewGameLogic
import com.example.sudokujetpackcompose.ui.theme.SudokuJetpackComposeTheme

class NewGameActivity : AppCompatActivity(), NewGameContainer {
    private lateinit var logic: NewGameLogic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = NewGameViewModel()

        setContent {
            SudokuJetpackComposeTheme() {
                NewGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildNewGameLogic(this, viewModel, applicationContext)

    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(NewGameEvent.OnStart)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onDoneClick() {
        startActiveGameActivity()
    }

    /**
     * I want the other feature to be completely restarted each time
     */
    override fun onBackPressed() {
        startActiveGameActivity()
    }

    private fun startActiveGameActivity() {
        startActivity(
            Intent(
                this,
                ActiveGameActivity::class.java
            ).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

}