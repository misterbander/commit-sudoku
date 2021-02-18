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
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.InputWindow
import misterbander.commitsudoku.scene2d.SudokuPanel
import misterbander.commitsudoku.scene2d.Toolbar
import misterbander.gframework.GScreen
import misterbander.gframework.scene2d.MBTextField
import misterbander.gframework.util.LayoutSizeChangeListener
import misterbander.gframework.util.PersistentStateMapper

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game), LayoutSizeChangeListener
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	val sudokuPanel = SudokuPanel(this)
	private val toolbar = Toolbar(this)
	val textInputWindow = InputWindow(game, isModal = true)
	val valueInputWindow = InputWindow(game, isModal = true, digitsOnly = true, maxLength = 2)
	
	private val mapper = PersistentStateMapper("commit_sudoku_state")
	
	override fun show()
	{
		super.show()
		println("Show CommitSudokuScreen")
		stage += object : Actor() // Fallback actor
		{
			init
			{
				onTouchDown { sudokuPanel.grid.unselect() }
			}
			
			override fun hit(x: Float, y: Float, touchable: Boolean): Actor
			{
				return this
			}
		}
		stage += scene2d.table {
			setFillParent(true)
			debug = true
			actor(toolbar).cell(expandY = true).inCell.top()
			actor(sudokuPanel).cell(expand = true)
		}
		stage.keyboardFocus = sudokuPanel.grid
		stage += textInputWindow
		stage += valueInputWindow
		
		if (mapper.read())
		{
			sudokuPanel.readState(mapper)
			toolbar.readState(mapper)
		}
	}
	
	private fun updateActorStyle(actor: Actor, otherSkin: Skin, excludeKeypads: Boolean = true)
	{
		if (excludeKeypads)
		{
			if (actor == sudokuPanel.digitKeypad || actor == sudokuPanel.cornerMarkKeypad
				|| actor == sudokuPanel.centerMarkKeypad || actor == sudokuPanel.colorKeypad)
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
				actor.cells.forEach { updateActorStyle(it.actor, otherSkin) }
				actor.closeButton.style = game.skin[otherSkin.find(actor.closeButton.style)]
			}
			is Table -> actor.cells.forEach { updateActorStyle(it.actor, otherSkin) }
			is Group -> actor.children.forEach { updateActorStyle(it, otherSkin) }
		}
	}
	
	fun updateStyles()
	{
		val otherSkin = if (game.skin == game.lightSkin) game.darkSkin else game.lightSkin
		stage.actors.forEach { updateActorStyle(it, otherSkin) }
		updateActorStyle(sudokuPanel.digitKeypad, otherSkin, false)
		updateActorStyle(sudokuPanel.cornerMarkKeypad, otherSkin, false)
		updateActorStyle(sudokuPanel.centerMarkKeypad, otherSkin, false)
		updateActorStyle(sudokuPanel.colorKeypad, otherSkin, false)
	}
	
	override fun pause()
	{
		println("Pause! Saving game state...")
		sudokuPanel.writeState(mapper)
		toolbar.writeState(mapper)
		mapper.write()
	}
	
	override fun onLayoutSizeChange(screenWidth: Int, screenHeight: Int)
	{
		textInputWindow.adjustPosition(screenHeight)
		valueInputWindow.adjustPosition(screenHeight)
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
	}
}
