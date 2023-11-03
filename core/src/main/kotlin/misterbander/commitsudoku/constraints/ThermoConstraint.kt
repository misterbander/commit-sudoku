package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
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

	fun unhighlight()
	{
		isHighlighted = false
	}

	fun isOver(row: Int, col: Int): Boolean = thermoLine.isOver(row, col)

	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		if (type == Type.DECORATION)
			return true
		var correctFlag = true

		for (i in 0 until thermoLine.lineCells.size)
		{
			for (j in i + 1 until thermoLine.lineCells.size)
			{
				val (row1, col1) = thermoLine.lineCells[i]
				val (row2, col2) = thermoLine.lineCells[j]
				val cell1 = cells[row1][col1]
				val cell2 = cells[row2][col2]
				if (cell1.digit == 0 || cell2.digit == 0)
					continue
				if (type == Type.NORMAL && cell1.digit >= cell2.digit || type == Type.SLOW && cell1.digit > cell2.digit)
				{
					cell1.isCorrect = false
					cell2.isCorrect = false
					correctFlag = false
				}
			}
		}
		return correctFlag
	}

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
