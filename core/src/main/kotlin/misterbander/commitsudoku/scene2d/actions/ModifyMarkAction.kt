package misterbander.commitsudoku.scene2d.actions

import ktx.log.info
import misterbander.commitsudoku.scene2d.SudokuGrid
import java.io.Serializable

class ModifyMarkAction(
	private val cell: SudokuGrid.Cell,
	private val type: Type,
	private val digit: Int,
	private val from: Boolean = when (type)
	{
		Type.CORNER -> cell.cornerMarks[digit - 1]
		Type.CENTER -> cell.centerMarks[digit - 1]
		else -> throw IllegalArgumentException("Invalid action type!")
	},
	private val to: Boolean
) : ModifyCellAction()
{
	override val dataObject: HashMap<String, Serializable> = hashMapOf(
		"type" to type,
		"i" to cell.i,
		"j" to cell.j,
		"digit" to digit,
		"from" to from,
		"to" to to
	)
	
	override fun run()
	{
		val from = if (inverse) this.to else this.from
		val to = if (inverse) this.from else this.to
		info("ModifyMarkAction      | INFO") { "Set cell (${cell.i}, ${cell.j}) ${type.name} mark from $from to $to" }
		if (type == Type.CORNER)
			cell.cornerMarks[digit - 1] = to
		else if (type == Type.CENTER)
			cell.centerMarks[digit - 1] = to
	}
}
