package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Input
import ktx.actors.onChange
import ktx.actors.onKeyDown
import ktx.actors.onKeyboardFocus
import ktx.actors.setKeyboardFocus
import ktx.actors.txt
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.INFO_LABEL_STYLE
import misterbander.commitsudoku.TEXT_BUTTON_STYLE
import misterbander.commitsudoku.TEXT_FIELD_STYLE
import misterbander.gframework.scene2d.GTextField
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.gTextField

class SingleInputDialog(
	screen: CommitSudokuScreen,
	digitsOnly: Boolean = false,
	maxLength: Int = 0
) : CommitSudokuDialog(screen, "")
{
	private val messageLabel = scene2d.label("", INFO_LABEL_STYLE)
	private val textField = scene2d.gTextField(this, "", TEXT_FIELD_STYLE)
	var onSuccess: (String) -> Unit = {}
	
	init
	{
		contentTable.add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			actor(messageLabel)
			row()
			actor(textField) {
				if (digitsOnly)
					textFieldFilter = GTextField.GTextFieldFilter.DigitsOnlyFilter()
				this.maxLength = maxLength
				onKeyboardFocus { focused -> textField.onscreenKeyboard.show(focused) }
			}.cell(colspan = 2, fillX = true)
			row()
			textButton("OK", TEXT_BUTTON_STYLE) {
				onChange {
					onSuccess(textField.text)
					hide()
				}
			}.cell(width = 96F)
			textButton("Cancel", TEXT_BUTTON_STYLE) { onChange { hide() } }.cell(width = 96F)
		})
		left()
		pack()
		
		addListener(UnfocusListener(this))
		onKeyDown { keycode ->
			if (keycode == Input.Keys.ENTER)
			{
				onSuccess(textField.text)
				hide()
			}
			else if (keycode == Input.Keys.ESCAPE)
				hide()
		}
	}
	
	fun show(title: String, message: String, onSuccess: (String) -> Unit)
	{
		titleLabel.txt = title
		messageLabel.txt = message
		textField.text = null
		this.onSuccess = onSuccess
		show()
		textField.setKeyboardFocus(true)
	}
}
