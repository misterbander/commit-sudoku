package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.IntMap
import ktx.actors.plusAssign
import ktx.collections.*
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.decorations.Decoration
import misterbander.commitsudoku.highlightColors
import misterbander.commitsudoku.markColor
import misterbander.commitsudoku.modifiers.GridModfier
import misterbander.commitsudoku.modifiers.GridModifiers
import misterbander.commitsudoku.nonGivenColor
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.actions.ActionController
import misterbander.commitsudoku.scene2d.actions.ModifyCellAction
import misterbander.commitsudoku.scene2d.actions.ModifyColorAction
import misterbander.commitsudoku.scene2d.actions.ModifyDigitAction
import misterbander.commitsudoku.scene2d.actions.ModifyMarkAction
import misterbander.commitsudoku.secondaryColor
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.GdxStringBuilder
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.drawCenter
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.min

class SudokuGrid(val panel: SudokuPanel) : Actor(), PersistentState
{
	val game: CommitSudoku
		get() = panel.game
	val screen: CommitSudokuScreen
		get() = panel.screen
	
	val cells = Array(9) { i -> Array(9) { j -> Cell(i, j) } }
	val cellSize = 64F
	private val gridSize = 9*cellSize
	
	var mainSelectedCell: Cell? = null
		private set
	var completionCharmT = 0F
		private set
	
	val decorations: GdxArray<Decoration> = GdxArray()
	val foreDecorations: GdxArray<Decoration> = GdxArray()
	val modifiers = GridModifiers(this)
	var modifier: GridModfier<*>? = null
		set(value)
		{
			field = value
			unselect()
			if (value != modifiers.cageSetter)
				modifiers.cageSetter.unselect()
			if (value == modifiers.cageSetter)
				panel.showZero = panel.screen.toolbar.cageMultibuttonMenu.checkedIndex == 0
			else
				panel.showZero = value == modifiers.sandwichConstraintSetter
		}
	val constraintsChecker = ConstraintsChecker(this)
	val actionController = ActionController(this)
	
	init
	{
		width = gridSize
		height = gridSize
		
		addListener(SudokuGridClickListener(this))
		addListener(SudokuGridGestureListener(this))
		addListener(SudokuGridKeyListener(this))
	}
	
	override fun hit(x: Float, y: Float, touchable: Boolean): Actor?
	{
		if (touchable && this.touchable != Touchable.enabled)
			return null
		if (!isVisible)
			return null
		return if (x >= -64 && x < width + 64 && y >= -64 && y < height + 64) this else null
	}
	
	override fun act(delta: Float)
	{
		super.act(delta)
		if (panel.isFinished)
		{
			completionCharmT += min(delta, 1/60F)
			if (completionCharmT > 2)
				Gdx.graphics.isContinuousRendering = false
		}
		else
			completionCharmT = 0F
	}
	
	fun iToX(i: Float): Float = x + i*cellSize
	
	fun jToY(j: Float): Float = y + j*cellSize
	
	fun xToI(x: Float): Int = floor((x - this.x)/cellSize).toInt()
	
	fun xToI(x: Float, precision: Float): Float = floor((x - this.x)/(cellSize*precision))*precision
	
	fun yToJ(y: Float): Int = floor((y - this.y)/cellSize).toInt()
	
	fun yToJ(y: Float, precision: Float): Float = floor((y - this.y)/(cellSize*precision))*precision
	
	fun select(i: Int, j: Int)
	{
		if (i in 0..8 && j in 0..8)
			select(cells[i][j])
	}
	
	fun select(cell: Cell)
	{
		mainSelectedCell = cell
		cell.isSelected = true
	}
	
	fun unselect() = cells.forEach { it.forEach { cell -> cell.isSelected = false } }
	
	fun unselect(i: Int, j: Int)
	{
		if (i in 0..8 && j in 0..8)
			unselect(cells[i][j])
	}
	
	private fun unselect(cell: Cell)
	{
		mainSelectedCell = cell
		cell.isSelected = false
	}
	
