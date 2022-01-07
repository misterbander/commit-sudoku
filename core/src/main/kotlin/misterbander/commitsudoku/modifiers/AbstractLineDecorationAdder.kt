package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.LineDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray


abstract class AbstractLineDecorationAdder<T : LineDecoration>(grid: SudokuGrid) : GridModfier<T>(grid)
{
	private val lineDecorations: GdxArray<T> = GdxArray()
	private var currentLine: T? = null
	
	override val isValidIndex
		get() = selectI in -1..9 && selectJ in -1..9
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		if (button == Input.Buttons.RIGHT)
		{
			tryDelete()
			return
		}
		
		currentLine = newLine(grid, selectI, selectJ)
		currentLine!!.isHighlighted = true
		addModification(currentLine!!)
	}
	
	abstract fun newLine(grid: SudokuGrid, selectI: Int, selectJ: Int) : T
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		if (currentLine != null)
		{
			if (currentLine!!.length >= 2)
				currentLine!!.isHighlighted = false
			else
			{
				removeModification(currentLine!!)
				tryDelete()
			}
		}
		currentLine = null
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		val cellCenterX = grid.iToX(selectI.toFloat() + 0.5F)
		val cellCenterY = grid.jToY(selectJ.toFloat() + 0.5F)
		if (Vector2.dst2(x, y, cellCenterX, cellCenterY) > grid.cellSize*grid.cellSize*0.16F)
			return
		
		if (currentLine != null)
			currentLine!!.addLineCell(selectI, selectJ)
	}
	
	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		if (count > 1)
		{
			updateSelect(x, y)
			tryDelete()
		}
	}
	
	private fun tryDelete()
	{
		for (lineDecoration: T in lineDecorations)
		{
			if (lineDecoration.isOver(selectI, selectJ))
			{
				removeModification(lineDecoration)
				return
			}
		}
	}
	
	override fun addModification(modification: T)
	{
		lineDecorations.insert(0, modification)
		grid.decorations += modification
	}
	
	override fun removeModification(modification: T)
	{
		lineDecorations -= modification
		grid.decorations -= modification
	}
	
	override fun clear() = lineDecorations.clear()
	
	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val lineDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper[dataObjectKey()]
		lineDecorationDataObjects?.forEach {
			val lineCells = it["cells"] as Array<Pair<Int, Int>>
			val lineDecoration = newLine(grid, lineCells[0].first, lineCells[0].second)
			lineCells.forEachIndexed { index, pair ->
				if (index == 0)
					return@forEachIndexed
				lineDecoration.addLineCell(pair.first, pair.second)
			}
			addModification(lineDecoration)
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper[dataObjectKey()] = lineDecorations.map { it.dataObject }.toTypedArray()
	}
	
	abstract fun dataObjectKey(): String
}
