package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.scene2d.SudokuGrid

class GridModifiers(grid: SudokuGrid)
{
	val textDecorationAdder = TextDecorationAdder(grid)
}