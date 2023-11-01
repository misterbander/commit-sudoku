package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import misterbander.commitsudoku.modifiers.GridModification
import misterbander.commitsudoku.scene2d.SudokuGrid
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

abstract class Decoration(protected val grid: SudokuGrid) : GridModification
{
	var color: Color? = null
	abstract val dataObject: HashMap<String, Serializable>

	abstract fun draw(shapeDrawer: ShapeDrawer)
}
