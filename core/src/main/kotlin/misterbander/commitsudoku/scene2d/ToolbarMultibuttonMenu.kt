package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import ktx.actors.onClick
import ktx.actors.onTouchEvent
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.*

class ToolbarMultibuttonMenu(
	parentMultibutton: ToolbarMultibutton,
	isDarkMode: Boolean,
	vararg buttons: ImageButton
) : Stack()
{
	private val background = Image()

	init
	{
		background.drawable =
			Scene2DSkin.defaultSkin["toolbar_multibutton_menu_background${if (isDarkMode) "_dark" else ""}"]
		this += background
		val horizontalGroup = HorizontalGroup()
		val buttonGroup = ButtonGroup<ImageButton>()
		for (button in buttons)
		{
			buttonGroup.add(button)
			horizontalGroup += button
			button.onClick {
				this@ToolbarMultibuttonMenu.isVisible = false
				parentMultibutton.style = style
			}
		}
		val container = scene2d.container(horizontalGroup)
		this += container
		setSize(container.minWidth, container.minHeight)

		onTouchEvent { event, x, y ->
			if (event.type == InputEvent.Type.touchDown)
			{
				if (x !in 0F..minWidth || y !in 0F..minHeight)
					isVisible = false
			}
		}
	}

	fun updateStyle(skin: Skin, oldSkin: Skin)
	{
		background.drawable =
			Scene2DSkin.defaultSkin["toolbar_multibutton_menu_background${if (false) "_dark" else ""}"] // FIXME
		children.forEach { it.updateStyle(skin, oldSkin) }
	}

	override fun hit(x: Float, y: Float, touchable: Boolean): Actor?
	{
		val hit: Actor? = super.hit(x, y, touchable)
		return if (hit == null && isVisible) this else hit
	}
}
