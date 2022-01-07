package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.drawCenter
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
	
	override fun draw(batch: Batch)
	{
		val x = grid.iToX(i + 0.5F)
		val y = grid.jToY(j + 0.5F)
		game.segoeuil.color = color ?: primaryColor
		game.segoeuil.drawCenter(batch, text, x, y)
	}
}
