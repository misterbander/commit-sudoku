package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg
import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import ktx.math.vec2
import misterbander.commitsudoku.decorationColor2
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.angle
import java.io.Serializable

class CircleDecoration(
	grid: SudokuGrid,
	private val i1: Int,
	private val j1: Int,
	private val radius: Float,
) : Decoration(grid)
{
	var i2: Int = i1
	var j2: Int = j1
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("i1" to i1, "j1" to j1, "i2" to i2, "j2" to j2, "radius" to radius)
	
	private val vertices = GdxArray<Vector2>().apply {
		repeat(122) { this += vec2() }
	}
	
	fun isOver(i: Int, j: Int): Boolean =
		Intersector.distanceSegmentPoint(
			i1.toFloat(),
			j1.toFloat(),
			i2.toFloat(),
			j2.toFloat(),
			i.toFloat(),
			j.toFloat()
		) < radius/grid.cellSize
	
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		val x1 = grid.iToX(i1 + 0.5F)
		val y1 = grid.jToY(j1 + 0.5F)
		val x2 = grid.iToX(i2 + 0.5F)
		val y2 = grid.jToY(j2 + 0.5F)
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
