package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.graphics.use
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.GScreen

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	private val table: Table by lazy {
		scene2d.table {
			width = 1280F
			height = 720F
			setDebug(true, true)
			actor(grid).cell(space = 32F)
			table {
				label("Commit Sudoku\ntest", "infolabel", game.skin) {
					setAlignment(Align.left)
				}.inCell.left()
				row()
				label("Quick brown fox really jumped over the lazy dog", "infolabel", game.skin)
			}.inCell.top().left()
		}
	}
	private val grid = SudokuGrid(game)
	
	override fun show()
	{
		super.show()
		stage.addActor(table)
	}
	
	override fun render(delta: Float)
	{
		super.render(delta)
		game.shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
			it.line(32F, 32F, 100F, 100F)
		}
		game.batch.use {
			game.shapeDrawer.setColor(Color.RED)
			game.shapeDrawer.circle(32F, 32F, 200F)
		}
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
	}
}
