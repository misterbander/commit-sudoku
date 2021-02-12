package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import ktx.actors.plusAssign
import ktx.scene2d.buttonGroup
import ktx.scene2d.imageButton
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get
import misterbander.commitsudoku.CommitSudokuScreen

class Toolbar(screen: CommitSudokuScreen) : VerticalGroup()
{
	private val game = screen.game
	
	init
	{
		this += scene2d.buttonGroup(1, 1, game.skin) {
			imageButton("setgivensbuttonstyle", game.skin) { isChecked = true }
			imageButton("addthermobuttonstyle", game.skin)
			row()
			imageButton("addsandwichbuttonstyle", game.skin)
			imageButton("addtextdecorationbuttonstyle", game.skin)
			row()
			imageButton("addsmalltextdecorationbuttonstyle", game.skin)
			imageButton("addcircledecorationbuttonstyle", game.skin)
			row()
			imageButton("addlinedecorationbuttonstyle", game.skin)
			imageButton("addarrowdecorationbuttonstyle", game.skin)
			row()
			imageButton("addcagedecorationbuttonstyle", game.skin)
		}
		this += Image(game.skin["divider"], Scaling.none, Align.center)
		this += scene2d.table {
			imageButton("xbuttonstyle", game.skin)
			imageButton("antikingbuttonstyle", game.skin)
			row()
			imageButton("antiknightbuttonstyle", game.skin)
		}
	}
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		game.shapeDrawer.filledRectangle(0F, 0F, width, 720F, game.skin["toolbarbackgroundcolor", Color::class.java])
		super.draw(batch, parentAlpha)
	}
}
