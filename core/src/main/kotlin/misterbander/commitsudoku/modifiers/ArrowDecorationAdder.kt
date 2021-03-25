package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.decorations.ArrowDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid


class ArrowDecorationAdder(grid: SudokuGrid) : AbstractLineDecorationAdder<ArrowDecoration>(grid)
{
	override fun newLine(grid: SudokuGrid, selectI: Int, selectJ: Int): ArrowDecoration = ArrowDecoration(grid, selectI, selectJ)
	
	override fun dataObjectKey(): String = "arrowDecorations"
}
