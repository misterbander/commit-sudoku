package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import ktx.actors.setKeyboardFocus

class SudokuGridClickListener(private val grid: SudokuGrid) : ClickListener(-1)
{
	private var selectRow = -1
	private var selectCol = -1
	private var isUnselecting = false

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		super.touchDown(event, x, y, pointer, button)
		grid.setKeyboardFocus(true)
		selectRow = grid.yToRow(y)
		selectCol = grid.xToCol(x)
		if (pointer == 0 && !UIUtils.shift() && !UIUtils.ctrl())
			grid.unselect()
		val modifier = grid.modifier
		if (modifier == null)
		{
			isUnselecting =
				selectRow in 0..8 && selectCol in 0..8 && UIUtils.ctrl() && grid.cells[selectRow][selectCol].isSelected
			if (isUnselecting)
				grid.unselect(selectRow, selectCol)
			else
				grid.select(selectRow, selectCol)
		}
		else
			modifier.touchDown(event, x, y, pointer, button)
		return true
	}

	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		super.touchUp(event, x, y, pointer, button)
		grid.modifier?.touchUp(event, x, y, pointer, button)
	}

	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		super.touchDragged(event, x, y, pointer)
		if (grid.modifier != null)
		{
			grid.modifier!!.touchDragged(event, x, y, pointer)
			return
		}

		val row = grid.yToRow(y)
		val col = grid.xToCol(x)
		if (row == selectRow && col == selectCol)
			return
		selectRow = row
		selectCol = col
		if (isUnselecting)
			grid.unselect(selectRow, selectCol)
		else
			grid.select(selectRow, selectCol)
	}
}
