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
		
		grid.select(selectI, selectJ, false)
		val existingCornerTextDecoration = findTextDecoration(selectI, selectJ)
		if (existingCornerTextDecoration != null)
			removeModification(existingCornerTextDecoration)
		else
		{
			grid.panel.screen.textInputWindow.show("Add Corner Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@show
				addModification(CornerTextDecoration(grid, selectI, selectJ, result))
			}
		}
	}
	
	private fun findTextDecoration(i: Int, j: Int): CornerTextDecoration?
	{
		cornerTextDecorations.forEach {
			if (it.i == i && it.j == j)
				return it
		}
		return null
	}
	
	override fun addModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations += modification
		grid.decorations += modification
		grid.cells[selectI][selectJ].hasCornerTextDecoration = true
	}
	
	override fun removeModification(modification: CornerTextDecoration)
	{
		cornerTextDecorations -= modification
		grid.decorations -= modification
		grid.cells[selectI][selectJ].hasCornerTextDecoration = false
	}
	
	override fun clear()
	{
		cornerTextDecorations.clear()
	}
	
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
	
	override fun draw(batch: Batch) {}
}