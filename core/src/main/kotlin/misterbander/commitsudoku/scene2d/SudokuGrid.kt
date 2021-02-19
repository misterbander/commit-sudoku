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
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.decorations.Decoration
import misterbander.commitsudoku.modifiers.GridModfier
import misterbander.commitsudoku.modifiers.GridModifiers
import misterbander.commitsudoku.scene2d.actions.*
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.cycle
import misterbander.gframework.util.drawCenter
import java.io.Serializable
import kotlin.math.floor
import com.badlogic.gdx.utils.StringBuilder as GdxStringBuilder


class SudokuGrid(val panel: SudokuPanel) : Actor(), PersistentState
{
	private val game = panel.screen.game
	
	val cells = Array(9) { i -> Array(9) { j -> Cell(i, j) } }
	val cellSize = 64F
	private val gridSize = 9*cellSize
	
	var mainSelectedCell: Cell? = null
		private set
	var completionCharmT = 0F
		private set
	
	val decorations: GdxArray<Decoration> = GdxArray()
	val modifiers = GridModifiers(this)
	var modifier: GridModfier? = null
		set(value)
		{
			field = value
			unselect()
			if (value == modifiers.sandwichConstraintSetter)
			{
				panel.keypadButtonGroup.buttonGroup.apply {
					buttons[0].isChecked = true
					buttons.forEach { it.isDisabled = true }
				}
				panel.showZero = true
			}
			else
			{
				panel.keypadButtonGroup.buttonGroup.buttons.forEach { it.isDisabled = false }
				panel.showZero = false
			}
		}
	val constraintsChecker = ConstraintsChecker(this)
	val actionController = ActionController(this)
	
	init
	{
		width = gridSize
		height = gridSize
		
		addListener(SudokuGridClickListener(this))
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
			completionCharmT += delta
			if (completionCharmT > 2)
				Gdx.graphics.isContinuousRendering = false
		}
		else
			completionCharmT = 0F
	}
	
	fun iToX(i: Float): Float
	{
		return x + i*cellSize
	}
	
	fun jToY(j: Float): Float
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
	
	fun select(i: Int, j: Int, toggleUnselect: Boolean)
	{
		if (i in 0..8 && j in 0..8)
			select(cells[i][j], toggleUnselect)
	}
	
	fun select(cell: Cell, toggleUnselect: Boolean)
	{
		mainSelectedCell = cell
		cell.isSelected = if (toggleUnselect) !cell.isSelected else true
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
					selectedCells += cell
			}
		}
		return selectedCells
	}
	
	fun typedDigit(digit: Int, isKeypad: Boolean = false)
	{
		if (modifier != null)
		{
			modifier!!.typedDigit(digit)
			return
		}
		val to = if (digit == -1) 0 else digit
		
		val selectedCells = getSelectedCells()
		if (selectedCells.isEmpty)
			return
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		var shouldCheck = false
		
		when
		{
			// Clear cell except color
			to == 0 && (isKeypad && panel.keypadInputMode != SudokuPanel.InputMode.COLOR || !isKeypad && !UIUtils.alt()) ->
			{
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
					{
						shouldCheck = true
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
			// Insert corner mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CORNER_MARK || !isKeypad && UIUtils.shift() ->
			{
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, to)
				}
			}
			// Insert center mark
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.CENTER_MARK || !isKeypad && (UIUtils.ctrl()) ->
			{
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, to)
				}
			}
			// Highlight color
			isKeypad && panel.keypadInputMode == SudokuPanel.InputMode.COLOR || !isKeypad && UIUtils.alt() ->
				selectedCells.forEach { cell -> modifyCellActions += ModifyColorAction(cell, to = to) }
			// Insert digit
			else ->
			{
				shouldCheck = true
				selectedCells.forEach { cell ->
					if (!cell.isGiven)
						modifyCellActions += ModifyDigitAction(cell, to = to)
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
	
	override fun readState(mapper: PersistentStateMapper)
	{
		actionController.readState(mapper)
		val digits: Array<Int> = mapper["digits"] ?: return
		val colors: Array<Int> = mapper["colors"] ?: return
		val isGiven: Array<Boolean> = mapper["isGiven"] ?: return
		val cornerMarks: Array<Boolean> = mapper["cornerMarks"] ?: return
		val centerMarks: Array<Boolean> = mapper["centerMarks"] ?: return
		
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
		constraintsChecker.check()
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		actionController.writeState(mapper)
		val digits = Array(81) { 0 }
		val colors = Array(81) { 0 }
		val isGiven = Array(81) { false }
		val cornerMarks = Array(81*9) { false }
		val centerMarks = Array(81*9) { false }
		
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
		
		decorations.forEach { it.draw(batch) }
		constraintsChecker.drawAdditionalConstraints(batch)
		
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
		var hasCornerTextDecoration = false
		private val white: Color = Color.WHITE.cpy()
		private val lightGray: Color = Color.LIGHT_GRAY.cpy()
		
		private val x: Float
			get() = iToX(i.toFloat())
		
		private val y: Float
			get() = jToY(j.toFloat())
		
		fun offset(iOffset: Int, jOffset: Int): Cell
		{
			val i2 = i + iOffset cycle 0..8
			val j2 = j + jOffset cycle 0..8
			return cells[i2][j2]
		}
		
		fun draw(batch: Batch)
		{
			val shapeDrawer = game.shapeDrawer
			val segoeui = game.segoeui
			val segoeui2 = game.segoeui2
			
			val highlightColorsMap: GdxMap<Int, Color> = game.skin["highlightcolors"]
			
			
			if (constraintsChecker.xConstraint in constraintsChecker && (i == j || i == 8 - j)) // Color X
			{
				shapeDrawer.setColor(highlightColorsMap[8])
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
