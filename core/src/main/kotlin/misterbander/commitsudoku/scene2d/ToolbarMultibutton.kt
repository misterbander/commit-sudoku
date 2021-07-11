package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.onClick
import ktx.math.vec2
import ktx.style.*
import misterbander.commitsudoku.CommitSudokuScreen

class ToolbarMultibutton(
	private val screen: CommitSudokuScreen,
	skin: Skin,
	styleName: String,
) : ImageButton(skin, styleName)
{
	private val multibuttonIcon: TextureRegion
		get() = screen.game.skin[if (screen.game.isDarkMode) "darkmultibuttonicon" else "multibuttonicon"]
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
		batch.draw(multibuttonIcon, x + width - multibuttonIcon.regionWidth - 4, y + 4)
	}
}
