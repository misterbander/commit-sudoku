package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.collections.plusAssign
import ktx.log.info
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.*
import misterbander.gframework.GScreen
import misterbander.gframework.scene2d.MBTextField
import misterbander.gframework.util.PersistentStateMapper
import java.io.ObjectInputStream
import kotlin.concurrent.thread

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	val panel = SudokuPanel(this)
	val toolbar = Toolbar(this)
	val textInputWindow = SingleInputWindow(this, isModal = true)
	val connectWindow = ConnectWindow(this)
	val messageDialog = MessageDialog(this)
	
	val mapper = PersistentStateMapper()
	
	lateinit var serverSocket: ServerSocket
	@Volatile var shouldCloseServer = false
		set(value)
		{
			field = value
			if (value)
				serverSocket.dispose()
		}
	
	init
	{
		accessibleInputWindows += textInputWindow
		accessibleInputWindows += connectWindow
	}
	
	override fun show()
	{
		super.show()
		info("CommitSudokuScreen    | INFO") { "Show CommitSudokuScreen" }
		stage += object : Actor() // Fallback actor
		{
			init
			{
				onTouchDown { panel.grid.unselect() }
			}
			
			override fun hit(x: Float, y: Float, touchable: Boolean): Actor
			{
				return this
			}
		}
		stage += scene2d.table {
			setFillParent(true)
			actor(toolbar).cell(expandY = true).inCell.top()
			actor(panel).cell(expand = true)
		}
		stage.keyboardFocus = panel.grid
		stage += toolbar.thermoMultibuttonMenu
		stage += toolbar.cageMultibuttonMenu
		stage += textInputWindow
		stage += connectWindow
		stage += messageDialog
		
		if (mapper.read("commit_sudoku_state"))
			panel.readState(mapper)
		
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
	
	private fun updateActorStyle(actor: Actor, otherSkin: Skin, vararg exclude: Actor)
	{
		exclude.forEach {
			if (actor == it)
				return
		}
		when (actor)
		{
			is Label -> actor.style = game.skin[otherSkin.find(actor.style)]
			is TextButton -> actor.style = game.skin[otherSkin.find(actor.style)]
			is ImageButton -> actor.style = game.skin[otherSkin.find(actor.style)]
			is MBTextField -> actor.style = game.skin[otherSkin.find(actor.style)]
			is CommitSudokuWindow ->
			{
				actor.style = game.skin[otherSkin.find(actor.style)]
				actor.cells.forEach { updateActorStyle(it.actor, otherSkin, *exclude) }
				actor.closeButton.style = game.skin[otherSkin.find(actor.closeButton.style)]
			}
			is Table -> actor.cells.forEach { updateActorStyle(it.actor, otherSkin, *exclude) }
			is ToolbarMultibuttonMenu ->
			{
				actor.updateStyle()
				actor.children.forEach { updateActorStyle(it, otherSkin, *exclude) }
			}
			is Group -> actor.children.forEach { updateActorStyle(it, otherSkin, *exclude) }
		}
	}
	
	fun updateStyles()
	{
		val otherSkin = if (game.isDarkMode) game.lightSkin else game.darkSkin
		stage.actors.forEach {
			updateActorStyle(
				it, otherSkin,
				panel.digitKeypad,
				panel.cornerMarkKeypad,
				panel.centerMarkKeypad,
				panel.colorKeypad,
				panel.undoRedoTray,
				panel.zeroButton
			)
		}
		updateActorStyle(panel.digitKeypad, otherSkin)
		updateActorStyle(panel.cornerMarkKeypad, otherSkin)
		updateActorStyle(panel.centerMarkKeypad, otherSkin)
		updateActorStyle(panel.colorKeypad, otherSkin)
		updateActorStyle(panel.undoRedoTray, otherSkin)
		updateActorStyle(panel.zeroButton, otherSkin)
	}
	
	override fun resize(width: Int, height: Int)
	{
		viewport.update(width, height, true)
	}
	
	override fun pause()
	{
		info("CommitSudokuScreen    | INFO") { "Pause! Saving game state..." }
		panel.writeState(mapper)
		mapper.write("commit_sudoku_state")
		shouldCloseServer = true // Stop the thread
	}
	
	override fun resume()
	{
		runServer()
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
	}
}
