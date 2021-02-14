package misterbander.commitsudoku.constraints

import com.badlogic.gdx.utils.IntIntMap
import ktx.collections.get
import ktx.collections.set
import misterbander.commitsudoku.scene2d.SudokuGrid

class XConstraint(private val cells: Array<Array<SudokuGrid.Cell>>) : Constraint
{
	private val digitIndexMap: IntIntMap = IntIntMap()
	
	override fun check(): Boolean
	{
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