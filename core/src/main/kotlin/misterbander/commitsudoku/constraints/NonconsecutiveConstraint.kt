package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid
import kotlin.math.abs

object NonconsecutiveConstraint : Constraint
{
	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var correctFlag = true

		for (row in 0..8)
		{
			for (col in 0 until 8)
			{
				val cell = cells[row][col]
				val rightCell = cells[row][col + 1]
				val bottomCell = if (row < 8) cells[row + 1][col] else null
				if (cell.digit == 0)
					continue
				if (rightCell.digit != 0 && abs(cell.digit - rightCell.digit) == 1)
				{
					cell.isCorrect = false
					rightCell.isCorrect = false
					correctFlag = false
				}
				if (bottomCell != null && bottomCell.digit != 0 && abs(cell.digit - bottomCell.digit) == 1)
				{
					cell.isCorrect = false
					bottomCell.isCorrect = false
					correctFlag = false
				}
			}
		}
		return correctFlag
	}
}
