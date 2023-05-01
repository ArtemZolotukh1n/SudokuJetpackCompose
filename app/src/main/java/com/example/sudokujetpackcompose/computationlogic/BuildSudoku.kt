package com.example.sudokujetpackcompose.computationlogic

import com.example.sudokujetpackcompose.domain.Difficulty
import com.example.sudokujetpackcompose.domain.SudokuNode
import com.example.sudokujetpackcompose.domain.SudokuPuzzle
import com.example.sudokujetpackcompose.domain.getHash
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.random.Random
internal fun buildNewSudoku(
    boundary: Int,
    difficulty: Difficulty
): SudokuPuzzle = buildNodes(boundary, difficulty)
    .buildEdges()
    .seedColors()
    .solve()
    .unsolve()

/**
 * 1. Generate a Map which contains n*n nodes.
 * 2. for each adjacent node (as per rules of sudoku), add an Edge to the hashset
 *  - By column
 *  - By row
 *  - By n sized subgrid
 *
 *  LinkedHashMap: I chose to use a LinkedHashMap because it preserves the ordering of
 *  the elements placed within the Map, but also allows lookups by hash code, which are
 *  generated by x and y values.
 *
 *  As for the LinkedList in each bucket (element) of the map, assume that the first element
 *  is the node at hashCode(x, y), and subsequent elements are edges of that element.
 *  Apart from the ordering the first element as the Head of the LinkedList, the rest of
 *  the elements need not be ordering in any particular fashion.
 *
 *
 *  */
internal fun buildNodes(n: Int, difficulty: Difficulty): SudokuPuzzle {
    val newMap = LinkedHashMap<Int, LinkedList<SudokuNode>>()

    (1..n).forEach { xIndex ->
        (1..n).forEach { yIndex ->
            val newNode = SudokuNode(
                xIndex,
                yIndex,
                0
            )

            //Each adjacency list begins with the f
            val newList = LinkedList<SudokuNode>()
            newList.add(newNode)
            newMap.put(
                newNode.hashCode(),
                newList
            )
        }
    }
    return SudokuPuzzle(n, difficulty, newMap)
}

/**
 * For each node:
 * 1. Get X and Y for first node which will be head of LinkedList
 * 2. Create new list containing any appropriate node without any repeats:
 * - column
 * - row
 * - subgrid
 * 3. Append list to original
 *
 */
internal fun SudokuPuzzle.buildEdges(): SudokuPuzzle {
    this.graph.forEach {
        val x = it.value.first.x
        val y = it.value.first.y

        it.value.mergeWithoutRepeats(
            getNodesByColumn(this.graph, x)
        )

        it.value.mergeWithoutRepeats(
            getNodesByRow(this.graph, y)
        )

        it.value.mergeWithoutRepeats(
            getNodesBySubgrid(this.graph, x, y, boundary)
        )

    }
    return this
}

/**
 * Allows merging of two lists without repeats by hashValue.
 * @param rootNode is the subgraph which we will be appending any non-repeated node to as an edge
 */
internal fun LinkedList<SudokuNode>.mergeWithoutRepeats(new: List<SudokuNode>) {
    val hashes: MutableList<Int> = this.map { it.hashCode() }.toMutableList()
    new.forEach {
        if (!hashes.contains(it.hashCode())) {
            this.add(it)
            hashes.add(it.hashCode())
        }
    }
}

