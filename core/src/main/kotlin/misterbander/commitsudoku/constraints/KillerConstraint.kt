package misterbander.commitsudoku.constraints

import ktx.collections.*
import misterbander.commitsudoku.decorations.CageDecoration
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class KillerConstraint(grid: SudokuGrid, cage: CageDecoration) : Constraint
{
	private val killerCells = GdxArray<SudokuGrid.Cell>()
	val cornerTextDecoration = CornerTextDecoration(grid, cage.topLeftRow, cage.topLeftCol, "")
	var killerSum = 0
		set(value)
		{
			field = value
			cornerTextDecoration.text = if (value > 0) value.toString() else ""
		}
	private val digitCellMap = Array<SudokuGrid.Cell?>(9) { null }

	init
	{
		for (i in cage.mask.indices.reversed())
		{
			for (j in cage.mask[i].indices)
			{
				if (cage.mask[i][j])
					killerCells += grid.cells[i][j]
			}
		}
	}

	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		var correctFlag = true
		var allFilled = true
		var sum = 0

		digitCellMap.fill(null)
		for (cell: SudokuGrid.Cell in killerCells)
		{
			if (cell.digit == 0)
			{
				allFilled = false
				continue
			}
			val conflictingCell = digitCellMap[cell.digit - 1]
			if (conflictingCell != null)
			{
				cell.isCorrect = false
				conflictingCell.isCorrect = false
				correctFlag = false
			}
			else
				digitCellMap[cell.digit - 1] = cell
			sum += cell.digit
		}

		if (killerSum > 0 && allFilled && sum != killerSum)
		{
			killerCells.forEach { cell -> cell.isCorrect = false }
			correctFlag = false
		}

		return correctFlag
	}
}
