package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.*
import ktx.style.get
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class SudokuPanel(val screen: CommitSudokuScreen) : Table(screen.game.skin), PersistentState
{
	private val game = screen.game
	
	val grid = SudokuGrid(this)
	
	private val buttonSize = 80F
	val modeLabel = Label("Edit Mode", game.skin, "infolabelstyle")
	val timerLabel = Label("0 : 00", game.skin, "infolabelstyle").apply { isVisible = false }
	private val editButton = ImageButton(game.skin, "newbuttonstyle").apply {
		onChange {
			if (!isEditing)
				isEditing = true
			else
				grid.reset()
		}
	}
	val playButton = ImageButton(game.skin, "playbuttonstyle").apply {
		onChange {
			if (isEditing)
				isEditing = false
			else
			{
				timer.isRunning = !timer.isRunning
				if (!isFinished)
					modeLabel.txt = if (timer.isRunning) "Playing" else "Paused"
			}
		}
	}
	val undoButton = ImageButton(game.skin, "undobuttonstyle").apply {
		isDisabled = true
		onChange { grid.actionController.undo() }
	}
	val redoButton = ImageButton(game.skin, "redobuttonstyle").apply {
		isDisabled = true
		onChange { grid.actionController.redo() }
	}
	val digitKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle2"))
		row()
		actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle2"))
		row()
		actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle2"))
		actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle2"))
	}
	val cornerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle", true))
		actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle", true))
		actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle", true))
		row()
		actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle", true))
		actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle", true))
		actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle", true))
		row()
		actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle",true))
		actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle",true))
		actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle",true))
		row()
	}
	val centerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 8, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 9, game.skin, "textbuttonstyle"))
		row()
		actor(ModifyCellButton(grid, 4, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 5, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 6, game.skin, "textbuttonstyle"))
		row()
		actor(ModifyCellButton(grid, 1, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 2, game.skin, "textbuttonstyle"))
		actor(ModifyCellButton(grid, 3, game.skin, "textbuttonstyle"))
	}
	val colorKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
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
			onChange {
				grid.typedDigit(9, true)
				isChecked = false
			}
		}
	}
	val zeroButton = ModifyCellButton(grid, 0, game.skin, "textbuttonstyle2")
	val undoRedoTray = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(undoButton)
		actor(redoButton)
	}
	private val keypad = scene2d.table {
		defaults().pad(3F)
		actor(digitKeypad)
		row()
		table {
			defaults().size(buttonSize, buttonSize).pad(3F)
			imageButton("deletebuttonstyle", game.skin) {
				onChange { grid.typedDigit(-1, true) }
			}
			actor(undoRedoTray).cell(width = buttonSize*2 + 6, height = buttonSize)
		}
	}
	val keypadButtonGroup = scene2d.buttonGroup(1, 1, game.skin) {
		defaults().size(buttonSize, buttonSize).pad(4F)
		textButton("#", "checkabletextbuttonstyle2", game.skin) {
			isChecked = true
			onChange { keypadInputMode = InputMode.DIGIT }
		}
		row()
		textButton("#", "checkabletextbuttonstyle", game.skin) {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { keypadInputMode = InputMode.CORNER_MARK }
		}
		row()
		textButton("#", "checkabletextbuttonstyle", game.skin) {
			onChange { keypadInputMode = InputMode.CENTER_MARK }
		}
		row()
		imageButton("colorbuttonstyle", game.skin) {
			onChange { keypadInputMode = InputMode.COLOR }
		}
	}
	
	private val timer = SudokuTimer(this)
	
	private var isEditing = true
		set(value)
		{
			field = value
			modeLabel.txt = if (value) "Edit Mode" else "Playing"
			timerLabel.isVisible = !value
			editButton.style = if (value) game.skin["newbuttonstyle"] else game.skin["editbuttonstyle"]
			grid.setGivens(!value)
			timer.isRunning = !value
			grid.actionController.clearHistory()
			if (value)
			{
				timer.reset()
				isFinished = false
			}
		}
	var isFinished = false
		set(value)
		{
			field = value
			if (value)
			{
				modeLabel.txt = "Completed!"
				timer.isRunning = false
				Gdx.graphics.isContinuousRendering = true
			}
		}
	var keypadInputMode = InputMode.DIGIT
		private set(value)
		{
			field = value
			val keypad = when (value)
			{
				InputMode.CORNER_MARK -> cornerMarkKeypad
				InputMode.CENTER_MARK -> centerMarkKeypad
				InputMode.COLOR -> colorKeypad
				else -> digitKeypad
			}
			(this.keypad.cells[0] as Cell<*>).setActor(keypad)
		}
	var showZero = false
		set(value)
		{
			field = value
			((keypad.cells[1].actor as Table).cells[1] as Cell<*>).setActor(if (value) zeroButton else undoRedoTray)
		}
	
	init
	{
		add(grid).space(grid.cellSize)
		add(scene2d.table {
			defaults().pad(5F)
			actor(modeLabel).inCell.left()
			row()
			actor(timerLabel).cell(spaceBottom = 64F).inCell.left()
			row()
			table {
				defaults().pad(10F).size(54F, 54F)
				actor(editButton)
				actor(playButton)
				imageButton("clearbuttonstyle", game.skin) { onChange { grid.clearGrid() } }
				imageButton("darkmodebuttonstyle", game.skin) {
					isChecked = game.isDarkMode
					onChange {
						game.skin = if (isChecked) game.darkSkin else game.lightSkin
						screen.updateStyles()
					}
				}
				imageButton("connectbuttonstyle", game.skin) {
					onChange {
						screen.connectWindow.show()
					}
				}
			}.inCell.left()
			row()
			table {
				defaults().pad(5F)
				actor(keypad)
				actor(keypadButtonGroup)
				
				/* Set to touchable so accidentally clicking on the gaps between the buttons does
				   not unselect grid cells for improved user experience */
				touchable = Touchable.enabled
			}
		}).top().left()
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		isEditing = mapper["isEditing"] ?: isEditing
		isFinished = mapper["isFinished"] ?: isFinished
		timer.readState(mapper)
		grid.readState(mapper)
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["isEditing"] = isEditing
		mapper["isFinished"] = isFinished
		timer.writeState(mapper)
		grid.writeState(mapper)
	}

	enum class InputMode
	{
		DIGIT, CORNER_MARK, CENTER_MARK, COLOR
	}
}
