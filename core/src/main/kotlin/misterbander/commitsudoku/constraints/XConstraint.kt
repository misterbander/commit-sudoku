package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid

object XConstraint : Constraint
{
	private val digitIndexMap = IntArray(9) { -1 }

	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var correctFlag = true

		digitIndexMap.fill(-1)
		for (i in 0..8)
		{
			val cell = cells[i][i]
			if (cell.digit == 0)
				continue
			val conflictingIndex = digitIndexMap[cell.digit - 1]
			if (conflictingIndex != -1)
			{
				cell.isCorrect = false
				cells[conflictingIndex][conflictingIndex].isCorrect = false
				correctFlag = false
			}
			else
				digitIndexMap[cell.digit - 1] = i
		}
		digitIndexMap.fill(-1)
		for (i in 0..8)
		{
			val cell = cells[i][8 - i]
			if (cell.digit == 0)
				continue
			val conflictingIndex = digitIndexMap[cell.digit - 1]
			if (conflictingIndex != -1)
			{
				cell.isCorrect = false
				cells[conflictingIndex][8 - conflictingIndex].isCorrect = false
				correctFlag = false
			}
			else
				digitIndexMap[cell.digit - 1] = i
		}
		return correctFlag
	}
}
