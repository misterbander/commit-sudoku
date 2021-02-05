package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import ktx.style.get
import misterbander.commitsudoku.CommitSudoku
import misterbander.gframework.getTextSize
import kotlin.math.floor

class SudokuGrid(private val game: CommitSudoku) : Actor()
{
	val cells = Array(9) { i -> Array(9) { j -> Cell(i, j) } }
	private val cellSize: Float
		get() = 64F
	
	private val gridSize: Float
		get() = 9*cellSize
	
	private var mainSelectedCell: Cell? = null
	
	init
	{
		width = gridSize
		height = gridSize
	}
	
	override fun hit(x: Float, y: Float, touchable: Boolean): Actor?
	{
		return when
		{
			touchable && this.touchable != Touchable.enabled -> null
			!isVisible -> null
			else -> this
		}
	}
	
	fun iToX(i: Int): Float
	{
		return x + i*cellSize
	}
	
	fun jToY(j: Int): Float
	{
		return y + j*cellSize
	}
	
	fun xToI(x: Float): Int
	{
		return floor((x - this.x)/cellSize).toInt()
	}
	
	fun yToJ(y: Float): Int
	{
		return floor((y - this.y)/cellSize).toInt()
	}
	
	/**
	 * Selects a cell at world coordinate (x, y)
	 */
	fun select(x: Float, y: Float)
	{
		val selectedI = floor(x/cellSize).toInt()
		val selectedJ = floor(y/cellSize).toInt()
		println("x = $x, y = $y, i = $selectedI, j = $selectedJ")
		if (selectedI in 0..8 && selectedJ in 0..8)
		{
			mainSelectedCell = cells[selectedI][selectedJ]
			cells[selectedI][selectedJ].isSelected = true
		}
	}
	
	fun unselect()
	{
		cells.forEach { it.forEach { cell -> cell.isSelected = false } }
	}
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val shapeDrawer = game.shapeDrawer
		val lineColor: Color = game.skin["secondarycolor"]
		shapeDrawer.rectangle(x, y, gridSize, gridSize, lineColor, 3F)
		cells.forEach { it.forEach { cell -> cell.draw(batch) } }
		for (i in 1 until 9)
		{
			// Draw vertical lines
			shapeDrawer.line(x + i*cellSize, y, x + i*cellSize, y + 9*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
			// Draw horizontal lines
			shapeDrawer.line(x, y + i*cellSize, x + 9*cellSize, y + i*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
		}
	}
	
	/**
	 * @property i 0 based horizontal index of the cell
	 * @property j 0 based vertical index of the cell
	 */
	inner class Cell(private val i: Int, private val j: Int)
	{
		var value = (1..9).random()
		var color = Color.CLEAR
		var isGiven = false
		var isCorrect = false
		var isSelected = false
		val cornerMarks = Array(9) { false }
		val centerMarks = Array(9) { false }
		var hasCornerTextDecoration = false
		
		private val x: Float
			get() = iToX(i)
		
		private val y: Float
			get() = jToY(j)
		
		fun draw(batch: Batch)
		{
			val shapeDrawer = game.shapeDrawer
			if (isSelected)
			{
				shapeDrawer.setColor(game.skin.getColor("selectedcolor"))
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			game.segoeui2.apply {
				// Draw the value
				val valueStr = value.toString()
				color = if (isGiven) game.skin["primarycolor"] else game.skin["nongivencolor"]
				val textSize = getTextSize(this, valueStr)
				draw(batch, valueStr, x, y + (cellSize + textSize.y)/2, cellSize, Align.center, false)
			}
		}
	}
	
	inner class ClickListener : com.badlogic.gdx.scenes.scene2d.utils.ClickListener(-1)
	{
		override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
		{
			super.touchDown(event, x, y, pointer, button)
			println("pointer = $pointer, button = $button")
			if (pointer == 0)
				unselect()
			select(x, y)
			return true
		}
		
		override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
		{
			super.touchDragged(event, x, y, pointer)
			select(x, y)
		}
	}
}
