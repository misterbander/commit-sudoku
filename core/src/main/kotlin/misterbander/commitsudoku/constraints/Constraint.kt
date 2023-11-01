package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid
import space.earlygrey.shapedrawer.ShapeDrawer

interface Constraint
{
	/**
	 * @return True if the Sudoku satisfies the constraint, false otherwise
	 */
	fun check(grid: SudokuGrid): Boolean

	fun drawConstraint(shapeDrawer: ShapeDrawer) = Unit
}
