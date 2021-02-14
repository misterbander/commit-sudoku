package misterbander.commitsudoku.constraints

import misterbander.commitsudoku.scene2d.SudokuGrid

class CompoundStatement(
	private val cells: Array<Array<SudokuGrid.Cell>>,
	vararg statementStrings: String
) : Statement
{
	private val statements: Array<Statement> = Array(statementStrings.size) { i -> SingleStatement(cells, statementStrings[i]) }
	override val isGlobal by lazy {
		var isGlobalFlag = false
		statements.forEach { if (it.isGlobal) isGlobalFlag = true }
		isGlobalFlag
	}
	
	override fun check(): Boolean
	{
		var correctFlag = true
		for (s in statements)
			correctFlag = s.check() && correctFlag
		return correctFlag
	}
}
