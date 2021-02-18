package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.decorations.TextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid

class SandwichConstraint(
	private val grid: SudokuGrid,
	private val index: Int,
	private val isColumn: Boolean,
	private val sandwichValue: Int
) : Constraint
{
	private val textDecoration = TextDecoration(
		grid,
		if (isColumn) index else -1,
		if (isColumn) 9 else index,
		sandwichValue.toString()
	)
	private var correctFlag = true
	
	override fun check(): Boolean
	{
		correctFlag = true
		var sandwichStart = -1
		var sandwichEnd = -1
		var sandwichStartNum = 0
		var sum = 0
		for (i in 0..8)
		{
			val theDigit = if (isColumn) grid.cells[index][i].digit else grid.cells[i][index].digit
			if (sandwichStart != -1) // Sandwich has begun
			{
				if (theDigit != 9/sandwichStartNum) // Keep on adding until the sandwich terminates
				{
					if (theDigit == 0)
						break
					sum += theDigit
				}
				else
				{
					sandwichEnd = i
					break
				}
			}
			else if (theDigit == 1 || theDigit == 9)
			{
				sandwichStart = i
				sandwichStartNum = theDigit
			}
		}
		if (sandwichStart != -1 && sandwichEnd != -1) // Complete sandwich
		{
			if (sum != sandwichValue)
				correctFlag = false
		}
		return correctFlag
	}
	
	override fun drawConstraint(batch: Batch)
	{
		textDecoration.color = if (correctFlag) null else Color.RED
		textDecoration.draw(batch)
	}
}
