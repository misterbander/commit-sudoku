package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxMap
import ktx.collections.set
import misterbander.commitsudoku.constraints.SandwichConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid


class SandwichConstraintSetter(grid: SudokuGrid) : TextDecorationAdder(grid)
{
	private val sandwichConstraints: GdxMap<Int, SandwichConstraint> = GdxMap()
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		
		if (!isValidIndex(highlightI, highlightJ))
			return false
		
		val key = if (highlightI == -1) highlightJ + 9 else highlightI
		val existingSandwichConstraint: SandwichConstraint? = sandwichConstraints[key]
		if (existingSandwichConstraint != null)
		{
			sandwichConstraints.remove(key)
			grid.constraintsChecker -= existingSandwichConstraint
		}
		else
		{
			grid.panel.screen.valueInputWindow.show("Add Sandwich Constraint", "Enter Sandwich Value:") { result ->
				if (result.isEmpty())
					return@show
				val sandwichConstraint = SandwichConstraint(
					grid,
					if (highlightI == -1) highlightJ else highlightI,
					highlightJ == 9,
					result.toInt()
				)
				sandwichConstraints[key] = sandwichConstraint
				grid.constraintsChecker += sandwichConstraint
			}
		}
		grid.constraintsChecker.check()
		return false
	}
	
	override fun isValidIndex(i: Int, j: Int): Boolean
	{
		return i == -1 && j in 0..8 || j == 9 && i in 0..8
	}
	
	override fun clear()
	{
		sandwichConstraints.clear()
	}
	
	override fun draw(batch: Batch)
	{
		for (i in 0..8)
			drawClickableArea(i, 9)
		for (j in 0..8)
			drawClickableArea(-1, j)
	}
}
