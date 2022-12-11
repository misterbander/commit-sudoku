package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import com.badlogic.gdx.math.Vector2
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

class ArrowDecoration(grid: SudokuGrid, startX: Int, startY: Int) : LineDecoration(grid, startX, startY)
{
	private val arrowVertices = GdxArray<Vector2>().apply { this += vec2() }
	private val arrowHeadVertices = GdxArray<Vector2>().apply { repeat(3) { this += vec2() } }
	
	override fun addLineCell(endI: Int, endJ: Int)
	{
		super.addLineCell(endI, endJ)
		if (lineJoints.size > arrowVertices.size)
			arrowVertices += vec2()
	}
	
	override fun draw(batch: Batch)
	{
		if (lineJoints.size < 2)
			return
		val shapeDrawer = game.shapeDrawer
		val startDirection = angle(lineJoints[0].first, lineJoints[0].second, lineJoints[1].first, lineJoints[1].second)
		val endDirection = angle(
			lineJoints[lineJoints.size - 2].first, lineJoints[lineJoints.size - 2].second,
			lineJoints.peek().first, lineJoints.peek().second
		)
		val offsetX = cosDeg(startDirection)*28F
		val offsetY = sinDeg(startDirection)*28F
		// Convert grid coordinates to stage coordinates
		arrowVertices.forEachIndexed { index, vec2 ->
			val arrowJoint = lineJoints[index]
			vec2.set(
				grid.iToX(arrowJoint.first + 0.5F) + if (index == 0) offsetX else 0F,
				grid.jToY(arrowJoint.second + 0.5F) + if (index == 0) offsetY else 0F
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
