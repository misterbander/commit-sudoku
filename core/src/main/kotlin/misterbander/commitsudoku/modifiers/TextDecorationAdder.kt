package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.TextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.cycle
import java.io.Serializable


class TextDecorationAdder(grid: SudokuGrid): GridModfier<TextDecoration>(grid)
{
	private val textDecorations: GdxSet<TextDecoration> = GdxSet()
	
	override val isValidIndex
		get() = selectI in -1..9 && selectJ in -1..9 && (selectI == -1 || selectI == 9 || selectJ == -1 || selectJ == 9)
	
	private val gray = Color(0.5F, 0.5F, 0.5F, 0.4F)
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		enter()
	}
	
	override fun navigate(up: Int, down: Int, left: Int, right: Int)
	{
		if (!isValidIndex)
		{
			selectI = -1
			selectJ = 9
		}
		
		val di = right - left
		val dj = up - down
		if (di != 0)
		{
			if (selectJ in 0..8)
				selectI = if (selectI == 9) -1 else 9
			else
				selectI += di
			selectI = selectI cycle -1..9
		}
		else if (dj != 0)
		{
			if (selectI in 0..8)
				selectJ = if (selectJ == 9) -1 else 9
			else
				selectJ += dj
			selectJ = selectJ cycle -1..9
		}
	}
	
	override fun enter()
	{
		if (!isValidIndex)
			return
		val existingTextDecoration = findTextDecoration()
		if (existingTextDecoration != null)
			removeModification(existingTextDecoration)
		else
		{
			grid.panel.screen.textInputWindow.show("Add Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@show
				addModification(TextDecoration(grid, selectI, selectJ, result))
			}
		}
	}
	
	private fun findTextDecoration(): TextDecoration?
	{
		textDecorations.forEach {
			if (it.i == selectI && it.j == selectJ)
				return it
		}
		return null
	}
	
	override fun addModification(modification: TextDecoration)
	{
		textDecorations += modification
		grid.decorations += modification
	}
	
	override fun removeModification(modification: TextDecoration)
	{
		textDecorations -= modification
		grid.decorations -= modification
	}
	
	override fun clear()
	{
		textDecorations.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val textDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["textDecorations"]
		textDecorationDataObjects?.forEach { dataObject ->
			val i = dataObject["i"] as Int
			val j = dataObject["j"] as Int
			val text = dataObject["text"] as String
			addModification(TextDecoration(grid, i, j, text))
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["textDecorations"] = textDecorations.map { it.dataObject }.toTypedArray()
	}
	
	override fun draw(batch: Batch)
	{
		for (i in -1..9)
		{
			drawClickableArea(i, -1)
			drawClickableArea(i, 9)
		}
		for (j in 0..8)
		{
			drawClickableArea(-1, j)
			drawClickableArea(9, j)
		}
	}
	
	private fun drawClickableArea(i: Int, j: Int)
	{
		val x = grid.iToX(i.toFloat())
		val y = grid.jToY(j.toFloat())
		val isSelected = i == selectI && j == selectJ
		game.shapeDrawer.filledRectangle(x + 8, y + 8, 48F, 48F, if (isSelected) game.skin["selectedcolor"] else gray)
	}
}
