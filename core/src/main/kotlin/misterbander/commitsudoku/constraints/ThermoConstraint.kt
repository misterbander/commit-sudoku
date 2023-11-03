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
	bulbRow: Int,
	bulbCol: Int,
	private val type: Type
) : Constraint, GridModification
{
	private var thermoStatement = CompoundStatement()
	private val thermoLine = LineDecoration(grid, bulbRow, bulbCol)
	val length: Int
		get() = thermoLine.length

	private val internalColor = Color()
	private var isHighlighted = true

	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"cells" to thermoLine.lineCells.toArray(Pair::class.java),
			"type" to type.toString()
		)

	fun addThermoCell(endRow: Int, endCol: Int) = thermoLine.addLineCell(endRow, endCol)

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
			for (i in 0 until thermoLine.lineCells.size)
			{
				for (j in i + 1 until thermoLine.lineCells.size)
				{
					val (row1, col1) = thermoLine.lineCells[i]
					val (row2, col2) = thermoLine.lineCells[j]
					val statement = "[r${row1 + 1}c${col1 + 1}]$operator[r${row2 + 1}c${col2 + 1}]"
					statementStrs.add(statement)
				}
			}
			thermoStatement = CompoundStatement(*statementStrs.toArray(String::class.java))
		}
		isHighlighted = false
	}

	fun isOver(row: Int, col: Int): Boolean = thermoLine.isOver(row, col)

	override fun check(grid: SudokuGrid): Boolean = thermoStatement.check(grid)

	override fun drawConstraint(shapeDrawer: ShapeDrawer)
	{
		val (firstRow, firstCol) = thermoLine.lineCells[0]
		val x = grid.colToX(firstCol + 0.5F)
		val y = grid.rowToY(firstRow + 0.5F)
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
