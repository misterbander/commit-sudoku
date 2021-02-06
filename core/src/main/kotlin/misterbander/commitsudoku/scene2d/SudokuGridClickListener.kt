package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class SudokuGridClickListener(private val grid: SudokuGrid) : ClickListener(-1)
{
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		super.touchDown(event, x, y, pointer, button)
		if (pointer == 0)
			grid.unselect()
		grid.select(x, y)
		return true
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		super.touchDragged(event, x, y, pointer)
		grid.select(x, y)
	}
}