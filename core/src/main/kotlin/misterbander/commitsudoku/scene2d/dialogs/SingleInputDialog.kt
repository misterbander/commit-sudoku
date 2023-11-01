package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import ktx.actors.onChange
import ktx.actors.onKeyDown
import ktx.actors.onKeyboardFocus
import ktx.actors.setKeyboardFocus
import ktx.collections.*
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.GTextField
import misterbander.gframework.scene2d.GTextWidget
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.gTextField
import misterbander.gframework.scene2d.scene2d

class SingleInputDialog(
	private val screen: CommitSudokuScreen,
	title: String,
	message: String,
	digitsOnly: Boolean = false,
	maxLength: Int = 0,
	private val onSuccess: (String) -> Unit = {}
) : CommitSudokuDialog(title)
{
	private val textField: GTextField
	private var input = ""

	init
	{
		contentTable.scene2d {
			defaults().left().space(16F)
			label(message).inCell.left()
			row()
			textField = gTextField(this@SingleInputDialog, input) {
				if (digitsOnly)
					filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
				this.maxLength = maxLength
				onKeyboardFocus { focused -> keyboard.show(focused) }
				onChange { input = text }
			}.cell(growX = true)
		}
		contentTable.left()
		buttonTable.scene2d {
			defaults().space(16F)
			textButton("OK") {
				onChange {
					hide()
					onSuccess(input)
				}
			}.cell(preferredWidth = 96F)
			textButton("Cancel") {
				onChange { hide() }
			}.cell(preferredWidth = 96F)
		}
		pack()

		addListener(UnfocusListener(this))
		onKeyDown { keycode ->
			if (keycode == Input.Keys.ENTER)
			{
				hide()
				onSuccess(input)
			}
			else if (keycode == Input.Keys.ESCAPE)
				hide()
		}

		screen.keyboardHeightObservers += this
	}

	override fun show(stage: Stage): Dialog
	{
		super.show(stage)
		textField.setKeyboardFocus(true)
		return this
	}

	override fun hide()
	{
		super.hide()
		screen.keyboardHeightObservers -= this
	}
}
