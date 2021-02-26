package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.CornerTextDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable


class CornerTextDecorationAdder(grid: SudokuGrid) : TextDecorationAdder(grid)
{
	private val cornerTextDecorations: GdxArray<CornerTextDecoration> = GdxArray()
	
	override val isValidIndex
		get() = highlightI in 0..8 && highlightJ in 0..8
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		grid.unselect()
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		if (!isValidIndex)
			return
		
		grid.select(highlightI, highlightJ, false)
		val existingCornerTextDecoration = findTextDecoration(highlightI, highlightJ)
		if (existingCornerTextDecoration != null)
		{
			cornerTextDecorations -= existingCornerTextDecoration
			grid.decorations -= existingCornerTextDecoration
			grid.cells[highlightI][highlightJ].hasCornerTextDecoration = false
		}
		else
		{
			grid.panel.screen.textInputWindow.show("Add Corner Text Decoration", "Enter Text:") { result ->
				if (result.isEmpty())
					return@show
				val cornerTextDecoration = CornerTextDecoration(grid, highlightI, highlightJ, result)
				cornerTextDecorations += cornerTextDecoration
				grid.decorations += cornerTextDecoration
				grid.cells[highlightI][highlightJ].hasCornerTextDecoration = true
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
	
	override fun clear()
	{
		cornerTextDecorations.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val cornerTextDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["cornertextdecorations"]
		cornerTextDecorationDataObjects?.forEach { dataObject ->
			val i = dataObject["i"] as Int
			val j = dataObject["j"] as Int
			val text = dataObject["text"] as String
			val cornerTextDecoration = CornerTextDecoration(grid, i, j, text)
			cornerTextDecorations += cornerTextDecoration
			grid.decorations += cornerTextDecoration
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["cornertextdecorations"] = cornerTextDecorations.map { it.dataObject }.toTypedArray()
	}
	
	override fun draw(batch: Batch) {}
}
