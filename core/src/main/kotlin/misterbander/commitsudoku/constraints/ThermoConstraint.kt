package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.modifiers.GridModification
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.blend
import java.io.Serializable


class ThermoConstraint(private val grid: SudokuGrid, bulbI: Int, bulbJ: Int) : Constraint, GridModification
{
	private var thermoStatement: CompoundStatement = CompoundStatement(grid.cells)
	var operator = when (grid.panel.screen.toolbar.thermoMultibuttonMenu.checkedIndex)
	{
		0 -> "<"
		1 -> "<="
		else -> ""
	}
	private val thermoCells: GdxArray<Pair<Int, Int>> = gdxArrayOf(Pair(bulbI, bulbJ))
	private var lastJointPos = thermoCells[0]
	private var lastJointDI = 0
	private var lastJointDJ = 0
	private val thermoLines: GdxArray<LineDecoration> = GdxArray()
	val length
		get() = thermoCells.size
	
	private val internalColor = Color()
	private var isHighlighted = true
	
	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"cells" to thermoCells.toArray(Pair::class.java),
			"operator" to operator
		)
	
	fun addThermoCell(endI: Int, endJ: Int)
	{
		val lastThermoCell: Pair<Int, Int> = thermoCells[thermoCells.size - 1]
		if (endI == lastThermoCell.first && endJ == lastThermoCell.second)
			return
		thermoCells += Pair(endI, endJ)
		
		// Check if new cell forms a line with the last thermometer line
		val di = endI.compareTo(lastThermoCell.first)
		val dj = endJ.compareTo(lastThermoCell.second)
		
		thermoLines += if (di == lastJointDI && dj == lastJointDJ
			&& (endI == lastJointPos.first || endJ == lastJointPos.second || endI - lastJointPos.first == endJ - lastJointPos.second))
		{
			thermoLines.pop()
			LineDecoration(grid, lastJointPos.first, lastJointPos.second, endI, endJ)
		}
		else
		{
			lastJointDI = endI.compareTo(lastThermoCell.first)
			lastJointDJ = endJ.compareTo(lastThermoCell.second)
			lastJointPos = lastThermoCell
			LineDecoration(grid, lastThermoCell.first, lastThermoCell.second, endI, endJ)
		}
	}
	
	fun generateThermoStatement()
	{
		if (operator != "")
		{
			val statementStrs: GdxArray<String> = GdxArray()
			for (i in 0 until thermoCells.size)
			{
				for (j in 0 until i)
				{
					val statement = "[r${(thermoCells[j].second + 1)}c${(thermoCells[j].first + 1)}]$operator[r${(thermoCells[i].second + 1)}c${(thermoCells[i].first + 1)}]"
					statementStrs.add(statement)
				}
			}
			thermoStatement = CompoundStatement(grid.cells, *statementStrs.toArray(String::class.java))
		}
		isHighlighted = false
		grid.constraintsChecker.check()
	}
	
	fun isOver(i: Int, j: Int): Boolean
	{
		if (thermoCells[0].first == i && thermoCells[0].second == j)
			return true
		thermoLines.forEach { line ->
			if (line.isOver(i, j))
				return true
		}
		return false
	}
	
	override fun check(): Boolean
	{
		return thermoStatement.check()
	}
	
	override fun drawConstraint(batch: Batch)
	{
		val shapeDrawer = grid.game.shapeDrawer
		val x = grid.iToX(thermoCells[0].first + 0.5F)
		val y = grid.jToY(thermoCells[0].second + 0.5F)
		internalColor.blend(
			if (isHighlighted) grid.game.skin["selectedcolor"] else grid.game.skin["defaultdecorationcolor"],
			grid.game.skin["backgroundcolor"]
		)
		shapeDrawer.filledCircle(x, y, grid.cellSize*0.3F, internalColor)
		for (line in thermoLines)
		{
			line.color = if (isHighlighted) grid.game.skin["selectedcolor"] else null
			line.draw(batch)
		}
	}
}
