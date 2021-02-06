package misterbander.commitsudoku.scene2d.actions

import misterbander.commitsudoku.scene2d.SudokuGrid

class ModifyColorAction(
	private val cell: SudokuGrid.Cell,
	private val from: Int = cell.colorCode,
	private val to: Int
) : ModifyCellAction()
{
	init
	{
		runnable = Runnable {
			val from = if (inverse) this.to else this.from
			val to = if (inverse) this.from else this.to
			println("Set cell (${cell.i}, ${cell.j}) color from $from to $to")
			cell.colorCode = to
		}
	}
}
