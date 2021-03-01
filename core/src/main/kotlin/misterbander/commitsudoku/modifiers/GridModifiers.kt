package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class GridModifiers(grid: SudokuGrid) : PersistentState
{
	val thermoAdder = ThermoAdder(grid)
	val sandwichConstraintSetter = SandwichConstraintSetter(grid)
	val textDecorationAdder = TextDecorationAdder(grid)
	val cornerTextDecorationAdder = CornerTextDecorationAdder(grid)
	val arrowDecorationAdder = ArrowDecorationAdder(grid)
	val circleDecorationAdder = CircleDecorationAdder(grid)
	val cageSetter = CageSetter(grid)
	val borderDecorationSetter = BorderDecorationSetter(grid)
	
	private val modifiers = arrayOf(
		thermoAdder,
		sandwichConstraintSetter,
		textDecorationAdder,
		cornerTextDecorationAdder,
		arrowDecorationAdder,
		circleDecorationAdder,
		cageSetter,
		borderDecorationSetter
	)
	
	fun clear()
	{
		modifiers.forEach { it.clear() }
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		modifiers.forEach { it.readState(mapper) }
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		modifiers.forEach { it.writeState(mapper) }
	}
}
