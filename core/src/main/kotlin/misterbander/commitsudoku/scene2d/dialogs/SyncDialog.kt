package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import ktx.actors.onChange
import ktx.scene2d.*
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.GTextWidget
import misterbander.gframework.scene2d.UnfocusListener
import misterbander.gframework.scene2d.gTextField
import misterbander.gframework.scene2d.scene2d
import misterbander.gframework.util.PersistentStateMapper
import java.io.ObjectOutputStream

class SyncDialog(
	private val screen: CommitSudokuScreen,
	private val mapper: PersistentStateMapper
) : CommitSudokuDialog("Sync Grid With")
{
	private var hostAddress = "192.168."
	private var port = "11530"

	init
	{
		contentTable.scene2d {
			defaults().left().space(16F)
			label("Host Address:").inCell.left()
			row()
			gTextField(this@SyncDialog, hostAddress) {
				onChange { hostAddress = text }
			}.cell(colspan = 2, growX = true, row = true)
			label("Port").inCell.left()
			row()
			gTextField(this@SyncDialog, port) {
				onChange { port = text }
				filter = GTextWidget.GTextWidgetFilter.DigitsOnlyFilter()
			}.cell(colspan = 2, growX = true)
		}
		buttonTable.scene2d {
			defaults().space(16F)
			textButton("OK") {
				onChange {
					hide()
					syncGrid()
				}
			}.cell(preferredWidth = 96F)
			textButton("Cancel") {
				onChange { hide() }
			}.cell(preferredWidth = 96F)
		}

		addListener(UnfocusListener(this))
	}

	private fun syncGrid()
	{
		try
		{
			val socket = Gdx.net.newClientSocket(Net.Protocol.TCP, hostAddress, port.toInt(), null)
			val outputStream = ObjectOutputStream(socket.outputStream)
			screen.panel.writeState(mapper)
			mapper.write(outputStream)
			outputStream.close()
			socket.dispose()
		}
		catch (e: Exception)
		{
			e.printStackTrace()
			screen.messageDialog.show(screen.uiStage, "Error", "${e.message}! Cause: ${e.cause}") {
				show(screen.uiStage)
			}
		}
	}
}
