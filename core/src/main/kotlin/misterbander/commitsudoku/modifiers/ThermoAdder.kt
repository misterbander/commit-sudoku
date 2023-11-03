package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2.dst2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.constraints.ThermoConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class ThermoAdder(
	private val grid: SudokuGrid,
	private val constraintsChecker: ConstraintsChecker
) : GridModifier<ThermoConstraint>
{
	var type = ThermoConstraint.Type.NORMAL
	private val thermoConstraints = GdxArray<ThermoConstraint>()
	private var currentThermoConstraint: ThermoConstraint? = null

	private var selectedRow = 0
	private var selectedCol = 0
	private val isValidIndex
		get() = if (type == ThermoConstraint.Type.DECORATION)
			selectedRow in -1..9 && selectedCol in -1..9
		else
			selectedRow in 0..8 && selectedCol in 0..8

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex)
			return
		if (button == Input.Buttons.RIGHT)
		{
			tryDeleteThermo()
			return
		}

		currentThermoConstraint = ThermoConstraint(grid, selectedRow, selectedCol, type)
		addModification(currentThermoConstraint!!)
	}

	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		if (currentThermoConstraint != null)
		{
			if (currentThermoConstraint!!.length >= 2)
			{
				currentThermoConstraint!!.unhighlight()
				constraintsChecker.check(grid.cells)
			}
			else
			{
				removeModification(currentThermoConstraint!!)
				tryDeleteThermo()
			}
		}
		currentThermoConstraint = null
	}

	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex)
			return
		val cellCenterX = grid.colToX(selectedCol.toFloat() + 0.5F)
		val cellCenterY = grid.rowToY(selectedRow.toFloat() + 0.5F)
		if (dst2(x, y, cellCenterX, cellCenterY) > grid.cellSize*grid.cellSize*0.16F)
			return

		if (currentThermoConstraint != null)
			currentThermoConstraint!!.addThermoCell(selectedRow, selectedCol)
	}

	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		if (count > 1)
		{
			selectedRow = grid.yToRow(y)
			selectedCol = grid.xToCol(x)
			tryDeleteThermo()
		}
	}

	private fun tryDeleteThermo()
	{
		for (thermoConstraint: ThermoConstraint in thermoConstraints)
		{
			if (thermoConstraint.isOver(selectedRow, selectedCol))
			{
				removeModification(thermoConstraint)
				return
			}
		}
	}

	override fun addModification(modification: ThermoConstraint)
	{
		thermoConstraints.insert(0, modification)
		constraintsChecker += modification
	}

	override fun removeModification(modification: ThermoConstraint)
	{
		thermoConstraints -= modification
		constraintsChecker -= modification
		constraintsChecker.check(grid.cells)
	}

	override fun clear() = thermoConstraints.clear()

	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val thermometers: Array<HashMap<String, Serializable>>? = mapper["thermoConstraints"]
		thermometers?.forEach {
			val thermoCells = it["cells"] as Array<Pair<Int, Int>>
			val modeStr = it["type"] as String
			val type = ThermoConstraint.Type.valueOf(modeStr)
			val thermoConstraint = ThermoConstraint(grid, thermoCells[0].first, thermoCells[0].second, type)
			thermoCells.forEachIndexed { index, (row, col) ->
				if (index == 0)
					return@forEachIndexed
				thermoConstraint.addThermoCell(row, col)
			}
			thermoConstraint.unhighlight()
			addModification(thermoConstraint)
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["thermoConstraints"] = thermoConstraints.map { it.dataObject }.toTypedArray()
	}
}
