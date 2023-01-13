package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.modifiers.GridModification
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

abstract class Decoration(protected val grid: SudokuGrid) : GridModification
{
	protected val game: CommitSudoku
		get() = grid.game
	protected val screen: CommitSudokuScreen
		get() = grid.screen
	var color: Color? = null
	abstract val dataObject: HashMap<String, Serializable>
	
	abstract fun draw(batch: Batch)
}
