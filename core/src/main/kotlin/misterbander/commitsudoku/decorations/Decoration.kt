package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.scene2d.SudokuGrid

abstract class Decoration(protected val grid: SudokuGrid)
{
	protected val game = grid.panel.screen.game
	abstract fun draw(batch: Batch, parentAlpha: Float)
}
