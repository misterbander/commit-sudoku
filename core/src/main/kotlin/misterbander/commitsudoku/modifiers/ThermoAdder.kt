package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.math.vec2
import misterbander.commitsudoku.constraints.ThermoConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import kotlin.math.abs


class ThermoAdder(grid: SudokuGrid) : GridModfier(grid)
{
	private val thermoConstraints: GdxArray<ThermoConstraint> = GdxArray()
	private var currentThermoConstraint: ThermoConstraint? = null
	
	private var highlightI = 0
	private var highlightJ = 0
	private var prevI = -1
	private var prevJ = -1
	private val isValidIndex
		get() = highlightI in 0..8 && highlightJ in 0..8
	
	private val distVector = vec2()
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		if (!isValidIndex)
			return
		if (button == Input.Buttons.RIGHT)
		{
			tryDeleteThermo()
			return
		}
		
		val thermoConstraint = ThermoConstraint(grid, highlightI, highlightJ)
		thermoConstraints.insert(0, thermoConstraint)
		grid.constraintsChecker += thermoConstraint
		currentThermoConstraint = thermoConstraint
		prevI = highlightI
		prevJ = highlightJ
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		if (currentThermoConstraint != null)
		{
			if (currentThermoConstraint!!.length > 1)
				currentThermoConstraint!!.generateThermoStatement()
			else
				tryDeleteThermo()
		}
		currentThermoConstraint = null
		prevI = -1
		prevJ = -1
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		if (!isValidIndex || !(abs(highlightI - prevI) <= 1 && abs(highlightJ - prevJ) <= 1))
			return
		distVector.set(grid.iToX(highlightI.toFloat() + 0.5F), grid.jToY(highlightJ.toFloat() + 0.5F))
		if (distVector.dst2(x, y) > grid.cellSize*grid.cellSize*0.16F)
			return
		
		if (currentThermoConstraint != null)
		{
			currentThermoConstraint!!.addThermoCell(highlightI, highlightJ)
			prevI = highlightI
			prevJ = highlightJ
		}
	}
	
	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		if (count > 1)
		{
			highlightI = grid.xToI(x)
			highlightJ = grid.yToJ(y)
			tryDeleteThermo()
		}
	}
	
	private fun tryDeleteThermo()
	{
		thermoConstraints.forEach { thermoConstraint ->
			if (thermoConstraint.isOver(highlightI, highlightJ))
			{
				thermoConstraints -= thermoConstraint
				grid.constraintsChecker -= thermoConstraint
				grid.constraintsChecker.check()
				return
			}
		}
	}
	
	override fun clear()
	{
		thermoConstraints.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val thermometers: Array<Array<Pair<Int, Int>>>? = mapper["thermometers"]
		thermometers?.forEach { thermoCells ->
			val thermoConstraint = ThermoConstraint(grid, thermoCells[0].first, thermoCells[0].second)
			thermoConstraints.insert(0, thermoConstraint)
			grid.constraintsChecker += thermoConstraint
			thermoCells.forEachIndexed { index, pair ->
				if (index == 0)
					return@forEachIndexed
				thermoConstraint.addThermoCell(pair.first, pair.second)
			}
			thermoConstraint.generateThermoStatement()
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["thermometers"] = thermoConstraints.map { thermoConstraint -> thermoConstraint.dataObject }.toTypedArray()
	}
	
	override fun draw(batch: Batch) {}
}
