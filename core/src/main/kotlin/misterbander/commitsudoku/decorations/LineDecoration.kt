package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Intersector
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.roundedLine
import java.io.Serializable

class LineDecoration(
	grid: SudokuGrid,
	private val i1: Int,
	private val j1: Int,
	private val i2: Int,
	private val j2: Int
) : Decoration(grid)
{
	var color: Color? = null
	override val dataObject: HashMap<String, Serializable> = hashMapOf("i1" to i1, "j1" to j1, "i2" to i2, "j2" to j2)
	
	fun isOver(i: Int, j: Int): Boolean
	{
		return Intersector.distanceSegmentPoint(i1.toFloat(), j1.toFloat(), i2.toFloat(), j2.toFloat(), i.toFloat(), j.toFloat()) < 16/grid.cellSize
	}
	
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		val x1 = grid.iToX(i1 + 0.5F)
		val y1 = grid.jToY(j1 + 0.5F)
		val x2 = grid.iToX(i2 + 0.5F)
		val y2 = grid.jToY(j2 + 0.5F)
		shapeDrawer.roundedLine(x1, y1, x2, y2, color ?: game.skin["thermocolor"], 16F)
	}
}
