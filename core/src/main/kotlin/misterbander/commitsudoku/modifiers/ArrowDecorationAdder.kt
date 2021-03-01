package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2.dst2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.ArrowDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable


class ArrowDecorationAdder(grid: SudokuGrid) : GridModfier<ArrowDecoration>(grid)
{
	private val arrowDecorations: GdxArray<ArrowDecoration> = GdxArray()
	private var currentArrow: ArrowDecoration? = null
	
	override val isValidIndex
		get() = selectI in -1..9 && selectJ in -1..9
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		if (button == Input.Buttons.RIGHT)
		{
			tryDeleteArrow()
			return
		}
		
		currentArrow = ArrowDecoration(grid, selectI, selectJ)
		currentArrow!!.color = Color.ORANGE
		addModification(currentArrow!!)
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		if (currentArrow != null)
		{
			if (currentArrow!!.length >= 2)
				currentArrow!!.color = game.skin["decorationcolor2"]
			else
			{
				removeModification(currentArrow!!)
				tryDeleteArrow()
			}
		}
		currentArrow = null
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		val cellCenterX = grid.iToX(selectI.toFloat() + 0.5F)
		val cellCenterY = grid.jToY(selectJ.toFloat() + 0.5F)
		if (dst2(x, y, cellCenterX, cellCenterY) > grid.cellSize*grid.cellSize*0.16F)
			return
		
		if (currentArrow != null)
			currentArrow!!.addArrowCell(selectI, selectJ)
	}
	
	override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int)
	{
		if (count > 1)
		{
			updateSelect(x, y)
			tryDeleteArrow()
		}
	}
	
	private fun tryDeleteArrow()
	{
		arrowDecorations.forEach {
			if (it.isOver(selectI, selectJ))
			{
				removeModification(it)
				println(arrowDecorations.size
				)
				return
			}
		}
	}
	
	override fun addModification(modification: ArrowDecoration)
	{
		arrowDecorations.insert(0, modification)
		grid.decorations += modification
	}
	
	override fun removeModification(modification: ArrowDecoration)
	{
		arrowDecorations -= modification
		grid.decorations -= modification
	}
	
	override fun clear()
	{
		arrowDecorations.clear()
	}
	
	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val arrowDataObjects: Array<HashMap<String, Serializable>>? = mapper["arrowDecorations"]
		arrowDataObjects?.forEach {
			val arrowCells = it["cells"] as Array<Pair<Int, Int>>
			val arrowDecoration = ArrowDecoration(grid, arrowCells[0].first, arrowCells[0].second)
			arrowCells.forEachIndexed { index, pair ->
				if (index == 0)
					return@forEachIndexed
				arrowDecoration.addArrowCell(pair.first, pair.second)
			}
			addModification(arrowDecoration)
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["arrowDecorations"] = arrowDecorations.map { it.dataObject }.toTypedArray()
	}
}
