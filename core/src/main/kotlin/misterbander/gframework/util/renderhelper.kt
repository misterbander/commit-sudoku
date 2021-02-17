package misterbander.gframework.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer


private val temp = vec2()

/**
 * Draws a line with rounded caps.
 * @param x1 – the x-component of the first point
 * @param y1 – the y-component of the first point
 * @param x2 – the x-component of the second point
 * @param y2 – the y-component of the second point
 * @param color – color the colour of the line
 * @param lineWidth – the width of the line in world units
 */
fun ShapeDrawer.roundedLine(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, lineWidth: Float)
{
	val dir = temp.set(x2 - x1, y2 - y1).angle()
	line(x1, y1, x2, y2, color, lineWidth)
	sector(x1, y1, lineWidth/2, (dir + 90)*MathUtils.degreesToRadians, 180*MathUtils.degreesToRadians, color, color)
	sector(x2, y2, lineWidth/2, (dir - 90)*MathUtils.degreesToRadians, 180*MathUtils.degreesToRadians, color, color)
}