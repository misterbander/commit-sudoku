package misterbander.commitsudoku.constraints

import com.badlogic.gdx.utils.IntIntMap
import ktx.collections.set
import misterbander.commitsudoku.scene2d.SudokuGrid

class SudokuConstraint(private val grid: SudokuGrid) : Constraint
{
	private val digitIndexMap: IntIntMap = IntIntMap()
	
	override fun check(): Boolean
	{
		var allFilled = true
		var correctFlag = true
		
		// Check row for duplicates
		for (j in 0..8)
		{
			digitIndexMap.clear()
			for (i in 0..8)
			{
				val cell = grid.cells[i][j]
				if (cell.digit == 0)
				{
					allFilled = false
					continue
				}
				val digitIndex = digitIndexMap.get(cell.digit, -1)
				if (digitIndex != -1)
				{
					cell.isCorrect = false
					grid.cells[digitIndex][j].isCorrect = false
					correctFlag = false
				}
				else
					digitIndexMap[cell.digit] = i
			}
		}
		
		// Check column for duplicates
		for (i in 0..8)
		{
			digitIndexMap.clear()
			for (j in 0..8)
			{
				val cell = grid.cells[i][j]
				if (cell.digit == 0)
				{
					allFilled = false
					continue
				}
				val digitIndex = digitIndexMap.get(cell.digit, -1)
				if (digitIndex != -1)
				{
					cell.isCorrect = false
					grid.cells[i][digitIndex].isCorrect = false
					correctFlag = false
				}
				else
					digitIndexMap[cell.digit] = j
			}
		}
		
		// Check boxes for duplicates
		for (i1 in 0 until 9 step 3)
		{
			for (j1 in 0 until 9 step 3)
			{
				digitIndexMap.clear()
				for (k in 0..8)
				{
					val i = k%3 + i1
					val j = k/3 + j1
					val cell = grid.cells[i][j]
					if (cell.digit == 0)
					{
						allFilled = false
						continue
					}
					val digitIndex = digitIndexMap.get(cell.digit, -1)
					if (digitIndex != -1)
					{
						cell.isCorrect = false
						grid.cells[digitIndex%3 + i1][digitIndex/3 + j1].isCorrect = false
						correctFlag = false
					}
					else
						digitIndexMap[cell.digit] = k
				}
			}
		}
		
		return correctFlag && allFilled
	}
}
