package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Input
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

class SingleInputDialog(
	screen: CommitSudokuScreen,
	title: String,
	message: String,
	digitsOnly: Boolean = false,
	maxLength: Int = 0,
	private val onSuccess: (String) -> Unit = {}
) : CommitSudokuDialog(screen, title)
{
	private val textField: GTextField
	private var input = ""
	
	init
	{
		contentTable.add(scene2d.table {
			defaults().left().space(16F)
			label(message)
			row()
			textField = gTextField(this@SingleInputDialog, input) {
				if (digitsOnly)
					filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
				this.maxLength = maxLength
				onKeyboardFocus { focused -> keyboard.show(focused) }
				onChange { input = text }
			}.cell(growX = true)
		}).grow()
		buttonTable.add(scene2d.table {
			defaults().space(16F)
			textButton("OK") {
				onChange {
					hide()
					onSuccess(input)
				}
			}.cell(preferredWidth = 96F)
			textButton("Cancel") { onChange { hide() } }.cell(preferredWidth = 96F)
		})
		left()
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
	
	override fun show()
	{
		super.show()
		textField.setKeyboardFocus(true)
	}
	
	override fun hide()
	{
		super.hide()
		screen.keyboardHeightObservers -= this
	}
}
