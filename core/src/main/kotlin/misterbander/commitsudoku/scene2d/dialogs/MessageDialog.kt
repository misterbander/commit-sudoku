package misterbander.commitsudoku.scene2d.dialogs

import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.INFO_LABEL_STYLE
import misterbander.commitsudoku.TEXT_BUTTON_STYLE
import misterbander.gframework.util.wrap

class MessageDialog(screen: CommitSudokuScreen) : CommitSudokuDialog(screen, "")
{
	private val messageLabel = scene2d.label("", INFO_LABEL_STYLE)
	private var fallbackWindow: CommitSudokuDialog? = null
	
	init
	{
		contentTable.add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			actor(messageLabel)
			row()
			textButton("OK", TEXT_BUTTON_STYLE) { onChange { hide() } }.cell(width = 96F).inCell.center()
		})
	}
	
	fun show(title: String, message: String, fallbackWindow: CommitSudokuDialog? = null)
	{
		this.fallbackWindow = fallbackWindow
		titleLabel.txt = title
		messageLabel.txt = game.segoeui.wrap(message, 720)
		show()
	}
	
	override fun hide()
	{
		super.hide()
		fallbackWindow?.show()
	}
}
