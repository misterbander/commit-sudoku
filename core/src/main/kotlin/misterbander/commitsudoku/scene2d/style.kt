package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.style.*
import misterbander.commitsudoku.scene2d.dialogs.CommitSudokuDialog
import misterbander.gframework.scene2d.GTextField

fun Actor.updateStyle(skin: Skin, oldSkin: Skin)
{
	when (this)
	{
		is Label -> oldSkin.find(style)?.let { style = skin[it] }
		is TextButton -> oldSkin.find(style)?.let { style = skin[it] }
		is ImageButton -> oldSkin.find(style)?.let { style = skin[it] }
		is GTextField -> oldSkin.find(style)?.let { style = skin[it] }
		is CommitSudokuDialog -> updateStyle(skin, oldSkin)
		is Table -> cells.forEach { it.actor?.updateStyle(skin, oldSkin) }
		is ToolbarMultibuttonMenu -> updateStyle(skin, oldSkin)
		is Group -> children.forEach { it.updateStyle(skin, oldSkin) }
	}
}
