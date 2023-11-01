package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class GridModifiers(
	screen: CommitSudokuScreen,
	grid: SudokuGrid,
	constraintsChecker: ConstraintsChecker
) : PersistentState
{
	val thermoAdder = ThermoAdder(grid, constraintsChecker)
	val sandwichConstraintSetter = SandwichConstraintSetter(grid, constraintsChecker)
	val textDecorationAdder = TextDecorationAdder(screen, grid)
	val cornerTextDecorationAdder = CornerTextDecorationAdder(screen, grid)
	val arrowDecorationAdder = ArrowDecorationAdder(grid)
	val littleArrowDecorationAdder = LittleArrowDecorationAdder(grid)
	val circleDecorationAdder = CircleDecorationAdder(grid)
	val lineDecorationAdder = LineDecorationAdder(grid)
	val cageSetter = CageSetter(grid, constraintsChecker)
	val borderDecorationSetter = BorderDecorationSetter(grid)

	private val modifiers = arrayOf(
		thermoAdder,
		sandwichConstraintSetter,
		textDecorationAdder,
		cornerTextDecorationAdder,
		arrowDecorationAdder,
		littleArrowDecorationAdder,
		circleDecorationAdder,
		lineDecorationAdder,
		cageSetter,
		borderDecorationSetter
	)

	fun clear() = modifiers.forEach { it.clear() }

	override fun readState(mapper: PersistentStateMapper) = modifiers.forEach { it.readState(mapper) }

	override fun writeState(mapper: PersistentStateMapper) = modifiers.forEach { it.writeState(mapper) }
}
