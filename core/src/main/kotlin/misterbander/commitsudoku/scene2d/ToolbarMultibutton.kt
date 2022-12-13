package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.actors.onClick
import ktx.math.vec2
import ktx.scene2d.*
import ktx.style.*
import misterbander.commitsudoku.CommitSudokuScreen

class ToolbarMultibutton(
	private val screen: CommitSudokuScreen,
	styleName: String,
) : ImageButton(Scene2DSkin.defaultSkin, styleName)
{
	private val multibuttonIcon: Drawable
		get() = Scene2DSkin.defaultSkin[if (screen.game.isDarkMode) "multibutton_icon_dark" else "multibutton_icon_light"]
	private val posVector = vec2()
	private var shouldExpand = false
	var multibuttonMenu: ToolbarMultibuttonMenu? = null
		set(value)
		{
			field = value
			value?.isVisible = false
		}
	
	init
	{
		onClick {
			if (shouldExpand)
			{
				localToStageCoordinates(posVector.set(0F, 0F))
				multibuttonMenu?.setPosition(posVector.x, posVector.y)
				multibuttonMenu?.isVisible = true
			}
			else
				shouldExpand = true
		}
	}
	
	override fun act(delta: Float)
	{
		super.act(delta)
		if (!isChecked && shouldExpand)
			shouldExpand = false
	}
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		super.draw(batch, parentAlpha)
		multibuttonIcon.draw(batch, x + width - multibuttonIcon.minWidth - 4, y + 4, multibuttonIcon.minWidth, multibuttonIcon.minHeight)
	}
}
