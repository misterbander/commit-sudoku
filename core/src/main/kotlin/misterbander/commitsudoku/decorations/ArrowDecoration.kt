package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.plusAssign
import ktx.math.vec2
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.angle
import misterbander.gframework.util.tempVec
import space.earlygrey.shapedrawer.JoinType
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max


class ArrowDecoration(grid: SudokuGrid, startX: Int, startY: Int) : Decoration(grid)
{
	private val arrowCells: GdxArray<Pair<Int, Int>> = gdxArrayOf(Pair(startX, startY))
	private val arrowJoints: GdxArray<Pair<Int, Int>> = gdxArrayOf(Pair(startX, startY))
	private val arrowVertices = GdxArray<Vector2>().apply {
		this += vec2()
	}
	private var lastJointPos = arrowCells[0]
	private var lastJointDI = 0
	private var lastJointDJ = 0
	val length
		get() = arrowCells.size
	
	override var color: Color? = game.skin["decorationcolor2"]
	
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("cells" to arrowCells.toArray(Pair::class.java))
	
	fun addArrowCell(endI: Int, endJ: Int)
	{
		val lastArrowCell: Pair<Int, Int> = arrowCells.peek()
		val nextToLastArrowCell: Pair<Int, Int>? = if (arrowCells.size > 1) arrowCells[arrowCells.size - 2] else null
		if (max(abs(endI - lastArrowCell.first), abs(endJ - lastArrowCell.second)) != 1
			|| nextToLastArrowCell != null && endI == nextToLastArrowCell.first && endJ == nextToLastArrowCell.second)
			return
		arrowCells += Pair(endI, endJ)
		
		// Check if new cell forms a line with the last arrow joint
		val di = endI.compareTo(lastArrowCell.first)
		val dj = endJ.compareTo(lastArrowCell.second)
		
		if (di == lastJointDI && dj == lastJointDJ
			&& (endI == lastJointPos.first || endJ == lastJointPos.second || endI - lastJointPos.first == endJ - lastJointPos.second))
			arrowJoints.pop()
		else
		{
			lastJointDI = endI.compareTo(lastArrowCell.first)
			lastJointDJ = endJ.compareTo(lastArrowCell.second)
			lastJointPos = lastArrowCell
			arrowVertices += vec2()
		}
		arrowJoints += Pair(endI, endJ)
	}
	
	fun isOver(i: Int, j: Int): Boolean
	{
		for (k in 0..arrowJoints.size - 2)
		{
			val i1 = arrowJoints[k].first
			val j1 = arrowJoints[k].second
			val i2 = arrowJoints[k + 1].first
			val j2 = arrowJoints[k + 1].second
			if (Intersector.distanceSegmentPoint(i1.toFloat(), j1.toFloat(), i2.toFloat(), j2.toFloat(), i.toFloat(), j.toFloat()) < 16/grid.cellSize)
				return true
		}
		return false
	}
	
	override fun draw(batch: Batch)
	{
		if (arrowJoints.size < 2)
			return
		val shapeDrawer = game.shapeDrawer
		val startDirection = angle(arrowJoints[0].first, arrowJoints[0].second, arrowJoints[1].first, arrowJoints[1].second)
		val endDirection = angle(
			arrowJoints[arrowJoints.size - 2].first, arrowJoints[arrowJoints.size - 2].second,
			arrowJoints.peek().first, arrowJoints.peek().second
		)
		val offsetX = cosDeg(startDirection)*28F
		val offsetY = sinDeg(startDirection)*28F
		// Convert grid coordinates to stage coordinates
		arrowVertices.forEachIndexed { index, vec2 ->
			val arrowJoint = arrowJoints[index]
			vec2.set(
				grid.iToX(arrowJoint.first + 0.5F) + if (index == 0) offsetX else 0F,
				grid.jToY(arrowJoint.second + 0.5F) + if (index == 0) offsetY else 0F
			)
		}
		shapeDrawer.setColor(color)
		shapeDrawer.path(arrowVertices, 2F, JoinType.SMOOTH, true)
		
		// Draw arrow head
		tempVec.set(16F, 0F).setAngle(endDirection + 135F)
		shapeDrawer.line(arrowVertices.peek().x, arrowVertices.peek().y, arrowVertices.peek().x + tempVec.x, arrowVertices.peek().y + tempVec.y, 2F)
		tempVec.set(16F, 0F).setAngle(endDirection - 135F)
		shapeDrawer.line(arrowVertices.peek().x, arrowVertices.peek().y, arrowVertices.peek().x + tempVec.x, arrowVertices.peek().y + tempVec.y, 2F)
	}
}