/**
 * The goal of this algorithm is to "seed" an empty sudoku grid with 25% of it's potential values.
 * The way this is achieved, is by picking a number, and allocating it to a given column or row,
 * in such a way that it does not make the puzzle invalid.
 *
 * For each row:
 * - Items must be placed such that their Y values do not match
 * - The X values may be random
 * For each column:
 * - Items must be placed such that their X values do not match
 * - The y values may be random
 *
 * At first, I was distributing them diagonally, but this lead to an uneven distribution of colored
 * nodes as the size of the puzzle grew larger. By instead going vertically and horizontally
 * (instead of just diagonally), this ought to ensure an even distribution regardless of the size.
 * 1. loop forEach row or column: crIndex
 * 2. loop one node per subgrid: nodeIndex
 *      - get the value of the first node in the first subgrid, which equals crIndex:
 *          boundary.sqrt * crIndex * nodeIndex - boundary.sqrt + 1
 *          First row, first subgrid is 3 * 1 * 1 - 3 + 1 = 1
 *          First row, second subgrid is 3 * 1 * 2 - 3 + 1 = 4
 *          First row, third subgrid is 3 * 1 * 3 - 3 + 1 = 7
 *          Second row, first subgrid is 3 * 2 * 1 - 3 + 1 = 4
 *          Second row, first subgrid is 3 * 2 * 2 - 3 + 1 = 10
 *
 *
 * Expected allocations without breaking a pass, to reach 25% threshold:
 * 4 boundary: expect 4 numbers (25%), which will be one pass by column/row
 * 9 boundary: expect 27 numbers (33%), which will be two passes
 * 16 boundary: expect 64 numbers (25%), which will be four passes
 * 25 boundary: expect 175 numbers (28%), which will be six passes
 */
internal fun SudokuPuzzle.seedColors(): SudokuPuzzle {
    //numbers which have been allocated and are no longer unique
    val allocatedNumbers = mutableListOf<Int>()

    //the total of numbers which have been allocated
    var allocations = 0

    //by Row or column, which switches every time
    var byRow = true
    var ttb = true

    var loopCounter = 0

    //first loop executes until we have color a sufficient # of nodes.
    //This loop is terminated by the return statement or if something really screws up, a counter
    while (loopCounter < boundary * 1000) {
        //the second loop is for each column or row
        val rowOrColumnProgression = mutableListOf<Int>()
        if (ttb) (1..boundary.sqrt).forEach { rowOrColumnProgression.add(it) }
        else (boundary.sqrt downTo 1).forEach { rowOrColumnProgression.add(it) }

        rowOrColumnProgression.forEach { rowOrColumnIndex ->
            var newInt = Random.nextInt(1, boundary + 1)
            var notNew = true

            //only begin allocating if it's a new number
            while (notNew) {
                if (!allocatedNumbers.contains(newInt)) notNew = false
                else if (allocatedNumbers.size == boundary) notNew = false
                else newInt = Random.nextInt(1, boundary + 1)
            }

            allocatedNumbers.add(newInt)

            //randomly set top to bottom for next iteration
            ttb = Random.nextBoolean()

            //the third loop is for each subgrid in a given column or row
            (1..boundary.sqrt).forEach { subgridOffset ->
                //fixed coordinate is the boundary.sqrt * rowOrColumnIndex - boundary.sqrt
                //i.e. 0, 3, 6
                val fixedCoordinate = boundary.sqrt * rowOrColumnIndex - boundary.sqrt
                val variantLowerBound = boundary.sqrt * subgridOffset - boundary.sqrt + 1
                val variantUpperBound = variantLowerBound + boundary.sqrt
                //list of possible hash values to pick from
                val hashList = mutableListOf<Int>()

                if (byRow) {
                    (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                        hashList.add(getHash(variantCoordinate, fixedCoordinate + subgridOffset))
                    }
                } else {
                    (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                        hashList.add(getHash(fixedCoordinate + subgridOffset, variantCoordinate))
                    }
                }

                hashList.firstOrNull { this.graph[it]?.first?.color == 0 }.let {
                    if (it != null) {
                        this.graph[it]!!.first.color = newInt
                        allocations++
                    }
                }

                if (boundary == 4 || allocatedNumbers.size == boundary - 1) return this
                else if (allocatedNumbers.size == boundary) {
                    return this
                }
            }
        }
        byRow = !byRow
        loopCounter++
    }
    return this
}
