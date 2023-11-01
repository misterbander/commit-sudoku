package misterbander.commitsudoku.constraints

import com.badlogic.gdx.graphics.Color
import misterbander.commitsudoku.decorations.TextDecoration
import misterbander.commitsudoku.modifiers.GridModification
import misterbander.commitsudoku.scene2d.SudokuGrid
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class SandwichConstraint(
	grid: SudokuGrid,
	private val index: Int,
	private val isColumn: Boolean,
	sandwichValue: Int
) : Constraint, GridModification
{
	var sandwichValue = sandwichValue
		set(value)
		{
			field = value
			textDecoration.text = value.toString()
		}

	private val textDecoration = TextDecoration(
		grid,
		if (isColumn) index else -1,
		if (isColumn) 9 else index,
		sandwichValue.toString()
	)
	private var correctFlag = true

	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"index" to index,
			"isColumn" to isColumn,
			"sandwichValue" to sandwichValue,
		)

	override fun check(grid: SudokuGrid): Boolean
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
				if (theDigit != if (sandwichStartNum == 9) 1 else 9) // Keep on adding until the sandwich terminates
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

	override fun drawConstraint(shapeDrawer: ShapeDrawer)
	{
		textDecoration.color = if (correctFlag) null else Color.RED
		textDecoration.draw(shapeDrawer)
	}
}
