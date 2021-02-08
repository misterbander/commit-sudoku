package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.Timer
import ktx.actors.KtxInputListener
import ktx.async.interval

class SudokuGridKeyListener(private val grid: SudokuGrid) : KtxInputListener()
{
	private var keyRepeatTask: Timer.Task? = null
	
	override fun keyDown(event: InputEvent, keycode: Int): Boolean
	{
		keyRepeatTask?.cancel()
		keyRepeatTask = null
		when (keycode)
		{
			Input.Keys.LEFT ->
			{
				navigate(left = 1)
				keyRepeatTask = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(left = 1) }
			}
			Input.Keys.RIGHT ->
			{
				navigate(right = 1)
				keyRepeatTask = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(right = 1) }
			}
			Input.Keys.UP ->
			{
				navigate(up = 1)
				keyRepeatTask = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(up = 1) }
			}
			Input.Keys.DOWN ->
			{
				navigate(down = 1)
				keyRepeatTask = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(down = 1) }
			}
			Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> grid.typedDigit(1)
			Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> grid.typedDigit(2)
			Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> grid.typedDigit(3)
			Input.Keys.NUM_4, Input.Keys.NUMPAD_4 -> grid.typedDigit(4)
			Input.Keys.NUM_5, Input.Keys.NUMPAD_5 -> grid.typedDigit(5)
			Input.Keys.NUM_6, Input.Keys.NUMPAD_6 -> grid.typedDigit(6)
			Input.Keys.NUM_7, Input.Keys.NUMPAD_7 -> grid.typedDigit(7)
			Input.Keys.NUM_8, Input.Keys.NUMPAD_8 -> grid.typedDigit(8)
			Input.Keys.NUM_9, Input.Keys.NUMPAD_9 -> grid.typedDigit(9)
			Input.Keys.NUM_0, Input.Keys.NUMPAD_0, Input.Keys.BACKSPACE, Input.Keys.FORWARD_DEL -> grid.typedDigit(0)
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
		if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
			grid.unselect()
		if (grid.mainSelectedCell == null)
			grid.select(grid.cells[0][8])
		else
			grid.select(grid.mainSelectedCell!!.getCell(right - left, up - down))
	}
}