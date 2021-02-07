package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.*
import ktx.style.get
import misterbander.commitsudoku.scene2d.ModifyCellButton
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.commitsudoku.scene2d.SudokuGridClickListener
import misterbander.commitsudoku.scene2d.SudokuGridKeyListener
import misterbander.gframework.GScreen

class CommitSudokuScreen(game: CommitSudoku) : GScreen<CommitSudoku>(game)
{
	override val viewport by lazy { ExtendViewport(1280F, 720F, camera) }
	
	private val buttonSize = 80F
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
	
	private val keypad by lazy {
		scene2d.table {
			defaults().pad(3F)
			actor(digitKeypad)
			row()
			table {
				defaults().size(buttonSize, buttonSize)
				defaults().pad(3F)
				imageButton("deletebuttonstyle", game.skin) {
					onClick { grid.typedDigit(0) }
				}
				imageButton("undobuttonstyle", game.skin)
				imageButton("redobuttonstyle", game.skin)
			}
		}
	}
	var inputMode = InputMode.DIGIT
	private val grid = SudokuGrid(this)
	
	override fun show()
	{
		super.show()
		stage += scene2d.table {
			setDebug(true, true)
			setFillParent(true)
			actor(grid).cell(space = grid.cellSize)
			
			table {
				defaults().pad(5F)
				
				// Status labels
				label("Edit Mode", "infolabelstyle", game.skin).inCell.left()
				row()
				label("0 : 00", "infolabelstyle", game.skin).cell(spaceBottom = 128F).inCell.left()
				row()
				
				// Control panel
				table {
					defaults().pad(5F)
					actor(keypad)
					buttonGroup(1, 1, game.skin) {
						defaults().size(buttonSize, buttonSize)
						defaults().pad(4F)
						textButton("#", "textbuttonstyle2", game.skin) {
							isChecked = true
							onClick { setKeypad(digitKeypad) }
						}
						row()
						textButton("#", "textbuttonstyle", game.skin) {
							label.setAlignment(Align.topLeft)
							padLeft(5F)
							onClick { setKeypad(cornerMarkKeypad) }
						}
						row()
						textButton("#", "textbuttonstyle", game.skin) {
							onClick { setKeypad(centerMarkKeypad) }
						}
						row()
						textButton("", "textbuttonstyle", game.skin)
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
	}
	
	private fun setKeypad(keypad: Table)
	{
		(this.keypad.cells[0] as Cell<*>).setActor(keypad)
		inputMode = when (keypad)
		{
			cornerMarkKeypad -> InputMode.CORNER_MARK
			centerMarkKeypad -> InputMode.CENTER_MARK
			else -> InputMode.DIGIT
		}
	}
	
	override fun clearScreen()
	{
		val backgroundColor: Color = game.skin["backgroundcolor"]
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
	}
	
	enum class InputMode
	{
		DIGIT, CORNER_MARK, CENTER_MARK, COLOR
	}
}
