package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.*
import misterbander.commitsudoku.ANTIKING_BUTTON_STYLE
import misterbander.commitsudoku.ANTIKNIGHT_BUTTON_STYLE
import misterbander.commitsudoku.ARROW_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.BORDER_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.CAGE_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.CIRCLE_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.CORNER_TEXT_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.EMPTY_THERMO_BUTTON_STYLE
import misterbander.commitsudoku.KILLER_CAGE_BUTTON_STYLE
import misterbander.commitsudoku.LINE_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.LITTLE_ARROW_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.NON_CONSECUTIVE_BUTTON_STYLE
import misterbander.commitsudoku.SANDWICH_BUTTON_STYLE
import misterbander.commitsudoku.SET_GIVENS_BUTTON_STYLE
import misterbander.commitsudoku.SOFT_THERMO_BUTTON_STYLE
import misterbander.commitsudoku.TEXT_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.THERMO_BUTTON_STYLE
import misterbander.commitsudoku.X_BUTTON_STYLE
import misterbander.commitsudoku.toolbarBackgroundColor

class Toolbar(private val screen: CommitSudokuScreen) : VerticalGroup()
{
	private val game = screen.game
	private val grid
		get() = screen.panel.grid
	private val constraintsChecker
		get() = grid.constraintsChecker
	
	private val thermoMultibutton = ToolbarMultibutton(screen, THERMO_BUTTON_STYLE)
	val thermoMultibuttonMenu = ToolbarMultibuttonMenu(
		game,
		thermoMultibutton,
		scene2d.imageButton(THERMO_BUTTON_STYLE),
		scene2d.imageButton(SOFT_THERMO_BUTTON_STYLE),
		scene2d.imageButton(EMPTY_THERMO_BUTTON_STYLE)
	)
	private val cageMultibutton = ToolbarMultibutton(screen, KILLER_CAGE_BUTTON_STYLE)
	val cageMultibuttonMenu = ToolbarMultibuttonMenu(
		game,
		cageMultibutton,
		scene2d.imageButton(KILLER_CAGE_BUTTON_STYLE) { onClick { screen.panel.showZero = true } },
		scene2d.imageButton(CAGE_DECORATION_BUTTON_STYLE) { onClick { screen.panel.showZero = false } }
	)
	
	val xButton = scene2d.imageButton(X_BUTTON_STYLE).apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.xConstraint
			else
				constraintsChecker -= constraintsChecker.xConstraint
		}
	}
	val antiKingButton = scene2d.imageButton(ANTIKING_BUTTON_STYLE).apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKingStatement
			else
				constraintsChecker -= constraintsChecker.antiKingStatement
		}
	}
	val antiKnightButton = scene2d.imageButton(ANTIKNIGHT_BUTTON_STYLE).apply {
		setProgrammaticChangeEvents(true)
		onChange {
			if (isChecked)
				constraintsChecker += constraintsChecker.antiKnightStatement
			else
				constraintsChecker -= constraintsChecker.antiKnightStatement
		}
	}
	val nonconsecutiveButton = scene2d.imageButton(NON_CONSECUTIVE_BUTTON_STYLE).apply {
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
		this += scene2d.buttonGroup(1, 1) {
			imageButton(SET_GIVENS_BUTTON_STYLE) {
				isChecked = true
				onChange { grid.modifier = null }
			}
			actor(thermoMultibutton) {
				multibuttonMenu = thermoMultibuttonMenu
				onChange { grid.modifier = grid.modifiers.thermoAdder }
			}
			row()
			imageButton(SANDWICH_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.sandwichConstraintSetter }
			}
			imageButton(TEXT_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.textDecorationAdder }
			}
			row()
			imageButton(CORNER_TEXT_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.cornerTextDecorationAdder }
			}
			imageButton(CIRCLE_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.circleDecorationAdder }
			}
			row()
			imageButton(LINE_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.lineDecorationAdder }
			}
			imageButton(ARROW_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.arrowDecorationAdder }
			}
			row()
			imageButton(LITTLE_ARROW_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.littleArrowDecorationAdder }
			}
			actor(cageMultibutton) {
				multibuttonMenu = cageMultibuttonMenu
				onChange { grid.modifier = grid.modifiers.cageSetter }
			}
			row()
			imageButton(BORDER_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.borderDecorationSetter }
			}
		}
		this += Image(Scene2DSkin.defaultSkin["divider"], Scaling.none, Align.center)
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
		game.shapeDrawer.filledRectangle(0F, 0F, width, screen.viewport.worldHeight, toolbarBackgroundColor)
		super.draw(batch, parentAlpha)
	}
}
