package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.txt
import ktx.scene2d.*
import ktx.style.*
import misterbander.commitsudoku.BLUE_BUTTON_STYLE
import misterbander.commitsudoku.CHECKABLE_TEXT_BUTTON_LARGE_STYLE
import misterbander.commitsudoku.CHECKABLE_TEXT_BUTTON_STYLE
import misterbander.commitsudoku.CLEAR_BUTTON_STYLE
import misterbander.commitsudoku.COLOR_BUTTON_STYLE
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.CommitSudokuScreen
import misterbander.commitsudoku.DARK_BLUE_BUTTON_STYLE
import misterbander.commitsudoku.DARK_MODE_BUTTON_STYLE
import misterbander.commitsudoku.DELETE_BUTTON_STYLE
import misterbander.commitsudoku.EDIT_BUTTON_STYLE
import misterbander.commitsudoku.GRAY_BUTTON_STYLE
import misterbander.commitsudoku.GREEN_BUTTON_STYLE
import misterbander.commitsudoku.NEW_BUTTON_STYLE
import misterbander.commitsudoku.ORANGE_BUTTON_STYLE
import misterbander.commitsudoku.PINK_BUTTON_STYLE
import misterbander.commitsudoku.PLAY_BUTTON_STYLE
import misterbander.commitsudoku.PURPLE_BUTTON_STYLE
import misterbander.commitsudoku.REDO_BUTTON_STYLE
import misterbander.commitsudoku.RED_BUTTON_STYLE
import misterbander.commitsudoku.SYNC_BUTTON_STYLE
import misterbander.commitsudoku.TEXT_BUTTON_LARGE_STYLE
import misterbander.commitsudoku.UNDO_BUTTON_STYLE
import misterbander.commitsudoku.YELLOW_BUTTON_STYLE
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class SudokuPanel(val screen: CommitSudokuScreen) : Table(Scene2DSkin.defaultSkin), PersistentState
{
	val game: CommitSudoku
		get() = screen.game
	
	val grid = SudokuGrid(this)
	
	private val buttonSize = 80F
	private val modeLabel = scene2d.label("Edit Mode")
	val timerLabel = scene2d.label("0 : 00").apply { isVisible = false }
	private val editButton = scene2d.imageButton(NEW_BUTTON_STYLE).apply {
		onChange {
			if (!isEditing)
				isEditing = true
			else
				grid.reset()
		}
	}
	val playButton = scene2d.imageButton(PLAY_BUTTON_STYLE).apply {
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
	val undoButton = scene2d.imageButton(UNDO_BUTTON_STYLE).apply {
		isDisabled = true
		onChange { grid.actionController.undo() }
	}
	val redoButton = scene2d.imageButton(REDO_BUTTON_STYLE).apply {
		isDisabled = true
		onChange { grid.actionController.redo() }
	}
	val digitKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 8, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 9, TEXT_BUTTON_LARGE_STYLE))
		row()
		actor(ModifyCellButton(grid, 4, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 5, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 6, TEXT_BUTTON_LARGE_STYLE))
		row()
		actor(ModifyCellButton(grid, 1, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 2, TEXT_BUTTON_LARGE_STYLE))
		actor(ModifyCellButton(grid, 3, TEXT_BUTTON_LARGE_STYLE))
	}
	val cornerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 8, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 9, alignLabelTopLeft = true))
		row()
		actor(ModifyCellButton(grid, 4, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 5, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 6, alignLabelTopLeft = true))
		row()
		actor(ModifyCellButton(grid, 1, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 2, alignLabelTopLeft = true))
		actor(ModifyCellButton(grid, 3, alignLabelTopLeft = true))
		row()
	}
	val centerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyCellButton(grid, 7))
		actor(ModifyCellButton(grid, 8))
		actor(ModifyCellButton(grid, 9))
		row()
		actor(ModifyCellButton(grid, 4))
		actor(ModifyCellButton(grid, 5))
		actor(ModifyCellButton(grid, 6))
		row()
		actor(ModifyCellButton(grid, 1))
		actor(ModifyCellButton(grid, 2))
		actor(ModifyCellButton(grid, 3))
	}
	val colorKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(ModifyColorButton(grid, 1, RED_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 2, ORANGE_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 3, YELLOW_BUTTON_STYLE))
		row()
		actor(ModifyColorButton(grid, 4, GREEN_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 5, BLUE_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 6, DARK_BLUE_BUTTON_STYLE))
		row()
		actor(ModifyColorButton(grid, 7, PURPLE_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 8, PINK_BUTTON_STYLE))
		actor(ModifyColorButton(grid, 9, GRAY_BUTTON_STYLE))
	}
	val zeroButton = ModifyCellButton(grid, 0, TEXT_BUTTON_LARGE_STYLE)
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
			imageButton(DELETE_BUTTON_STYLE) { onChange { grid.typedDigit(-1, true) } }
			actor(undoRedoTray).cell(width = buttonSize*2 + 6, height = buttonSize)
		}
	}
	private val keypadButtonGroup = scene2d.buttonGroup(1, 1) {
		defaults().size(buttonSize, buttonSize).pad(4F)
		textButton("#", CHECKABLE_TEXT_BUTTON_LARGE_STYLE) {
			isChecked = true
			onChange { keypadInputMode = InputMode.DIGIT }
		}
		row()
		textButton("#", CHECKABLE_TEXT_BUTTON_STYLE) {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { keypadInputMode = InputMode.CORNER_MARK }
		}
		row()
		textButton("#", CHECKABLE_TEXT_BUTTON_STYLE) { onChange { keypadInputMode = InputMode.CENTER_MARK } }
		row()
		imageButton(COLOR_BUTTON_STYLE) { onChange { keypadInputMode = InputMode.COLOR } }
	}
	
	private val timer = SudokuTimer(this)
	
	private var isEditing = true
		set(value)
		{
			field = value
			modeLabel.txt = if (value) "Edit Mode" else "Playing"
			timerLabel.isVisible = !value
			editButton.style =
				if (value) Scene2DSkin.defaultSkin[NEW_BUTTON_STYLE] else Scene2DSkin.defaultSkin[EDIT_BUTTON_STYLE]
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
			if (value)
				keypadButtonGroup.buttonGroup.apply {
					buttons[0].isChecked = true
					buttons.forEach { it.isDisabled = true }
				}
			else
				keypadButtonGroup.buttonGroup.buttons.forEach { it.isDisabled = false }
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
				imageButton(CLEAR_BUTTON_STYLE) { onChange { grid.clearGrid() } }
				imageButton(DARK_MODE_BUTTON_STYLE) {
					isChecked = screen.isDarkMode
					onChange {
						Scene2DSkin.defaultSkin = if (isChecked) screen.darkSkin else screen.lightSkin
					}
				}
				imageButton(SYNC_BUTTON_STYLE) {
					onChange {
						screen.syncDialog.show()
					}
				}
			}.inCell.left()
			row()
			table {
				defaults().pad(5F)
				actor(keypad)
				actor(keypadButtonGroup)
				
				// Set to touchable so accidentally clicking on the gaps between the buttons does
				// not unselect grid cells for improved user experience
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
		grid.constraintsChecker.check()
		Gdx.graphics.requestRendering()
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
