package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid

object AntiKnightConstraint : Constraint
{
	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var correctFlag = true

		for (row in 0 until 8)
		{
			for (col in 0..8)
			{
				val cell = cells[row][col]
				val leftLeftDownCell = if (col > 1) cells[row + 1][col - 2] else null
				val leftDownDownCell = if (row < 7 && col > 0) cells[row + 2][col - 1] else null
				val rightDownDownCell = if (row  < 7 && col < 8) cells[row + 2][col + 1] else null
				val rightRightDownCell = if (col < 7) cells[row + 1][col + 2] else null
				if (cell.digit == 0)
					continue
				if (cell.digit == leftLeftDownCell?.digit)
				{
					cell.isCorrect = false
					leftLeftDownCell.isCorrect = false
					correctFlag = false
				}
				if (cell.digit == leftDownDownCell?.digit)
				{
					cell.isCorrect = false
					leftDownDownCell.isCorrect = false
					correctFlag = false
				}
				if (cell.digit == rightDownDownCell?.digit)
				{
					cell.isCorrect = false
					rightDownDownCell.isCorrect = false
					correctFlag = false
				}
				if (cell.digit == rightRightDownCell?.digit)
				{
					cell.isCorrect = false
					rightRightDownCell.isCorrect = false
					correctFlag = false
				}
			}
		}
		return correctFlag
	}
}
