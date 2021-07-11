package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.style.*
import misterbander.commitsudoku.constraints.KillerConstraint
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.blend
import misterbander.gframework.util.dashedLine
import java.io.Serializable
import kotlin.math.min

class CageDecoration(grid: SudokuGrid, i: Int, j: Int) : Decoration(grid)
{
	object Defaults
	{
		val dashSegmentLengths = floatArrayOf(4F, 4F)
	}
	
	val mask = Array(9) { BooleanArray(9) }
	var topLeftI = i
	var topLeftJ = j
	var killerConstraint: KillerConstraint? = null
	private val internalColor = Color()
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("cageMask" to mask, "killerSum" to (killerConstraint?.killerSum ?: -1))
	
	init
	{
		addCell(i, j)
	}
	
	fun addCell(i: Int, j: Int)
	{
		if (mask[i][j])
			return
		mask[i][j] = true
		if (j > topLeftJ)
		{
			topLeftI = i
			topLeftJ = j
		}
		else if (j == topLeftJ)
			topLeftI = min(topLeftI, i)
	}
	
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		for (i in mask.indices)
		{
			for (j in mask[i].indices)
			{
				if (!mask[i][j])
					continue
				val x = grid.iToX(i.toFloat())
				val y = grid.jToY(j.toFloat())
				val size = grid.cellSize
				val bottomConnected = j > 0 && mask[i][j - 1]
				val topConnected = j < 8 && mask[i][j + 1]
				val leftConnected = i > 0 && mask[i - 1][j]
				val rightConnected = i < 8 && mask[i + 1][j]
				val bottomLeftConnected = bottomConnected && leftConnected && mask[i - 1][j - 1]
				val bottomRightConnected = bottomConnected && rightConnected && mask[i + 1][j - 1]
				val topLeftConnected = topConnected && leftConnected && mask[i - 1][j + 1]
				val topRightConnected = topConnected && rightConnected && mask[i + 1][j + 1]
				
				internalColor.set(game.skin["primarycolor", Color::class.java])
				if (color != null)
					internalColor.blend(color!!, game.skin["backgroundcolor"])
				
				if (!bottomConnected)
					shapeDrawer.dashedLine(
						x + 4, y + 4, x + size - 4, y + 4,
						internalColor, 1F, Defaults.dashSegmentLengths, 2F
					)
				if (!topConnected)
					shapeDrawer.dashedLine(
						x + 4, y + size - 4, x + size - 4, y + size - 4,
						internalColor, 1F, Defaults.dashSegmentLengths, 2F
					)
				if (!leftConnected)
					shapeDrawer.dashedLine(
						x + 4, y + 4, x + 4, y + size - 4,
						internalColor, 1F, Defaults.dashSegmentLengths, 2F
					)
				if (!rightConnected)
					shapeDrawer.dashedLine(
						x + size - 4, y + 4, x + size - 4, y + size - 4,
						internalColor, 1F, Defaults.dashSegmentLengths, 2F
					)
				if (!bottomLeftConnected)
				{
					if (leftConnected)
						shapeDrawer.dashedLine(
							x, y + 4, x + 4, y + 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
					if (bottomConnected)
						shapeDrawer.dashedLine(
							x + 4, y, x + 4, y + 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
				}
				if (!bottomRightConnected)
				{
					if (rightConnected)
						shapeDrawer.dashedLine(
							x + size, y + 4, x + size - 4, y + 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
					if (bottomConnected)
						shapeDrawer.dashedLine(
							x + size - 4, y, x + size - 4, y + 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
				}
				if (!topLeftConnected)
				{
					if (leftConnected)
						shapeDrawer.dashedLine(
							x, y + size - 4, x + 4, y + size - 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
					if (topConnected)
						shapeDrawer.dashedLine(
							x + 4, y + size, x + 4, y + size - 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
				}
				if (!topRightConnected)
				{
					if (rightConnected)
						shapeDrawer.dashedLine(
							x + size, y + size - 4, x + size - 4, y + size - 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
					if (topConnected)
						shapeDrawer.dashedLine(
							x + size - 4, y + size, x + size - 4, y + size - 4,
							internalColor, 1F, Defaults.dashSegmentLengths, 6F
						)
				}
			}
		}
	}
}
