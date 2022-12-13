package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import ktx.actors.onChange
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.GTextWidget
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.gTextField
import java.io.ObjectOutputStream

class SyncDialog(screen: CommitSudokuScreen) : CommitSudokuDialog(screen, "Sync Grid With")
{
	init
	{
		contentTable.add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			label("Host Address:")
			row()
			val hostAddressTextField =
				gTextField(this@SyncDialog, "192.168.").cell(colspan = 2, fillX = true)
			row()
			label("Port")
			row()
			val portTextField = gTextField(this@SyncDialog, "11530") {
				filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
			}.cell(colspan = 2, fillX = true)
			row()
			textButton("OK") {
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
						screen.messageDialog.show("Error", "${e.message}! Cause: ${e.cause}", this@SyncDialog)
					}
					hide()
				}
			}.cell(width = 96F)
			textButton("Cancel") { onChange { hide() } }.cell(width = 96F)
		})
		
		addListener(UnfocusListener(this))
	}
}
