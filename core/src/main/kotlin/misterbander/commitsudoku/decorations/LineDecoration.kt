package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import ktx.collections.*
import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.decorationColor1
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.selectedColor
import misterbander.gframework.util.blend
import misterbander.gframework.util.roundedLine
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max

open class LineDecoration(grid: SudokuGrid, startRow: Int, startCol: Int) : Decoration(grid)
{
	val lineCells = gdxArrayOf(Pair(startRow, startCol))
	protected val lineJoints = gdxArrayOf(Pair(startRow, startCol))
	val length: Int
		get() = lineCells.size

	protected val internalColor = Color()
	var isHighlighted = false

	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("cells" to lineCells.toArray(Pair::class.java))

	fun addLineCell(row: Int, col: Int)
	{
		val (lastRow, lastCol) = lineCells.peek()
		val secondLastCell: Pair<Int, Int>? = if (lineCells.size > 1) lineCells[lineCells.size - 2] else null
		val isOneDistanceAway = max(abs(row - lastRow), abs(col - lastCol)) == 1
		val isSameWithLastLineCell =
			secondLastCell != null && row == secondLastCell.first && col == secondLastCell.second
		if (!isOneDistanceAway || isSameWithLastLineCell) return
		lineCells += Pair(row, col)
		generateJoints()
	}

	protected open fun generateJoints()
	{
		lineJoints.clear()
		var prevDRow = 0
		var prevDCol = 0
		for (i in 0 until lineCells.size - 1)
		{
			val cell1: Pair<Int, Int> = lineCells[i]
			val cell2: Pair<Int, Int> = lineCells[i + 1]
			val (row1, col1) = cell1
			val (row2, col2) = cell2
			val dRow = row2 - row1
			val dCol = col2 - col1
			if (dRow != prevDRow || dCol != prevDCol) lineJoints += cell1
			prevDRow = dRow
			prevDCol = dCol
		}
		lineJoints += lineCells.peek()
	}

	fun isOver(row: Int, col: Int): Boolean
	{
		for (i in 0 until lineJoints.size - 1)
		{
			val (row1, col1) = lineJoints[i]
			val (row2, col2) = lineJoints[i + 1]
			if (Intersector.distanceSegmentPoint(
					col1.toFloat(),
					row1.toFloat(),
					col2.toFloat(),
					row2.toFloat(),
					col.toFloat(),
					row.toFloat()
				) < 16/grid.cellSize)
				return true
		}
		return false
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		for (i in 0 until lineJoints.size - 1)
		{
			val (row1, col1) = lineJoints[i]
			val (row2, col2) = lineJoints[i + 1]
			val x1 = grid.colToX(col1 + 0.5F)
			val y1 = grid.rowToY(row1 + 0.5F)
			val x2 = grid.colToX(col2 + 0.5F)
			val y2 = grid.rowToY(row2 + 0.5F)

			shapeDrawer.roundedLine(
				x1, y1, x2, y2, internalColor.blend(
					src = if (isHighlighted) selectedColor else color ?: decorationColor1, dest = backgroundColor
				), 16F
			)
		}
	}
}
