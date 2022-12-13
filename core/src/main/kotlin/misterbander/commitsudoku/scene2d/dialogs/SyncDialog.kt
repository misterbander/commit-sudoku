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
	private var hostAddress = "192.168."
	private var port = "11530"
	
	init
	{
		contentTable.add(scene2d.table {
			defaults().left().space(16F)
			label("Host Address:")
			row()
			gTextField(this@SyncDialog, hostAddress) {
				onChange { hostAddress = text }
			}.cell(colspan = 2, growX = true)
			row()
			label("Port")
			row()
			gTextField(this@SyncDialog, port) {
				onChange { port = text }
				filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
			}.cell(colspan = 2, growX = true)
			row()
		}).grow()
		buttonTable.add(scene2d.table {
			defaults().space(16F)
			textButton("OK") {
				onChange {
					hide()
					syncGrid()
				}
			}.cell(preferredWidth = 96F)
			textButton("Cancel") { onChange { hide() } }.cell(preferredWidth = 96F)
		})
		
		addListener(UnfocusListener(this))
	}
	
	private fun syncGrid()
	{
		try
		{
			val socket = Gdx.net.newClientSocket(Net.Protocol.TCP, hostAddress, port.toInt(), null)
			val outputStream = ObjectOutputStream(socket.outputStream)
			screen.panel.writeState(screen.mapper)
			screen.mapper.write(outputStream)
			outputStream.close()
			socket.dispose()
		}
		catch (e: Exception)
		{
			e.printStackTrace()
			screen.messageDialog.show("Error", "${e.message}! Cause: ${e.cause}") { show() }
		}
	}
}
