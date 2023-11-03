package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import ktx.collections.*
import ktx.math.minusAssign
import ktx.math.vec2
import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.decorationColor2
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.angle
import misterbander.gframework.util.blend
import misterbander.gframework.util.tempVec
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer

class ArrowDecoration(grid: SudokuGrid, startRow: Int, startCol: Int) : LineDecoration(grid, startRow, startCol)
{
	private val arrowVertices = gdxArrayOf(vec2())
	private val arrowHeadVertices = gdxArrayOf(vec2(), vec2(), vec2())

	override fun generateJoints()
	{
		super.generateJoints()
		arrowVertices.clear()
		repeat(lineJoints.size) { arrowVertices += vec2() }
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		if (lineJoints.size < 2)
			return
		if (lineJoints.size < 2)
			return
		val (startRow0, startCol0) = lineJoints[0]
		val (startRow1, startCol1) = lineJoints[1]
		val (endRow0, endCol0) = lineJoints[lineJoints.size - 2]
		val (endRow1, endCol1) = lineJoints.peek()
		val startDirection = -angle(startCol0, startRow0, startCol1, startRow1)
		val endDirection = -angle(endCol0, endRow0, endCol1, endRow1)
		val offsetX = cosDeg(startDirection)*28F
		val offsetY = sinDeg(startDirection)*28F
		lineJoints.forEachIndexed { index, (jointRow, jointCol) ->
			arrowVertices[index].set(
				grid.colToX(jointCol.toFloat() + 0.5F) + if (index == 0) offsetX else 0F,
				grid.rowToY(jointRow.toFloat() + 0.5F) + if (index == 0) offsetY else 0F
			)
		}
		shapeDrawer.setColor(
			if (isHighlighted)
				Color.ORANGE
			else internalColor.blend(
				src = color ?: decorationColor2,
				dest = backgroundColor
			)
		)
		shapeDrawer.path(arrowVertices, 2F, JoinType.SMOOTH, true)

		// Draw arrow head
		tempVec.set(16F, 0F).setAngleDeg(endDirection + 45F)
		arrowHeadVertices[0].set(arrowVertices.peek()) -= tempVec
		arrowHeadVertices[1].set(arrowVertices.peek())
		tempVec.set(16F, 0F).setAngleDeg(endDirection - 45F)
		arrowHeadVertices[2].set(arrowVertices.peek()) -= tempVec
		shapeDrawer.path(arrowHeadVertices, 2F, JoinType.POINTY, true)
	}
}
