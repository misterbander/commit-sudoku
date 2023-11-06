package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.actors.onClick
import ktx.actors.onTouchEvent
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.*

class ToolbarMultibuttonMenu(vararg buttons: ImageButton) : Stack()
{
	private val buttonGroup = ButtonGroup<ImageButton>()
	var parentMultibutton: ToolbarMultibutton? = null
		set(value)
		{
			field = value
			if (value != null)
			{
				for (button: ImageButton in buttonGroup.buttons)
				{
					if (button.style == value.style)
						button.isChecked = true
				}
			}
		}

	init
	{
		this += scene2d.image(Scene2DSkin.defaultSkin.get<Drawable>("toolbar_multibutton_menu_background"))
		val horizontalGroup = HorizontalGroup()
		for (button in buttons)
		{
			buttonGroup.add(button)
			horizontalGroup += button
			button.onClick {
				this@ToolbarMultibuttonMenu.remove()
				parentMultibutton?.style = style
			}
		}
		val container = scene2d.container(horizontalGroup)
		this += container
		setSize(container.minWidth, container.minHeight)

		onTouchEvent { event, x, y ->
			if (event.type == InputEvent.Type.touchDown)
			{
				if (x !in 0F..minWidth || y !in 0F..minHeight)
					remove()
			}
		}
	}

	override fun hit(x: Float, y: Float, touchable: Boolean): Actor = super.hit(x, y, touchable) ?: this
}
