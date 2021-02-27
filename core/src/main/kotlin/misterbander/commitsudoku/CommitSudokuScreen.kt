package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.collections.plusAssign
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.InputWindow
import misterbander.commitsudoku.scene2d.SudokuPanel
import misterbander.commitsudoku.scene2d.Toolbar
import misterbander.commitsudoku.scene2d.ToolbarMultibuttonMenu
import misterbander.gframework.GScreen
import misterbander.gframework.scene2d.MBTextField
import misterbander.gframework.util.PersistentStateMapper

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	val panel = SudokuPanel(this)
	val toolbar = Toolbar(this)
	val textInputWindow = InputWindow(this, isModal = true)
	
	private val mapper = PersistentStateMapper("commit_sudoku_state")
	
	init
	{
		accessibleInputWindows += textInputWindow
	}
	
	override fun show()
	{
		super.show()
		println("Show CommitSudokuScreen")
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
		stage += textInputWindow
		
		if (mapper.read())
			panel.readState(mapper)
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
			is InputWindow ->
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
	
	override fun pause()
	{
		println("Pause! Saving game state...")
		panel.writeState(mapper)
		mapper.write()
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
	}
}
