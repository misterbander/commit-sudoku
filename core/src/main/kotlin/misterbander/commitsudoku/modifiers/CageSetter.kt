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


class CageSetter(grid: SudokuGrid) : GridModfier(grid)
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
	
	private var highlightI = 0
	private var highlightJ = 0
	private val isValidIndex
		get() = highlightI in 0..8 && highlightJ in 0..8
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		currentCage = null
		if (!isValidIndex)
			return
		val cage = cageMap[highlightI][highlightJ]
		if (cage != null)
		{
			for (i in cage.mask.indices)
			{
				for (j in cage.mask[i].indices)
				{
					if (cage.mask[i][j])
						cageMap[i][j] = null
				}
			}
			grid.decorations -= cage
			justRemovedCage = true
		}
		else if (cageMap[highlightI][highlightJ] == null && !justRemovedCage)
		{
			if (currentCage == null)
			{
				currentCage = CageDecoration(grid, highlightI, highlightJ)
				currentCage!!.color = Color.ORANGE
				grid.decorations += currentCage!!
			}
			else
				currentCage!!.addCell(highlightI, highlightJ)
			cageMap[highlightI][highlightJ] = currentCage
		}
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		if (!isValidIndex || justRemovedCage || currentCage == null || cageMap[highlightI][highlightJ] != null)
			return
		currentCage!!.addCell(highlightI, highlightJ)
		cageMap[highlightI][highlightJ] = currentCage
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		justRemovedCage = false
	}
	
	fun unselect()
	{
		currentCage = null
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
							grid.decorations += cageDecoration
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