	fun setGivens(isGiven: Boolean)
	{
		for (cellRow in cells)
		{
			for (cell in cellRow)
			{
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
		for (cellRow in cells)
		{
			for (cell in cellRow)
			{
				if (cell.isSelected)
					selectedCells += cell
			}
		}
		return selectedCells
	}
	
	fun typedDigit(digitId: Int, isKeypad: Boolean = false)
	{
		if (modifier != null)
		{
			modifier!!.typedDigit(digitId)
			return
		}
		val digit = if (digitId == -1) 0 else digitId
		
		val selectedCells = getSelectedCells()
		if (selectedCells.isEmpty)
			return
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		var shouldCheck = false
		
		when
		{
			// Clear cell except color
			digit == 0 && (isKeypad && panel.keypadInputMode != SudokuPanel.InputMode.COLOR || !isKeypad && !UIUtils.alt()) ->
			{
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
					{
						shouldCheck = true
						modifyCellActions.apply {
							add(ModifyDigitAction(cell, to = 0))
							for (i in 1..9)
							{
								add(ModifyMarkAction(cell, ModifyCellAction.Type.CORNER, i, to = false))
								add(ModifyMarkAction(cell, ModifyCellAction.Type.CENTER, i, to = false))
							}
						}
					}
				}
			}
			// Insert corner mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CORNER_MARK || !isKeypad && UIUtils.shift() ->
			{
				// Only delete mark if all selected cells have the corner mark
				var to = false
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (cell.digit == 0 && !cell.cornerMarks[digit - 1])
					{
						to = true
						break
					}
				}
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(cell, ModifyCellAction.Type.CORNER, digit, to = to)
				}
			}
			// Insert center mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CENTER_MARK || !isKeypad && (UIUtils.ctrl()) ->
			{
				// Only delete mark if all selected cells have the center mark
				var to = false
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (cell.digit == 0 && !cell.centerMarks[digit - 1])
					{
						to = true
						break
					}
				}
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(cell, ModifyCellAction.Type.CENTER, digit, to = to)
				}
			}
			// Highlight color
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.COLOR || !isKeypad && UIUtils.alt() ->
				selectedCells.forEach { cell -> modifyCellActions += ModifyColorAction(cell, to = digit) }
			// Insert digit
			else ->
			{
				shouldCheck = true
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyDigitAction(cell, to = digit)
				}
			}
		}
		if (modifyCellActions.isEmpty)
			return
		modifyCellActions.forEach { this += it }
		actionController.addActions(modifyCellActions)
		if (shouldCheck)
			this += Actions.run { constraintsChecker.check() }
	}
	
	fun clearGrid()
	{
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		for (cellRow in cells)
		{
			for (cell in cellRow)
			{
				if (!cell.isGiven)
				{
					modifyCellActions.apply {
						add(ModifyDigitAction(cell, to = 0))
						for (i in 1..9)
						{
							add(ModifyMarkAction(cell, ModifyCellAction.Type.CORNER, i, to = false))
							add(ModifyMarkAction(cell, ModifyCellAction.Type.CENTER, i, to = false))
						}
					}
				}
			}
		}
		modifyCellActions.forEach { this += it }
		actionController.addActions(modifyCellActions)
	}
	
	fun reset()
	{
		cells.forEach { it.forEach { cell -> cell.reset() } }
		decorations.clear()
		foreDecorations.clear()
		actionController.clearHistory()
		constraintsChecker.clear()
		modifiers.clear()
		panel.screen.toolbar.apply {
			xButton.isChecked = false
			antiKingButton.isChecked = false
			antiKnightButton.isChecked = false
			nonconsecutiveButton.isChecked = false
		}
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		actionController.readState(mapper)
		modifiers.readState(mapper)
		constraintsChecker.readState(mapper)
		
		val digits: IntArray = mapper["digits"] ?: return
		val colors: IntArray = mapper["colors"] ?: return
		val isGiven: BooleanArray = mapper["isGiven"] ?: return
		val cornerMarks: BooleanArray = mapper["cornerMarks"] ?: return
		val centerMarks: BooleanArray = mapper["centerMarks"] ?: return
		
		for (i in 0..8)
		{
			for (j in 0..8)
			{
				val cell = cells[i][j]
				val index = i*9 + j
				
				cell.digit = digits[index]
				cell.colorCode = colors[index]
				cell.isGiven = isGiven[index]
				for (k in 0..8)
				{
					cell.cornerMarks[k] = cornerMarks[index*9 + k]
					cell.centerMarks[k] = centerMarks[index*9 + k]
				}
			}
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		actionController.writeState(mapper)
		modifiers.writeState(mapper)
		constraintsChecker.writeState(mapper)
		
		val digits = IntArray(81)
		val colors = IntArray(81)
		val isGiven = BooleanArray(81)
		val cornerMarks = BooleanArray(81*9)
		val centerMarks = BooleanArray(81*9)
		
		for (i in 0..8)
		{
			for (j in 0..8)
			{
				val cell = cells[i][j]
				val index = i*9 + j
				
				digits[index] = cell.digit
				colors[index] = cell.colorCode
				isGiven[index] = cell.isGiven
				for (k in 0..8)
				{
					if (cell.cornerMarks[k])
						cornerMarks[index*9 + k] = true
					if (cell.centerMarks[k])
						centerMarks[index*9 + k] = true
				}
			}
		}
		mapper["digits"] = digits
		mapper["colors"] = colors
		mapper["isGiven"] = isGiven
		mapper["cornerMarks"] = cornerMarks
		mapper["centerMarks"] = centerMarks
	}
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val shapeDrawer = game.shapeDrawer
		
		constraintsChecker.drawAdditionalConstraints(batch)
		decorations.forEach { it.draw(batch) }
		
		val lineColor: Color = secondaryColor
		shapeDrawer.rectangle(x, y, gridSize, gridSize, lineColor, 3F)
		cells.forEach { it.forEach { cell -> cell.draw(batch) } }
		for (i in 1 until 9)
		{
			// Draw vertical lines
			shapeDrawer.line(x + i*cellSize, y, x + i*cellSize, y + 9*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
			// Draw horizontal lines
			shapeDrawer.line(x, y + i*cellSize, x + 9*cellSize, y + i*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
		}
		
		foreDecorations.forEach { it.draw(batch) }
		
		modifier?.draw(batch)
	}
	
	/**
	 * @property i 0 based horizontal index of the cell
	 * @property j 0 based vertical index of the cell
	 */
	inner class Cell(val i: Int, val j: Int) : Serializable
	{
		var digit = 0
		var colorCode = 0
		var isGiven = false
		var isCorrect = true
		var isSelected = false
		val cornerMarks = Array(9) { false }
		val centerMarks = Array(9) { false }
		var cornerTextDecorationCount = 0
		private val white: Color = Color.WHITE.cpy()
		private val lightGray: Color = Color.LIGHT_GRAY.cpy()
		
		private val x
			get() = iToX(i.toFloat())
		
		private val y
			get() = jToY(j.toFloat())
		
		fun offset(iOffset: Int, jOffset: Int): Cell
		{
			val i2 = (i + iOffset).mod(9)
			val j2 = (j + jOffset).mod(9)
			return cells[i2][j2]
		}
		
		fun reset()
		{
			digit = 0
			colorCode = 0
			cornerMarks.fill(false)
			centerMarks.fill(false)
			cornerTextDecorationCount = 0
		}
		
		fun draw(batch: Batch)
		{
			val shapeDrawer = game.shapeDrawer
			val segoeui = screen.segoeUi
			val segoeui2 = screen.segoeUiLarge
			
			val highlightColorsMap: IntMap<Color> = highlightColors
			
			if (constraintsChecker.xConstraint in constraintsChecker && (i == j || i == 8 - j)) // Color X
			{
				shapeDrawer.setColor(highlightColorsMap[9])
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			
			val highlightColor: Color? = highlightColorsMap[colorCode]
			if (highlightColor != null) // Draw highlight
			{
				shapeDrawer.setColor(highlightColor)
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			
			if (isSelected) // Draw selection
			{
				shapeDrawer.setColor(selectedColor)
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
			
			if (digit != 0) // Draw digits
			{
				segoeui2.color = if (isGiven) primaryColor else if (isCorrect) nonGivenColor else Color.RED
				segoeui2.drawCenter(batch, digit.toString(), x + cellSize/2, y + cellSize/2)
			}
			else // Draw marks
			{
				segoeui.color = markColor
				// Corner marks
				var markCount = 0
				if (cornerTextDecorationCount != 0)
					markCount++
				for (k in 0..8)
				{
					if (cornerMarks[k])
					{
						var drawX: Float = iToX(i.toFloat())
						var drawY: Float = jToY(j.toFloat() + 1)
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
				val centerMarkBuilder = GdxStringBuilder()
				for (k in 0..8)
				{
					if (centerMarks[k])
						centerMarkBuilder.append(k + 1)
				}
				segoeui.drawCenter(batch, centerMarkBuilder.toString(), iToX(i.toFloat() + 0.5F), jToY(j.toFloat() + 0.5F))
			}
			
			// Draw completion charm
			if (panel.isFinished)
			{
				val shift = (i + j)/16F
				var t = MathUtils.clamp((completionCharmT - shift)*2, 0F, 1F)
				if (t > 0.5F)
					t = 1 - t
				val charmColor = if (colorCode in 1..8) white else lightGray
				charmColor.a = Interpolation.smoother.apply(t)
				shapeDrawer.setColor(charmColor)
				shapeDrawer.filledRectangle(x, y, cellSize, cellSize)
			}
		}
	}
}
