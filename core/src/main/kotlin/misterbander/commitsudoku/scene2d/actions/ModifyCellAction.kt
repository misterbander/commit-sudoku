package misterbander.commitsudoku.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import misterbander.commitsudoku.scene2d.SudokuGrid

class ModifyCellAction(
	private val cell: SudokuGrid.Cell,
	private val type: Type,
	private val from: Int = cell.digit,
	private val to: Int
) : RunnableAction()
{
	var inverse: Boolean = false
	
	init
	{
		runnable = Runnable {
			val from = if (inverse) this.to else this.from
			val to = if (inverse) this.from else this.to
			println("Set cell (${cell.i}, ${cell.j}) ${type.name} from $from to $to")
			cell.digit = to
		}
	}
	
	enum class Type
	{
		DIGIT, CORNER_MARK, CENTER_MARK, COLOR
	}
}
