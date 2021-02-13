package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.plusAssign
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuPanel
import misterbander.commitsudoku.scene2d.Toolbar
import misterbander.gframework.GScreen
import misterbander.gframework.util.PersistentStateMapper

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	private val sudokuPanel = SudokuPanel(this)
	private val toolbar = Toolbar(this)
	
	private val mapper = PersistentStateMapper("commit_sudoku_state")
	
	override fun show()
	{
		super.show()
		println("Show CommitSudokuScreen")
		stage += scene2d.table {
			setFillParent(true)
			debug = true
			actor(toolbar).cell(expandY = true).inCell.top()
			actor(sudokuPanel).cell(expand = true)
		}
		stage.keyboardFocus = sudokuPanel.grid
		
		if (mapper.read())
			sudokuPanel.readState(mapper)
	}
	
	private fun updateActorStyle(actor: Actor, otherSkin: Skin)
	{
		when (actor)
		{
			is Label -> actor.style = game.skin[otherSkin.find(actor.style)]
			is TextButton -> actor.style = game.skin[otherSkin.find(actor.style)]
			is ImageButton -> actor.style = game.skin[otherSkin.find(actor.style)]
			is Table -> actor.cells.forEach { updateActorStyle(it.actor, otherSkin) }
			is Group -> actor.children.forEach { updateActorStyle(it, otherSkin) }
		}
	}
	
	fun updateStyles()
	{
		val otherSkin = if (game.skin == game.lightSkin) game.darkSkin else game.lightSkin
		stage.actors.forEach { updateActorStyle(it, otherSkin) }
	}
	
	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
	{
		sudokuPanel.grid.unselect()
		return true
	}
	
	override fun pause()
	{
		println("Pause! Saving game state...")
		sudokuPanel.writeState(mapper)
		mapper.write()
	}
	
	override fun render(delta: Float)
	{
		super.render(delta)
		sudokuPanel.timer.update(delta)
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
	}
}
