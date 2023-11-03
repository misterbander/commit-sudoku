package misterbander.commitsudoku.constraints

import com.badlogic.gdx.utils.IntMap
import ktx.collections.*
import ktx.collections.set
import misterbander.commitsudoku.decorations.CageDecoration
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.GdxStringBuilder

class KillerConstraint(
	private val grid: SudokuGrid,
	cage: CageDecoration,
	private val constraintsChecker: ConstraintsChecker
) : Constraint
{
	private val killerCells = GdxArray<SudokuGrid.Cell>()
	val cornerTextDecoration = CornerTextDecoration(grid, cage.topLeftRow, cage.topLeftCol, "")
	private var killerStatement: SingleStatement? = null
	var killerSum = 0
		set(value)
		{
			field = value
			cornerTextDecoration.text = if (value > 0) value.toString() else ""
			generateKillerStatement()
		}
	private val digitCellMap = IntMap<SudokuGrid.Cell>()

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

	private fun generateKillerStatement()
	{
		if (killerSum == 0)
			killerStatement = null
		else
		{
			var first = true
			val statementBuilder = GdxStringBuilder()
			for (cell: SudokuGrid.Cell in killerCells)
			{
				if (first)
					first = false
				else
					statementBuilder.append("+")
				statementBuilder.append("[r${cell.row + 1}c${cell.col + 1}]")
			}
			statementBuilder.append("=$killerSum")
			killerStatement = SingleStatement(statementBuilder.toString())
		}
		constraintsChecker.check(grid)
	}

	override fun check(grid: SudokuGrid): Boolean
	{
		var correctFlag = true
		correctFlag = killerStatement?.check(grid) ?: true && correctFlag
		digitCellMap.clear()
		for (cell: SudokuGrid.Cell in killerCells)
		{
			if (cell.digit == 0)
				break
			if (digitCellMap[cell.digit] != null)
			{
				cell.isCorrect = false
				digitCellMap[cell.digit].isCorrect = false
				correctFlag = false
			}
			else
				digitCellMap[cell.digit] = cell
		}
		return correctFlag
	}
}
