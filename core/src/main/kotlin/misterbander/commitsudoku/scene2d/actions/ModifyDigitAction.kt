package misterbander.commitsudoku.scene2d.actions

import misterbander.commitsudoku.scene2d.SudokuGrid

class ModifyDigitAction(
	private val cell: SudokuGrid.Cell,
	private val from: Int = cell.digit,
	private val to: Int
) : ModifyCellAction()
{
	init
	{
		runnable = Runnable {
			val from = if (inverse) this.to else this.from
			val to = if (inverse) this.from else this.to
			println("Set cell (${cell.i}, ${cell.j}) digit from $from to $to")
			cell.digit = to
		}
	}
	
	override fun toString(): String
	{
		return "digit (${cell.i},${cell.j}) $from $to"
	}
}
