package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import ktx.actors.onChange
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.INFO_LABEL_STYLE
import misterbander.commitsudoku.TEXT_BUTTON_STYLE
import misterbander.commitsudoku.TEXT_FIELD_STYLE
import misterbander.gframework.scene2d.GTextWidget
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.gTextField
import java.io.ObjectOutputStream

class ConnectDialog(screen: CommitSudokuScreen) : CommitSudokuDialog(screen, "Sync Grid With")
{
	init
	{
		contentTable.add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			label("Host Address:", INFO_LABEL_STYLE)
			row()
			val hostAddressTextField =
				gTextField(this@ConnectDialog, "192.168.", TEXT_FIELD_STYLE).cell(colspan = 2, fillX = true)
			row()
			label("Port", INFO_LABEL_STYLE)
			row()
			val portTextField = gTextField(this@ConnectDialog, "11530", TEXT_FIELD_STYLE) {
				filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
			}.cell(colspan = 2, fillX = true)
			row()
			textButton("OK", TEXT_BUTTON_STYLE) {
				onChange {
					try
					{
						print("${hostAddressTextField.text}, ${portTextField.text}")
						val socket = Gdx.net.newClientSocket(
							Net.Protocol.TCP,
							hostAddressTextField.text,
							portTextField.text.toInt(),
							null
						)
						val outputStream = ObjectOutputStream(socket.outputStream)
						screen.panel.writeState(screen.mapper)
						screen.mapper.write(outputStream)
						outputStream.close()
						socket.dispose()
					}
					catch (e: Exception)
					{
						e.printStackTrace()
						screen.messageDialog.show("Error", "${e.message}! Cause: ${e.cause}", this@ConnectDialog)
					}
					hide()
				}
			}.cell(width = 96F)
			textButton("Cancel", TEXT_BUTTON_STYLE) { onChange { hide() } }.cell(width = 96F)
		})
		
		addListener(UnfocusListener(this))
	}
}
