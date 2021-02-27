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
	var thermoStatement: CompoundStatement = CompoundStatement(grid.cells)
	var operator = when (grid.panel.screen.toolbar.thermoMultibuttonMenu.checkedIndex)
	{
		0 -> "<"
		1 -> "<="
		else -> ""
	}
	private val thermoCells: GdxArray<SudokuGrid.Cell> = gdxArrayOf(grid.cells[bulbI][bulbJ])
	private var lastJointCell: SudokuGrid.Cell = thermoCells[0]
	private var lastJointDI = 0
	private var lastJointDJ = 0
	private val thermoLines: GdxArray<LineDecoration> = GdxArray()
	val length
		get() = thermoCells.size
	
	private val internalColor = Color()
	private var isHighlighted = true
	
	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"cells" to thermoCells.map { cell -> Pair(cell.i, cell.j) }.toTypedArray(),
			"operator" to operator
		)
	
	fun addThermoCell(endI: Int, endJ: Int)
	{
		val lastThermoCell: SudokuGrid.Cell = thermoCells[thermoCells.size - 1]
		if (endI == lastThermoCell.i && endJ == lastThermoCell.j)
			return
		thermoCells += grid.cells[endI][endJ]
		
		// Check if new cell forms a line with the last thermometer line
		val di = endI.compareTo(lastThermoCell.i)
		val dj = endJ.compareTo(lastThermoCell.j)
		
		thermoLines += if (di == lastJointDI && dj == lastJointDJ
			&& (endI == lastJointCell.i || endJ == lastJointCell.j || endI - lastJointCell.i == endJ - lastJointCell.j))
		{
			thermoLines.pop()
			LineDecoration(grid, lastJointCell.i, lastJointCell.j, endI, endJ)
		}
		else
		{
			lastJointDI = endI.compareTo(lastThermoCell.i)
			lastJointDJ = endJ.compareTo(lastThermoCell.j)
			lastJointCell = lastThermoCell
			LineDecoration(grid, lastThermoCell.i, lastThermoCell.j, endI, endJ)
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
					val statement = "[r${(thermoCells[j].j + 1)}c${(thermoCells[j].i + 1)}]$operator[r${(thermoCells[i].j + 1)}c${(thermoCells[i].i + 1)}]"
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
		if (thermoCells[0].i == i && thermoCells[0].j == j)
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
		val x = grid.iToX(thermoCells[0].i + 0.5F)
		val y = grid.jToY(thermoCells[0].j + 0.5F)
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
