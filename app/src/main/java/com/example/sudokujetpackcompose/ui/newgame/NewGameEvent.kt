package com.example.sudokujetpackcompose.ui.newgame


import com.example.sudokujetpackcompose.domain.Difficulty

sealed class NewGameEvent {
    object OnStart: NewGameEvent()
    data class OnSizeChanged(val boundary: Int): NewGameEvent()
    data class OnDifficultyChanged(val diff: Difficulty): NewGameEvent()
    object OnDonePressed: NewGameEvent()
}