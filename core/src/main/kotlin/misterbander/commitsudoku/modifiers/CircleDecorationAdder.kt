package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import misterbander.commitsudoku.decorations.CircleDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.angle
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max

class CircleDecorationAdder(grid: SudokuGrid) : GridModfier<CircleDecoration>(grid)
{
	private val circleDecorations: GdxArray<CircleDecoration> = GdxArray()
	private var currentCircleDecoration: CircleDecoration? = null
	private var justRemovedCircle = false
	
	private var startI = -1
	private var startJ = -1
	override val isValidIndex
		get() = selectI in -1..9 && selectJ in -1..9
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		updateSelect(x, y)
		currentCircleDecoration = null
		if (!isValidIndex)
			return
		val existingCircleDecoration = findCircleDecoration()
		if (existingCircleDecoration != null)
		{
			removeModification(existingCircleDecoration)
			justRemovedCircle = true
		}
		else
		{
			currentCircleDecoration = CircleDecoration(grid, selectI, selectJ, 28F)
			currentCircleDecoration!!.color = Color.ORANGE
			addModification(currentCircleDecoration!!)
			startI = selectI
			startJ = selectJ
		}
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		currentCircleDecoration?.color = null
		currentCircleDecoration = null
		startI = -1
		startJ = -1
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex || currentCircleDecoration == null)
			return
		val d: Int = max(abs(selectI - startI), abs(selectJ - startJ))
		val angleFromStart: Float = angle(startI, startJ, selectI, selectJ)
		val snappedI: Int
		val snappedJ: Int
		
		if (angleFromStart < 22.5F || angleFromStart >= 337.5F)
		{
			snappedI = startI + d
			snappedJ = startJ
		}
		else if (angleFromStart < 67.5F)
		{
			snappedI = startI + d
			snappedJ = startJ + d
		}
		else if (angleFromStart < 112.5F)
		{
			snappedI = startI
			snappedJ = startJ + d
		}
		else if (angleFromStart < 157.5F)
		{
			snappedI = startI - d
			snappedJ = startJ + d
		}
		else if (angleFromStart < 202.5F)
		{
			snappedI = startI - d
			snappedJ = startJ
		}
		else if (angleFromStart < 247.5F)
		{
			snappedI = startI - d
			snappedJ = startJ - d
		}
		else if (angleFromStart < 292.5F)
		{
			snappedI = startI
			snappedJ = startJ - d
		}
		else
		{
			snappedI = startI + d
			snappedJ = startJ - d
		}
		
		if (snappedI in -1..9 && snappedJ in -1..9)
		{
			currentCircleDecoration!!.i2 = snappedI
			currentCircleDecoration!!.j2 = snappedJ
		}
	}
	
	private fun findCircleDecoration(): CircleDecoration?
	{
		circleDecorations.forEach {
			if (it.isOver(selectI, selectJ))
				return it
		}
		return null
	}
	
	override fun addModification(modification: CircleDecoration)
	{
		circleDecorations.insert(0, modification)
		grid.decorations += modification
	}
	
	override fun removeModification(modification: CircleDecoration)
	{
		circleDecorations -= modification
		grid.decorations -= modification
	}
	
	override fun clear()
	{
		circleDecorations.clear()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val circleDecorationDataObjects: Array<HashMap<String, Serializable>>? = mapper["circleDecorations"]
		circleDecorationDataObjects?.forEach { dataObject ->
			val i1 = dataObject["i1"] as Int
			val j1 = dataObject["j1"] as Int
			val i2 = dataObject["i2"] as Int
			val j2 = dataObject["j2"] as Int
			val circleDecoration = CircleDecoration(grid, i1, j1, 28F)
			circleDecoration.i2 = i2
			circleDecoration.j2 = j2
			circleDecorations.insert(0, circleDecoration)
			grid.decorations += circleDecoration
		}
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["circleDecorations"] = circleDecorations.map { it.dataObject }.toTypedArray()
	}
}
