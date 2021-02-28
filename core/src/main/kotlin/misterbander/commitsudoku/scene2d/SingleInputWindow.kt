package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.*
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.MBTextField

class SingleInputWindow(
	screen: CommitSudokuScreen,
	isModal: Boolean = false,
	digitsOnly: Boolean = false,
	maxLength: Int = 0
) : CommitSudokuWindow(screen, "", isModal)
{
	private val messageLabel = Label("", game.skin, "infolabelstyle")
	private val textField = MBTextField("", game.skin, "textfieldstyle")
	var onSuccess: (String) -> Unit = {}
	
	init
	{
		add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			actor(messageLabel)
			row()
			actor(textField) {
				if (digitsOnly)
					textFieldFilter = MBTextField.MBTextFieldFilter.DigitsOnlyFilter()
				this.maxLength = maxLength
				onKeyboardFocus { focused -> textField.onscreenKeyboard.show(focused) }
			}.cell(colspan = 2, fillX = true)
			row()
			textButton("OK", "textbuttonstyle", game.skin) {
				onChange {
					onSuccess(textField.text)
					close()
				}
			}.cell(width = 96F)
			textButton("Cancel", "textbuttonstyle", game.skin) { onChange { close() } }.cell(width = 96F)
		})
		left()
		pack()
		
		onTouchEvent { event ->
			if (event.type == InputEvent.Type.touchDown && event.target !is MBTextField)
				stage.keyboardFocus = null
		}
		onKeyDown { keycode ->
			if (keycode == Input.Keys.ENTER)
			{
				onSuccess(textField.text)
				close()
			}
			else if (keycode == Input.Keys.ESCAPE)
				close()
		}
	}
	
	fun show(title: String, message: String, onSuccess: (String) -> Unit)
	{
		isVisible = true
		titleLabel.txt = title
		messageLabel.txt = message
		textField.text = null
		textField.setKeyboardFocus(true)
		this.onSuccess = onSuccess
		pack()
		centerPosition()
	}
}