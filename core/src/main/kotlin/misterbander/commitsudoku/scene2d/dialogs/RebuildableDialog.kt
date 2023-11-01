package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Align

abstract class RebuildableDialog(title: String) : CommitSudokuDialog(title)
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

	override fun show(stage: Stage): Dialog
	{
		rebuild()
		return super.show(stage)
	}
}
