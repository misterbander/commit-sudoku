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
		if (isColumn) -1 else index,
		if (isColumn) index else -1,
		sandwichValue.toString()
	)
	private var correctFlag = true

	val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"index" to index,
			"isColumn" to isColumn,
			"sandwichValue" to sandwichValue,
		)

	override fun check(cells: Array<Array<SudokuGrid.Cell>>): Boolean
	{
		correctFlag = true
		var sandwichStart = -1
		var sandwichEnd = -1
		var sandwichStartNum = 0
		var sum = 0
		for (i in 0..8)
		{
			val digit = if (isColumn) cells[i][index].digit else cells[index][i].digit
			if (sandwichStart != -1) // Sandwich has begun
			{
				if (digit != if (sandwichStartNum == 9) 1 else 9) // Keep on adding until the sandwich terminates
				{
					if (digit == 0)
						break
					sum += digit
				}
				else
				{
					sandwichEnd = i
					break
				}
			}
			else if (digit == 1 || digit == 9)
			{
				sandwichStart = i
				sandwichStartNum = digit
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
