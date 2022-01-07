package misterbander.commitsudoku.scene2d.actions

import ktx.log.info
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

class ModifyDigitAction(
	private val cell: SudokuGrid.Cell,
	private val from: Int = cell.digit,
	private val to: Int
) : ModifyCellAction()
{
	override val dataObject: HashMap<String, Serializable> = hashMapOf(
		"type" to Type.DIGIT,
		"i" to cell.i,
		"j" to cell.j,
		"from" to from,
		"to" to to
	)
	
	override fun run()
	{
		val from = if (inverse) this.to else this.from
		val to = if (inverse) this.from else this.to
		info("ModifyDigitAction     | INFO") { "Set cell (${cell.i}, ${cell.j}) digit from $from to $to" }
		cell.digit = to
	}
}
