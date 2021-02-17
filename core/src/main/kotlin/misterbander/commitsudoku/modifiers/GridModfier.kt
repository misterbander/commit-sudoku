package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import misterbander.commitsudoku.scene2d.SudokuGrid

abstract class GridModfier(protected val grid: SudokuGrid)
{
	protected val game = grid.panel.screen.game
	abstract fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	open fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {}
	abstract fun clear()
	abstract fun draw(batch: Batch)
}
