package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2.dst2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import misterbander.commitsudoku.constraints.ThermoConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class ThermoAdder(grid: SudokuGrid) : GridModfier<ThermoConstraint>(grid)
{
	private val thermoConstraints: GdxArray<ThermoConstraint> = GdxArray()
	private var currentThermoConstraint: ThermoConstraint? = null
	
	override val isValidIndex
		get() = if (grid.panel.screen.toolbar.thermoMultibuttonMenu.checkedIndex == 2)
			selectI in -1..9 && selectJ in -1..9
		else
			selectI in 0..8 && selectJ in 0..8
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		if (button == Input.Buttons.RIGHT)
		{
			tryDeleteThermo()
			return
		}
		
		currentThermoConstraint = ThermoConstraint(grid, selectI, selectJ)
		addModification(currentThermoConstraint!!)
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		if (currentThermoConstraint != null)
		{
			if (currentThermoConstraint!!.length >= 2)
				currentThermoConstraint!!.generateThermoStatement()
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
		updateSelect(x, y)
		if (!isValidIndex)
			return
		val cellCenterX = grid.iToX(selectI.toFloat() + 0.5F)
		val cellCenterY = grid.jToY(selectJ.toFloat() + 0.5F)
		if (dst2(x, y, cellCenterX, cellCenterY) > grid.cellSize*grid.cellSize*0.16F)
			return
		
		if (currentThermoConstraint != null)
			currentThermoConstraint!!.addThermoCell(selectI, selectJ)
	}
	
	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		if (count > 1)
		{
			updateSelect(x, y)
			tryDeleteThermo()
		}
	}
	
	private fun tryDeleteThermo()
	{
		for (thermoConstraint: ThermoConstraint in thermoConstraints)
		{
			if (thermoConstraint.isOver(selectI, selectJ))
			{
				removeModification(thermoConstraint)
				return
			}
		}
	}
	
	override fun addModification(modification: ThermoConstraint)
	{
		thermoConstraints.insert(0, modification)
		grid.constraintsChecker += modification
	}
	
	override fun removeModification(modification: ThermoConstraint)
	{
		thermoConstraints -= modification
		grid.constraintsChecker -= modification
		grid.constraintsChecker.check()
	}
	
	override fun clear() = thermoConstraints.clear()
	
	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val thermometers: Array<HashMap<String, Serializable>>? = mapper["thermoConstraints"]
		thermometers?.forEach {
			val thermoCells = it["cells"] as Array<Pair<Int, Int>>
			val thermoConstraint = ThermoConstraint(grid, thermoCells[0].first, thermoCells[0].second)
			thermoCells.forEachIndexed { index, pair ->
				if (index == 0)
					return@forEachIndexed
				thermoConstraint.addThermoCell(pair.first, pair.second)
			}
			thermoConstraint.operator = it["operator"] as String
			thermoConstraint.generateThermoStatement()
			addModification(thermoConstraint)
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["thermoConstraints"] = thermoConstraints.map { it.dataObject }.toTypedArray()
	}
}
