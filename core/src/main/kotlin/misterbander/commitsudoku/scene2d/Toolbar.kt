package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import ktx.actors.onChange
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.get
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class Toolbar(private val screen: CommitSudokuScreen) : VerticalGroup(), PersistentState
{
	private val game = screen.game
	private val grid
		get() = screen.sudokuPanel.grid
	private val constraintsChecker
		get() = grid.constraintsChecker
	
	private val xButton = ImageButton(game.skin, "xbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.xConstraint
			else
				constraintsChecker -= constraintsChecker.xConstraint
		}
	}
	private val antiKingButton = ImageButton(game.skin, "antikingbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKingStatement
			else
				constraintsChecker -= constraintsChecker.antiKingStatement
		}
	}
	private val antiKnightButton = ImageButton(game.skin, "antiknightbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKnightStatement
			else
				constraintsChecker -= constraintsChecker.antiKnightStatement
		}
	}
	private val nonconsecutiveButton = ImageButton(game.skin, "nonconsecutivebuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.nonconsecutiveStatement
			else
				constraintsChecker -= constraintsChecker.nonconsecutiveStatement
		}
	}
	
	init
	{
		this += scene2d.buttonGroup(1, 1, game.skin) {
			imageButton("setgivensbuttonstyle", game.skin) {
				isChecked = true
				onChange { grid.modifier = null }
			}
			imageButton("addthermobuttonstyle", game.skin)
			row()
			imageButton("addsandwichbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.sandwichConstraintSetter }
			}
			imageButton("addtextdecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.textDecorationAdder }
			}
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
			actor(xButton)
			actor(antiKingButton)
			row()
			actor(antiKnightButton)
			actor(nonconsecutiveButton)
		}
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		xButton.isChecked = mapper["x"] ?: xButton.isChecked
		antiKingButton.isChecked = mapper["antiKing"] ?: antiKingButton.isChecked
		antiKnightButton.isChecked = mapper["antiKnight"] ?: antiKnightButton.isChecked
		nonconsecutiveButton.isChecked = mapper["nonconsecutive"] ?: nonconsecutiveButton.isChecked
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["x"] = xButton.isChecked
		mapper["antiKing"] = antiKingButton.isChecked
		mapper["antiKnight"] = antiKnightButton.isChecked
		mapper["nonconsecutive"] = nonconsecutiveButton.isChecked
	}
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		game.shapeDrawer.filledRectangle(
			0F, 0F, width, screen.viewport.worldHeight, game.skin["toolbarbackgroundcolor", Color::class.java]
		)
		super.draw(batch, parentAlpha)
	}
}
