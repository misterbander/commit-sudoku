package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.collections.GdxArray
import ktx.style.*
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
	private val thermoLine = LineDecoration(grid, bulbI, bulbJ)
	val length
		get() = thermoLine.length
	
	private val internalColor = Color()
	private var isHighlighted = true
	
	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"cells" to thermoLine.lineCells.toArray(Pair::class.java),
			"operator" to operator
		)
	
	fun addThermoCell(endI: Int, endJ: Int)
	{
		thermoLine.addLineCell(endI, endJ)
	}
	
	fun generateThermoStatement()
	{
		if (operator != "")
		{
			val statementStrs: GdxArray<String> = GdxArray()
			for (k in 0 until thermoLine.lineCells.size)
			{
				for (l in 0 until k)
				{
					val i1 = thermoLine.lineCells[l].first
					val j1 = thermoLine.lineCells[l].second
					val i2 = thermoLine.lineCells[k].first
					val j2 = thermoLine.lineCells[k].second
					val statement = "[r${(9 - j1)}c${(i1 + 1)}]$operator[r${(9 - j2)}c${(i2 + 1)}]"
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
		return thermoLine.isOver(i, j)
	}
	
	override fun check(): Boolean
	{
		return thermoStatement.check()
	}
	
	override fun drawConstraint(batch: Batch)
	{
		val shapeDrawer = grid.game.shapeDrawer
		val x = grid.iToX(thermoLine.lineCells[0].first + 0.5F)
		val y = grid.jToY(thermoLine.lineCells[0].second + 0.5F)
		internalColor.blend(
			if (isHighlighted) grid.game.skin["selectedcolor"] else grid.game.skin["decorationcolor1"],
			grid.game.skin["backgroundcolor"]
		)
		shapeDrawer.filledCircle(x, y, grid.cellSize*0.3F, internalColor)
		thermoLine.color = internalColor
		thermoLine.draw(batch)
	}
}
