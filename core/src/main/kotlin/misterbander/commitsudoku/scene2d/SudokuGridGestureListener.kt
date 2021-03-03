package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener

class SudokuGridGestureListener(
	private val grid: SudokuGrid
) : ActorGestureListener(20F, 0.4F, 0.5F, 0.15F)
{
	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		grid.modifier?.tap(event, x, y, count, button)
	}
	
	override fun longPress(actor: Actor?, x: Float, y: Float): Boolean
	{
		return grid.modifier?.longPress(x, y) ?: false
	}
}
