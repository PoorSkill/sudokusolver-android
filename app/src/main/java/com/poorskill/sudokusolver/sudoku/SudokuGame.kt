package com.poorskill.sudokusolver.sudoku

import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt


class SudokuGame internal constructor(private val fieldSize: Int) {

    private val sqtFieldSize = sqrt(fieldSize.toDouble()).toInt()

    internal var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    internal var cellsLiveData = MutableLiveData<List<Cell>>()
    internal val isSolvedLiveData = MutableLiveData<Boolean>()
    internal val isConflictLiveData = MutableLiveData<Boolean>()

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board =
        Board(fieldSize, List(fieldSize * fieldSize) { i -> Cell(i / fieldSize, i % fieldSize, 0) })

    private var isSolved = false

    init {
        isSolvedLiveData.postValue(false)
        isConflictLiveData.postValue(false)
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    private fun handleChange() {
        checkConflictInBoard()
    }

    fun handleInput(number: Int) {
        if (selectedRow < 0 || selectedCol < 0) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isFixed) return
        cell.value = number
        cellsLiveData.postValue(board.cells)
        handleChange()
    }

    private fun checkConflictInBoard() {
        board.cells.forEach { cell -> cell.resetConflictLevel() }
        isConflictInBoard()
    }

    private fun handleCellListConflict(cellList: List<Cell>): Boolean {
        var result = false
        if (isConflictInCellList(cellList)) {
            result = true
            cellList.forEach { cell -> cell.addConflictLevel() }
        }
        return result
    }


    private fun isConflictInBoard(): Boolean {
        var isConflict = false
        var squareStartRow = 0
        var squareStartCol = 0
        for (i in 0 until fieldSize) {
            //Per Row
            isConflict = if (isConflict) true else handleCellListConflict(List(fieldSize) {
                board.getCell(
                    i,
                    it
                )
            })
            //Per Col
            isConflict = if (isConflict) true else handleCellListConflict(List(fieldSize) {
                board.getCell(
                    it,
                    i
                )
            })
            //Per Square
            squareStartRow += sqtFieldSize
            if (squareStartRow > fieldSize) {
                squareStartRow = 3
                squareStartCol += sqtFieldSize
            }
            val arrayOfCell = arrayOfNulls<Cell>(fieldSize)
            var squareRowPos = squareStartRow
            var squareColPos = squareStartCol
            for (j in 0 until fieldSize) {
                if (++squareColPos - squareStartCol > sqtFieldSize) {
                    --squareRowPos
                    squareColPos = squareStartCol + 1
                }
                arrayOfCell[j] = board.getCell(squareRowPos - 1, squareColPos - 1)
            }
            isConflict = if (isConflict) true else handleCellListConflict(
                arrayOfCell.filterNotNull().toList()
            )
        }
        isConflictLiveData.postValue(isConflict)
        return isConflict
    }

    private fun isConflictInCellList(cellList: List<Cell>): Boolean {
        val usedNumber = BooleanArray(fieldSize)
        for (cell in cellList) {
            val valueInCord: Int = cell.value
            if (valueInCord != 0 && usedNumber[valueInCord - 1]) {
                return true
            } else if (valueInCord != 0) {
                usedNumber[valueInCord - 1] = true
            }
        }
        return false
    }


    fun updateSelectedCell(row: Int, col: Int) {
        if (isSolved)
            return
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))
    }

    fun delete() {
        if (selectedCol >= 0 && selectedRow >= 0 && selectedCol < fieldSize && selectedRow < fieldSize) {
            val cell = board.getCell(selectedRow, selectedCol)
            cell.reset()
            cellsLiveData.postValue(board.cells)
            handleChange()
        }
    }

    private fun tryNumber(row: Int, col: Int, number: Int): Boolean {
        val cell = board.getCell(row, col)
        if (cell.isFixed) {
            return false
        }
        cell.value = number
        return if (isConflictInBoard()) {
            cell.reset()
            false
        } else {
            true
        }
    }

    private fun solveByBacktracking(row: Int, col: Int): Boolean {
        for (i in 1..fieldSize) {
            if (board.getCell(row, col).isFixed || tryNumber(row, col, i)) {
                if (row == fieldSize - 1 && col == fieldSize - 1) {
                    return true
                }
                var nextR = row + 1
                var nextC = col
                if (nextR >= fieldSize) {
                    nextR = 0
                    ++nextC
                }
                if (solveByBacktracking(nextR, nextC)) {
                    return true
                } else {
                    if (board.getCell(row, col).isFixed) {
                        continue
                    } else {
                        board.getCell(row, col).reset()
                    }
                }
            }
        }
        return false
    }

    fun resetGame() {
        isSolved = false
        isSolvedLiveData.postValue(false)
        board.cells.forEach { cell -> cell.reset() }
        handleChange()
        cellsLiveData.postValue(board.cells)
        selectedRow = -1
        selectedCol = -1
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    internal fun solve() {
        updateSelectedCell(-1, -1)
        setCurrentSetCellsFixed()
        solveByBacktracking(0, 0)
        isSolved = true
        isSolvedLiveData.postValue(true)
        cellsLiveData.postValue(board.cells)
        handleChange()
    }

    private fun setCurrentSetCellsFixed() {
        board.cells.forEach { cell -> if (cell.value != 0) cell.isFixed = true }
        cellsLiveData.postValue(board.cells)
    }

}


