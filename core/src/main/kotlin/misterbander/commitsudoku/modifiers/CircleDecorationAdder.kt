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


class CircleDecorationAdder(grid: SudokuGrid) : GridModfier(grid)
{
	private val circleDecorations: GdxArray<CircleDecoration> = GdxArray()
	private var currentCircleDecoration: CircleDecoration? = null
	private var justRemovedCircle = false
	
	private var highlightI = 0
	private var highlightJ = 0
	private var prevI = -1
	private var prevJ = -1
	private val isValidIndex
		get() = highlightI in -1..9 && highlightJ in -1..9
	
	
	override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int)
	{
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		currentCircleDecoration = null
		if (!isValidIndex)
			return
		val existingCircleDecoration = findCircleDecoration()
		if (existingCircleDecoration != null)
		{
			circleDecorations -= existingCircleDecoration
			grid.decorations -= existingCircleDecoration
			justRemovedCircle = true
		}
		else
		{
			val circleDecoration = CircleDecoration(grid, highlightI, highlightJ, 28F)
			circleDecoration.color = Color.ORANGE
			circleDecorations.insert(0, circleDecoration)
			grid.decorations += circleDecoration
			currentCircleDecoration = circleDecoration
			prevI = highlightI
			prevJ = highlightJ
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
		highlightI = grid.xToI(x)
		highlightJ = grid.yToJ(y)
		if (!isValidIndex || !(abs(highlightI - prevI) <= 1 && abs(highlightJ - prevJ) <= 1))
			return
		if (findCircleDecoration() == null)
		{
			currentCircleDecoration?.i2 = highlightI
			currentCircleDecoration?.j2 = highlightJ
		}
	}
	
	private fun findCircleDecoration(): CircleDecoration?
	{
		circleDecorations.forEach {
			if (it.i1 == highlightI && it.j1 == highlightJ || it.i2 == highlightI && it.j2 == highlightJ)
				return it
		}
		return null
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
