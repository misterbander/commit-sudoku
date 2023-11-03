package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.scene2d.dialogs.SingleInputDialog
import misterbander.gframework.util.PersistentStateMapper
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class CornerTextDecorationAdder(
	private val screen: CommitSudokuScreen,
	private val grid: SudokuGrid
) : GridModifier<CornerTextDecoration>
{
	private val cornerTextDecorations = GdxSet<CornerTextDecoration>()

	private var selectedRow = 0
	private var selectedCol = 0
	private val isValidIndex
		get() = selectedRow in 0..8 && selectedCol in 0..8

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		grid.unselect()
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex)
			return

		grid.select(selectedRow, selectedCol)
		val existingCornerTextDecoration = findTextDecoration(selectedRow, selectedCol)
		if (existingCornerTextDecoration != null)
			removeModification(existingCornerTextDecoration)
		else
		{
			SingleInputDialog(screen, "Add Corner Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@SingleInputDialog
				addModification(CornerTextDecoration(grid, selectedRow, selectedCol, result))
			}.show(screen.uiStage)
		}
	}

	private fun findTextDecoration(row: Int, col: Int): CornerTextDecoration?
	{
		for (cornerTextDecoration: CornerTextDecoration in cornerTextDecorations)
		{
			if (cornerTextDecoration.row == row && cornerTextDecoration.col == col)
				return cornerTextDecoration
		}
		return null
	}

	override fun addModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations += modification
		grid.decorations += modification
		grid.cells[modification.row][modification.col].cornerTextDecorationCount++
	}

	override fun removeModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations -= modification
		grid.decorations -= modification
		grid.cells[modification.row][modification.col].cornerTextDecorationCount--
	}

	override fun clear() = cornerTextDecorations.clear()

	override fun readState(mapper: PersistentStateMapper)
	{
		val cornerTextDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["cornerTextDecorations"]
		cornerTextDecorationDataObjects?.forEach { dataObject ->
			val row = dataObject["row"] as Int
			val col = dataObject["col"] as Int
			val text = dataObject["text"] as String
			addModification(CornerTextDecoration(grid, row, col, text))
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["cornerTextDecorations"] = cornerTextDecorations.map { it.dataObject }.toTypedArray()
	}

	override fun draw(shapeDrawer: ShapeDrawer) = Unit
}
