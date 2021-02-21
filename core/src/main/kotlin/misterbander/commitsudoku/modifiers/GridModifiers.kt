package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class GridModifiers(grid: SudokuGrid) : PersistentState
{
	val thermoAdder = ThermoAdder(grid)
	val sandwichConstraintSetter = SandwichConstraintSetter(grid)
	val textDecorationAdder = TextDecorationAdder(grid)
	
	override fun readState(mapper: PersistentStateMapper)
	{
		thermoAdder.readState(mapper)
		sandwichConstraintSetter.readState(mapper)
		textDecorationAdder.readState(mapper)
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		thermoAdder.writeState(mapper)
		sandwichConstraintSetter.writeState(mapper)
		textDecorationAdder.writeState(mapper)
	}
}
