package misterbander.commitsudoku.modifiers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.collections.GdxArray
import ktx.collections.minusAssign
import ktx.collections.plusAssign
import ktx.style.get
import misterbander.commitsudoku.decorations.CircleDecoration
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable
import kotlin.math.abs


class CircleDecorationAdder(grid: SudokuGrid) : GridModfier<CircleDecoration>(grid)
{
	private val circleDecorations: GdxArray<CircleDecoration> = GdxArray()
	private var currentCircleDecoration: CircleDecoration? = null
	private var justRemovedCircle = false
	
	private var prevI = -1
	private var prevJ = -1
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
			prevI = selectI
			prevJ = selectJ
		}
	}
	
	override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		currentCircleDecoration?.color = game.skin["secondarycolor"]
		currentCircleDecoration = null
		prevI = -1
		prevJ = -1
	}
	
	override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int)
	{
		updateSelect(x, y)
		if (!isValidIndex || !(abs(selectI - prevI) <= 1 && abs(selectJ - prevJ) <= 1))
			return
		if (findCircleDecoration() == null)
		{
			currentCircleDecoration?.i2 = selectI
			currentCircleDecoration?.j2 = selectJ
		}
	}
	
	private fun findCircleDecoration(): CircleDecoration?
	{
		circleDecorations.forEach {
			if (it.i1 == selectI && it.j1 == selectJ || it.i2 == selectI && it.j2 == selectJ)
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
