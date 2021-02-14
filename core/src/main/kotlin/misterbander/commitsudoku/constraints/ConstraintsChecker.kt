package misterbander.commitsudoku.constraints

import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.scene2d.SudokuGrid

class ConstraintsChecker(private val grid: SudokuGrid)
{
	private val globalStatements: GdxArray<Statement> = GdxArray()
	private val staticStatements: GdxArray<Statement> = GdxArray()
	
	// Preset constraints
	val sudokuConstraint = SudokuConstraint(grid)
	val antiKingStatement = CompoundStatement(grid.cells, "!=[~1~1]", "!=[~-1~1]")
	val antiKnightStatement = CompoundStatement(
		grid.cells,
		"!=[~1~2]",
		"!=[~2~1]",
		"!=[~-1~2]",
		"!=[~-2~1]"
	)
	val nonconsecutiveStatement = CompoundStatement(
		grid.cells,
		"!=[~1~0]+1",
		"!=[~1~0]-1",
		"!=[~0~1]+1",
		"!=[~0~1]-1"
	)
	
	fun check()
	{
		println("Checking constraints")
		grid.cells.forEach { it.forEach { cell -> cell.isCorrect = true } }
		var correctFlag = true
		globalStatements.forEach { correctFlag = it.check() && correctFlag }
		staticStatements.forEach { correctFlag = it.check() && correctFlag }
		correctFlag = sudokuConstraint.check() && correctFlag
		if (correctFlag)
			grid.panel.isFinished = true
	}
	
	operator fun plusAssign(statement: Statement)
	{
		if (statement.isGlobal && statement !in globalStatements)
			globalStatements += statement
		else if (statement !in staticStatements)
			staticStatements += statement
		check()
	}
	
	operator fun minusAssign(statement: Statement)
	{
		globalStatements -= statement
		staticStatements -= statement
		check()
	}
}
