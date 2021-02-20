package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import misterbander.commitsudoku.scene2d.SudokuGrid

abstract class GridModfier(protected val grid: SudokuGrid)
{
	protected val game = grid.game
	abstract fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	open fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {}
	open fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {}
	open fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int) {}
	open fun navigate(up: Int = 0, down: Int = 0, left: Int = 0, right: Int = 0) {}
	open fun enter() {}
	open fun typedDigit(digit: Int) {}
	abstract fun clear()
	abstract fun draw(batch: Batch)
}
