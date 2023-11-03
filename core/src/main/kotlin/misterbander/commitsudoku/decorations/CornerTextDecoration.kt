package misterbander.commitsudoku.decorations

import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.notoSans
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.textSize
import space.earlygrey.shapedrawer.ShapeDrawer

class CornerTextDecoration(
	grid: SudokuGrid,
	row: Int,
	col: Int,
	text: String,
) : TextDecoration(grid, row, col, text)
{
	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x = grid.colToX(col + 0.05F)
		val y = grid.rowToY(row + 0.05F)
		val textSize = notoSans.textSize(text)
		shapeDrawer.filledRectangle(x, y - textSize.y, textSize.x, textSize.y, backgroundColor)
		notoSans.color = color ?: primaryColor
		notoSans.draw(shapeDrawer.batch, text, x, y)
	}
}
