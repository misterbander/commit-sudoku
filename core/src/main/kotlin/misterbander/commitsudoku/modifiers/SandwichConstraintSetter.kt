package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.*
import ktx.collections.set
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.constraints.SandwichConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.PersistentStateMapper
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class SandwichConstraintSetter(
	private val grid: SudokuGrid,
	private val constraintsChecker: ConstraintsChecker
) : GridModifier<SandwichConstraint>
{
	private val sandwichConstraints = GdxMap<Int, SandwichConstraint>()

	private var selectedRow = 0
	private var selectedCol = 0
	private val key
		get() = if (selectedCol == -1) selectedRow + 9 else selectedCol
	private val isValidIndex
		get() = selectedRow in 0..8 && selectedCol == -1 || selectedRow == -1 && selectedCol in 0..8

	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
	}

	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex)
		{
			selectedRow = 0
			selectedCol = 0
		}

		val dRow = down - up
		val dCol = right - left
		if (dRow != 0)
		{
			selectedRow += dRow
			selectedRow = (selectedRow + 1).mod(10) - 1
			selectedCol = if (selectedRow == -1) 0 else -1
		}
		else if (dCol != 0)
		{
			selectedCol += dCol
			selectedCol = (selectedCol + 1).mod(10) - 1
			selectedRow = if (selectedCol == -1) 0 else -1
		}
	}

	override fun enter() = Unit

	override fun typedDigit(digit: Int)
	{
		if (!isValidIndex)
			return

		val sandwichConstraint: SandwichConstraint? = sandwichConstraints[key]
		if (sandwichConstraint != null)
		{
			if (digit == -1) // Backspace
			{
				if (sandwichConstraint.sandwichValue < 10)
					removeModification(sandwichConstraint)
				else
					sandwichConstraint.sandwichValue /= 10
			}
			else
				sandwichConstraint.sandwichValue = sandwichConstraint.sandwichValue*10 + digit
		}
		else if (digit != -1)
		{
			val newSandwichConstraint = SandwichConstraint(
				grid,
				if (selectedCol == -1) selectedRow else selectedCol,
				selectedRow == 9,
				digit
			)
			addModification(newSandwichConstraint)
		}
		constraintsChecker.check(grid.cells)
	}

	override fun addModification(modification: SandwichConstraint)
	{
		sandwichConstraints[key] = modification
		constraintsChecker += modification
	}

	override fun removeModification(modification: SandwichConstraint)
	{
		sandwichConstraints.remove(key)
		constraintsChecker -= modification
	}

	override fun clear() = sandwichConstraints.clear()

	override fun readState(mapper: PersistentStateMapper)
	{
		val sandwichConstraintDataObjects: Array<HashMap<String, Serializable>>? = mapper["sandwichConstraints"]
		sandwichConstraintDataObjects?.forEach { dataObject ->
			val index = dataObject["index"] as Int
			val isColumn = dataObject["isColumn"] as Boolean
			val sandwichValue = dataObject["sandwichValue"] as Int
			val sandwichConstraint = SandwichConstraint(grid, index, isColumn, sandwichValue)
			val key = if (isColumn) index else index + 9
			sandwichConstraints[key] = sandwichConstraint
			constraintsChecker += sandwichConstraint
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["sandwichConstraints"] = sandwichConstraints.values().map { it.dataObject }.toTypedArray()
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		for (row in 0..8)
			drawClickableArea(shapeDrawer, row, -1)
		for (col in 0..8)
			drawClickableArea(shapeDrawer, -1, col)
	}

	private fun drawClickableArea(shapeDrawer: ShapeDrawer, row: Int, col: Int)
	{
		val x = grid.colToX(col.toFloat())
		val y = grid.rowToY(row + 1F)
		val isSelected = row == selectedRow && col == selectedCol
		shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) selectedColor else gray)
	}
}
