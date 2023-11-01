package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
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
import misterbander.commitsudoku.scene2d.actions.ActionController
import misterbander.gframework.scene2d.scene2d
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class SidePanel(
	private val screen: CommitSudokuScreen,
	private val actionController: ActionController,
	isDarkMode: Boolean
) : Table(Scene2DSkin.defaultSkin), PersistentState
{
	private val grid: SudokuGrid
		get() = screen.grid

	private val modeLabel = scene2d.label("Edit Mode")
	private val timerLabel = scene2d.label("0 : 00") { isVisible = false }
	private val editButton = scene2d.imageButton(NEW_BUTTON_STYLE) {
		onChange {
			if (!isEditing)
				isEditing = true
			else
				grid.reset()
		}
	}
	private val playButton: ImageButton = scene2d.imageButton(PLAY_BUTTON_STYLE) {
		onChange {
			if (isEditing)
				isEditing = false
			else
			{
				timer.isRunning = !timer.isRunning
				if (!screen.isFinished)
					modeLabel.txt = if (timer.isRunning) "Playing" else "Paused"
			}
		}
	}
	private val buttonSize = 80F
	private val undoButton = scene2d.imageButton(UNDO_BUTTON_STYLE) {
		isDisabled = true
		onChange { actionController.undo() }
	}
	private val redoButton = scene2d.imageButton(REDO_BUTTON_STYLE) {
		isDisabled = true
		onChange { actionController.redo() }
	}
	private val undoRedoTray = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		actor(undoButton)
		actor(redoButton)
	}
	private val digitKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		textButton("7", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(7, InputMode.DIGIT) } }
		textButton("8", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(8, InputMode.DIGIT) } }
		textButton("9", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(9, InputMode.DIGIT) } }
		row()
		textButton("4", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(4, InputMode.DIGIT) } }
		textButton("5", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(5, InputMode.DIGIT) } }
		textButton("6", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(6, InputMode.DIGIT) } }
		row()
		textButton("1", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(1, InputMode.DIGIT) } }
		textButton("2", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(2, InputMode.DIGIT) } }
		textButton("3", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(3, InputMode.DIGIT) } }
	}
	private val cornerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		textButton("7") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(7, InputMode.CORNER_MARK) }
		}
		textButton("8") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(8, InputMode.CORNER_MARK) }
		}
		textButton("9") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(9, InputMode.CORNER_MARK) }
		}
		row()
		textButton("4") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(4, InputMode.CORNER_MARK) }
		}
		textButton("5") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(5, InputMode.CORNER_MARK) }
		}
		textButton("6") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(6, InputMode.CORNER_MARK) }
		}
		row()
		textButton("1") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(1, InputMode.CORNER_MARK) }
		}
		textButton("2") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(2, InputMode.CORNER_MARK) }
		}
		textButton("3") {
			label.setAlignment(Align.topLeft)
			padLeft(5F)
			onChange { grid.typedDigit(3, InputMode.CORNER_MARK) }
		}
	}
	private val centerMarkKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		textButton("7") { onChange { grid.typedDigit(7, InputMode.CENTER_MARK) } }
		textButton("8") { onChange { grid.typedDigit(8, InputMode.CENTER_MARK) } }
		textButton("9") { onChange { grid.typedDigit(9, InputMode.CENTER_MARK) } }
		row()
		textButton("4") { onChange { grid.typedDigit(4, InputMode.CENTER_MARK) } }
		textButton("5") { onChange { grid.typedDigit(5, InputMode.CENTER_MARK) } }
		textButton("6") { onChange { grid.typedDigit(6, InputMode.CENTER_MARK) } }
		row()
		textButton("1") { onChange { grid.typedDigit(1, InputMode.CENTER_MARK) } }
		textButton("2") { onChange { grid.typedDigit(2, InputMode.CENTER_MARK) } }
		textButton("3") { onChange { grid.typedDigit(3, InputMode.CENTER_MARK) } }
	}
	private val colorKeypad = scene2d.table {
		defaults().size(buttonSize, buttonSize).pad(3F)
		imageButton(RED_BUTTON_STYLE) { onChange { grid.typedDigit(1, InputMode.COLOR) } }
		imageButton(ORANGE_BUTTON_STYLE) { onChange { grid.typedDigit(2, InputMode.COLOR) } }
		imageButton(YELLOW_BUTTON_STYLE) { onChange { grid.typedDigit(3, InputMode.COLOR) } }
		row()
		imageButton(GREEN_BUTTON_STYLE) { onChange { grid.typedDigit(4, InputMode.COLOR) } }
		imageButton(BLUE_BUTTON_STYLE) { onChange { grid.typedDigit(5, InputMode.COLOR) } }
		imageButton(DARK_BLUE_BUTTON_STYLE) { onChange { grid.typedDigit(6, InputMode.COLOR) } }
		row()
		imageButton(PURPLE_BUTTON_STYLE) { onChange { grid.typedDigit(7, InputMode.COLOR) } }
		imageButton(PINK_BUTTON_STYLE) { onChange { grid.typedDigit(8, InputMode.COLOR) } }
		imageButton(GRAY_BUTTON_STYLE) { onChange { grid.typedDigit(9, InputMode.COLOR) } }
	}
	private val keypad = scene2d.table {
		defaults().pad(3F)
		actor(digitKeypad)
		row()
		table {
			defaults().size(buttonSize, buttonSize).pad(3F)
			imageButton(DELETE_BUTTON_STYLE) { onChange { grid.typedDigit(-1, keypadInputMode) } }
			actor(undoRedoTray).cell(width = buttonSize*2 + 6, height = buttonSize)
		}
	}
	private val zeroButton =
		scene2d.textButton("0", TEXT_BUTTON_LARGE_STYLE) { onChange { grid.typedDigit(0, keypadInputMode) } }
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

	private val timer = SudokuTimer(timerLabel, playButton)

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
			actionController.clearHistory()
			if (value)
			{
				timer.reset()
				screen.isFinished = false
			}
		}
	private var keypadInputMode = InputMode.DIGIT
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
	private var showZero = false
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
		scene2d {
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
					isChecked = isDarkMode
					onChange {
						Scene2DSkin.defaultSkin = if (isChecked) screen.darkSkin else screen.lightSkin
					}
				}
				imageButton(SYNC_BUTTON_STYLE) {
					onChange {
						screen.syncDialog.show(screen.uiStage)
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
		}

		screen.isFinishedObservable.addObserver { value ->
			if (value)
			{
				modeLabel.txt = "Completed!"
				timer.isRunning = false
				Gdx.graphics.isContinuousRendering = true
			}
		}
		grid.modifierObservable.addObserver { value ->
			showZero = if (value == grid.modifiers.cageSetter)
				grid.modifiers.cageSetter.isKillerMode
			else
				false
		}
		grid.modifiers.cageSetter.isKillerModeObservable.addObserver { value ->
			showZero = value
		}
		actionController.onChange { historySize, undidActionCount ->
			undoButton.isDisabled = undidActionCount == historySize
			redoButton.isDisabled = undidActionCount == 0
		}
	}

	fun resetTimer() = timer.reset()

	override fun readState(mapper: PersistentStateMapper)
	{
		isEditing = mapper["isEditing"] ?: isEditing
		timer.readState(mapper)
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["isEditing"] = isEditing
		timer.writeState(mapper)
	}
}
