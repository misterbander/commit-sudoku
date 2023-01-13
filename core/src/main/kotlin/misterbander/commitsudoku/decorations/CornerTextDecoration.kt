package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.textSize

class CornerTextDecoration(
	grid: SudokuGrid,
	i: Int,
	j: Int,
	text: String,
) : TextDecoration(grid, i, j, text)
{
	override fun draw(batch: Batch)
	{
		val x = grid.iToX(i + 0.05F)
		val y = grid.jToY(j + 0.95F)
		val textSize = screen.segoeUi.textSize(text)
		game.shapeDrawer.filledRectangle(x, y - textSize.y, textSize.x, textSize.y, backgroundColor)
		screen.segoeUi.color = color ?: primaryColor
		screen.segoeUi.draw(game.batch, text, x, y)
	}
}
