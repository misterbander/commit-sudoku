package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.g2d.Batch
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class ConstraintsChecker(private val grid: SudokuGrid) : PersistentState
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
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val toolbar = grid.panel.screen.toolbar
		toolbar.xButton.isChecked = mapper["x"] ?: false
		toolbar.antiKingButton.isChecked = mapper["antiKing"] ?: false
		toolbar.antiKnightButton.isChecked = mapper["antiKnight"] ?: false
		toolbar.nonconsecutiveButton.isChecked = mapper["nonconsecutive"] ?: false
		
		val globalStatementStrs: Array<Array<String>>? = mapper["globalStatements"]
		val staticStatementStrs: Array<Array<String>>? = mapper["staticStatements"]
		globalStatementStrs?.forEach { statementStrGroup ->
			if (statementStrGroup.size == 1)
				globalStatements += SingleStatement(grid.cells, statementStrGroup[0])
			else
				globalStatements += CompoundStatement(grid.cells, *statementStrGroup)
		}
		staticStatementStrs?.forEach { statementStrGroup ->
			if (statementStrGroup.size == 1)
				staticStatements += SingleStatement(grid.cells, statementStrGroup[0])
			else
				staticStatements += CompoundStatement(grid.cells, *statementStrGroup)
		}
		check()
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["x"] = xConstraint in additionalConstraints
		mapper["antiKing"] = false
		mapper["antiKnight"] = false
		mapper["nonconsecutive"] = false
		
		val globalStatementStrs: GdxArray<Array<String>> = GdxArray()
		val staticStatementStrs: GdxArray<Array<String>> = GdxArray()
		globalStatements.forEach { statement ->
			when (statement)
			{
				antiKingStatement -> mapper["antiKing"] = true
				antiKnightStatement -> mapper["antiKnight"] = true
				nonconsecutiveStatement -> mapper["nonconsecutive"] = true
				is SingleStatement ->
					globalStatementStrs += arrayOf(statement.statementStr)
				is CompoundStatement ->
					globalStatementStrs += statement.statementStrs
			}
		}
		staticStatements.forEach { statement ->
			when (statement)
			{
				is SingleStatement ->
					staticStatementStrs += arrayOf(statement.statementStr)
				is CompoundStatement ->
					staticStatementStrs += statement.statementStrs
			}
		}
		mapper["globalStatements"] = globalStatementStrs.toArray(Array<Array<String>>::class.java)
		mapper["staticStatements"] = staticStatementStrs.toArray(Array<Array<String>>::class.java)
	}
	
	fun drawAdditionalConstraints(batch: Batch)
	{
		additionalConstraints.forEach { it.drawConstraint(batch) }
	}
}
