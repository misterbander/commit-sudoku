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
	val circleDecorationAdder = CircleDecorationAdder(grid)
	val cageSetter = CageSetter(grid)
	
	fun clear()
	{
		thermoAdder.clear()
		sandwichConstraintSetter.clear()
		textDecorationAdder.clear()
		cornerTextDecorationAdder.clear()
		circleDecorationAdder.clear()
		cageSetter.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		thermoAdder.readState(mapper)
		sandwichConstraintSetter.readState(mapper)
		textDecorationAdder.readState(mapper)
		cornerTextDecorationAdder.readState(mapper)
		circleDecorationAdder.readState(mapper)
		cageSetter.readState(mapper)
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		thermoAdder.writeState(mapper)
		sandwichConstraintSetter.writeState(mapper)
		textDecorationAdder.writeState(mapper)
		cornerTextDecorationAdder.writeState(mapper)
		circleDecorationAdder.writeState(mapper)
		cageSetter.writeState(mapper)
	}
}
