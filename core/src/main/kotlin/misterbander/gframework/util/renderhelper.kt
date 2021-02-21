package misterbander.gframework.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.min


private val temp = vec2()

/**
 * Draws a line with rounded caps.
 * @param x1 the x-component of the first point
 * @param y1 the y-component of the first point
 * @param x2 the x-component of the second point
 * @param y2 the y-component of the second point
 * @param color the color of the line
 * @param lineWidth the width of the line in world units
 */
fun ShapeDrawer.roundedLine(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, lineWidth: Float)
{
	val dir = temp.set(x2 - x1, y2 - y1).angle()
	line(x1, y1, x2, y2, color, lineWidth)
	sector(x1, y1, lineWidth/2, (dir + 90)*MathUtils.degreesToRadians, 180*MathUtils.degreesToRadians, color, color)
	sector(x2, y2, lineWidth/2, (dir - 90)*MathUtils.degreesToRadians, 180*MathUtils.degreesToRadians, color, color)
}

/**
 * Draws a dashed line.
 * @param x1 the x-component of the first point
 * @param y1 the y-component of the first point
 * @param x2 the x-component of the second point
 * @param y2 the y-component of the second point
 * @param color the color of the line
 * @param lineWidth the width of the line in world units
 * @param dashSegmentLengths float array defining the lengths of each dash segments, in world units, `ShapeDrawer` will
 * interpret the values as lengths alternating between visible dash segments and invisible dash segments
 * @param phaseShift positive value that shifts the current phase of the dash drawn
 */
fun ShapeDrawer.dashedLine(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, lineWidth: Float,
						   dashSegmentLengths: Array<Float>, phaseShift: Float = 0F)
{
	// Account for phase shifting
	var index = 0
	var accumulator = 0F
	var segmentLength: Float
	var visible = true
	while (true)
	{
		accumulator += dashSegmentLengths[index]
		if (accumulator > phaseShift)
		{
			segmentLength = accumulator - phaseShift
			break
		}
		index = (index + 1)%dashSegmentLengths.size
		visible = !visible
	}
	
	// Draw the dashed line
	temp.set(x2 - x1, y2 - y1)
	var drawX = x1
	var drawY = y1
	var distLeft = temp.len()
	while (distLeft > 0)
	{
		temp.setLength(min(segmentLength, distLeft))
		if (visible)
			line(drawX, drawY, drawX + temp.x, drawY + temp.y, color, lineWidth)
		drawX += temp.x
		drawY += temp.y
		distLeft -= segmentLength
		index = (index + 1)%dashSegmentLengths.size
		segmentLength = dashSegmentLengths[index]
		visible = !visible
	}
}
