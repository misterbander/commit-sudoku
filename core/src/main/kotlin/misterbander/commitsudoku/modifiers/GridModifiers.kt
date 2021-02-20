package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.scene2d.SudokuGrid

class GridModifiers(grid: SudokuGrid)
{
	val thermoAdder = ThermoAdder(grid)
	val sandwichConstraintSetter = SandwichConstraintSetter(grid)
	val textDecorationAdder = TextDecorationAdder(grid)
}
