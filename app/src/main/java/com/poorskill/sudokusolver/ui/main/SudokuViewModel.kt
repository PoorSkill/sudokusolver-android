package com.poorskill.sudokusolver.ui.main

import androidx.lifecycle.ViewModel
import com.poorskill.sudokusolver.sudoku.SudokuGame

open class SudokuViewModel : ViewModel() {
    open val sudokuGame = SudokuGame(9)
}