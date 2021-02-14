package misterbander.commitsudoku.constraints

import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.scene2d.SudokuGrid

class ConstraintsChecker(private val grid: SudokuGrid)
{
	private val globalStatements: GdxArray<Statement> = GdxArray()
	private val staticStatements: GdxArray<Statement> = GdxArray()
	private val additionalConstraints: GdxArray<Constraint> = GdxArray()
	
	// Preset constraints
	private val sudokuConstraint = SudokuConstraint(grid.cells)
	val xConstraint = XConstraint(grid.cells)
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
		additionalConstraints.forEach { correctFlag = it.check() && correctFlag }
		correctFlag = sudokuConstraint.check() && correctFlag
		if (correctFlag)
			grid.panel.isFinished = true
	}
	
	operator fun plusAssign(constraint: Constraint)
	{
		if (constraint is Statement)
		{
			if (constraint.isGlobal && constraint !in globalStatements)
				globalStatements += constraint
			else if (constraint !in staticStatements)
				staticStatements += constraint
		}
		else if (constraint !in additionalConstraints)
			additionalConstraints += constraint
		check()
	}
	
	operator fun minusAssign(constraint: Constraint)
	{
		if (constraint is Statement)
		{
			globalStatements -= constraint
			staticStatements -= constraint
		}
		else
			additionalConstraints -= constraint
		check()
	}
	
	operator fun contains(constraint: Constraint): Boolean
	{
		return constraint in globalStatements || constraint in staticStatements || constraint in additionalConstraints
	}
}
