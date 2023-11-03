package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.BorderDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class BorderDecorationSetter(private val grid: SudokuGrid) : GridModifier<BorderDecoration>
{
	private val borderDecorations = GdxArray<BorderDecoration>()

	private var selectedRow = 0F
	private var selectedCol = 0F
	private val isValidIndex: Boolean
		get() = selectedRow in 0F..9F && selectedCol in 0F..9F

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y - grid.cellSize/4, 0.5F)
		selectedCol = grid.xToCol(x + grid.cellSize/4, 0.5F)
		if (!isValidIndex)
			return
		val existingBorderDecoration = findBorderDecoration()
		if (existingBorderDecoration != null)
		{
			val nextType = existingBorderDecoration.type.nextType()
			if (nextType == null)
				removeModification(existingBorderDecoration)
			else
				existingBorderDecoration.type = nextType
		}
		else
			addModification(BorderDecoration(grid, selectedRow, selectedCol))
	}

	private fun findBorderDecoration(): BorderDecoration?
	{
		for (borderDecoration: BorderDecoration in borderDecorations)
		{
			if (MathUtils.isEqual(borderDecoration.row, selectedRow) && MathUtils.isEqual(borderDecoration.col, selectedCol))
				return borderDecoration
		}
		return null
	}

	override fun addModification(modification: BorderDecoration)
	{
		borderDecorations += modification
		grid.foreDecorations += modification
	}

	override fun removeModification(modification: BorderDecoration)
	{
		borderDecorations -= modification
		grid.foreDecorations -= modification
	}

	override fun clear() = borderDecorations.clear()

	override fun readState(mapper: PersistentStateMapper)
	{
		val borderDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["borderDecorations"]
		borderDecorationDataObjects?.forEach { dataObject ->
			val row = dataObject["row"] as Float
			val col = dataObject["col"] as Float
			val type = dataObject["type"] as BorderDecoration.Type
			addModification(BorderDecoration(grid, row, col, type))
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["borderDecorations"] = borderDecorations.map { it.dataObject }.toTypedArray()
	}
}
