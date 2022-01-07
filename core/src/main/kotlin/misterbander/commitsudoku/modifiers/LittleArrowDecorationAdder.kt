package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.LittleArrowDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class LittleArrowDecorationAdder(grid: SudokuGrid) : GridModfier<LittleArrowDecoration>(grid)
{
	private val arrowMap: Array<Array<LittleArrowDecoration?>> = Array(11) { arrayOfNulls(11) }
	
	override val isValidIndex: Boolean
		get() = selectI in -1..9 && selectJ in -1..9
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return
		val existingLittleArrow = arrowMap[selectI + 1][selectJ + 1]
		if (existingLittleArrow != null)
		{
			if (button == Input.Buttons.RIGHT)
			{
				removeModification(existingLittleArrow)
				return
			}
			existingLittleArrow.pointingDirection -= 45F
		}
		else
			addModification(LittleArrowDecoration(grid, selectI, selectJ))
	}
	
	override fun longPress(x: Float, y: Float): Boolean
	{
		updateSelect(x, y)
		if (!isValidIndex)
			return false
		val existingLittleArrow = arrowMap[selectI + 1][selectJ + 1]
		if (existingLittleArrow != null)
		{
			removeModification(existingLittleArrow)
			return true
		}
		return false
	}
	
	override fun addModification(modification: LittleArrowDecoration)
	{
		arrowMap[modification.i + 1][modification.j + 1] = modification
		grid.decorations += modification
	}
	
	override fun removeModification(modification: LittleArrowDecoration)
	{
		arrowMap[modification.i + 1][modification.j + 1] = null
		grid.decorations -= modification
	}
	
	override fun clear() = arrowMap.forEach { it.fill(null) }
	
	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val littleArrowDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["littleArrowDecorations"]
		littleArrowDecorationDataObjects?.forEach { dataObject ->
			val i = dataObject["i"] as Int
			val j = dataObject["j"] as Int
			val pointingDirection = dataObject["pointingDirection"] as Float
			addModification(LittleArrowDecoration(grid, i, j, pointingDirection))
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		val littleArrowDecorations: GdxSet<LittleArrowDecoration> = GdxSet()
		for (arrows in arrowMap)
		{
			for (arrow in arrows)
			{
				if (arrow != null)
					littleArrowDecorations += arrow
			}
		}
		mapper["littleArrowDecorations"] = littleArrowDecorations.map { it.dataObject }.toTypedArray()
	}
}
