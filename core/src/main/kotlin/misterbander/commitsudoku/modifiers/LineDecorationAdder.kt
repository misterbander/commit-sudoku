package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class LineDecorationAdder(grid: SudokuGrid) : AbstractLineDecorationAdder<LineDecoration>(grid)
{
	override fun newLine(grid: SudokuGrid, selectI: Int, selectJ: Int): LineDecoration =
		LineDecoration(grid, selectI, selectJ)

	override fun dataObjectKey(): String = "lineDecorations"
}
