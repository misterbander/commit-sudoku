package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ScreenUtils
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.collections.*
import ktx.log.info
import ktx.scene2d.*
import misterbander.commitsudoku.scene2d.SudokuPanel
import misterbander.commitsudoku.scene2d.Toolbar
import misterbander.commitsudoku.scene2d.dialogs.MessageDialog
import misterbander.commitsudoku.scene2d.dialogs.SyncDialog
import misterbander.commitsudoku.scene2d.updateStyle
import misterbander.gframework.GScreen
import misterbander.gframework.util.PersistentStateMapper
import java.io.ObjectInputStream
import kotlin.concurrent.thread

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	val panel = SudokuPanel(this)
	val toolbar = Toolbar(this)
	val syncDialog = SyncDialog(this)
	val messageDialog = MessageDialog(this)
	
	val mapper = PersistentStateMapper()
	
	private lateinit var serverSocket: ServerSocket
	@Volatile var shouldCloseServer = false
		set(value)
		{
			field = value
			if (value)
				serverSocket.dispose()
		}
	
	init
	{
		uiStage += object : Actor() // Fallback actor
		{
			init
			{
				onTouchDown { panel.grid.unselect() }
			}
			
			override fun hit(x: Float, y: Float, touchable: Boolean): Actor = this
		}
		uiStage += scene2d.table {
			setFillParent(true)
			actor(toolbar).cell(expandY = true).inCell.top()
			actor(panel).cell(expand = true)
		}
		uiStage.keyboardFocus = panel.grid
		uiStage += toolbar.thermoMultibuttonMenu
		uiStage += toolbar.cageMultibuttonMenu
		
		if (mapper.read("commit_sudoku_state"))
			panel.readState(mapper)
		
		keyboardHeightObservers += syncDialog
		
		Scene2DSkin.addListener { updateStyles(it) }
	}
	
	override fun show()
	{
		super.show()
		info("CommitSudokuScreen    | INFO") { "Show CommitSudokuScreen" }
		runServer()
	}
	
	private fun runServer()
	{
		shouldCloseServer = false
		thread(isDaemon = true) {
			val hints = ServerSocketHints()
			hints.acceptTimeout = 0
			serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, 11530, hints)
			info("CommitSudokuScreen    | INFO") { "Running server..." }
			while (true)
			{
				try
				{
					val socket = serverSocket.accept(null)
					info("CommitSudokuScreen    | INFO") { "Accepting connection from ${socket.remoteAddress}" }
					val objectInputStream = ObjectInputStream(socket.inputStream)
					mapper.read(objectInputStream)
					objectInputStream.close()
					socket.dispose()
					panel.grid.reset()
					panel.readState(mapper)
				}
				catch (e: GdxRuntimeException)
				{
					if (shouldCloseServer)
						break
					else
						e.printStackTrace()
				}
			}
		}
	}
	
	private fun updateStyles(skin: Skin)
	{
		val oldSkin = if (skin == game.lightSkin) game.darkSkin else game.lightSkin
		uiStage.root.updateStyle(skin, oldSkin)
		syncDialog.updateStyle(skin, oldSkin)
		messageDialog.updateStyle(skin, oldSkin)
	}
	
	override fun pause()
	{
		info("CommitSudokuScreen    | INFO") { "Pause! Saving game state..." }
		panel.writeState(mapper)
		mapper.write("commit_sudoku_state")
		shouldCloseServer = true // Stop the thread
	}
	
	override fun resume() = runServer()
	
	override fun clearScreen() = ScreenUtils.clear(backgroundColor, true)
}
