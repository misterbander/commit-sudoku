package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
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
import misterbander.commitsudoku.SLOW_THERMO_BUTTON_STYLE
import misterbander.commitsudoku.TEXT_DECORATION_BUTTON_STYLE
import misterbander.commitsudoku.THERMO_BUTTON_STYLE
import misterbander.commitsudoku.X_BUTTON_STYLE
import misterbander.commitsudoku.constraints.ConstraintsChecker
import misterbander.commitsudoku.constraints.ThermoConstraint
import misterbander.commitsudoku.toolbarBackgroundColor
import space.earlygrey.shapedrawer.ShapeDrawer

class Toolbar(
	private val screen: CommitSudokuScreen,
	private val constraintsChecker: ConstraintsChecker
) : VerticalGroup()
{
	private val grid: SudokuGrid
		get() = screen.grid
	private val shapeDrawer: ShapeDrawer
		get() = screen.game.shapeDrawer

	private lateinit var xButton: ImageButton
	private lateinit var antiKingButton: ImageButton
	private lateinit var antiKnightButton: ImageButton
	private lateinit var nonconsecutiveButton: ImageButton

	init
	{
		build()

		constraintsChecker.xObservable.addObserver { value -> xButton.isChecked = value }
		constraintsChecker.antiKingObservable.addObserver { value -> antiKingButton.isChecked = value }
		constraintsChecker.antiKnightObservable.addObserver { value -> antiKnightButton.isChecked = value }
		constraintsChecker.nonconsecutiveObservable.addObserver { value -> nonconsecutiveButton.isChecked = value }
	}

	fun build()
	{
		clear()
		this += scene2d.buttonGroup(1, 1) {
			imageButton(SET_GIVENS_BUTTON_STYLE) {
				isChecked = true
				onChange { grid.modifier = null }
			}
			actor(ToolbarMultibutton(THERMO_BUTTON_STYLE) {
				ToolbarMultibuttonMenu(
					scene2d.imageButton(THERMO_BUTTON_STYLE) {
						onClick { grid.modifiers.thermoAdder.type = ThermoConstraint.Type.NORMAL }
					},
					scene2d.imageButton(SLOW_THERMO_BUTTON_STYLE) {
						onClick { grid.modifiers.thermoAdder.type = ThermoConstraint.Type.SLOW }
					},
					scene2d.imageButton(EMPTY_THERMO_BUTTON_STYLE) {
						onClick { grid.modifiers.thermoAdder.type = ThermoConstraint.Type.DECORATION }
					}
				)
			}) {
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
			actor(ToolbarMultibutton(CAGE_DECORATION_BUTTON_STYLE) {
				ToolbarMultibuttonMenu(
					scene2d.imageButton(KILLER_CAGE_BUTTON_STYLE) {
						onClick { grid.modifiers.cageSetter.isKillerMode = true }
					},
					scene2d.imageButton(CAGE_DECORATION_BUTTON_STYLE) {
						onClick { grid.modifiers.cageSetter.isKillerMode = false }
					}
				)
			}) {
				onChange { grid.modifier = grid.modifiers.cageSetter }
			}
			row()
			imageButton(BORDER_DECORATION_BUTTON_STYLE) {
				onChange { grid.modifier = grid.modifiers.borderDecorationSetter }
			}
		}
		this += Image(Scene2DSkin.defaultSkin["divider"], Scaling.none, Align.center)

		this += scene2d.table {
			xButton = imageButton(X_BUTTON_STYLE).apply {
				onChange {
					constraintsChecker.x = isChecked
					constraintsChecker.check(grid.cells)
				}
			}
			antiKingButton = imageButton(ANTIKING_BUTTON_STYLE).apply {
				onChange {
					constraintsChecker.antiKing = isChecked
					constraintsChecker.check(grid.cells)
				}
			}
			row()
			antiKnightButton = imageButton(ANTIKNIGHT_BUTTON_STYLE).apply {
				onChange {
					constraintsChecker.antiKnight = isChecked
					constraintsChecker.check(grid.cells)
				}
			}
			nonconsecutiveButton = imageButton(NON_CONSECUTIVE_BUTTON_STYLE).apply {
				onChange {
					constraintsChecker.nonconsecutive = isChecked
					constraintsChecker.check(grid.cells)
				}
			}
		}
	}

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		shapeDrawer.filledRectangle(x, y, width, height, toolbarBackgroundColor)
		super.draw(batch, parentAlpha)
	}
}
