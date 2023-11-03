package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.decorations.ArrowDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class ArrowDecorationAdder(grid: SudokuGrid) : AbstractLineDecorationAdder<ArrowDecoration>(grid)
{
	override fun newLine(grid: SudokuGrid, selectRow: Int, selectCol: Int): ArrowDecoration =
		ArrowDecoration(grid, selectRow, selectCol)

	override fun dataObjectKey(): String = "arrowDecorations"
}
