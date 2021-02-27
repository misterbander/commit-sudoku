package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.CageDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable


class CageSetter(grid: SudokuGrid) : GridModfier<CageDecoration>(grid)
{
	private val cageMap: Array<Array<CageDecoration?>> = Array(9) { arrayOfNulls(9) }
	private var currentCage: CageDecoration? = null
		set(value)
		{
			if (value == null)
				field?.color = null
			field = value
		}
	private var justRemovedCage = false
	
	override val isValidIndex
		get() = selectI in 0..8 && selectJ in 0..8
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		currentCage = null
		if (!isValidIndex)
			return
		val cage = cageMap[selectI][selectJ]
		if (cage != null)
		{
			removeModification(cage)
			justRemovedCage = true
		}
		else if (cageMap[selectI][selectJ] == null && !justRemovedCage)
		{
			if (currentCage == null)
			{
				currentCage = CageDecoration(grid, selectI, selectJ)
				currentCage!!.color = Color.ORANGE
				addModification(currentCage!!)
			}
			else
				currentCage!!.addCell(selectI, selectJ)
			cageMap[selectI][selectJ] = currentCage
		}
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex || justRemovedCage || currentCage == null || cageMap[selectI][selectJ] != null)
			return
		currentCage!!.addCell(selectI, selectJ)
		cageMap[selectI][selectJ] = currentCage
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		justRemovedCage = false
	}
	
	fun unselect()
	{
		currentCage = null
	}
	
	override fun addModification(modification: CageDecoration)
	{
		grid.decorations += modification
	}
	
	override fun removeModification(modification: CageDecoration)
	{
		for (i in modification.mask.indices)
		{
			for (j in modification.mask[i].indices)
			{
				if (modification.mask[i][j])
					cageMap[i][j] = null
			}
		}
		grid.decorations -= modification
	}
	
	override fun clear()
	{
		for (i in cageMap.indices)
		{
			for (j in cageMap[i].indices)
				cageMap[i][j] = null
		}
	}
	
	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val cageDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["cageDecorations"]
		cageDecorationDataObjects?.forEach { dataObject ->
			var cageDecoration: CageDecoration? = null
			val cageMask = dataObject["cageMask"] as Array<BooleanArray>
			for (i in cageMask.indices)
			{
				for (j in cageMask[i].indices)
				{
					if (cageMask[i][j])
					{
						if (cageDecoration == null)
						{
							cageDecoration = CageDecoration(grid, i, j)
							addModification(cageDecoration)
						}
						else
							cageDecoration.addCell(i, j)
						cageMap[i][j] = cageDecoration
					}
				}
			}
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		val cageDecorations: GdxSet<CageDecoration> = GdxSet()
		cageMap.forEach {
			it.forEach { cage ->
				if (cage != null)
					cageDecorations += cage
			}
		}
		mapper["cageDecorations"] = cageDecorations.map { it.dataObject }.toTypedArray()
	}
}
