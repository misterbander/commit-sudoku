package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.utils.Align
import misterbander.commitsudoku.CommitSudokuScreen

abstract class RebuildableDialog(screen: CommitSudokuScreen, title: String) : CommitSudokuDialog(screen, title)
{
	abstract fun build()
	
	fun rebuild()
	{
		val centerX = x + width/2
		val centerY = y + height/2
		contentTable.clear()
		buttonTable.clear()
		build()
		pack()
		setPosition(centerX, centerY, Align.center)
	}
	
	override fun show()
	{
		rebuild()
		super.show()
	}
}
