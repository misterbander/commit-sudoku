package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.plusAssign
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuPanel
import misterbander.gframework.GScreen

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	private val sudokuPanel = SudokuPanel(this)
	
	override fun show()
	{
		super.show()
		println("Show CommitSudokuScreen")
		stage += scene2d.table {
			setFillParent(true)
			debug = true
			actor(sudokuPanel).cell(expand = true)
		}
		stage.keyboardFocus = sudokuPanel.grid
	}
	
	fun updateStyles()
	{
		val otherSkin = if (game.skin == game.lightSkin) game.darkSkin else game.lightSkin
		fun updateTable(table: Table)
		{
			table.cells.forEach {
				when (val actor: Actor = it.actor)
				{
					is Label -> actor.style = game.skin[otherSkin.find(actor.style)]
					is TextButton -> actor.style = game.skin[otherSkin.find(actor.style)]
					is ImageButton -> actor.style = game.skin[otherSkin.find(actor.style)]
					is Table -> updateTable(actor)
				}
			}
		}
		stage.actors.forEach {
			if (it is Table)
				updateTable(it)
		}
	}
	
	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
	{
		sudokuPanel.grid.unselect()
		return true
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
