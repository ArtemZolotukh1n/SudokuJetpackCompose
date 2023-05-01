package com.example.sudokujetpackcompose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.sudokujetpackcompose.common.makeToast
import com.example.sudokujetpackcompose.ui.activegame.*
import com.example.sudokujetpackcompose.ui.newgame.NewGameActivity
import com.example.sudokujetpackcompose.ui.activegame.BuildLogic.buildActiveGameLogic
import com.example.sudokujetpackcompose.ui.theme.SudokuJetpackComposeTheme

class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {
    private lateinit var logic: ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            SudokuJetpackComposeTheme() {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)

        //guarantee that onRestart not called

        finish()
    }

    override fun onNewGameClick() {
        startActivity(
            Intent(
                this,
                NewGameActivity::class.java
            )
        )
    }

    override fun showError() = makeToast(getString(R.string.generic_error))
}
