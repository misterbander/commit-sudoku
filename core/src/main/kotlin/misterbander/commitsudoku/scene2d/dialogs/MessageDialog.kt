package misterbander.commitsudoku.scene2d.dialogs

import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.util.wrap

class MessageDialog(screen: CommitSudokuScreen) : RebuildableDialog(screen, "")
{
	private var message = ""
	private var hideAction: () -> Unit = {}
	
	override fun build()
	{
		contentTable.add(scene2d.label(screen.segoeUi.wrap(message, 720)))
		buttonTable.add(scene2d.textButton("OK") {
			onChange { hide() }
		}).prefWidth(96F)
	}
	
	fun show(title: String, message: String, hideAction: () -> Unit = {})
	{
		titleLabel.txt = title
		this.message = message
		this.hideAction = hideAction
		show()
	}
	
	override fun hide()
	{
		super.hide()
		hideAction()
	}
}
