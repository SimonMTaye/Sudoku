package com.potato.sudoku

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.selects.select
import kotlin.math.min

class SudokuGameView (context: Context, attribSet: AttributeSet): View(context, attribSet){
    private var size = 9
    private var sqrtSize = 3
    //Width - padding (might be changed to hold width - (padding * 2) for clarity
    private var length = 0f
    private var cellSize = 1f
    private val padding = 32f
    private val fontSizeLarge = 64f
    private val fontSizeSmall = 38f
    var sudokuGame: SudokuGame? = null
    var selected: Pair<Int, Int>? = null
    var mistakes: MutableSet<Pair<Int, Int>>? = null

    var cellClickedListener: ((x: Int, y: Int)->Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        length = w - padding
        cellSize = (length - padding) / size
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dimen = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(dimen, dimen)
    }

    private val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDarkFaded, context.theme)
        strokeWidth = 4f
    }
    private val heavyPaint = Paint(lightPaint).apply {
        strokeWidth = 7f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, context.theme)
        //typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.mulish), Typeface.NORMAL)
        textSize = fontSizeLarge
    }
    private val redTextPaint = Paint(textPaint).apply{
        color = ResourcesCompat.getColor(resources, R.color.colorError, context.theme)
    }

    private val lightTextPaint = Paint(textPaint).apply {
        textSize = fontSizeSmall
        color = ResourcesCompat.getColor(resources, R.color.colorAccent, context.theme)
    }
    private val highlightPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorHighlightGrey, context.theme)
        alpha = 40
    }
    private val fadedHighlightPaint = Paint(highlightPaint).apply {
        alpha = 20
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBorders(canvas)
        drawGrid(canvas)
        drawGame(canvas)
        drawHighlight(canvas)

    }

    private fun drawHighlight(canvas: Canvas) {
        if (selected != null) {
            Log.d("SELECTED CELL", "(${selected!!.first} , ${selected!!.second} )")
            highlightCell(selected!!.first, selected!!.second, canvas)
            for (i in (0..8)) {
                highlightCell(selected!!.first, i, canvas, fadedHighlightPaint)
                highlightCell(i, selected!!.second, canvas, fadedHighlightPaint)
            }
            val xGroup = (selected!!.first / 3) * 3
            val yGroup = (selected!!.second / 3) * 3
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    if (i + xGroup != selected!!.first && j + xGroup != selected!!.second) {
                        highlightCell(i + xGroup, j + yGroup, canvas, fadedHighlightPaint)
                    }
                }
            }
        }
    }

    private fun drawGame(canvas: Canvas) {
        if (sudokuGame != null) {
            drawNotes(canvas, sudokuGame!!.userNotes)
            drawEntries(canvas, sudokuGame!!.mPuzzle)
            drawEntries(canvas, sudokuGame!!.userEntries)
        }
    }

    private fun drawNotes(canvas: Canvas, notesBoard: Array<Array<MutableList<Int>>>){
        for (i in 0 until 9){
            for (j in 0 until 9){
                var counter = 1
                for (note in notesBoard[i][j]){
                    if (counter > 6){
                        break
                    }
                    drawNumSmall(note, (j + 1),(i + 1), counter, canvas)
                    counter ++
                }
            }
        }
    }
    private fun drawEntries(canvas: Canvas, entryBoard: Board){
        for (i in 0 until 9){
            for (j in 0 until 9){
                if (entryBoard[i][j] != null){
                    if (mistakes != null && mistakes!!.contains(Pair(j, i))){
                        drawNum(entryBoard[i][j]!!, j + 1,i + 1,canvas, redTextPaint)
                        Log.d("BOARD VIEW", "Mistake found")
                    } else {
                        Log.d("BOARD VIEW", "($j , $i) not a mistake")
                        drawNum(entryBoard[i][j]!!, j + 1, i + 1, canvas)
                    }
                }
            }
        }
    }

    private fun drawBorders(canvas: Canvas) {
        canvas.drawRect(RectF(padding, padding, length, length), heavyPaint)
    }
    private fun drawGrid(canvas: Canvas){
        for (x in 1 until size){
            val mPaint = if(x % sqrtSize == 0) heavyPaint else lightPaint
            canvas.drawLine(padding, (padding + (cellSize * x)), length, (padding + (cellSize * x)), mPaint)
            canvas.drawLine((padding + (cellSize * x)), padding, (padding + (cellSize * x)), length, mPaint)

        }
    }

    // Disposable variable. Needed to render text properly
    private val textBounds = Rect()
    private fun drawNum(num: Int, x: Int, y: Int, canvas: Canvas, paint: Paint = textPaint){
        if (x > size || y > size || y < 1 || x < 1){
            Log.e("Arguments", "X value is: $x Y value is: $y")
            throw IllegalArgumentException("X and Y positions must be between 1 and size of board")
        }
        //Get the bounds (size of rectangle) that will be occupied by the text
        paint.getTextBounds(num.toString(), 0, num.toString().length, textBounds)
        val xPos = padding + (cellSize * x) - (cellSize / 2)
        val yPos = padding + (cellSize * y) - (cellSize / 2)
        canvas.drawText(num.toString(), xPos - textBounds.exactCenterX(), yPos - textBounds.exactCenterY(), paint)
    }

    //Similar to drawNum. Used to draw small numbers in the corners of grid, when the user is thinking
    private fun drawNumSmall(num: Int, x: Int, y: Int, index: Int ,canvas: Canvas){
        if (index > 6 || index < 1){
            throw IllegalArgumentException("Index must be from 1 - 6")
        }
        lightTextPaint.getTextBounds(num.toString(), 0, num.toString().length, textBounds)
        val xCen = padding + (cellSize * x) - (cellSize / 2)
        val yCen = padding + (cellSize * y) - (cellSize / 2)
        val xPos = when (index) {
            in 1..3 ->  xCen - (cellSize / 4)
            in 4..6 ->  xCen + (cellSize / 4)
            else -> xCen
        }
        val yPos = when (index){
            1,4 -> yCen - (cellSize / 3)
            3,6 -> yCen + (cellSize / 3)
            else -> yCen
        }
        canvas.drawText(num.toString(), xPos - textBounds.exactCenterX(), yPos - textBounds.exactCenterY(), lightTextPaint)

    }
    private fun highlightCell(x: Int, y: Int, canvas: Canvas, paint: Paint = highlightPaint){
        val top = padding + (cellSize * y)
        val left = padding + (cellSize * x)
        val cellRect = RectF(left, top, left + cellSize, top + cellSize)
        canvas.drawRect(cellRect, paint)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_DOWN){
            val x = (event.x.toInt() - padding) / cellSize
            val y = (event.y.toInt() - padding) / cellSize
            cellClickedListener?.invoke(x.toInt(), y.toInt())
            return true
        }
        return false
    }
}