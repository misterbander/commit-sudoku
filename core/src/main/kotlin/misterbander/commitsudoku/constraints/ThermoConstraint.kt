package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.g2d.Batch
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.CircleDecoration
import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid


class ThermoConstraint(private val grid: SudokuGrid, bulbI: Int, bulbJ: Int) : Constraint
{
	var thermoStatement: CompoundStatement = CompoundStatement(grid.cells)
	
	private val bulb: CircleDecoration = CircleDecoration(
		grid,
		bulbI,
		bulbJ,
		grid.cellSize*0.3F,
	)
	private val thermoCells: GdxArray<SudokuGrid.Cell> = gdxArrayOf(grid.cells[bulbI][bulbJ])
	private var lastJointCell: SudokuGrid.Cell = thermoCells[0]
	private var lastJointDI = 0
	private var lastJointDJ = 0
	private val thermoLines: GdxArray<LineDecoration> = GdxArray()
	
	val length
		get() = thermoCells.size
	private var isHighlighted = true
	
	val dataObject: Array<Pair<Int, Int>>
		get() = thermoCells.map { cell -> Pair(cell.i, cell.j) }.toTypedArray()
	
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
		val statementStrs: GdxArray<String> = GdxArray()
		for (i in 0 until thermoCells.size)
		{
			for (j in 0 until i)
			{
				val statement = "[r${(thermoCells[j].j + 1)}c${(thermoCells[j].i + 1)}]<[r${(thermoCells[i].j + 1)}c${(thermoCells[i].i + 1)}]"
				statementStrs.add(statement)
			}
		}
		thermoStatement = CompoundStatement(grid.cells, *statementStrs.toArray(String::class.java))
		isHighlighted = false
		grid.constraintsChecker.check()
	}
	
	fun isOver(i: Int, j: Int): Boolean
	{
		if (bulb.i == i && bulb.j == j)
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
		bulb.color = if (isHighlighted) grid.game.skin["selectedthermocolor"] else grid.game.skin["thermocolor"]
		bulb.draw(batch)
		for (line in thermoLines)
		{
			line.color = if (isHighlighted) grid.game.skin["selectedthermocolor"] else null
			line.draw(batch)
		}
	}
}
