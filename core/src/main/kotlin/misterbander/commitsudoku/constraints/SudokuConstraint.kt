package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid

object SudokuConstraint : Constraint
{
	private val digitIndexMap = IntArray(9) { -1 }

	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var allFilled = true
		var correctFlag = true

		// Check row for duplicates
		for (row in 0..8)
		{
			digitIndexMap.fill(-1)
			for (col in 0..8)
			{
				val cell = cells[row][col]
				if (cell.digit == 0)
				{
					allFilled = false
					continue
				}
				val conflictingIndex = digitIndexMap[cell.digit - 1]
				if (conflictingIndex != -1)
				{
					cell.isCorrect = false
					cells[row][conflictingIndex].isCorrect = false
					correctFlag = false
				}
				else
					digitIndexMap[cell.digit - 1] = col
			}
		}

		// Check column for duplicates
		for (col in 0..8)
		{
			digitIndexMap.fill(-1)
			for (row in 0..8)
			{
				val cell = cells[row][col]
				if (cell.digit == 0)
				{
					allFilled = false
					continue
				}
				val conflictingIndex = digitIndexMap[cell.digit - 1]
				if (conflictingIndex != -1)
				{
					cell.isCorrect = false
					cells[conflictingIndex][col].isCorrect = false
					correctFlag = false
				}
				else
					digitIndexMap[cell.digit - 1] = row
			}
		}

		// Check boxes for duplicates
		for (i in 0 until 9 step 3)
		{
			for (j in 0 until 9 step 3)
			{
				digitIndexMap.fill(-1)
				for (k in 0..8)
				{
					val row = k%3 + i
					val col = k/3 + j
					val cell = cells[row][col]
					if (cell.digit == 0)
					{
						allFilled = false
						continue
					}
					val conflictingIndex = digitIndexMap[cell.digit - 1]
					if (conflictingIndex != -1)
					{
						cell.isCorrect = false
						cells[conflictingIndex%3 + i][conflictingIndex/3 + j].isCorrect = false
						correctFlag = false
					}
					else
						digitIndexMap[cell.digit - 1] = k
				}
			}
		}

		return correctFlag && allFilled
	}
}
