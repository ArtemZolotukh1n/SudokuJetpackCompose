package com.example.sudokujetpackcompose.domain

import com.example.sudokujetpackcompose.computationlogic.buildNewSudoku
import java.util.LinkedList

data class SudokuPuzzle(
    val boundary: Int,
    val difficulty: Difficulty,
    val graph: LinkedHashMap<Int, LinkedList<SudokuNode>> = buildNewSudoku(
        boundary,
        difficulty
    ).graph,
    var elapsedTime: Long = 0L
) : java.io.Serializable {
    fun getValue(): LinkedHashMap<Int, LinkedList<SudokuNode>> = graph
}


