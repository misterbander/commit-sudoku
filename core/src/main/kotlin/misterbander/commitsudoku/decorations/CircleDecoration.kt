package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.blend
import java.io.Serializable

class CircleDecoration(
	grid: SudokuGrid,
	val i: Int,
	val j: Int,
	private val radius: Float,
) : Decoration(grid)
{
	override var color: Color? = null
	var outlineColor: Color = Color.CLEAR
	private val internalColor = Color()
	override val dataObject: HashMap<String, Serializable> = hashMapOf("i" to i, "j" to j, "radius" to radius)
	
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		val x = grid.iToX(i + 0.5F)
		val y = grid.jToY(j + 0.5F)
		shapeDrawer.filledCircle(
			x, y, radius,
			internalColor.blend(color ?: game.skin["defaultdecorationcolor"], game.skin["backgroundcolor"])
		)
		shapeDrawer.setColor(outlineColor)
		shapeDrawer.circle(x, y, radius, 3F)
	}
}
