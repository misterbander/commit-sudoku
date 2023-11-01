package misterbander.commitsudoku.decorations

import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.notoSans
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.textSize
import space.earlygrey.shapedrawer.ShapeDrawer

class CornerTextDecoration(
	grid: SudokuGrid,
	i: Int,
	j: Int,
	text: String,
) : TextDecoration(grid, i, j, text)
{
	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x = grid.iToX(i + 0.05F)
		val y = grid.jToY(j + 0.95F)
		val textSize = notoSans.textSize(text)
		shapeDrawer.filledRectangle(x, y - textSize.y, textSize.x, textSize.y, backgroundColor)
		notoSans.color = color ?: primaryColor
		notoSans.draw(shapeDrawer.batch, text, x, y)
	}
}
