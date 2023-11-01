package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxSet
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.constraints.KillerConstraint
import misterbander.commitsudoku.decorations.CageDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.Observable
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.collections.map
import kotlin.collections.toTypedArray

class CageSetter(
	grid: SudokuGrid,
	private val constraintsChecker: ConstraintsChecker
) : GridModifier<CageDecoration>(grid)
{
	val isKillerModeObservable = Observable(true)
	var isKillerMode by isKillerModeObservable
	private val cageMap: Array<Array<CageDecoration?>> = Array(9) { arrayOfNulls(9) }
	private var currentCage: CageDecoration? = null
		set(value)
		{
			if (value == null)
				field?.color = null
			field = value
		}
	private var justRemovedCage = false

	private val isValidIndex
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
		if (isKillerMode && currentCage != null)
		{
			val killerConstraint = KillerConstraint(grid, currentCage!!, constraintsChecker)
			currentCage!!.killerConstraint = killerConstraint
			constraintsChecker += killerConstraint
			grid.decorations += killerConstraint.cornerTextDecoration
			grid.cells[currentCage!!.topLeftI][currentCage!!.topLeftJ].cornerTextDecorationCount++
		}
	}

	fun unselect()
	{
		currentCage = null
	}

	override fun typedDigit(digit: Int)
	{
		if (isKillerMode && currentCage?.killerConstraint != null)
		{
			val killerConstraint = currentCage!!.killerConstraint!!
			if (digit == -1) // Backspace
				killerConstraint.killerSum /= 10
			else
				killerConstraint.killerSum = killerConstraint.killerSum*10 + digit
		}
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
		if (modification.killerConstraint != null)
		{
			constraintsChecker -= modification.killerConstraint!!
			grid.decorations -= modification.killerConstraint!!.cornerTextDecoration
			grid.cells[modification.topLeftI][modification.topLeftJ].cornerTextDecorationCount--
		}
	}

	override fun clear() = cageMap.forEach { it.fill(null) }

	@Suppress("UNCHECKED_CAST")
	override fun readState(mapper: PersistentStateMapper)
	{
		val cageDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["cageDecorations"]
		cageDecorationDataObjects?.forEach { dataObject ->
			var cageDecoration: CageDecoration? = null
			val cageMask = dataObject["cageMask"] as Array<BooleanArray>
			val killerSum = dataObject["killerSum"] as Int
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
			if (killerSum != -1)
			{
				val killerConstraint = KillerConstraint(grid, cageDecoration!!, constraintsChecker)
				cageDecoration.killerConstraint = killerConstraint
				killerConstraint.killerSum = killerSum
				constraintsChecker += killerConstraint
				grid.decorations += killerConstraint.cornerTextDecoration
				grid.cells[cageDecoration.topLeftI][cageDecoration.topLeftJ].cornerTextDecorationCount++
			}
		}
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		val cageDecorations = GdxSet<CageDecoration>()
		for (cages in cageMap)
		{
			for (cage in cages)
			{
				if (cage != null)
					cageDecorations += cage
			}
		}
		mapper["cageDecorations"] = cageDecorations.map { it.dataObject }.toTypedArray()
	}
}
