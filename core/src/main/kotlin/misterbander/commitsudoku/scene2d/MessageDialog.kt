package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.centerPosition
import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.util.wrap

class MessageDialog(screen: CommitSudokuScreen) : CommitSudokuWindow(screen, "", true)
{
	private val messageLabel = Label("", game.skin, "infolabelstyle")
	private var fallbackWindow: CommitSudokuWindow? = null
	
	init
	{
		add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			actor(messageLabel)
			row()
			textButton("OK", "textbuttonstyle", game.skin) { onChange { close() } }.cell(width = 96F).inCell.center()
		})
	}
	
	fun show(title: String, message: String, fallbackWindow: CommitSudokuWindow? = null)
	{
		this.fallbackWindow = fallbackWindow
		isVisible = true
		titleLabel.txt = title
		messageLabel.txt = game.segoeui.wrap(message, 720)
		pack()
		centerPosition()
	}
	
	override fun close()
	{
		super.close()
		fallbackWindow?.show()
	}
}
