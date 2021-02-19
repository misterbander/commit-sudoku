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
import misterbander.gframework.util.cycle


open class TextDecorationAdder(grid: SudokuGrid): GridModfier(grid)
{
	private val textDecorations: GdxArray<TextDecoration> = GdxArray()
	
	protected var highlightI = 0
	protected var highlightJ = 0
	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		enter()
	}
	
	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex(highlightI, highlightJ))
		{
			highlightI = -1
			highlightJ = 9
		}
		
		val di = right - left
		val dj = up - down
		if (di != 0)
		{
			if (highlightJ in 0..8)
				highlightI = if (highlightI == 9) -1 else 9
			else
				highlightI += di
			highlightI = highlightI cycle -1..9
		}
		else if (dj != 0)
		{
			if (highlightI in 0..8)
				highlightJ = if (highlightJ == 9) -1 else 9
			else
				highlightJ += dj
			highlightJ = highlightJ cycle -1..9
		}
	}
	
	override fun enter()
	{
		if (!isValidIndex(highlightI, highlightJ))
			return
		
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
	}
	
	private fun findTextDecoration(i: Int, j: Int): TextDecoration?
	{
		textDecorations.forEach {
			if (it.i == i && it.j == j)
				return it
		}
		return null
	}
	
	protected open fun isValidIndex(i: Int, j: Int): Boolean
	{
		return i in -1..9 && j in -1..9 && (i == -1 || i == 9 || j == -1 || j == 9)
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
	
	protected fun drawClickableArea(i: Int, j: Int)
	{
		val x = grid.iToX(i.toFloat())
		val y = grid.jToY(j.toFloat())
		val isSelected = i == highlightI && j == highlightJ
		game.shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) game.skin["selectedcolor"] else gray)
	}
}
