package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class CornerTextDecorationAdder(grid: SudokuGrid) : GridModfier<CornerTextDecoration>(grid)
{
	private val cornerTextDecorations: GdxSet<CornerTextDecoration> = GdxSet()
	
	override val isValidIndex
		get() = selectI in 0..8 && selectJ in 0..8
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		grid.unselect()
		updateSelect(x, y)
		if (!isValidIndex)
			return
		
		grid.select(selectI, selectJ)
		val existingCornerTextDecoration = findTextDecoration(selectI, selectJ)
		if (existingCornerTextDecoration != null)
			removeModification(existingCornerTextDecoration)
		else
		{
			grid.panel.screen.textInputDialog.show("Add Corner Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@show
				addModification(CornerTextDecoration(grid, selectI, selectJ, result))
			}
		}
	}
	
	private fun findTextDecoration(i: Int, j: Int): CornerTextDecoration?
	{
		for (cornerTextDecoration: CornerTextDecoration in cornerTextDecorations)
		{
			if (cornerTextDecoration.i == i && cornerTextDecoration.j == j)
				return cornerTextDecoration
		}
		return null
	}
	
	override fun addModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations += modification
		grid.decorations += modification
		grid.cells[modification.i][modification.j].cornerTextDecorationCount++
	}
	
	override fun removeModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations -= modification
		grid.decorations -= modification
		grid.cells[modification.i][modification.j].cornerTextDecorationCount--
	}
	
	override fun clear() = cornerTextDecorations.clear()
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val cornerTextDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["cornerTextDecorations"]
		cornerTextDecorationDataObjects?.forEach { dataObject ->
			val i = dataObject["i"] as Int
			val j = dataObject["j"] as Int
			val text = dataObject["text"] as String
			addModification(CornerTextDecoration(grid, i, j, text))
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["cornerTextDecorations"] = cornerTextDecorations.map { it.dataObject }.toTypedArray()
	}
	
	override fun draw(batch: Batch) = Unit
}
