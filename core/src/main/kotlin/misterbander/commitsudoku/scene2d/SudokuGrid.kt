package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.style.get
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.scene2d.actions.*
import misterbander.gframework.util.drawCenter
import kotlin.math.floor


class SudokuGrid(val panel: SudokuPanel) : Actor()
{
	private val game = panel.screen.game
	
	val cells = Array(9) { i -> Array(9) { j -> Cell(i, j) } }
	val cellSize = 64F
	private val gridSize = 9*cellSize
	
	var mainSelectedCell: Cell? = null
		private set
	
	val actionController = ActionController(this)
	val constraintsChecker = ConstraintsChecker(this)
	
	init
	{
		width = gridSize
		height = gridSize
		
		addListener(SudokuGridClickListener(this))
		addListener(SudokuGridKeyListener(this))
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
		if (selectedI in 0..8 && selectedJ in 0..8)
			select(cells[selectedI][selectedJ])
	}
	
	fun select(cell: Cell)
	{
		mainSelectedCell = cell
		cell.isSelected = true
	}
	
	fun unselect()
	{
		cells.forEach { it.forEach { cell -> cell.isSelected = false } }
	}
	
	fun setGivens(isGiven: Boolean)
	{
		cells.forEach {
			it.forEach { cell ->
				if (isGiven)
				{
					if (cell.digit != 0)
						cell.isGiven = true
				}
				else
					cell.isGiven = false
			}
		}
	}
	
	private fun getSelectedCells(): GdxArray<Cell>
	{
		val selectedCells: GdxArray<Cell> = GdxArray()
		cells.forEach {
			it.forEach { cell ->
				if (cell.isSelected)
					selectedCells.add(cell)
			}
		}
		return selectedCells
	}
	
	fun typedDigit(digit: Int, isKeypad: Boolean = false)
	{
		val selectedCells = getSelectedCells()
		if (selectedCells.isEmpty)
			return
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		var shouldCheck = false
		
		when
		{
			// Insert corner mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CORNER_MARK
				|| !isKeypad && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) ->
			{
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
						modifyCellActions.add(ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, digit))
				}
			}
			// Insert center mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CENTER_MARK
				|| !isKeypad && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) ->
			{
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
						modifyCellActions.add(ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, digit))
				}
			}
			// Highlight color
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.COLOR
				|| !isKeypad && (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) ->
				selectedCells.forEach { cell -> modifyCellActions.add(ModifyColorAction(cell, to = digit)) }
			// Insert digit
			else ->
			{
				shouldCheck = true
				if (digit == 0)
				{
					// Clear cell except color
					selectedCells.forEach { cell ->
						if (!cell.isGiven)
						{
							modifyCellActions.apply {
								add(ModifyDigitAction(cell, to = 0))
								for (i in 1..9)
								{
									add(ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, i, to = false))
									add(ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, i, to = false))
								}
							}
						}
					}
				}
				else
				{
					selectedCells.forEach { cell ->
						if (!cell.isGiven)
							modifyCellActions.add(ModifyDigitAction(cell, to = digit))
					}
				}
			}
		}
		if (modifyCellActions.isEmpty)
			return
		modifyCellActions.forEach { this += it }
		actionController.addActions(modifyCellActions)
		if (shouldCheck)
		{
			this += object : RunnableAction()
			{
				init
				{
					runnable = Runnable { constraintsChecker.check() }
				}
			}
		}
	}
	
	fun clearGrid()
	{
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		cells.forEach {
			it.forEach { cell ->
				if (!cell.isGiven)
				{
					modifyCellActions.apply {
						add(ModifyDigitAction(cell, to = 0))
						for (i in 1..9)
						{
							add(ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, i, to = false))
							add(ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, i, to = false))
						}
					}
				}
			}
		}
		modifyCellActions.forEach { this += it }
		actionController.addActions(modifyCellActions)
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
	inner class Cell(val i: Int, val j: Int)
	{
		var digit = 0
		var colorCode = 0
		var isGiven = false
		var isCorrect = true
		var isSelected = false
		val cornerMarks = Array(9) { false }
		val centerMarks = Array(9) { false }
		var hasCornerTextDecoration = false
		
		private val x: Float
			get() = iToX(i)
		
		private val y: Float
			get() = jToY(j)
		
		fun getCell(iOffset: Int, jOffset: Int): Cell
		{
			val i2 = (i + iOffset + 9)%9
			val j2 = (j + jOffset + 9)%9
			return cells[i2][j2]
		}
		
		fun draw(batch: Batch)
		{
			val shapeDrawer = game.shapeDrawer
			val segoeui = game.segoeui
			val segoeui2 = game.segoeui2
			
			val highlightColorsMap: GdxMap<Int, Color> = game.skin["highlightcolors"]
			val highlightColor: Color? = highlightColorsMap[colorCode]
			if (highlightColor != null)
			{
				shapeDrawer.setColor(highlightColor)
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			
			if (isSelected)
			{
				shapeDrawer.setColor(game.skin.getColor("selectedcolor"))
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			
			if (digit != 0) // Draw digits
			{
				segoeui2.color = if (isGiven) game.skin["primarycolor"] else if (isCorrect) game.skin["nongivencolor"] else Color.RED
				segoeui2.drawCenter(batch, digit.toString(), x + cellSize/2, y + cellSize/2)
			}
			else // Draw marks
			{
				segoeui.color = game.skin["markcolor"]
				// Corner marks
				var markCount = 0
				if (hasCornerTextDecoration)
					markCount++
				for (k in 0..8)
				{
					if (cornerMarks[k])
					{
						var drawX: Float = iToX(i)
						var drawY: Float = jToY(j + 1)
						when (markCount)
						{
							1 -> { drawX += 5*cellSize/6; drawY -= cellSize/6 }
							2 -> { drawX += cellSize/6; drawY -= 5*cellSize/6 }
							3 -> { drawX += 5*cellSize/6; drawY -= 5*cellSize/6 }
							4 -> { drawX += cellSize/2; drawY -= cellSize/6 }
							5 -> { drawX += cellSize/6; drawY -= cellSize/2 }
							6 -> { drawX += 5*cellSize/6; drawY -= cellSize/2 }
							7 -> { drawX += cellSize/2; drawY -= 5*cellSize/6 }
							else -> { drawX += cellSize/6; drawY -= cellSize/6 }
						}
						val cornerMarkStr = (k + 1).toString()
						segoeui.drawCenter(batch, cornerMarkStr, drawX, drawY)
						markCount++
					}
				}
				
				// Center marks
				val centerMarkBuilder = StringBuilder()
				for (k in 0..8)
				{
					if (centerMarks[k])
						centerMarkBuilder.append(k + 1)
				}
				segoeui.drawCenter(batch, centerMarkBuilder.toString(), iToX(i) + cellSize/2, jToY(j) + cellSize/2)
			}
		}
	}
}
