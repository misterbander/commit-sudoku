package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Button
import ktx.actors.centerPosition
import ktx.actors.onChange
import ktx.actors.setKeyboardFocus
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.scene2d.AccessibleInputWindow


abstract class CommitSudokuWindow(
	protected val screen: CommitSudokuScreen,
	title: String,
	isModal: Boolean
) : AccessibleInputWindow(title, screen.game.skin, "windowstyle")
{
	protected val game = screen.game
	
	val closeButton = Button(game.skin, "closebuttonstyle").apply { onChange { close() } }
	
	init
	{
		titleTable.add(closeButton).right()
		titleTable.pad(2F, 16F, 0F, 2F)
		this.isModal = isModal
		isVisible = false
	}
	
	fun show()
	{
		isVisible = true
		pack()
		centerPosition()
	}
	
	protected open fun close()
	{
		isVisible = false
		Gdx.input.setOnscreenKeyboardVisible(false)
		screen.panel.grid.setKeyboardFocus(true)
	}
}
