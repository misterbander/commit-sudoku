package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.centerPosition
import ktx.actors.onChange
import ktx.scene2d.*
import ktx.scene2d.defaultStyle
import ktx.style.*
import misterbander.commitsudoku.CLOSE_BUTTON_STYLE
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.scene2d.updateStyle
import misterbander.gframework.scene2d.AccessibleInputDialog

abstract class CommitSudokuDialog(
	protected val screen: CommitSudokuScreen,
	title: String
) : AccessibleInputDialog(title, Scene2DSkin.defaultSkin, defaultStyle)
{
	private val closeButton = scene2d.button(CLOSE_BUTTON_STYLE) { onChange { hide() } }
	
	init
	{
		titleTable.add(closeButton).right()
		titleTable.pad(2F, 16F, 0F, 2F)
		contentTable.pad(24F)
		buttonTable.pad(0F, 24F, 24F, 24F)
	}
	
	open fun show()
	{
		show(screen.uiStage, null)
		centerPosition()
	}
	
	override fun hide() = hide(null)
	
	fun updateStyle(skin: Skin, oldSkin: Skin)
	{
		oldSkin.find(style)?.let { style = skin[it] }
		cells.forEach { it.actor?.updateStyle(skin, oldSkin) }
		closeButton.style = skin[oldSkin.find(closeButton.style)!!]
	}
}
