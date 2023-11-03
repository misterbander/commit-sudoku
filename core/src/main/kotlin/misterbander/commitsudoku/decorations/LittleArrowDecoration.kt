package misterbander.commitsudoku.decorations

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import ktx.collections.*
import ktx.math.minusAssign
import ktx.math.vec2
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.tempVec
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class LittleArrowDecoration(
	grid: SudokuGrid,
	val row: Int,
	val col: Int,
	pointingDirection: Float = 315F
) : Decoration(grid)
{
	private val arrowHeadVertices = gdxArrayOf(vec2(), vec2(), vec2())
	var pointingDirection = pointingDirection
		set(value)
		{
			field = value.mod(360F)
		}

	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("row" to row, "col" to col, "pointingDirection" to pointingDirection)

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x = grid.colToX(col + 0.5F) + when
		{
			MathUtils.isEqual(cosDeg(pointingDirection), 0F) -> 0F
			cosDeg(pointingDirection) > 0 -> 28F
			else -> -28F
		}
		val y = grid.rowToY(row + 0.5F) + when
		{
			MathUtils.isEqual(sinDeg(pointingDirection), 0F) -> 0F
			sinDeg(pointingDirection) > 0 -> 28F
			else -> -28F
		}

		// Draw arrow
		shapeDrawer.setColor(color ?: primaryColor)
		tempVec.set(16F, 0F).setAngleDeg(pointingDirection)
		shapeDrawer.line(x, y, x - tempVec.x, y - tempVec.y, 2F)

		// Draw arrow head
		tempVec.set(16F, 0F).setAngleDeg(pointingDirection + 45F)
		arrowHeadVertices[0].set(x, y) -= tempVec
		arrowHeadVertices[1].set(x, y)
		tempVec.set(16F, 0F).setAngleDeg(pointingDirection - 45F)
		arrowHeadVertices[2].set(x, y) -= tempVec
		shapeDrawer.path(arrowHeadVertices, 2F, JoinType.POINTY, true)
	}
}
