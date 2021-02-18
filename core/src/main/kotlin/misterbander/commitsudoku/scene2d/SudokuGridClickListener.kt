package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import ktx.actors.setKeyboardFocus

class SudokuGridClickListener(private val grid: SudokuGrid) : ClickListener(-1)
{
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		super.touchDown(event, x, y, pointer, button)
		grid.setKeyboardFocus(true)
		if (pointer == 0 && !UIUtils.shift() && !UIUtils.ctrl())
			grid.unselect()
		val modifier = grid.modifier
		if (modifier == null)
			grid.select(x, y)
		else
			modifier.touchDown(event, x, y, pointer, button)
		return true
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		super.touchDragged(event, x, y, pointer)
		val modifier = grid.modifier
		if (modifier == null)
			grid.select(x, y)
		else
			modifier.touchDragged(event, x, y, pointer)
	}
}
