package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ktx.actors.centerPosition
import ktx.actors.onClick
import ktx.actors.setKeyboardFocus
import ktx.actors.txt
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import misterbander.commitsudoku.CommitSudoku
import misterbander.gframework.scene2d.MBTextField

class InputWindow(game: CommitSudoku, isModal: Boolean = false) : Window("", game.skin, "windowstyle")
{
	val closeButton = Button(game.skin, "closebuttonstyle").apply { onClick { close() } }
	private val messageLabel = Label("", game.skin, "infolabelstyle")
	private val textField = MBTextField("", game.skin, "textfieldstyle")
	var onSuccess: (String) -> Unit = {}
	
	init
	{
		titleTable.add(closeButton).right()
		titleTable.pad(2F, 16F, 0F, 2F)
		this.isModal = isModal
		isVisible = false
		add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			actor(messageLabel)
			row()
			actor(textField).cell(colspan = 2, fillX = true)
			row()
			textButton("OK", "textbuttonstyle", game.skin) {
				onClick {
					onSuccess(textField.text)
					close()
				}
			}.cell(width = 96F)
			textButton("Cancel", "textbuttonstyle", game.skin) { onClick { close() }
			}.cell(width = 96F)
		})
		left()
		pack()
	}
	
	fun show(title: String, message: String, onSuccess: (String) -> Unit)
	{
		centerPosition()
		isVisible = true
		titleLabel.txt = title
		messageLabel.txt = message
		textField.text = null
		textField.setKeyboardFocus(true)
		this.onSuccess = onSuccess
	}
	
	fun close()
	{
		isVisible = false
		Gdx.input.setOnscreenKeyboardVisible(false)
	}
}
