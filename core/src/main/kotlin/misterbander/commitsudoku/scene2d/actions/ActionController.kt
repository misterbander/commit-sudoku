package misterbander.commitsudoku.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import ktx.collections.plusAssign
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper

class ActionController(private val grid: SudokuGrid): PersistentState
{
	private val actionHistory: GdxArray<GdxArray<ModifyCellAction>> = GdxArray()
	private var undidActionCount: Int = 0
	
	fun addActions(actions: GdxArray<ModifyCellAction>)
	{
		if (undidActionCount > 0)
		{
			actionHistory.removeRange(actionHistory.size - undidActionCount, actionHistory.size - 1)
			undidActionCount = 0
		}
		actionHistory += actions
		updateUndoRedoButtons()
	}
	
	fun undo()
	{
		if (undidActionCount == actionHistory.size)
			return
		undidActionCount++
		val lastAction: GdxArray<ModifyCellAction> = actionHistory[actionHistory.size - undidActionCount]
		lastAction.forEach { action ->
			action.inverse = true
			action.restart()
			grid += action
		}
		updateUndoRedoButtons()
		grid += Actions.run { grid.constraintsChecker.check() }
	}
	
	fun redo()
	{
		if (undidActionCount == 0)
			return
		val nextAction: GdxArray<ModifyCellAction> = actionHistory[actionHistory.size - undidActionCount]
		undidActionCount--
		nextAction.forEach { action ->
			action.inverse = false
			action.restart()
			grid += action
		}
		updateUndoRedoButtons()
		grid += Actions.run { grid.constraintsChecker.check() }
	}
	
	fun clearHistory()
	{
		actionHistory.clear()
		undidActionCount = 0
		updateUndoRedoButtons()
	}
	
	private fun updateUndoRedoButtons()
	{
		grid.panel.undoButton.isDisabled = undidActionCount == actionHistory.size
		grid.panel.redoButton.isDisabled = undidActionCount == 0
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		val actionHistoryStrs: Array<Array<String>>? = mapper["actionHistory"]
		actionHistoryStrs?.forEach { actionStrGroup ->
			val subActionHistory: GdxArray<ModifyCellAction> = GdxArray()
			actionStrGroup.forEach { actionStr ->
				val tokens = actionStr.split(" ".toRegex()).toTypedArray()
				val cell = grid.cells[tokens[1][1].toString().toInt()][tokens[1][3].toString().toInt()]
				when (tokens[0])
				{
					"digit" -> subActionHistory += ModifyDigitAction(cell, tokens[2].toInt(), tokens[3].toInt())
					"color" -> subActionHistory += ModifyColorAction(cell, tokens[2].toInt(), tokens[3].toInt())
					"corner" -> subActionHistory += ModifyMarkAction(cell, ModifyMarkAction.Type.CORNER, tokens[2].toInt(), tokens[3].toBoolean(), tokens[4].toBoolean())
					"center" -> subActionHistory += ModifyMarkAction(cell, ModifyMarkAction.Type.CENTER, tokens[2].toInt(), tokens[3].toBoolean(), tokens[4].toBoolean())
				}
			}
			actionHistory += subActionHistory
		}
		undidActionCount = mapper["undidActionCount"] ?: undidActionCount
		updateUndoRedoButtons()
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		val actionHistoryStr = Array(actionHistory.size) { i ->
			Array(actionHistory[i].size) { j -> actionHistory[i][j].toString() }
		}
		mapper["actionHistory"] = actionHistoryStr
		mapper["undidActionCount"] = undidActionCount
	}
}
