package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.get
import misterbander.commitsudoku.scene2d.*
import misterbander.gframework.GScreen

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	private val buttonSize = 80F
	
	private val timerLabel: Label by lazy { scene2d { label("0 : 00", "infolabelstyle", game.skin) } }
	
	private val editButton: ImageButton by lazy {
		scene2d {
			imageButton("editbuttonstyle", game.skin) {
				isDisabled = true
				onClick {
					isEditing = true
				}
			}
		}
	}
	private val playButton: ImageButton by lazy {
		scene2d {
			imageButton("playbuttonstyle", game.skin) {
				onClick {
					isEditing = false
				}
			}
		}
	}
	private val digitKeypad: Table by lazy {
		scene2d {
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle2"))
				row()
				actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle2"))
				row()
				actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle2"))
				actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle2"))
			}
		}
	}
	private val cornerMarkKeypad: Table by lazy {
		scene2d {
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle", true))
				actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle", true))
				actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle", true))
				row()
				actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle", true))
				actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle", true))
				actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle", true))
				row()
				actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle",true))
				actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle",true))
				actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle",true))
				row()
			}
		}
	}
	private val centerMarkKeypad: Table by lazy {
		scene2d {
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle"))
				row()
				actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle"))
				row()
				actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle"))
				actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle"))
			}
		}
	}
	private val colorKeypad: Table by lazy {
		scene2d {
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				actor(ModifyColorButton(grid, 1, game.skin, "redbuttonstyle"))
				actor(ModifyColorButton(grid, 2, game.skin, "orangebuttonstyle"))
				actor(ModifyColorButton(grid, 3, game.skin, "yellowbuttonstyle"))
				row()
				actor(ModifyColorButton(grid, 4, game.skin, "greenbuttonstyle"))
				actor(ModifyColorButton(grid, 5, game.skin, "bluebuttonstyle"))
				actor(ModifyColorButton(grid, 6, game.skin, "darkbluebuttonstyle"))
				row()
				actor(ModifyColorButton(grid, 7, game.skin, "purplebuttonstyle"))
				actor(ModifyColorButton(grid, 8, game.skin, "graybuttonstyle"))
				textButton("", "textbuttonstyle", game.skin) {
					onClick {
						grid.typedDigit(9, true)
						isChecked = false
					}
				}
			}
		}
	}
	private val keypad by lazy {
		scene2d.table {
			defaults().pad(3F)
			actor(digitKeypad)
			row()
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				imageButton("deletebuttonstyle", game.skin) {
					onClick { grid.typedDigit(0, true) }
				}
				actor(undoButton)
				actor(redoButton)
			}
		}
	}
	val undoButton: ImageButton by lazy {
		scene2d {
			imageButton("undobuttonstyle", game.skin) { onClick { grid.actionController.undo() } }
		}
	}
	val redoButton: ImageButton by lazy {
		scene2d {
			imageButton("redobuttonstyle", game.skin) { onClick { grid.actionController.redo() } }
		}
	}
	private val grid = SudokuGrid(this)
	private val timer = SudokuTimer(timerLabel)
	
	var isEditing = false
		set(value)
		{
			field = value
		}
	var keypadInputMode = InputMode.DIGIT
	
	override fun show()
	{
		super.show()
		println("Show CommitSudokuScreen")
		stage += scene2d.table {
			setDebug(true, true)
			setFillParent(true)
			actor(grid).cell(space = grid.cellSize)
			
			table {
				defaults().pad(5F)
				
				// Status labels
				label("Edit Mode", "infolabelstyle", game.skin).inCell.left()
				row()
				actor(timerLabel).cell(spaceBottom = 64F).inCell.left()
				row()
				
				// Control panel
				table {
					setDebug(true, true)
					defaults().pad(10F)
					defaults().size(54F, 54F)
					Scene2DSkin.defaultSkin
					actor(editButton)
					actor(playButton)
					imageButton("clearbuttonstyle", game.skin)
				}.inCell.left()
				row()
				table {
					setDebug(true, true)
					defaults().pad(5F)
					actor(keypad)
					buttonGroup(1, 1, game.skin) {
						defaults().size(buttonSize, buttonSize)
						defaults().pad(4F)
						textButton("#", "checkabletextbuttonstyle2", game.skin) {
							isChecked = true
							onClick { setKeypad(digitKeypad) }
						}
						row()
						textButton("#", "checkabletextbuttonstyle", game.skin) {
							label.setAlignment(Align.topLeft)
							padLeft(5F)
							onClick { setKeypad(cornerMarkKeypad) }
						}
						row()
						textButton("#", "checkabletextbuttonstyle", game.skin) {
							onClick { setKeypad(centerMarkKeypad) }
						}
						row()
						imageButton("colorbuttonstyle", game.skin) {
							onClick { setKeypad(colorKeypad) }
						}
					}
					
					/* Add an empty onClick listener so that accidentally clicking on the gaps between the buttons does
					   not unselect grid cells for improved user experience */
					touchable = Touchable.enabled
					onClick {}
				}
			}.inCell.top().left()
		}
		grid.addListener(SudokuGridClickListener(grid))
		grid.addListener(SudokuGridKeyListener(grid))
		stage.keyboardFocus = grid
		
		timer.isRunning = true
	}
	
	private fun setKeypad(keypad: Table)
	{
		(this.keypad.cells[0] as Cell<*>).setActor(keypad)
		keypadInputMode = when (keypad)
		{
			cornerMarkKeypad -> InputMode.CORNER_MARK
			centerMarkKeypad -> InputMode.CENTER_MARK
			colorKeypad -> InputMode.COLOR
			else -> InputMode.DIGIT
		}
	}
	
	override fun render(delta: Float)
	{
		super.render(delta)
		timer.update(delta)
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
	}
	
	enum class InputMode
	{
		DIGIT, CORNER_MARK, CENTER_MARK, COLOR
	}
}
