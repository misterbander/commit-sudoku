package misterbander.commitsudoku.scene2d.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.*
import misterbander.commitsudoku.notoSans
import misterbander.gframework.scene2d.scene2d
import misterbander.gframework.util.wrap

class MessageDialog : RebuildableDialog("")
{
	private var message = ""
	private var hideAction: () -> Unit = {}

	override fun build()
	{
		contentTable.scene2d {
			label(notoSans.wrap(message, 720))
		}
		buttonTable.scene2d {
			textButton("OK") {
				onChange { hide() }
			}.cell(preferredWidth = 96F)
		}
	}

	fun show(stage: Stage, title: String, message: String, hideAction: () -> Unit = {})
	{
		titleLabel.txt = title
		this.message = message
		this.hideAction = hideAction
		show(stage)
	}

	override fun hide()
	{
		super.hide()
		hideAction()
	}
}
