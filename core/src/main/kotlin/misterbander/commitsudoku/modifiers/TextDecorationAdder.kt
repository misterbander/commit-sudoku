package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.decorations.TextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.scene2d.dialogs.SingleInputDialog
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.PersistentStateMapper
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class TextDecorationAdder(
	private val screen: CommitSudokuScreen,
	private val grid: SudokuGrid
) : GridModifier<TextDecoration>
{
	private val textDecorations = GdxSet<TextDecoration>()

	private var selectedRow = 0
	private var selectedCol = 0
	private val isValidIndex
		get() = selectedRow in -1..9 && selectedCol in -1..9 && (selectedRow == -1 || selectedRow == 9 || selectedCol == -1 || selectedCol == 9)

	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		enter()
	}

	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex)
		{
			selectedRow = -1
			selectedCol = -1
		}

		val dRow = down - up
		val dCol = right - left
		if (dRow != 0)
		{
			if (selectedCol in 0..8)
				selectedRow = if (selectedRow == 9) -1 else 9
			else
				selectedRow += dRow
			selectedRow = (selectedRow + 1).mod(11) - 1
		}
		else if (dCol != 0)
		{
			if (selectedRow in 0..8)
				selectedCol = if (selectedCol == 9) -1 else 9
			else
				selectedCol += dCol
			selectedCol = (selectedCol + 1).mod(11) - 1
		}
	}

	override fun enter()
	{
		if (!isValidIndex)
			return
		val existingTextDecoration = findTextDecoration()
		if (existingTextDecoration != null)
			removeModification(existingTextDecoration)
		else
		{
			SingleInputDialog(screen, "Add Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@SingleInputDialog
				addModification(TextDecoration(grid, selectedRow, selectedCol, result))
			}.show(grid.stage)
		}
	}

	private fun findTextDecoration(): TextDecoration?
	{
		for (textDecoration: TextDecoration in textDecorations)
		{
			if (textDecoration.col == selectedCol && textDecoration.row == selectedRow)
				return textDecoration
		}
		return null
	}

	override fun addModification(modification: TextDecoration)
	{
		textDecorations += modification
		grid.decorations += modification
	}

	override fun removeModification(modification: TextDecoration)
	{
		textDecorations -= modification
		grid.decorations -= modification
	}

	override fun clear() = textDecorations.clear()

	override fun readState(mapper: PersistentStateMapper)
	{
		val textDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["textDecorations"]
		textDecorationDataObjects?.forEach { dataObject ->
			val row = dataObject["row"] as Int
			val col = dataObject["col"] as Int
			val text = dataObject["text"] as String
			addModification(TextDecoration(grid, row, col, text))
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["textDecorations"] = textDecorations.map { it.dataObject }.toTypedArray()
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		for (row in -1..9)
		{
			drawClickableArea(shapeDrawer, row, -1)
			drawClickableArea(shapeDrawer, row, 9)
		}
		for (col in 0..8)
		{
			drawClickableArea(shapeDrawer, -1, col)
			drawClickableArea(shapeDrawer, 9, col)
		}
	}

	private fun drawClickableArea(shapeDrawer: ShapeDrawer, row: Int, col: Int)
	{
		val x = grid.colToX(col.toFloat())
		val y = grid.rowToY(row + 1F)
		val isSelected = row == selectedRow && col == selectedCol
		shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) selectedColor else gray)
	}
}
