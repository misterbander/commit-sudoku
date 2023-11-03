package misterbander.commitsudoku.constraints

import ktx.collections.GdxArray
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
	private val constraints = GdxArray<Constraint>()

	// Constraint settings
	val xObservable = Observable(false)
	var x by xObservable
	val antiKingObservable = Observable(false)
	var antiKing by antiKingObservable
	val antiKnightObservable = Observable(false)
	var antiKnight by antiKnightObservable
	val nonconsecutiveObservable = Observable(false)
	var nonconsecutive by nonconsecutiveObservable

	fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		info("ConstraintsChecker    | INFO") { "Checking constraints" }
		cells.forEach { it.forEach { cell -> cell.isCorrect = true } }
		var correctFlag = true
		if (x)
			correctFlag = XConstraint.check(cells) && correctFlag
		if (antiKing)
			correctFlag = AntiKingConstraint.check(cells) && correctFlag
		if (antiKnight)
			correctFlag = AntiKnightConstraint.check(cells) && correctFlag
		if (nonconsecutive)
			correctFlag = NonconsecutiveConstraint.check(cells) && correctFlag
		constraints.forEach { correctFlag = it.check(cells) && correctFlag }
		correctFlag = SudokuConstraint.check(cells) && correctFlag
		return correctFlag
	}

	operator fun plusAssign(constraint: Constraint)
	{
		constraints += constraint
	}

	operator fun minusAssign(constraint: Constraint)
	{
		constraints -= constraint
	}

	operator fun contains(constraint: Constraint): Boolean = constraint in constraints

	fun clear()
	{
		constraints.clear()
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
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["x"] = x
		mapper["antiKing"] = antiKing
		mapper["antiKnight"] = antiKnight
		mapper["nonconsecutive"] = nonconsecutive
	}

	fun drawConstraints(shapeDrawer: ShapeDrawer)
	{
		if (x)
			XConstraint.drawConstraint(shapeDrawer)
		constraints.forEach { it.drawConstraint(shapeDrawer) }
	}
}
