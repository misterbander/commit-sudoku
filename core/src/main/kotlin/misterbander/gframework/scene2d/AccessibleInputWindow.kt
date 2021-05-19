package misterbander.gframework.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ktx.math.vec2
import misterbander.gframework.GScreen

/**
 * Makes [Window]s that contain text fields accessible on mobile such that while editing them, the window gets shifted
 * upwards so it doesn't get covered by the on-screen keyboard
 *
 * For this to work, a layout size listener should be attached in Android that calls `GFramework::notifySizeChange()`,
 * and the window must be added to the [GScreen]'s accessible window list.
 */
abstract class AccessibleInputWindow(title: String, skin: Skin, styleName: String) : Window(title, skin, styleName)
{
	private val prevWindowPos = vec2()
	private val windowScreenPos = vec2()
	private val textFieldScreenPos = vec2()
	private var shouldShift = false
	
	fun adjustPosition(screenHeight: Int)
	{
		val focusedTextField: MBTextField = stage?.keyboardFocus as? MBTextField ?: return
		stage.stageToScreenCoordinates(windowScreenPos.set(x, y))
		localToScreenCoordinates(textFieldScreenPos.set(focusedTextField.x, focusedTextField.y))
		
		if (screenHeight < Gdx.graphics.height - 160) // Keyboard up, 160 is an arbitrary keyboard height
		{
			prevWindowPos.set(x, y)
			if (textFieldScreenPos.y > screenHeight) // TextField is off screen
			{
				val diff = textFieldScreenPos.y - screenHeight
				windowScreenPos.y -= diff
				stage.screenToStageCoordinates(windowScreenPos)
				setPosition(windowScreenPos.x, windowScreenPos.y)
				shouldShift = true
			}
		}
		else if (shouldShift)
		{
			setPosition(x, prevWindowPos.y)
			shouldShift = false
		}
		Gdx.graphics.requestRendering()
	}
}
