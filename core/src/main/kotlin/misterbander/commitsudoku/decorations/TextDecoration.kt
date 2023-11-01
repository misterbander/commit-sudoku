package misterbander.commitsudoku.decorations

import misterbander.commitsudoku.notoSansLarge
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.drawCenter
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

open class TextDecoration(
	grid: SudokuGrid,
	val i: Int,
	val j: Int,
	var text: String
) : Decoration(grid)
{
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("i" to i, "j" to j, "text" to text)

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x = grid.iToX(i + 0.5F)
		val y = grid.jToY(j + 0.5F)
		notoSansLarge.color = color ?: primaryColor
		notoSansLarge.drawCenter(shapeDrawer.batch, text, x, y)
	}
}
