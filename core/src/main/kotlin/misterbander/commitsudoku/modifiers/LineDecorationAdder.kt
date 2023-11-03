package misterbander.commitsudoku.modifiers

import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class LineDecorationAdder(grid: SudokuGrid) : AbstractLineDecorationAdder<LineDecoration>(grid)
{
	override fun newLine(grid: SudokuGrid, selectRow: Int, selectCol: Int): LineDecoration =
		LineDecoration(grid, selectRow, selectCol)

	override fun dataObjectKey(): String = "lineDecorations"
}
