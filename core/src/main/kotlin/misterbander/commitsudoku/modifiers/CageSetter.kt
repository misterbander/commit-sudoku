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
	private val grid: SudokuGrid,
	private val constraintsChecker: ConstraintsChecker
) : GridModifier<CageDecoration>
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

	private var selectedRow = 0
	private var selectedCol = 0
	private val isValidIndex
		get() = selectedRow in 0..8 && selectedCol in 0..8

	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		currentCage = null
		if (!isValidIndex)
			return
		val cage = cageMap[selectedRow][selectedCol]
		if (cage != null)
		{
			removeModification(cage)
			justRemovedCage = true
		}
		else if (cageMap[selectedRow][selectedCol] == null && !justRemovedCage)
		{
			if (currentCage == null)
			{
				currentCage = CageDecoration(grid, selectedRow, selectedCol)
				currentCage!!.color = Color.ORANGE
				addModification(currentCage!!)
			}
			else
				currentCage!!.addCell(selectedRow, selectedCol)
			cageMap[selectedRow][selectedCol] = currentCage
		}
	}

	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		selectedRow = grid.yToRow(y)
		selectedCol = grid.xToCol(x)
		if (!isValidIndex || justRemovedCage || currentCage == null || cageMap[selectedRow][selectedCol] != null)
			return
		currentCage!!.addCell(selectedRow, selectedCol)
		cageMap[selectedRow][selectedCol] = currentCage
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
			grid.cells[currentCage!!.topLeftRow][currentCage!!.topLeftCol].cornerTextDecorationCount++
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
		for (row in modification.mask.indices)
		{
			for (col in modification.mask[row].indices)
			{
				if (modification.mask[row][col])
					cageMap[row][col] = null
			}
		}
		grid.decorations -= modification
		if (modification.killerConstraint != null)
		{
			constraintsChecker -= modification.killerConstraint!!
			grid.decorations -= modification.killerConstraint!!.cornerTextDecoration
			grid.cells[modification.topLeftRow][modification.topLeftCol].cornerTextDecorationCount--
			constraintsChecker.check(grid)
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
			for (row in cageMask.indices)
			{
				for (col in cageMask[row].indices)
				{
					if (cageMask[row][col])
					{
						if (cageDecoration == null)
						{
							cageDecoration = CageDecoration(grid, row, col)
							addModification(cageDecoration)
						}
						else
							cageDecoration.addCell(row, col)
						cageMap[row][col] = cageDecoration
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
				grid.cells[cageDecoration.topLeftCol][cageDecoration.topLeftRow].cornerTextDecorationCount++
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
