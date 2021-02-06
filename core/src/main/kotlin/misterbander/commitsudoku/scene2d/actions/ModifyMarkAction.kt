package misterbander.commitsudoku.scene2d.actions

import misterbander.commitsudoku.scene2d.SudokuGrid

class ModifyMarkAction(
	private val cell: SudokuGrid.Cell,
	private val type: Type,
	private val digit: Int,
	private val from: Boolean = when (type)
	{
		Type.CORNER -> cell.cornerMarks[digit - 1]
		Type.CENTER -> cell.centerMarks[digit - 1]
	},
	private val to: Boolean = !from
) : ModifyCellAction()
{
	init
	{
		runnable = Runnable {
			val from = if (inverse) this.to else this.from
			val to = if (inverse) this.from else this.to
			println("Set cell (${cell.i}, ${cell.j}) ${type.name} mark from $from to $to")
			when (type)
			{
				Type.CORNER -> cell.cornerMarks[digit - 1] = to
				Type.CENTER -> cell.centerMarks[digit - 1] = to
			}
		}
	}
	
	enum class Type
	{
		CORNER, CENTER
	}
}