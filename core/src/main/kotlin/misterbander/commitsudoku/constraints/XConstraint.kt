package misterbander.commitsudoku.constraints

import com.badlogic.gdx.utils.IntIntMap
import ktx.collections.*
import misterbander.commitsudoku.scene2d.SudokuGrid

class XConstraint : Constraint
{
	private val digitIndexMap = IntIntMap()

	override fun check(grid: SudokuGrid): Boolean
	{
		val cells = grid.cells
		var correctFlag = true

		digitIndexMap.clear()
		for (i in 0..8)
		{
			val cell = cells[i][i]
			if (cell.digit == 0)
				continue
			if (digitIndexMap[cell.digit, -1] != -1)
			{
				cell.isCorrect = false
				val conflictIndex = digitIndexMap[cell.digit]
				cells[conflictIndex][conflictIndex].isCorrect = false
				correctFlag = false
			}
			else
				digitIndexMap[cell.digit] = i
		}
		digitIndexMap.clear()
		for (i in 0..8)
		{
			val cell = cells[8 - i][i]
			if (cell.digit == 0)
				continue
			if (digitIndexMap[cell.digit, -1] != -1)
			{
				cell.isCorrect = false
				val conflictIndex = digitIndexMap[cell.digit]
				cells[8 - conflictIndex][conflictIndex].isCorrect = false
				correctFlag = false
			}
			else
				digitIndexMap[cell.digit] = i
		}
		return correctFlag
	}
}
