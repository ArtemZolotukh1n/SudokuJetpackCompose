package com.example.sudokujetpackcompose.domain

import androidx.compose.ui.Modifier

enum class Difficulty(val modifier: Double) {
    EASY(0.5),
    MEDIUM(0.44),
    HARD(0.38)
}