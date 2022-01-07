package misterbander.commitsudoku.scene2d.dialogs

import ktx.actors.centerPosition
import ktx.actors.onChange
import ktx.scene2d.*
import misterbander.commitsudoku.CLOSE_BUTTON_STYLE
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.WINDOW_STYLE
import misterbander.gframework.scene2d.AccessibleInputDialog

abstract class CommitSudokuDialog(
	protected val screen: CommitSudokuScreen,
	title: String
) : AccessibleInputDialog(title, Scene2DSkin.defaultSkin, WINDOW_STYLE)
{
	protected val game = screen.game
	
	val closeButton = scene2d.button(CLOSE_BUTTON_STYLE) { onChange { hide() } }
	
	init
	{
		titleTable.add(closeButton).right()
		titleTable.pad(2F, 16F, 0F, 2F)
	}
	
	fun show()
	{
		show(screen.uiStage, null)
		centerPosition()
	}
	
	override fun hide() = hide(null)
}
