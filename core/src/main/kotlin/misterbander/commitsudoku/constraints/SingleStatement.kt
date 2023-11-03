package misterbander.commitsudoku.constraints

import com.fathzer.soft.javaluator.DoubleEvaluator
import ktx.collections.*
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.GdxStringBuilder

class SingleStatement(val statementStr: String) : Statement
{
	private val operatorPredicateMap: GdxMap<Regex, (Double, Double) -> Boolean> = gdxMapOf(
		"(^|[^!><])=".toRegex() to { x, y -> x.compareTo(y) == 0 },
		"!=".toRegex() to { x, y -> x.compareTo(y) != 0 },
		">([^=]|$)".toRegex() to { x, y -> x > y },
		">=".toRegex() to { x, y -> x >= y },
		"<([^=]|$)".toRegex() to { x, y -> x < y },
		"<=".toRegex() to { x, y -> x <= y }
	)
	private val evaluator = DoubleEvaluator()

	/** Whether the statement applies globally i.e. to all cells  */
	override val isGlobal = statementStr.startsWith("=")
		|| statementStr.startsWith("!=")
		|| statementStr.startsWith(">")
		|| statementStr.startsWith("<")
		|| '#' in statementStr
		|| '~' in statementStr
	private val involvingCells = GdxArray<SudokuGrid.Cell>()

	override fun check(grid: SudokuGrid): Boolean
	{
		val cells = grid.cells
		var correctFlag = true
		if (isGlobal)
		{
			for (row in 0..8)
			{
				for (col in 0..8)
				{
					if (!grid.evaluate(row, col))
					{
						cells[row][col].isCorrect = false
						involvingCells.forEach { it.isCorrect = false }
						correctFlag = false
					}
				}
			}
		}
		else if (!grid.evaluate(0, 0))
		{
			involvingCells.forEach { it.isCorrect = false }
			correctFlag = false
		}
		return correctFlag
	}

	/**
	 * Evaluate this statement at cell (row, col).
	 * @param row row number, top-most row is 0
	 * @param col column number, left-most column is 0
	 * @return True if statement evaluates to true, false otherwise. If statement contains variables that are undefined
	 * then true is returned.
	 */
	private fun SudokuGrid.evaluate(row: Int, col: Int): Boolean
	{
		val cell = cells[row][col]
		if (isGlobal && cell.digit == 0)
			return true

		// Plug variables, or return true if statement contains undefined variables
		val statementStr2 = plugVariables(row, col) ?: return true
		var predicate: (Double, Double) -> Boolean = { _, _ -> true }
		var operatorCount = 0

		for (operator: Regex in operatorPredicateMap.keys())
		{
			if (operator in statementStr2)
			{
				operatorCount++
				predicate = operatorPredicateMap[operator]
			}
		}
		require(operatorCount == 1) {
			"Statement must contain only 1 comparison operator but found $operatorCount in statement \"$statementStr\""
		}

		val expressions = statementStr2.split("(!=|>=|<=|=|>|<)".toRegex()).toTypedArray()
		require(expressions.size == 2) {
			"Must compare 2 expressions but found ${expressions.size} in statement: \"$statementStr\""
		}

		val lhs = if (expressions[0].isEmpty()) // LHS omitted, implicitly assumed to be #
		{
			if (cell.digit == 0)
				return true
			cell.digit.toDouble()
		}
		else
			evaluator.evaluate(expressions[0])
		val rhs = evaluator.evaluate(expressions[1])
		return predicate.invoke(lhs, rhs)
	}

	/**
	 * Substitutes all variables such as `[rxcy]`, `[~x~y]`, `#` etc. with values.
	 * @param row row number, top-most row is 0
	 * @param col column number, left-most column is 0
	 * @return Statement with variables plugged in if parsed successfully, null if one of the variables is empty or out of bounds.
	 * @throws IllegalArgumentException if statement contains syntax errors, i.e. mismatched brackets
	 */
	private fun SudokuGrid.plugVariables(row: Int, col: Int): String?
	{
		involvingCells.clear()

		// Remove all spaces and replace all #s with the cell digit
		val builder = GdxStringBuilder(statementStr.replace("#".toRegex(), cells[row][col].toString()))

		// Plug in variables
		while (true)
		{
			val start = builder.indexOf("[")
			val end = builder.indexOf("]")
			if (start == -1 && end == -1)
				break
			require(start != -1 && end != -1) {
				"Malformed variable string! Incorrect enclosing of square brackets in statement: \"$statementStr\""
			}

			val tildeIndex = builder.indexOf("~")
			if (end - start == 5 && builder[start + 1] == 'r' && builder[start + 3] == 'c') // [rxcy] notation, absolute cell reference
			{
				val r = builder[start + 2].toString().toInt() - 1
				val c = builder[start + 4].toString().toInt() - 1
				if (!isValidCell(r, c))
					return null
				val cell = cells[r][c]
				if (cell.digit == 0)
					return null
				involvingCells += cell
				builder.replace(start, end + 1, cell.digit.toString())
			}
			else if (tildeIndex != -1) // [~x~y] notation, relative cell reference
			{
				val secondTildeIndex = builder.indexOf("~", tildeIndex + 1)
				require(secondTildeIndex != -1) {
					"Malformed variable string in statement: \" $statementStr\"! Expecting [~x~y] notation."
				}
				val x = builder.substring(tildeIndex + 1, secondTildeIndex).toInt()
				val y = -builder.substring(secondTildeIndex + 1, end).toInt()
				if (!isValidCell(row + x, col + y))
					return null
				val cell = cells[row][col].offset(x, y)
				if (cell.digit == 0)
					return null
				involvingCells += cell
				builder.replace(start, end + 1, cell.digit.toString())
			}
			else throw IllegalArgumentException(
				"Malformed variable string in statement: \"$statementStr\"! Expecting"
					+ " [rxcy] or [~x~y] notation."
			)
		}
		return builder.toString()
	}

	private fun isValidCell(row: Int, col: Int): Boolean = row in 0..8 && col in 0..8
}
