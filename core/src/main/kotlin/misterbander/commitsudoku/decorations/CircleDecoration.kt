package misterbander.commitsudoku.decorations

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import ktx.math.vec2
import misterbander.commitsudoku.decorationColor2
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.angle
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class CircleDecoration(
	grid: SudokuGrid,
	private val row1: Int,
	private val col1: Int,
	private val radius: Float,
) : Decoration(grid)
{
	var row2: Int = row1
	var col2: Int = col1
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("row1" to row1, "col1" to col1, "row2" to row2, "col2" to col2, "radius" to radius)

	private val vertices = GdxArray<Vector2>().apply {
		repeat(122) { this += vec2() }
	}

	fun isOver(row: Int, col: Int): Boolean =
		Intersector.distanceSegmentPoint(
			col1.toFloat(),
			row1.toFloat(),
			col2.toFloat(),
			row2.toFloat(),
			col.toFloat(),
			row.toFloat()
		) < radius/grid.cellSize

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x1 = grid.colToX(col1 + 0.5F)
		val y1 = grid.rowToY(row1 + 0.5F)
		val x2 = grid.colToX(col2 + 0.5F)
		val y2 = grid.rowToY(row2 + 0.5F)
		for (i in 0 until vertices.size)
		{
			val dir = angle(x1, y1, x2, y2)
			if (i < 61)
			{
				val theta = dir + 90 + 180*i/60F
				vertices[i].set(x1 + radius*cosDeg(theta), y1 + radius*sinDeg(theta))
			}
			else
			{
				val theta = dir - 90 + 180*(i - 61)/60F
				vertices[i].set(x2 + radius*cosDeg(theta), y2 + radius*sinDeg(theta))
			}
		}
		shapeDrawer.setColor(color ?: decorationColor2)
		shapeDrawer.path(vertices, 2F, false)
	}
}
