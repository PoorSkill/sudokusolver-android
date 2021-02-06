package com.poorskill.sudokusolver.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.poorskill.sudokusolver.R
import com.poorskill.sudokusolver.sudoku.Cell
import com.poorskill.sudokusolver.ui.sudoku.SudokuBoardView


class SudokuFragment() : Fragment(), SudokuBoardView.OnTouchListener {

    private lateinit var sudokuBoardView: SudokuBoardView
    private lateinit var numberButtons: List<Button>
    private lateinit var solveBoardButton: Button

    companion object {
        fun newInstance() = SudokuFragment()
    }

    private lateinit var viewModel: SudokuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.sudoku_fragment, container, false)

        setHasOptionsMenu(true)

        sudokuBoardView = view.findViewById(R.id.sudokuBoardView)

        sudokuBoardView.registerListener(this)

        numberButtons = listOf(
            view.findViewById(R.id.oneButton),
            view.findViewById(R.id.twoButton),
            view.findViewById(R.id.threeButton),
            view.findViewById(R.id.fourButton),
            view.findViewById(R.id.fiveButton),
            view.findViewById(R.id.sixButton),
            view.findViewById(R.id.sevenButton),
            view.findViewById(R.id.eightButton),
            view.findViewById(R.id.nineButton)
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        view.findViewById<Button>(R.id.deleteButton)
            .setOnClickListener { viewModel.sudokuGame.delete() }

        view.findViewById<Button>(R.id.resetBoardButton)
            .setOnClickListener { viewModel.sudokuGame.resetGame() }
        solveBoardButton = view.findViewById(R.id.solveBoardButton)
        solveBoardButton.setOnClickListener { viewModel.sudokuGame.solve() }
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(
            SudokuViewModel::class.java
        )
        viewModel.sudokuGame.selectedCellLiveData.observe(
            viewLifecycleOwner,
            { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(viewLifecycleOwner, { updateCells(it) })

        viewModel.sudokuGame.selectedCellLiveData.observe(
            viewLifecycleOwner,
            { updateSelectedCellUI(it) })

        viewModel.sudokuGame.isSolvedLiveData.observe(viewLifecycleOwner, { updateSolveState(it) })
        viewModel.sudokuGame.isConflictLiveData.observe(
            viewLifecycleOwner,
            { if (!sudokuBoardView.isSolved) switchSolveButton(!it) })

        if (viewModel.sudokuGame.isSolvedLiveData.value != null) {
            updateSolveState(viewModel.sudokuGame.isSolvedLiveData.value!!)
        }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateSolveState(isSolved: Boolean) {
        sudokuBoardView.isSolved = isSolved
        switchSolveButton(!isSolved)
    }

    override fun onCellTouch(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    private fun switchSolveButton(isActive: Boolean) {
        solveBoardButton.isClickable = isActive
        solveBoardButton.isEnabled = isActive
    }


}