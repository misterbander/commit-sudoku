package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import ktx.actors.onClick
import ktx.actors.onTouchEvent
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.*
import misterbander.commitsudoku.CommitSudoku

class ToolbarMultibuttonMenu(
	private val game: CommitSudoku,
	private val parentMultibutton: ToolbarMultibutton,
	vararg buttons: ImageButton
) : Stack()
{
	private val background = Image()
	private val buttonGroup: ButtonGroup<ImageButton> = ButtonGroup()
	val checkedIndex
		get() = buttonGroup.checkedIndex
	
	init
	{
		updateStyle()
		this += background
		val horizontalGroup = HorizontalGroup()
		buttons.forEach {
			buttonGroup.add(it)
			horizontalGroup += it
			it.onClick {
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
	
	fun updateStyle()
	{
		background.drawable = game.skin["${if (game.isDarkMode) "dark" else ""}toolbarmultibuttonmenubackground"]
	}
	
	override fun hit(x: Float, y: Float, touchable: Boolean): Actor?
	{
		val hit: Actor? = super.hit(x, y, touchable)
		return if (hit == null && isVisible) this else hit
	}
}
