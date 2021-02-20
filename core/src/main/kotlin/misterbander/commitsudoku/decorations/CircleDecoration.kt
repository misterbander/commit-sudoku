package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.scene2d.SudokuGrid

class CircleDecoration(
	grid: SudokuGrid,
	val i: Int,
	val j: Int,
	private val radius: Float,
	var color: Color = Color.BLACK,
	var outlineColor: Color = Color.CLEAR
) : Decoration(grid)
{
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		val x = grid.iToX(i + 0.5F)
		val y = grid.jToY(j + 0.5F)
		shapeDrawer.filledCircle(x, y, radius, color)
		shapeDrawer.setColor(outlineColor)
		shapeDrawer.circle(x, y, radius, 3F)
	}
}
