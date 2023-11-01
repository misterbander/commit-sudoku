package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
import ktx.collections.*
import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.decorationColor1
import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.modifiers.GridModification
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.blend
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class ThermoConstraint(
	private val grid: SudokuGrid,
	bulbI: Int,
	bulbJ: Int,
	private val type: Type
) : Constraint, GridModification
{
	private var thermoStatement = CompoundStatement()
	private val thermoLine = LineDecoration(grid, bulbI, bulbJ)
	val length: Int
		get() = thermoLine.length

	private val internalColor = Color()
	private var isHighlighted = true

	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"cells" to thermoLine.lineCells.toArray(Pair::class.java),
			"type" to type.toString()
		)

	fun addThermoCell(endI: Int, endJ: Int) = thermoLine.addLineCell(endI, endJ)

	fun generateThermoStatement()
	{
		val operator = when (type)
		{
			Type.NORMAL -> "<"
			Type.SLOW -> "<="
			else -> null
		}
		if (operator != null)
		{
			val statementStrs = GdxArray<String>()
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
			thermoStatement = CompoundStatement(*statementStrs.toArray(String::class.java))
		}
		isHighlighted = false
	}

	fun isOver(i: Int, j: Int): Boolean = thermoLine.isOver(i, j)

	override fun check(grid: SudokuGrid): Boolean = thermoStatement.check(grid)

	override fun drawConstraint(shapeDrawer: ShapeDrawer)
	{
		val x = grid.iToX(thermoLine.lineCells[0].first + 0.5F)
		val y = grid.jToY(thermoLine.lineCells[0].second + 0.5F)
		internalColor.blend(
			if (isHighlighted) selectedColor else decorationColor1,
			backgroundColor
		)
		shapeDrawer.filledCircle(x, y, grid.cellSize*0.3F, internalColor)
		thermoLine.color = internalColor
		thermoLine.draw(shapeDrawer)
	}

	enum class Type
	{
		NORMAL, SLOW, DECORATION
	}
}
