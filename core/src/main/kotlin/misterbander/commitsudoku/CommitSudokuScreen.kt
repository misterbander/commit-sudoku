package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.utils.ScreenUtils
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.collections.*
import ktx.log.info
import ktx.scene2d.*
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.scene2d.SidePanel
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.scene2d.Toolbar
import misterbander.commitsudoku.scene2d.actions.ActionController
import misterbander.commitsudoku.scene2d.dialogs.MessageDialog
import misterbander.commitsudoku.scene2d.dialogs.SyncDialog
import misterbander.commitsudoku.scene2d.updateStyle
import misterbander.gframework.DefaultGScreen
import misterbander.gframework.util.Observable
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import kotlin.math.min

class CommitSudokuScreen(
	game: CommitSudoku,
	val lightSkin: Skin,
	val darkSkin: Skin
) : DefaultGScreen<CommitSudoku>(game), PersistentState
{
	private val constraintsChecker = ConstraintsChecker()
	private val actionController = ActionController()
	private val mapper = PersistentStateMapper()

	private val isDarkMode: Boolean
		get() = Scene2DSkin.defaultSkin == darkSkin
	val isFinishedObservable = Observable(false)
	var isFinished by isFinishedObservable

	// UI
	private val toolbar = Toolbar(this, constraintsChecker)
	val grid = SudokuGrid(this, constraintsChecker, actionController)
	val panel = SidePanel(this, actionController, isDarkMode)
	val messageDialog = MessageDialog()
	val syncDialog = SyncDialog(this, mapper)

	private val server = CommitSudokuServer(this)

	init
	{
		uiStage += object : Actor() // Fallback actor
		{
			init
			{
				onTouchDown { grid.unselect() }
			}

			override fun hit(x: Float, y: Float, touchable: Boolean): Actor = this
		}
		uiStage += scene2d.table {
			setFillParent(true)
			actor(toolbar).cell(growY = true).inCell.top()
			container(grid) {
				size(object : Value()
				{
					override fun get(context: Actor?): Float = min(width, height)
				})
			}.cell(grow = true, pad = 64F)
			actor(panel).cell(padBottom = 64F).inCell.bottom()
		}
		uiStage.keyboardFocus = grid

		if (mapper.read("commit_sudoku_state"))
			readState(mapper)

		keyboardHeightObservers += syncDialog

		Scene2DSkin.addListener { updateStyles(it) }
	}

	override fun show()
	{
		super.show()
		info("CommitSudokuScreen    | INFO") { "Show CommitSudokuScreen" }
		server.start(mapper)
	}

	private fun updateStyles(skin: Skin)
	{
		val oldSkin = if (skin == lightSkin) darkSkin else lightSkin
		uiStage.root.updateStyle(skin, oldSkin)
		syncDialog.updateStyle(skin, oldSkin)
		messageDialog.updateStyle(skin, oldSkin)
	}

	override fun pause()
	{
		info("CommitSudokuScreen    | INFO") { "Pause! Saving game state..." }
		writeState(mapper)
		mapper.write("commit_sudoku_state")
		server.stop()
	}

	override fun resume() = server.start(mapper)

	override fun clearScreen() = ScreenUtils.clear(backgroundColor, true)

	fun reset()
	{
		grid.reset()
		panel.resetTimer()
		actionController.clearHistory()
		constraintsChecker.clear()
	}

	override fun readState(mapper: PersistentStateMapper)
	{
		grid.readState(mapper)
		panel.readState(mapper)
		actionController.readState(grid, mapper)
		constraintsChecker.readState(mapper)
		constraintsChecker.check(grid.cells)
		Gdx.graphics.requestRendering()
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		grid.writeState(mapper)
		panel.writeState(mapper)
		actionController.writeState(mapper)
		constraintsChecker.writeState(mapper)
	}
}
