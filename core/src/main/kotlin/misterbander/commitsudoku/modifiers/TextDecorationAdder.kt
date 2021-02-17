package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.TextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid


class TextDecorationAdder(grid: SudokuGrid): GridModfier(grid)
{
	private val textDecorations: GdxArray<TextDecoration> = GdxArray()
	
	private var highlightI = 0
	private var highlightJ = 0
	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		
		if (!isValidIndex(highlightI, highlightJ))
			return false
		
		val existingTextDecoration = findTextDecoration(highlightI, highlightJ)
		if (existingTextDecoration != null)
		{
			textDecorations -= existingTextDecoration
			grid.decorations -= existingTextDecoration
		}
		else
		{
			grid.panel.screen.textInputWindow.show("Add Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@show
				val textDecoration = TextDecoration(grid, highlightI, highlightJ, result)
				textDecorations += textDecoration
				grid.decorations += textDecoration
			}
		}
		return false
	}
	
	private fun findTextDecoration(i: Int, j: Int): TextDecoration?
	{
		textDecorations.forEach {
			if (it.i == i && it.j == j)
				return it
		}
		return null
	}
	
	private fun isValidIndex(i: Int, j: Int): Boolean
	{
		return i >= -1 && i <= 9 && j >= -1 && j <= 9 && (i == -1 || i == 9 || j == -1 || j == 9)
	}
	
	override fun clear()
	{
		textDecorations.clear()
	}
	
	override fun draw(batch: Batch)
	{
		for (i in -1..9)
		{
			drawClickableArea(i, -1)
			drawClickableArea(i, 9)
		}
		for (j in 0..8)
		{
			drawClickableArea(-1, j)
			drawClickableArea(9, j)
		}
	}
	
	private fun drawClickableArea(i: Int, j: Int)
	{
		val x = grid.iToX(i.toFloat())
		val y = grid.jToY(j.toFloat())
		val isSelected = i == highlightI && j == highlightJ
		game.shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) game.skin["selectedcolor"] else gray)
	}
}
