package misterbander.commitsudoku.constraints

import ktx.collections.GdxArray
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import ktx.log.info
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.Observable
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.collections.contains

class ConstraintsChecker : PersistentState
{
	private val globalStatements = GdxSet<Statement>()
	private val staticStatements = GdxSet<Statement>()
	private val additionalConstraints = GdxArray<Constraint>()

	// Preset constraints
	private val sudokuConstraint = SudokuConstraint()
	private val xConstraint = XConstraint()
	private val antiKingStatement = CompoundStatement("!=[~1~1]", "!=[~-1~1]")
	private val antiKnightStatement = CompoundStatement(
		"!=[~1~2]",
		"!=[~2~1]",
		"!=[~-1~2]",
		"!=[~-2~1]"
	)
	private val nonconsecutiveStatement = CompoundStatement(
		"!=[~1~0]+1",
		"!=[~1~0]-1",
		"!=[~0~1]+1",
		"!=[~0~1]-1"
	)

	// Constraint settings
	val xObservable = Observable(false) { value ->
		if (value)
			this += xConstraint
		else
			this -= xConstraint
	}
	var x by xObservable
	val antiKingObservable = Observable(false) { value ->
		if (value)
			this += antiKingStatement
		else
			this -= antiKingStatement
	}
	var antiKing by antiKingObservable
	val antiKnightObservable = Observable(false) { value ->
		if (value)
			this += antiKnightStatement
		else
			this -= antiKnightStatement
	}
	var antiKnight by antiKnightObservable
	val nonconsecutiveObservable = Observable(false) { value ->
		if (value)
			this += nonconsecutiveStatement
		else
			this -= nonconsecutiveStatement
	}
	var nonconsecutive by nonconsecutiveObservable

	fun check(grid: SudokuGrid): Boolean
	{
		info("ConstraintsChecker    | INFO") { "Checking constraints" }
		grid.cells.forEach { it.forEach { cell -> cell.isCorrect = true } }
		var correctFlag = true
		globalStatements.forEach { correctFlag = it.check(grid) && correctFlag }
		staticStatements.forEach { correctFlag = it.check(grid) && correctFlag }
		additionalConstraints.forEach { correctFlag = it.check(grid) && correctFlag }
		correctFlag = sudokuConstraint.check(grid) && correctFlag
		return correctFlag
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
	}

	operator fun contains(constraint: Constraint): Boolean =
		constraint in globalStatements || constraint in staticStatements || constraint in additionalConstraints

	fun clear()
	{
		globalStatements.clear()
		staticStatements.clear()
		additionalConstraints.clear()
		x = false
		antiKing = false
		antiKnight = false
		nonconsecutive = false
	}

	override fun readState(mapper: PersistentStateMapper)
	{
		x = mapper["x"] ?: false
		antiKing = mapper["antiKing"] ?: false
		antiKnight = mapper["antiKnight"] ?: false
		nonconsecutive = mapper["nonconsecutive"] ?: false

		val globalStatementStrs: Array<Array<String>>? = mapper["globalStatements"]
		val staticStatementStrs: Array<Array<String>>? = mapper["staticStatements"]
		globalStatementStrs?.forEach { statementStrGroup ->
			if (statementStrGroup.size == 1)
				globalStatements += SingleStatement(statementStrGroup[0])
			else
				globalStatements += CompoundStatement(*statementStrGroup)
		}
		staticStatementStrs?.forEach { statementStrGroup ->
			if (statementStrGroup.size == 1)
				staticStatements += SingleStatement(statementStrGroup[0])
			else
				staticStatements += CompoundStatement(*statementStrGroup)
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["x"] = xConstraint in additionalConstraints
		mapper["antiKing"] = false
		mapper["antiKnight"] = false
		mapper["nonconsecutive"] = false

		val globalStatementStrs = GdxArray<Array<String>>()
		val staticStatementStrs = GdxArray<Array<String>>()
		for (statement: Statement in globalStatements)
		{
			when (statement)
			{
				antiKingStatement -> mapper["antiKing"] = true
				antiKnightStatement -> mapper["antiKnight"] = true
				nonconsecutiveStatement -> mapper["nonconsecutive"] = true
				is SingleStatement -> globalStatementStrs += arrayOf(statement.statementStr)
				is CompoundStatement -> globalStatementStrs += statement.statementStrs
			}
		}
		for (statement: Statement in staticStatements)
		{
			when (statement)
			{
				is SingleStatement ->
					staticStatementStrs += arrayOf(statement.statementStr)
				is CompoundStatement ->
					staticStatementStrs += statement.statementStrs
			}
		}
		mapper["globalStatements"] = globalStatementStrs.toArray(Array<String>::class.java)
		mapper["staticStatements"] = staticStatementStrs.toArray(Array<String>::class.java)
	}

	fun drawAdditionalConstraints(shapeDrawer: ShapeDrawer) =
		additionalConstraints.forEach { it.drawConstraint(shapeDrawer) }
}
