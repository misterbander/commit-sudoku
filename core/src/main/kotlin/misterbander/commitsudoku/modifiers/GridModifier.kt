package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.scenes.scene2d.InputEvent
import misterbander.gframework.util.PersistentState
import space.earlygrey.shapedrawer.ShapeDrawer

interface GridModifier<T : GridModification> : PersistentState
{
	fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)

	fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = Unit

	fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) = Unit

	fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int) = Unit

	fun longPress(x: Float, y: Float): Boolean = false

	fun navigate(up: Int = 0, down: Int = 0, left: Int = 0, right: Int = 0) = Unit

	fun enter() = Unit

	fun typedDigit(digit: Int) = Unit

	fun addModification(modification: T)

	fun removeModification(modification: T)

	fun clear()

	fun draw(shapeDrawer: ShapeDrawer) = Unit
}
