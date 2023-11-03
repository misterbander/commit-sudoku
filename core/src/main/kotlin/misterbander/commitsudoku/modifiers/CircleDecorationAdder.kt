package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.CircleDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.angle
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray
import kotlin.math.abs
import kotlin.math.max

class CircleDecorationAdder(private val grid: SudokuGrid) : GridModifier<CircleDecoration>
{
	private val circleDecorations = GdxArray<CircleDecoration>()
	private var currentCircleDecoration: CircleDecoration? = null
	private var justRemovedCircle = false

	private var selectedRow = 0
	private var selectedCol = 0
	private var startRow = -1
	private var startCol = -1
	private val isValidIndex
		get() = selectedRow in -1..9 && selectedCol in -1..9

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		currentCircleDecoration = null
		if (!isValidIndex)
			return
		val existingCircleDecoration = findCircleDecoration()
		if (existingCircleDecoration != null)
		{
			removeModification(existingCircleDecoration)
			justRemovedCircle = true
		}
		else
		{
			currentCircleDecoration = CircleDecoration(grid, selectedRow, selectedCol, 28F)
			currentCircleDecoration!!.color = Color.ORANGE
			addModification(currentCircleDecoration!!)
			startRow = selectedRow
			startCol = selectedCol
		}
	}

	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		currentCircleDecoration?.color = null
		currentCircleDecoration = null
		startRow = -1
		startCol = -1
	}

	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex || currentCircleDecoration == null)
			return
		val d = max(abs(selectedCol - startCol), abs(selectedRow - startRow))
		val angleFromStart = 360 - angle(startCol, startRow, selectedCol, selectedRow)
		val snappedRow: Int
		val snappedCol: Int

		if (angleFromStart < 22.5F || angleFromStart >= 337.5F)
		{
			snappedRow = startRow
			snappedCol = startCol + d
		}
		else if (angleFromStart < 67.5F)
		{
			snappedRow = startRow - d
			snappedCol = startCol + d
		}
		else if (angleFromStart < 112.5F)
		{
			snappedRow = startRow - d
			snappedCol = startCol
		}
		else if (angleFromStart < 157.5F)
		{
			snappedRow = startRow - d
			snappedCol = startCol - d
		}
		else if (angleFromStart < 202.5F)
		{
			snappedRow = startRow
			snappedCol = startCol - d
		}
		else if (angleFromStart < 247.5F)
		{
			snappedRow = startRow + d
			snappedCol = startCol - d
		}
		else if (angleFromStart < 292.5F)
		{
			snappedRow = startRow + d
			snappedCol = startCol
		}
		else
		{
			snappedRow = startRow + d
			snappedCol = startCol + d
		}

		if (snappedRow in -1..9 && snappedCol in -1..9)
		{
			currentCircleDecoration!!.row2 = snappedRow
			currentCircleDecoration!!.col2 = snappedCol
		}
	}

	private fun findCircleDecoration(): CircleDecoration?
	{
		for (circleDecoration: CircleDecoration in circleDecorations)
		{
			if (circleDecoration.isOver(selectedRow, selectedCol))
				return circleDecoration
		}
		return null
	}

	override fun addModification(modification: CircleDecoration)
	{
		circleDecorations.insert(0, modification)
		grid.decorations += modification
	}

	override fun removeModification(modification: CircleDecoration)
	{
		circleDecorations -= modification
		grid.decorations -= modification
	}

	override fun clear() = circleDecorations.clear()

	override fun readState(mapper: PersistentStateMapper)
	{
		val circleDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["circleDecorations"]
		circleDecorationDataObjects?.forEach { dataObject ->
			val row1 = dataObject["row1"] as Int
			val col1 = dataObject["col1"] as Int
			val row2 = dataObject["row2"] as Int
			val col2 = dataObject["col2"] as Int
			val circleDecoration = CircleDecoration(grid, row1, col1, 28F)
			circleDecoration.row2 = row2
			circleDecoration.col2 = col2
			circleDecorations.insert(0, circleDecoration)
			grid.decorations += circleDecoration
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["circleDecorations"] = circleDecorations.map { it.dataObject }.toTypedArray()
	}
}
