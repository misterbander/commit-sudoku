package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import ktx.collections.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.decorations.Decoration
import misterbander.commitsudoku.highlightColors
import misterbander.commitsudoku.markColor
import misterbander.commitsudoku.modifiers.GridModifier
import misterbander.commitsudoku.modifiers.GridModifiers
import misterbander.commitsudoku.nonGivenColor
import misterbander.commitsudoku.notoSans
import misterbander.commitsudoku.notoSansLarge
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.actions.ActionController
import misterbander.commitsudoku.scene2d.actions.ModifyCellAction
import misterbander.commitsudoku.scene2d.actions.ModifyColorAction
import misterbander.commitsudoku.scene2d.actions.ModifyDigitAction
import misterbander.commitsudoku.scene2d.actions.ModifyMarkAction
import misterbander.commitsudoku.secondaryColor
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.GdxStringBuilder
import misterbander.gframework.util.Observable
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.drawCenter
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class SudokuGrid(
	private val screen: CommitSudokuScreen,
	private val constraintsChecker: ConstraintsChecker,
	private val actionController: ActionController
) : Actor(), PersistentState
{
	private val shapeDrawer: ShapeDrawer
		get() = screen.game.shapeDrawer

	val cells = Array(9) { row -> Array(9) { col -> Cell(row, col) } }
	// Assuming width = height
	val cellSize: Float
		get() = width/9

	var selectedCell: Cell? = null
		private set
	var completionCharmT = 0F
		private set

	val decorations = GdxArray<Decoration>()
	val foreDecorations = GdxArray<Decoration>()
	val modifiers = GridModifiers(screen, this, constraintsChecker)
	val modifierObservable = Observable<GridModifier<*>?>(null) { value ->
		unselect()
		if (value != modifiers.cageSetter)
			modifiers.cageSetter.unselect()
	}
	var modifier by modifierObservable

	init
	{
		addListener(SudokuGridClickListener(this))
		addListener(SudokuGridGestureListener(this))
		addListener(SudokuGridKeyListener(this, actionController))
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
		if (screen.isFinished)
		{
			completionCharmT += min(delta, 1/60F)
			if (completionCharmT > 2)
				Gdx.graphics.isContinuousRendering = false
		}
		else
			completionCharmT = 0F
	}

	fun colToX(col: Float): Float = x + col*cellSize

	fun rowToY(row: Float): Float = y + (9 - row)*cellSize

	fun xToCol(x: Float): Int = floor(x/cellSize).toInt()

	fun xToCol(x: Float, precision: Float): Float = floor(x/(cellSize*precision))*precision

	fun yToRow(y: Float): Int = 9 - ceil(y/cellSize).toInt()

	fun yToRow(y: Float, precision: Float): Float = 9 - ceil(y/(cellSize*precision))*precision

	fun select(row: Int, col: Int)
	{
		if (row in 0..8 && col in 0..8)
			select(cells[row][col])
	}

	fun select(cell: Cell)
	{
		selectedCell = cell
		cell.isSelected = true
	}

	fun unselect() = cells.forEach { it.forEach { cell -> cell.isSelected = false } }

	fun unselect(row: Int, col: Int)
	{
		if (row in 0..8 && col in 0..8)
			unselect(cells[row][col])
	}

	private fun unselect(cell: Cell)
	{
		selectedCell = cell
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

	private fun getSelectedCells(): Array<Cell>
	{
		val selectedCells = GdxArray<Cell>()
		for (cellRow in cells)
		{
			for (cell in cellRow)
			{
				if (cell.isSelected)
					selectedCells += cell
			}
		}
		return selectedCells.toArray(Cell::class.java)
	}

	fun typedDigit(digit: Int, inputMode: InputMode)
	{
		if (modifier != null)
		{
			modifier!!.typedDigit(digit)
			return
		}
		val nonNegativeDigit = max(digit, 0)

		val selectedCells = getSelectedCells()
		if (selectedCells.isEmpty())
			return
		val modifyCellActions = GdxArray<ModifyCellAction>()
		var shouldCheck = false

		when
		{
			// Clear cell except color
			nonNegativeDigit <= 0 && inputMode != InputMode.COLOR ->
			{
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
					{
						shouldCheck = true
						modifyCellActions += ModifyDigitAction(cell, to = 0)
						for (i in 1..9)
						{
							modifyCellActions += ModifyMarkAction(cell, ModifyCellAction.Type.CORNER, i, to = false)
							modifyCellActions += ModifyMarkAction(cell, ModifyCellAction.Type.CENTER, i, to = false)
						}
					}
				}
			}
			// Insert corner mark
			inputMode == InputMode.CORNER_MARK ->
			{
				// Only delete mark if all selected cells have the corner mark
				var to = false
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (cell.digit == 0 && !cell.cornerMarks[nonNegativeDigit - 1])
					{
						to = true
						break
					}
				}
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(
							cell,
							ModifyCellAction.Type.CORNER,
							nonNegativeDigit,
							to = to
						)
				}
			}
			// Insert center mark
			inputMode == InputMode.CENTER_MARK ->
			{
				// Only delete mark if all selected cells have the center mark
				var to = false
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (cell.digit == 0 && !cell.centerMarks[nonNegativeDigit - 1])
					{
						to = true
						break
					}
				}
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyMarkAction(
							cell,
							ModifyCellAction.Type.CENTER,
							nonNegativeDigit,
							to = to
						)
				}
			}
			// Highlight color
			inputMode == InputMode.COLOR -> selectedCells.forEach { cell ->
				modifyCellActions += ModifyColorAction(
					cell,
					to = nonNegativeDigit
				)
			}
			// Insert digit
			else ->
			{
				shouldCheck = true
				for (cell: SudokuGrid.Cell in selectedCells)
				{
					if (!cell.isGiven)
						modifyCellActions += ModifyDigitAction(cell, to = nonNegativeDigit)
				}
			}
		}
		if (modifyCellActions.isEmpty)
			return
		modifyCellActions.forEach { it.run() }
		actionController.addActions(modifyCellActions.toArray(ModifyCellAction::class.java))
		if (shouldCheck)
			constraintsChecker.check(cells)
	}

	fun clearGrid()
	{
		val modifyCellActions = GdxArray<ModifyCellAction>()
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
		modifyCellActions.forEach { it.run() }
		actionController.addActions(modifyCellActions.toArray(ModifyCellAction::class.java))
	}

	fun reset()
	{
		cells.forEach { it.forEach { cell -> cell.reset() } }
		decorations.clear()
		foreDecorations.clear()
		modifiers.clear()
	}

	override fun readState(mapper: PersistentStateMapper)
	{
		modifiers.readState(mapper)

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
		modifiers.writeState(mapper)

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
		constraintsChecker.drawConstraints(shapeDrawer)
		decorations.forEach { it.draw(shapeDrawer) }

		val lineColor = secondaryColor
		shapeDrawer.rectangle(x, y, width, width, lineColor, 3F)
		cells.forEach { it.forEach { cell -> cell.draw(batch) } }
		for (i in 1 until 9)
		{
			// Draw vertical lines
			shapeDrawer.line(
				x + i*cellSize,
				y,
				x + i*cellSize,
				y + 9*cellSize,
				if (i%3 == 0) 3F else 1F,
				true,
				lineColor,
				lineColor
			)
			// Draw horizontal lines
			shapeDrawer.line(
				x,
				y + i*cellSize,
				x + 9*cellSize,
				y + i*cellSize,
				if (i%3 == 0) 3F else 1F,
				true,
				lineColor,
				lineColor
			)
		}

		foreDecorations.forEach { it.draw(shapeDrawer) }

		modifier?.draw(shapeDrawer)
	}

	/**
	 * @property row row number, the top-most row is 0
	 * @property col column number, the left-most column is 0
	 */
	inner class Cell(val row: Int, val col: Int) : Serializable
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

		fun offset(row: Int, col: Int): Cell
		{
			val row2 = (this.row + row).mod(9)
			val col2 = (this.col + col).mod(9)
			return cells[row2][col2]
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
			val notoSans = notoSans
			val notoSansLarge = notoSansLarge

			val highlightColorsMap = highlightColors

			if (constraintsChecker.x && (row == col || row == 8 - col)) // Color X
			{
				shapeDrawer.setColor(highlightColorsMap[9])
				shapeDrawer.filledRectangle(colToX(col.toFloat()), rowToY(row + 1F), cellSize, cellSize)
			}

			val highlightColor: Color? = highlightColorsMap[colorCode]
			if (highlightColor != null) // Draw highlight
			{
				shapeDrawer.setColor(highlightColor)
				shapeDrawer.filledRectangle(colToX(col.toFloat()), rowToY(row + 1F), cellSize, cellSize)
			}

			if (isSelected) // Draw selection
			{
				shapeDrawer.setColor(selectedColor)
				shapeDrawer.filledRectangle(colToX(col.toFloat()), rowToY(row + 1F), cellSize, cellSize)
			}

			if (digit != 0) // Draw digits
			{
				notoSansLarge.color = if (isGiven) primaryColor else if (isCorrect) nonGivenColor else Color.RED
				notoSansLarge.drawCenter(batch, digit.toString(), colToX(col + 0.5F), rowToY(row + 0.5F))
			}
			else // Draw marks
			{
				notoSans.color = markColor
				// Corner marks
				var markCount = 0
				if (cornerTextDecorationCount != 0)
					markCount++
				for (k in 0..8)
				{
					if (cornerMarks[k])
					{
						var drawX: Float
						var drawY: Float
						when (markCount)
						{
							1 ->
							{
								drawX = colToX(col + 5/6F)
								drawY = rowToY(row + 1/6F)
							}
							2 ->
							{
								drawX = colToX(col + 1/6F)
								drawY = rowToY(row + 5/6F)
							}
							3 ->
							{
								drawX = colToX(col + 5/6F)
								drawY = rowToY(row + 5/6F)
							}
							4 ->
							{
								drawX = colToX(col + 1/2F)
								drawY = rowToY(row + 1/6F)
							}
							5 ->
							{
								drawX = colToX(col + 1/6F)
								drawY = rowToY(row + 1/2F)
							}
							6 ->
							{
								drawX = colToX(col + 5/6F)
								drawY = rowToY(row + 1/2F)
							}
							7 ->
							{
								drawX = colToX(col + 1/2F)
								drawY = rowToY(row + 5/6F)
							}
							else ->
							{
								drawX = colToX(col + 1/6F)
								drawY = rowToY(row + 1/6F)
							}
						}
						val cornerMarkStr = (k + 1).toString()
						notoSans.drawCenter(batch, cornerMarkStr, drawX, drawY)
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
				notoSans.drawCenter(
					batch,
					centerMarkBuilder.toString(),
					colToX(col.toFloat() + 0.5F),
					rowToY(row.toFloat() + 0.5F)
				)
			}

			// Draw completion charm
			if (screen.isFinished)
			{
				val shift = (row + col)/16F
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
