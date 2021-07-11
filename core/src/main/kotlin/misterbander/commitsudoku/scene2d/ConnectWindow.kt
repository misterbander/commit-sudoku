package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import ktx.actors.onChange
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.MBTextField
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.mbTextField
import java.io.ObjectOutputStream


class ConnectWindow(screen: CommitSudokuScreen) : CommitSudokuWindow(screen, "Sync Grid With", true)
{
	init
	{
		add(scene2d.table {
			pad(24F)
			defaults().left().space(16F)
			label("Host Address:", "infolabelstyle", game.skin)
			row()
			val hostAddressTextField = mbTextField(this@ConnectWindow, "192.168.", "textfieldstyle", game.skin).cell(colspan = 2, fillX = true)
			row()
			label("Port", "infolabelstyle", game.skin)
			row()
			val portTextField = mbTextField(this@ConnectWindow, "11530", "textfieldstyle", game.skin) {
				textFieldFilter = MBTextField.MBTextFieldFilter.DigitsOnlyFilter()
			}.cell(colspan = 2, fillX = true)
			row()
			textButton("OK", "textbuttonstyle", game.skin) {
				onChange {
					try
					{
						print("${hostAddressTextField.text}, ${portTextField.text}")
						val socket = Gdx.net.newClientSocket(
							Net.Protocol.TCP,
							hostAddressTextField.text,
							portTextField.text.toString().toInt(),
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
						screen.messageDialog.show("Error", "${e.message}! Cause: ${e.cause}", this@ConnectWindow)
					}
					close()
				}
			}.cell(width = 96F)
			textButton("Cancel", "textbuttonstyle", game.skin) { onChange { close() } }.cell(width = 96F)
		})
		
		addListener(UnfocusListener(this))
	}
}
