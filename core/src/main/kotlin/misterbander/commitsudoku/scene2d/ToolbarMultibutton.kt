package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.math.vec2
import ktx.scene2d.*
import ktx.style.*

class ToolbarMultibutton(
	styleName: String,
	val buttonMenuProvider: () -> ToolbarMultibuttonMenu
) : ImageButton(Scene2DSkin.defaultSkin, styleName)
{
	private val multibuttonIcon: Drawable
		get() = Scene2DSkin.defaultSkin["multibutton_icon"]
	private val positionVec = vec2()
	private var shouldExpand = false

	init
	{
		onClick {
			if (shouldExpand)
			{
				localToStageCoordinates(positionVec.set(0F, 0F))
				val buttonMenu =  buttonMenuProvider()
				buttonMenu.setPosition(positionVec.x, positionVec.y)
				buttonMenu.parentMultibutton = this
				stage += buttonMenu
			}
			else
				shouldExpand = true
		}
	}

	override fun setChecked(isChecked: Boolean)
	{
		super.setChecked(isChecked)
		if (!isChecked && shouldExpand)
			shouldExpand = false
	}

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		super.draw(batch, parentAlpha)
		multibuttonIcon.draw(
			batch,
			x + width - multibuttonIcon.minWidth - 4,
			y + 4,
			multibuttonIcon.minWidth,
			multibuttonIcon.minHeight
		)
	}
}
