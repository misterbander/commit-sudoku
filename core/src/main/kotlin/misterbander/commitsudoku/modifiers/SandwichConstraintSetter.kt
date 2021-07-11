package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxMap
import ktx.collections.set
import ktx.style.*
import misterbander.commitsudoku.constraints.SandwichConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.cycle
import java.io.Serializable

class SandwichConstraintSetter(grid: SudokuGrid) : GridModfier<SandwichConstraint>(grid)
{
	private val sandwichConstraints: GdxMap<Int, SandwichConstraint> = GdxMap()
	
	private val key
		get() = if (selectI == -1) selectJ + 9 else selectI
	override val isValidIndex
		get() = selectI == -1 && selectJ in 0..8 || selectJ == 9 && selectI in 0..8
	
	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
	}
	
	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex)
		{
			selectI = 0
			selectJ = 9
		}
		
		val di = right - left
		val dj = up - down
		if (di != 0)
		{
			selectI += di
			selectI = selectI cycle -1..8
			selectJ = if (selectI == -1) 8 else 9
		}
		else if (dj != 0)
		{
			selectJ += dj
			selectJ = selectJ cycle 0..9
			selectI = if (selectJ == 9) 0 else -1
		}
	}
	
	override fun enter() {}
	
	override fun typedDigit(digit: Int)
	{
		if (!isValidIndex)
			return
		
		val sandwichConstraint: SandwichConstraint? = sandwichConstraints[key]
		if (sandwichConstraint != null)
		{
			if (digit == -1) // Backspace
			{
				if (sandwichConstraint.sandwichValue < 10)
					removeModification(sandwichConstraint)
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
				if (selectI == -1) selectJ else selectI,
				selectJ == 9,
				digit
			)
			addModification(newSandwichConstraint)
		}
		grid.constraintsChecker.check()
	}
	
	override fun addModification(modification: SandwichConstraint)
	{
		sandwichConstraints[key] = modification
		grid.constraintsChecker += modification
	}
	
	override fun removeModification(modification: SandwichConstraint)
	{
		sandwichConstraints.remove(key)
		grid.constraintsChecker -= modification
	}
	
	override fun clear()
	{
		sandwichConstraints.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val sandwichConstraintDataObjects: Array<HashMap<String, Serializable>>? = mapper["sandwichConstraints"]
		sandwichConstraintDataObjects?.forEach { dataObject ->
			val index = dataObject["index"] as Int
			val isColumn = dataObject["isColumn"] as Boolean
			val sandwichValue = dataObject["sandwichValue"] as Int
			val sandwichConstraint = SandwichConstraint(grid, index, isColumn, sandwichValue)
			val key = if (isColumn) index else index + 9
			sandwichConstraints[key] = sandwichConstraint
			grid.constraintsChecker += sandwichConstraint
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["sandwichConstraints"] = sandwichConstraints.values().map { it.dataObject }.toTypedArray()
	}
	
	override fun draw(batch: Batch)
	{
		for (i in 0..8)
			drawClickableArea(i, 9)
		for (j in 0..8)
			drawClickableArea(-1, j)
	}
	
	private fun drawClickableArea(i: Int, j: Int)
	{
		val x = grid.iToX(i.toFloat())
		val y = grid.jToY(j.toFloat())
		val isSelected = i == selectI && j == selectJ
		game.shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) game.skin["selectedcolor"] else gray)
	}
}
