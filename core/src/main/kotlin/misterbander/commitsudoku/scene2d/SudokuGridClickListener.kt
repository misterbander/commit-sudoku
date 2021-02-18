package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import ktx.actors.setKeyboardFocus

class SudokuGridClickListener(private val grid: SudokuGrid) : ClickListener(-1)
{
	private var selectI = -1
	private var selectJ = -1
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		super.touchDown(event, x, y, pointer, button)
		grid.setKeyboardFocus(true)
		selectI = grid.xToI(x)
		selectJ = grid.yToJ(y)
		if (pointer == 0 && !UIUtils.shift() && !UIUtils.ctrl())
			grid.unselect()
		val modifier = grid.modifier
		if (modifier == null)
			grid.select(selectI, selectJ, UIUtils.ctrl())
		else
			modifier.touchDown(event, x, y, pointer, button)
		return true
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		super.touchDragged(event, x, y, pointer)
		val i = grid.xToI(x)
		val j = grid.yToJ(y)
		if (i == selectI && j == selectJ)
			return
		selectI = i
		selectJ = j
		val modifier = grid.modifier
		if (modifier == null)
			grid.select(i, j, false)
		else
			modifier.touchDragged(event, x, y, pointer)
	}
}
