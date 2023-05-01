package com.example.sudokujetpackcompose.domain

// We are saving time in millSeconds - that why I use Long!
data class UserStatistics(
    val fourEasy: Long = 0,
    val nineEasy: Long = 0,
    val fourMedium: Long = 0,
    val nineMedium: Long = 0,
    val fourHard: Long = 0,
    val nineHard: Long = 0,
)