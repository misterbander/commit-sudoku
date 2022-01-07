package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.g2d.Batch

interface Constraint
{
	/**
	 * @return True if the Sudoku satisfies the constraint, false otherwise
	 */
	fun check(): Boolean
	
	fun drawConstraint(batch: Batch) = Unit
}
