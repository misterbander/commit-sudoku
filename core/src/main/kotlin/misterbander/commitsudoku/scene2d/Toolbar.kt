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

class Toolbar(private val screen: CommitSudokuScreen) : VerticalGroup()
{
	private val game = screen.game
	private val grid
		get() = screen.panel.grid
	private val constraintsChecker
		get() = grid.constraintsChecker
	
	private val thermoMultibutton = ToolbarMultibutton(screen, game.skin, "addthermobuttonstyle")
	val thermoMultibuttonMenu = ToolbarMultibuttonMenu(
		game,
		thermoMultibutton,
		scene2d.imageButton("addthermobuttonstyle", game.skin),
		scene2d.imageButton("softthermobuttonstyle", game.skin),
		scene2d.imageButton("emptythermobuttonstyle", game.skin)
	)
	
	val xButton = ImageButton(game.skin, "xbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.xConstraint
			else
				constraintsChecker -= constraintsChecker.xConstraint
		}
	}
	val antiKingButton = ImageButton(game.skin, "antikingbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKingStatement
			else
				constraintsChecker -= constraintsChecker.antiKingStatement
		}
	}
	val antiKnightButton = ImageButton(game.skin, "antiknightbuttonstyle").apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKnightStatement
			else
				constraintsChecker -= constraintsChecker.antiKnightStatement
		}
	}
	val nonconsecutiveButton = ImageButton(game.skin, "nonconsecutivebuttonstyle").apply {
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
			actor(thermoMultibutton) {
				multibuttonMenu = thermoMultibuttonMenu
				onChange { grid.modifier = grid.modifiers.thermoAdder }
			}
			row()
			imageButton("addsandwichbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.sandwichConstraintSetter }
			}
			imageButton("addtextdecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.textDecorationAdder }
			}
			row()
			imageButton("addcornertextdecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.cornerTextDecorationAdder }
			}
			imageButton("addcircledecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.circleDecorationAdder }
			}
			row()
			imageButton("addlinedecorationbuttonstyle", game.skin)
			imageButton("addarrowdecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.arrowDecorationAdder }
			}
			row()
			imageButton("addcagedecorationbuttonstyle", game.skin) {
				onChange { grid.modifier = grid.modifiers.cageSetter }
			}
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
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		game.shapeDrawer.filledRectangle(
			0F, 0F, width, screen.viewport.worldHeight, game.skin["toolbarbackgroundcolor", Color::class.java]
		)
		super.draw(batch, parentAlpha)
	}
}
