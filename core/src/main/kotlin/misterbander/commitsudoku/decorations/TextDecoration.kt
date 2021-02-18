package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.drawCenter


class TextDecoration(
	grid: SudokuGrid,
	val i: Int,
	val j: Int,
	private val text: String
) : Decoration(grid)
{
	var color: Color? = null
	
	override fun draw(batch: Batch)
	{
		val x = grid.iToX(i + 0.5F)
		val y = grid.jToY(j + 0.5F)
		game.segoeui2.color = color ?: game.skin["primarycolor"]
		game.segoeui2.drawCenter(batch, text, x, y)
	}
}
