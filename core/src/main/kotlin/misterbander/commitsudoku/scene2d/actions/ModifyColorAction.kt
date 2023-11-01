package misterbander.commitsudoku.scene2d.actions

import ktx.log.info
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

class ModifyColorAction(
	private val cell: SudokuGrid.Cell,
	private val from: Int = cell.colorCode,
	private val to: Int
) : ModifyCellAction()
{
	override val dataObject: HashMap<String, Serializable> = hashMapOf(
		"type" to Type.COLOR,
		"i" to cell.i,
		"j" to cell.j,
		"from" to from,
		"to" to to
	)

	override fun run(inverse: Boolean)
	{
		val from = if (inverse) this.to else this.from
		val to = if (inverse) this.from else this.to
		info("ModifyColorAction     | INFO") { "Set cell (${cell.i}, ${cell.j}) color from $from to $to" }
		cell.colorCode = to
	}
}
