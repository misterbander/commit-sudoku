package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

abstract class Decoration(protected val grid: SudokuGrid)
{
	protected val game = grid.game
	abstract var color: Color?
	abstract val dataObject: HashMap<String, Serializable>
	abstract fun draw(batch: Batch)
}
