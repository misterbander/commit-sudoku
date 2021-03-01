package misterbander.commitsudoku.constraints

import com.fathzer.soft.javaluator.DoubleEvaluator
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.gdxMapOf
import ktx.collections.plusAssign
import misterbander.commitsudoku.scene2d.SudokuGrid
import com.badlogic.gdx.utils.StringBuilder as GdxStringBuilder


class SingleStatement(private val cells: Array<Array<SudokuGrid.Cell>>, val statementStr: String) : Statement
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
	private val involvingCells: GdxArray<SudokuGrid.Cell> = GdxArray()
	
	override fun check(): Boolean
	{
		var correctFlag = true
		if (isGlobal)
		{
			for (i in 0..8)
			{
				for (j in 0..8)
				{
					if (!evaluate(i, j))
					{
						cells[i][j].isCorrect = false
						involvingCells.forEach { it.isCorrect = false }
						correctFlag = false
					}
				}
			}
		}
		else if (!evaluate(0, 0))
		{
			involvingCells.forEach { it.isCorrect = false }
			correctFlag = false
		}
		return correctFlag
	}
	
	/**
	 * Evaluate this statement at cell (i, j).
	 * @param i column number
	 * @param j row number
	 * @return True if statement evaluates to true, false otherwise. If statement contains variables that are undefined
	 * then true is returned.
	 */
	private fun evaluate(i: Int, j: Int): Boolean
	{
		val cell = cells[i][j]
		if (isGlobal && cell.digit == 0)
			return true
		
		// Plug variables, or return true if statement contains undefined variables
		val statementStr2 = plugVariables(i, j) ?: return true
		var predicate: (Double, Double) -> Boolean = { _, _ -> true }
		var operatorCount = 0
		
		operatorPredicateMap.keys().forEach { operator ->
			if (operator in statementStr2)
			{
				operatorCount++
				predicate = operatorPredicateMap[operator]
			}
		}
		require(operatorCount == 1) {
			"Statement must contain only 1 comparison operator but found $operatorCount in statement \"$statementStr\""
		}
		
		val expressions: Array<String> = statementStr2.split("(!=|>=|<=|=|>|<)".toRegex()).toTypedArray()
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
	 * Substitutes all variables such as \[rxcy\], \[~x~y\], `#` etc. with values.
	 * @param i column number
	 * @param j row number
	 * @return Statement with variables plugged in if parsed successfully, null if one of the variables is empty or out of bounds.
	 * @throws IllegalArgumentException if statement contains syntax errors, i.e. mismatched brackets
	 */
	private fun plugVariables(i: Int, j: Int): String?
	{
		involvingCells.clear()
		
		// Remove all spaces and replace all #s with the cell digit
		val builder = GdxStringBuilder(statementStr.replace("#".toRegex(), cells[i][j].toString()))
		
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
				val cell = cells[c][r]
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
				val y = builder.substring(secondTildeIndex + 1, end).toInt()
				if (!isValidCell(i + x, j + y))
					return null
				val cell = cells[i][j].offset(x, y)
				if (cell.digit == 0)
					return null
				involvingCells += cell
				builder.replace(start, end + 1, cell.digit.toString())
			}
			else throw IllegalArgumentException("Malformed variable string in statement: \"$statementStr\"! Expecting"
				+ " [rxcy] or [~x~y] notation.")
		}
		return builder.toString()
	}
	
	private fun isValidCell(i: Int, j: Int): Boolean
	{
		return i in 0..8 && j in 0..8
	}
}