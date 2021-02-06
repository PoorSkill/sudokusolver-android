package com.poorskill.sudokusolver.sudoku

class Board(private val size: Int, internal val cells: List<Cell>) {
    fun getCell(row: Int, col: Int) = cells[row * size + col]
}