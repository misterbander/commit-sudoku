package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import misterbander.commitsudoku.backgroundColor
import misterbander.commitsudoku.constraints.KillerConstraint
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.blend
import misterbander.gframework.util.dashedLine
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class CageDecoration(grid: SudokuGrid, row: Int, col: Int) : Decoration(grid)
{
	object Defaults
	{
		val dashSegmentLengths = floatArrayOf(4F, 4F)
	}

	val mask = Array(9) { BooleanArray(9) }
	val topLeftRow: Int
		get()
		{
			for (row in mask.indices)
			{
				for (col in mask[row].indices)
				{
					if (mask[row][col])
						return row
				}
			}
			return -1
		}
	val topLeftCol: Int
		get()
		{
			for (row in mask.indices)
			{
				for (col in mask[row].indices)
				{
					if (mask[row][col])
						return col
				}
			}
			return -1
		}
	var killerConstraint: KillerConstraint? = null
	private val internalColor = Color()
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("cageMask" to mask, "killerSum" to (killerConstraint?.killerSum ?: -1))

	init
	{
		addCell(row, col)
	}

	fun addCell(row: Int, col: Int)
	{
		mask[row][col] = true
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		for (row in mask.indices)
		{
			for (col in mask[row].indices)
			{
				if (!mask[row][col])
					continue
				val x = grid.colToX(col.toFloat())
				val y = grid.rowToY(row.toFloat() + 1)
				val size = grid.cellSize
				val topConnected = row > 0 && mask[row - 1][col]
				val bottomConnected = row < 8 && mask[row + 1][col]
				val leftConnected = col > 0 && mask[row][col - 1]
				val rightConnected = col < 8 && mask[row][col + 1]
				val topLeftConnected = topConnected && leftConnected && mask[row - 1][col - 1]
				val topRightConnected = topConnected && rightConnected && mask[row - 1][col + 1]
				val bottomLeftConnected = bottomConnected && leftConnected && mask[row + 1][col - 1]
				val bottomRightConnected = bottomConnected && rightConnected && mask[row + 1][col + 1]

				internalColor.set(primaryColor)
				if (color != null)
					internalColor.blend(color!!, backgroundColor)

				if (!topConnected)
					shapeDrawer.dashedLine(
						x + 4, y + size - 4, x + size - 4, y + size - 4,
						internalColor, 1F, Defaults.dashSegmentLengths, 2F
					)
				if (!bottomConnected)
					shapeDrawer.dashedLine(
						x + 4, y + 4, x + size - 4, y + 4,
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
