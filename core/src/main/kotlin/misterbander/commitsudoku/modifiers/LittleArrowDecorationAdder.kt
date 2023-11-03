package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.LittleArrowDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class LittleArrowDecorationAdder(private val grid: SudokuGrid) : GridModifier<LittleArrowDecoration>
{
	private val arrowMap: Array<Array<LittleArrowDecoration?>> = Array(11) { arrayOfNulls(11) }

	private var selectedRow = 0
	private var selectedCol = 0
	private val isValidIndex: Boolean
		get() = selectedRow in -1..9 && selectedCol in -1..9

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex)
			return
		val existingLittleArrow = arrowMap[selectedRow + 1][selectedCol + 1]
		if (existingLittleArrow != null)
		{
			if (button == Input.Buttons.RIGHT)
			{
				removeModification(existingLittleArrow)
				return
			}
			existingLittleArrow.pointingDirection -= 45F
		}
		else
			addModification(LittleArrowDecoration(grid, selectedRow, selectedCol))
	}

	override fun longPress(x: Float, y: Float): Boolean
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex)
			return false
		val existingLittleArrow = arrowMap[selectedRow + 1][selectedCol + 1]
		if (existingLittleArrow != null)
		{
			removeModification(existingLittleArrow)
			return true
		}
		return false
	}

	override fun addModification(modification: LittleArrowDecoration)
	{
		arrowMap[modification.row + 1][modification.col + 1] = modification
		grid.decorations += modification
	}

	override fun removeModification(modification: LittleArrowDecoration)
	{
		arrowMap[modification.row + 1][modification.col + 1] = null
		grid.decorations -= modification
	}

	override fun clear() = arrowMap.forEach { it.fill(null) }

	override fun readState(mapper: PersistentStateMapper)
	{
		val littleArrowDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["littleArrowDecorations"]
		littleArrowDecorationDataObjects?.forEach { dataObject ->
			val row = dataObject["row"] as Int
			val col = dataObject["col"] as Int
			val pointingDirection = dataObject["pointingDirection"] as Float
			addModification(LittleArrowDecoration(grid, row, col, pointingDirection))
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		val littleArrowDecorations = GdxSet<LittleArrowDecoration>()
		for (arrows in arrowMap)
		{
			for (arrow in arrows)
			{
				if (arrow != null)
					littleArrowDecorations += arrow
			}
		}
		mapper["littleArrowDecorations"] = littleArrowDecorations.map { it.dataObject }.toTypedArray()
	}
}
