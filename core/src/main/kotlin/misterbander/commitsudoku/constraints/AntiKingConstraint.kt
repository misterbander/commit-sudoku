package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid

object AntiKingConstraint : Constraint
{
	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var correctFlag = true

		for (row in 0 until 8)
		{
			for (col in 0..8)
			{
				val cell = cells[row][col]
				val bottomLeftCell = if (col > 0) cells[row + 1][col - 1] else null
				val bottomRightCell = if (col < 8) cells[row + 1][col + 1] else null
				if (cell.digit == 0)
					continue
				if (cell.digit == bottomLeftCell?.digit)
				{
					cell.isCorrect = false
					bottomLeftCell.isCorrect = false
					correctFlag = false
				}
				if (cell.digit == bottomRightCell?.digit)
				{
					cell.isCorrect = false
					bottomRightCell.isCorrect = false
					correctFlag = false
				}
			}
		}
		return correctFlag
	}
}
