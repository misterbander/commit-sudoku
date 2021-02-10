package misterbander.commitsudoku.constraints

interface Constraint
{
	/**
	 * @return True if the Sudoku satisfies the constraint, false otherwise
	 */
	fun check(): Boolean
}
