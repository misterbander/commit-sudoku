package misterbander.commitsudoku.decorations

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

class BorderDecoration(
	grid: SudokuGrid,
	val i: Float,
	val j: Float,
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
	override var color: Color? = Color.BLACK
	
	override val dataObject: HashMap<String, Serializable>
		get() = hashMapOf(
			"i" to i,
			"j" to j,
			"type" to type,
		)
	
	init
	{
		this.type = type
	}
	
	override fun draw(batch: Batch)
	{
		val shapeDrawer = game.shapeDrawer
		val x = grid.iToX(i)
		val y = grid.jToY(j)
		shapeDrawer.setColor(game.skin["primarycolor", Color::class.java])
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
