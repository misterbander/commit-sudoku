package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.Timer
import ktx.actors.KtxInputListener
import ktx.actors.plusAssign
import ktx.async.interval
import ktx.collections.GdxArray
import misterbander.commitsudoku.scene2d.actions.ModifyCellAction
import misterbander.commitsudoku.scene2d.actions.ModifyDigitAction
import misterbander.commitsudoku.scene2d.actions.ModifyMarkAction

class SudokuGridKeyListener(private val grid: SudokuGrid) : KtxInputListener()
{
	private var job: Timer.Task? = null
	
	override fun keyDown(event: InputEvent, keycode: Int): Boolean
	{
		job?.cancel()
		job = null
		when (keycode)
		{
			Input.Keys.LEFT ->
			{
				navigate(left = 1)
				job = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(left = 1) }
			}
			Input.Keys.RIGHT ->
			{
				navigate(right = 1)
				job = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(right = 1) }
			}
			Input.Keys.UP ->
			{
				navigate(up = 1)
				job = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(up = 1) }
			}
			Input.Keys.DOWN ->
			{
				navigate(down = 1)
				job = interval(delaySeconds = 0.3F, intervalSeconds = 0.025F) { navigate(down = 1) }
			}
			Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> typedDigit(1)
			Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> typedDigit(2)
			Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> typedDigit(3)
			Input.Keys.NUM_4, Input.Keys.NUMPAD_4 -> typedDigit(4)
			Input.Keys.NUM_5, Input.Keys.NUMPAD_5 -> typedDigit(5)
			Input.Keys.NUM_6, Input.Keys.NUMPAD_6 -> typedDigit(6)
			Input.Keys.NUM_7, Input.Keys.NUMPAD_7 -> typedDigit(7)
			Input.Keys.NUM_8, Input.Keys.NUMPAD_8 -> typedDigit(8)
			Input.Keys.NUM_9, Input.Keys.NUMPAD_9 -> typedDigit(9)
			Input.Keys.NUM_0, Input.Keys.NUMPAD_0, Input.Keys.BACKSPACE, Input.Keys.FORWARD_DEL -> typedDigit(0)
		}
		return true
	}
	
	override fun keyUp(event: InputEvent, keycode: Int): Boolean
	{
		job?.cancel()
		job = null
		return true
	}
	
	private fun navigate(up: Int = 0, down: Int = 0, left: Int = 0, right: Int = 0)
	{
		if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) and !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
			grid.unselect()
		if (grid.mainSelectedCell == null)
			grid.select(grid.cells[0][8])
		else
			grid.select(grid.mainSelectedCell!!.getCell(right - left, up - down))
	}
	
	private fun typedDigit(digit: Int)
	{
		val selectedCells = grid.getSelectedCells()
		val modifyCellActions: GdxArray<ModifyCellAction> = GdxArray()
		
		if (digit == 0)
		{
			// Clear cell except color
			selectedCells.forEach { cell ->
				modifyCellActions.apply {
					add(ModifyDigitAction(cell, to = 0))
					for (i in 1..9)
					{
						add(ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, i, to = false))
						add(ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, i, to = false))
					}
				}
			}
		}
		else
		{
			selectedCells.forEach { cell ->
				when
				{
					Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) or Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ->
						modifyCellActions.add(ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, digit))
					Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) or Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) ->
						modifyCellActions.add(ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, digit))
//					Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) or Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT) ->
//						modifyCellActions.add(ModifyDigitAction(cell, ModifyDigitAction.Type.COLOR, from = ))
					else -> modifyCellActions.add(ModifyDigitAction(cell, to = digit))
				}
			}
		}
		modifyCellActions.forEach { grid += it }
		grid.actionController.actionHistory.add(modifyCellActions)
	}
}