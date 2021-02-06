package com.poorskill.sudokusolver.sudoku

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isFixed: Boolean = false,
    var conflictLevel: Int = 0
) {

    val isEmpty: Boolean
        get() = value == 0

    fun resetConflictLevel() {
        this.conflictLevel = 0
    }

    fun addConflictLevel() {
        if (++conflictLevel > 3) {
            conflictLevel = 3
        }
    }

    fun reset() {
        this.isFixed = false
        this.value = 0
        this.conflictLevel = 0
    }
}
