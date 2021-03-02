package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.BorderDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable

class BorderDecorationSetter(grid: SudokuGrid) : GridModfier<BorderDecoration>(grid)
{
	private val borderDecorations: GdxArray<BorderDecoration> = GdxArray()
	
	private var selectIF = 0F
	private var selectJF = 0F
	override val isValidIndex: Boolean
		get() = selectIF in 0F..9F && selectJF in 0F..9F
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x + grid.cellSize/4, y + grid.cellSize/4)
		if (!isValidIndex)
			return
		val existingBorderDecoration = findBorderDecoration()
		if (existingBorderDecoration != null)
		{
			val nextType = existingBorderDecoration.type.nextType()
			if (nextType == null)
				removeModification(existingBorderDecoration)
			else
				existingBorderDecoration.type = nextType
		}
		else
			addModification(BorderDecoration(grid, selectIF, selectJF))
	}
	
	override fun updateSelect(x: Float, y: Float)
	{
		selectIF = grid.xToI(x, 0.5F)
		selectJF = grid.yToJ(y, 0.5F)
	}
	
	private fun findBorderDecoration(): BorderDecoration?
	{
		borderDecorations.forEach {
			if (MathUtils.isEqual(it.i, selectIF) && MathUtils.isEqual(it.j, selectJF))
				return it
		}
		return null
	}
	
	override fun addModification(modification: BorderDecoration)
	{
		borderDecorations += modification
		grid.foreDecorations += modification
	}
	
	override fun removeModification(modification: BorderDecoration)
	{
		borderDecorations -= modification
		grid.foreDecorations -= modification
	}
	
	override fun clear()
	{
		borderDecorations.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val borderDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["borderDecorations"]
		borderDecorationDataObjects?.forEach { dataObject ->
			val i = dataObject["i"] as Float
			val j = dataObject["j"] as Float
			val type = dataObject["type"] as BorderDecoration.Type
			addModification(BorderDecoration(grid, i, j, type))
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["borderDecorations"] = borderDecorations.map { it.dataObject }.toTypedArray()
	}
}
