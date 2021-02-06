package com.poorskill.sudokusolver.ui.sudoku

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.poorskill.sudokusolver.R
import com.poorskill.sudokusolver.sudoku.Cell
import com.poorskill.sudokusolver.ui.settings.PlayerPreferences
import kotlin.math.min
import kotlin.math.sqrt

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private lateinit var canvas: Canvas

    private var isDarkMode = isDarkMode()
    internal var isSolved = false
    private var fieldSize = PlayerPreferences.getSudokuSizePreferences(context)
    private var textPercentage = PlayerPreferences.getSudokuTextSizePreferences(context)
    private var sqrtSize = (sqrt(fieldSize.toDouble())).toInt()
    private var cellPixelSize = 0f

    private val thinLineWidth = 5f
    private val thickLineWidth = thinLineWidth * 3

    private var selectedRow = -1
    private var selectedCol = -1

    private var touchListener: OnTouchListener? = null

    private var cells: List<Cell>? = null


    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = if (isDarkMode) Color.WHITE else Color.BLACK
        strokeWidth = thickLineWidth
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        color = if (isDarkMode) Color.WHITE else Color.BLACK
        strokeWidth = thinLineWidth
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.selectedCell)
    }

    private val conflictingCellPaintLevel1 = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.conflictLevel1Cell)
    }

    private val conflictingCellPaintLevel2 = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.conflictLevel2Cell)
    }

    private val conflictingCellPaintLevel3 = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.conflictLevel3Cell)
    }


    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = if (isDarkMode) Color.WHITE else Color.BLACK
    }

    private val fixedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.fixedCell)
    }

    override fun onMeasure(widthMeasure: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasure, heightMeasureSpec)
        val smallestSide = min(widthMeasure, heightMeasureSpec)
        setMeasuredDimension(smallestSide, smallestSide)
    }


    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas
        updateMeasurements(width)
        fillCells(canvas)
        drawLines(canvas)
    }


    private fun updateMeasurements(width: Int) {
        cellPixelSize = width / fieldSize.toFloat()
        textPaint.textSize = cellPixelSize / textPercentage
    }


    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val r = it.row
            val c = it.col
            when (it.conflictLevel) {
                1 -> fillCell(canvas, r, c, conflictingCellPaintLevel1)
                2 -> fillCell(canvas, r, c, conflictingCellPaintLevel2)
                3 -> fillCell(canvas, r, c, conflictingCellPaintLevel3)
            }
            if (it.isFixed) {
                fillCell(canvas, r, c, fixedCellPaint)
            } else if (r == selectedRow && c == selectedCol) {
                fillCell(canvas, r, c, selectedCellPaint)
            }
            if (!it.isEmpty)
                drawText(
                    canvas,
                    c,
                    r,
                    it.value.toString(),
                    c % sqrtSize == 0 && c != 0,
                    r % sqrtSize == 0 && r != 0
                )

        }
    }

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        val offsetCol = (c / sqrtSize) * thinLineWidth / 2
        val offsetRow = (r / sqrtSize) * thinLineWidth / 2
        val offsetDifCol = if (c == fieldSize - 1) thinLineWidth else 0f
        val offsetDifRow = if (r == fieldSize - 1) thinLineWidth else 0f
        canvas.drawRect(
            c * cellPixelSize + offsetCol - offsetDifCol,
            r * cellPixelSize + offsetRow - offsetDifRow,
            (c + 1) * cellPixelSize + thinLineWidth / 2,
            (r + 1) * cellPixelSize + thinLineWidth / 2,
            paint
        )
    }

    private fun drawLines(canvas: Canvas) {
        var isAfterThickLine = false
        var thickLineOffset = 0f
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)
        for (i in 1 until fieldSize) {
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            when {
                isAfterThickLine -> {
                    isAfterThickLine = false
                }
                i % sqrtSize == 0 -> {
                    thickLineOffset += (thinLineWidth)
                    isAfterThickLine = true
                }
                else -> {
                    thickLineOffset = 0f
                }
            }
            canvas.drawLine(
                i * cellPixelSize + thickLineOffset,
                0F,
                i * cellPixelSize + thickLineOffset,
                height.toFloat(),
                paintToUse
            )
            canvas.drawLine(
                0F,
                i * cellPixelSize + thickLineOffset,
                width.toFloat(),
                i * cellPixelSize + thickLineOffset,
                paintToUse
            )
        }
    }


    private fun drawText(
        canvas: Canvas,
        col: Int,
        row: Int,
        value: String,
        isAfterThickCol: Boolean,
        isAfterThickRow: Boolean
    ) {
        val paintToUse = textPaint
        val textBounds = Rect()
        val thickLineOffsetCol = if (isAfterThickCol) thinLineWidth else 0f
        val thickLineOffsetRow = if (isAfterThickRow) thinLineWidth else 0f
        val thinLineDiffCol =
            if (col + 1 % sqrtSize == 0 || col == fieldSize - 1) thinLineWidth else 0f
        val thinLineDiffRow =
            if (row + 1 % sqrtSize == 0 || row == fieldSize - 1) thinLineWidth else 0f
        paintToUse.getTextBounds(value, 0, value.length, textBounds)
        val textWidth = paintToUse.measureText(value)
        val textHeight = textBounds.height()
        canvas.drawText(
            value,
            (col * cellPixelSize) + cellPixelSize / 2 - textWidth / 2 + thickLineOffsetCol + thinLineWidth / 2f - thinLineDiffCol,
            (row * cellPixelSize) + cellPixelSize / 2 + textHeight / 2 + thickLineOffsetRow + thinLineWidth / 2f - thinLineDiffRow,
            paintToUse
        )
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        if (isSolved)
            return
        val possibleSelectedRow = (y / cellPixelSize).toInt()
        val possibleSelectedCol = (x / cellPixelSize).toInt()

        updateSelectedCellUI(possibleSelectedRow, possibleSelectedCol)
        touchListener?.onCellTouch(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    internal fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    interface OnTouchListener {
        fun onCellTouch(row: Int, col: Int)
    }

    fun registerListener(listener: OnTouchListener) {
        this.touchListener = listener
    }

    private fun isDarkMode(): Boolean {
        return ((resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                )
    }

}