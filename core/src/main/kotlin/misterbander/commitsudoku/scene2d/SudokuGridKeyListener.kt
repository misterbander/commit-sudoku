package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.Timer
import ktx.actors.KtxInputListener
import ktx.async.interval
import misterbander.commitsudoku.scene2d.actions.ActionController

class SudokuGridKeyListener(
	private val grid: SudokuGrid,
	private val actionController: ActionController
) : KtxInputListener()
{
	private var keyRepeatTask: Timer.Task? = null
	private val keyRepeatDelay = 0.3F
	private val keyRepeatInterval = 0.025F

	override fun keyDown(event: InputEvent, keycode: Int): Boolean
	{
		keyRepeatTask?.cancel()
		keyRepeatTask = null

		val inputMode = when
		{
			UIUtils.shift() -> InputMode.CORNER_MARK
			UIUtils.ctrl() -> InputMode.CENTER_MARK
			UIUtils.alt() -> InputMode.COLOR
			else -> InputMode.DIGIT
		}
		when (keycode)
		{
			// Handle non repeatable key events
			Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> grid.typedDigit(1, inputMode)
			Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> grid.typedDigit(2, inputMode)
			Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> grid.typedDigit(3, inputMode)
			Input.Keys.NUM_4, Input.Keys.NUMPAD_4 -> grid.typedDigit(4, inputMode)
			Input.Keys.NUM_5, Input.Keys.NUMPAD_5 -> grid.typedDigit(5, inputMode)
			Input.Keys.NUM_6, Input.Keys.NUMPAD_6 -> grid.typedDigit(6, inputMode)
			Input.Keys.NUM_7, Input.Keys.NUMPAD_7 -> grid.typedDigit(7, inputMode)
			Input.Keys.NUM_8, Input.Keys.NUMPAD_8 -> grid.typedDigit(8, inputMode)
			Input.Keys.NUM_9, Input.Keys.NUMPAD_9 -> grid.typedDigit(9, inputMode)
			Input.Keys.NUM_0, Input.Keys.NUMPAD_0 -> grid.typedDigit(0, inputMode)
			Input.Keys.BACKSPACE, Input.Keys.FORWARD_DEL -> grid.typedDigit(-1, inputMode)
			Input.Keys.ENTER -> grid.modifier?.enter()
			else ->
			{
				// Handle repeatable key events
				if (keyDownRepeat(keycode))
				{
					keyRepeatTask = interval(delaySeconds = keyRepeatDelay, intervalSeconds = keyRepeatInterval) {
						keyDownRepeat(keycode)
					}
				}
			}
		}
		return true
	}

	private fun keyDownRepeat(keycode: Int): Boolean
	{
		when (keycode)
		{
			Input.Keys.LEFT -> navigate(left = 1)
			Input.Keys.RIGHT -> navigate(right = 1)
			Input.Keys.UP -> navigate(up = 1)
			Input.Keys.DOWN -> navigate(down = 1)
			Input.Keys.Z ->
			{
				if (UIUtils.ctrl())
					actionController.undo()
				else
					return false
			}
			Input.Keys.Y ->
			{
				if (UIUtils.ctrl())
					actionController.redo()
				else
					return false
			}
		}
		return true
	}

	override fun keyUp(event: InputEvent, keycode: Int): Boolean
	{
		keyRepeatTask?.cancel()
		keyRepeatTask = null
		return true
	}

	private fun navigate(up: Int = 0, down: Int = 0, left: Int = 0, right: Int = 0)
	{
		if (!UIUtils.shift() && !UIUtils.ctrl())
			grid.unselect()
		if (grid.modifier != null)
		{
			grid.modifier!!.navigate(up, down, left, right)
			return
		}
		if (grid.selectedCell == null)
			grid.select(grid.cells[0][0])
		else
			grid.select(grid.selectedCell!!.offset(down - up, right - left))
	}
}
