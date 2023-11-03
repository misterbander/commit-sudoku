package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import misterbander.commitsudoku.primaryColor
import misterbander.commitsudoku.scene2d.SudokuGrid
import space.earlygrey.shapedrawer.ShapeDrawer
import java.io.Serializable

class BorderDecoration(
	grid: SudokuGrid,
	val row: Float,
	val col: Float,
	type: Type = Type.BLACK_DOT
) : Decoration(grid)
{
	var type = type
		set(value)
		{
			field = value
			color = when (value)
			{
				Type.BLACK_DOT -> Color.BLACK
				Type.GRAY_DOT -> Color.LIGHT_GRAY
				Type.WHITE_DOT -> Color.WHITE
			}
		}

	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf("row" to row, "col" to col, "type" to type)

	init
	{
		this.type = type
	}

	override fun draw(shapeDrawer: ShapeDrawer)
	{
		val x = grid.colToX(col)
		val y = grid.rowToY(row)
		shapeDrawer.setColor(primaryColor)
		shapeDrawer.circle(x, y, 8F, 2F)
		shapeDrawer.filledCircle(x, y, 8F, color)
	}

	enum class Type
	{
		BLACK_DOT, GRAY_DOT, WHITE_DOT;

		fun nextType(): Type?
		{
			val types = values()
			if (ordinal == types.size - 1)
				return null
			return types[ordinal + 1]
		}
	}
}
