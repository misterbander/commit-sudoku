package misterbander.commitsudoku.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import ktx.actors.plusAssign
import ktx.collections.*
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable

class ActionController(private val grid: SudokuGrid) : PersistentState
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
		for (action: ModifyCellAction in lastAction)
		{
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
		for (action: ModifyCellAction in nextAction)
		{
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
		val actionHistoryDataObjects: Array<Array<HashMap<String, Serializable>>>? = mapper["actionHistory"]
		actionHistoryDataObjects?.forEach { dataObjectGroup ->
			val actionHistoryGroup: GdxArray<ModifyCellAction> = GdxArray()
			for (dataObject in dataObjectGroup)
			{
				val type = dataObject["type"] as ModifyCellAction.Type
				val i = dataObject["i"] as Int
				val j = dataObject["j"] as Int
				val cell = grid.cells[i][j]
				when (type)
				{
					ModifyCellAction.Type.DIGIT ->
						actionHistoryGroup += ModifyDigitAction(cell, dataObject["from"] as Int, dataObject["to"] as Int)
					ModifyCellAction.Type.CORNER ->
						actionHistoryGroup += ModifyMarkAction(cell, ModifyCellAction.Type.CORNER, dataObject["digit"] as Int, dataObject["from"] as Boolean, dataObject["to"] as Boolean)
					ModifyCellAction.Type.CENTER ->
						actionHistoryGroup += ModifyMarkAction(cell, ModifyCellAction.Type.CENTER, dataObject["digit"] as Int, dataObject["from"] as Boolean, dataObject["to"] as Boolean)
					ModifyCellAction.Type.COLOR ->
						actionHistoryGroup += ModifyColorAction(cell, dataObject["from"] as Int, dataObject["to"] as Int)
				}
			}
			actionHistory += actionHistoryGroup
		}
		undidActionCount = mapper["undidActionCount"] ?: undidActionCount
		updateUndoRedoButtons()
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		val actionHistoryDataObjects = Array(actionHistory.size) { i ->
			Array(actionHistory[i].size) { j -> actionHistory[i][j].dataObject }
		}
		mapper["actionHistory"] = actionHistoryDataObjects
		mapper["undidActionCount"] = undidActionCount
	}
}
