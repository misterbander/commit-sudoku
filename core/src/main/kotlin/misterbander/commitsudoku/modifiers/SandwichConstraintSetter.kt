package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxMap
import ktx.collections.set
import misterbander.commitsudoku.constraints.SandwichConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.cycle


class SandwichConstraintSetter(grid: SudokuGrid) : TextDecorationAdder(grid)
{
	private val sandwichConstraints: GdxMap<Int, SandwichConstraint> = GdxMap()
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
	}
	
	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex(highlightI, highlightJ))
		{
			highlightI = 0
			highlightJ = 9
		}
		
		val di = right - left
		val dj = up - down
		if (di != 0)
		{
			highlightI += di
			highlightI = highlightI cycle -1..8
			highlightJ = if (highlightI == -1) 8 else 9
		}
		else if (dj != 0)
		{
			highlightJ += dj
			highlightJ = highlightJ cycle 0..9
			highlightI = if (highlightJ == 9) 0 else -1
		}
	}
	
	override fun isValidIndex(i: Int, j: Int): Boolean
	{
		return i == -1 && j in 0..8 || j == 9 && i in 0..8
	}
	
	override fun enter() {}
	
	override fun typedDigit(digit: Int)
	{
		if (!isValidIndex(highlightI, highlightJ))
			return
		
		val key = if (highlightI == -1) highlightJ + 9 else highlightI
		val sandwichConstraint: SandwichConstraint? = sandwichConstraints[key]
		if (sandwichConstraint != null)
		{
			if (digit == -1) // Backspace
			{
				if (sandwichConstraint.sandwichValue < 10)
				{
					sandwichConstraints.remove(key)
					grid.constraintsChecker -= sandwichConstraint
				}
				else
					sandwichConstraint.sandwichValue/=10
			}
			else
				sandwichConstraint.sandwichValue = sandwichConstraint.sandwichValue*10 + digit
		}
		else if (digit != -1)
		{
			val newSandwichConstraint = SandwichConstraint(
				grid,
				if (highlightI == -1) highlightJ else highlightI,
				highlightJ == 9,
				digit
			)
			sandwichConstraints[key] = newSandwichConstraint
			grid.constraintsChecker += newSandwichConstraint
		}
		grid.constraintsChecker.check()
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
