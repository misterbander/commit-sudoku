package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState

abstract class GridModfier<T : GridModification>(protected val grid: SudokuGrid) : PersistentState
{
	protected val game = grid.game
	
	protected var selectI = 0
	protected var selectJ = 0
	protected abstract val isValidIndex: Boolean
	
	abstract fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	
	open fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = Unit
	
	open fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) = Unit
	
	open fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int) = Unit
	
	open fun longPress(x: Float, y: Float): Boolean = false
	
	protected open fun updateSelect(x: Float, y: Float)
	{
		selectI = grid.xToI(x)
		selectJ = grid.yToJ(y)
	}
	
	open fun navigate(up: Int = 0, down: Int = 0, left: Int = 0, right: Int = 0) = Unit
	
	open fun enter() = Unit
	
	open fun typedDigit(digit: Int) = Unit
	
	abstract fun addModification(modification: T)
	
	abstract fun removeModification(modification: T)
	
	abstract fun clear()
	
	open fun draw(batch: Batch) = Unit
}
