package misterbander.commitsudoku.constraints

import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.StringBuilder
import ktx.collections.GdxArray
import ktx.collections.plusAssign
import ktx.collections.set
import misterbander.commitsudoku.decorations.CageDecoration
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class KillerConstraint(private val grid: SudokuGrid, cage: CageDecoration) : Constraint
{
	private val killerCells: GdxArray<SudokuGrid.Cell> = GdxArray()
	val cornerTextDecoration: CornerTextDecoration = CornerTextDecoration(grid, cage.topLeftI, cage.topLeftJ, "")
	private var killerStatement: SingleStatement? = null
	var killerSum = 0
		set(value)
		{
			field = value
			cornerTextDecoration.text = if (value > 0) value.toString() else ""
			generateKillerStatement()
		}
	private val digitCellMap: IntMap<SudokuGrid.Cell> = IntMap()
	
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
			val statementBuilder = StringBuilder()
			killerCells.forEach {
				if (first)
					first = false
				else
					statementBuilder.append("+")
				statementBuilder.append("[r${9 - it.j}c${it.i + 1}]")
			}
			statementBuilder.append("=$killerSum")
			killerStatement = SingleStatement(grid.cells, statementBuilder.toString())
		}
		grid.constraintsChecker.check()
	}
	
	override fun check(): Boolean
	{
		var correctFlag = true
		correctFlag = killerStatement?.check() ?: true && correctFlag
		digitCellMap.clear()
		killerCells.forEach {
			if (it.digit == 0)
				return@forEach
			if (digitCellMap[it.digit] != null)
			{
				it.isCorrect = false
				digitCellMap[it.digit].isCorrect = false
				correctFlag = false
			}
			else
				digitCellMap[it.digit] = it
		}
		return correctFlag
	}
}
