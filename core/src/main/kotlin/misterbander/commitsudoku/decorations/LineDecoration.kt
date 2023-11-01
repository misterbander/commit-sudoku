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

open class LineDecoration(grid: SudokuGrid, startX: Int, startY: Int) : Decoration(grid)
{
	val lineCells = gdxArrayOf(Pair(startX, startY))
	protected val lineJoints = gdxArrayOf(Pair(startX, startY))
	private var lastJointPos: Pair<Int, Int> = lineCells[0]
	private var lastJointDI = 0
	private var lastJointDJ = 0
	val length: Int
		get() = lineCells.size

	protected val internalColor = Color()
	var isHighlighted = false

	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("cells" to lineCells.toArray(Pair::class.java))

	open fun addLineCell(endI: Int, endJ: Int)
	{
		val lastArrowCell: Pair<Int, Int> = lineCells.peek()
		val nextToLastArrowCell: Pair<Int, Int>? = if (lineCells.size > 1) lineCells[lineCells.size - 2] else null
		if (max(abs(endI - lastArrowCell.first), abs(endJ - lastArrowCell.second)) != 1
			|| nextToLastArrowCell != null && endI == nextToLastArrowCell.first && endJ == nextToLastArrowCell.second)
			return
		lineCells += Pair(endI, endJ)

		// Check if new cell forms a line with the last arrow joint
		val di = endI.compareTo(lastArrowCell.first)
		val dj = endJ.compareTo(lastArrowCell.second)

		if (di == lastJointDI && dj == lastJointDJ
			&& (endI == lastJointPos.first || endJ == lastJointPos.second || endI - lastJointPos.first == endJ - lastJointPos.second))
			lineJoints.pop()
		else
		{
			lastJointDI = endI.compareTo(lastArrowCell.first)
			lastJointDJ = endJ.compareTo(lastArrowCell.second)
			lastJointPos = lastArrowCell
		}
		lineJoints += Pair(endI, endJ)
	}

	fun isOver(i: Int, j: Int): Boolean
	{
		for (k in 0..lineJoints.size - 2)
		{
			val i1 = lineJoints[k].first
			val j1 = lineJoints[k].second
			val i2 = lineJoints[k + 1].first
			val j2 = lineJoints[k + 1].second
			if (Intersector.distanceSegmentPoint(
					i1.toFloat(),
					j1.toFloat(),
					i2.toFloat(),
					j2.toFloat(),
					i.toFloat(),
					j.toFloat()
				) < 16/grid.cellSize)
				return true
		}
		return false
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		if (lineJoints.size < 2)
			return
		for (i in 0..lineJoints.size - 2)
		{
			val x1 = grid.iToX(lineJoints[i].first + 0.5F)
			val y1 = grid.jToY(lineJoints[i].second + 0.5F)
			val x2 = grid.iToX(lineJoints[i + 1].first + 0.5F)
			val y2 = grid.jToY(lineJoints[i + 1].second + 0.5F)

			shapeDrawer.roundedLine(
				x1, y1, x2, y2,
				internalColor.blend(
					src = if (isHighlighted) selectedColor else color ?: decorationColor1,
					dest = backgroundColor
				),
				16F
			)
		}
	}
}
