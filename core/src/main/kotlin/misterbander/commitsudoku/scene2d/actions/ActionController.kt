package misterbander.commitsudoku.scene2d.actions

import ktx.actors.plusAssign
import ktx.collections.GdxArray
import misterbander.commitsudoku.scene2d.SudokuGrid

class ActionController(private val grid: SudokuGrid)
{
	private val actionHistory: GdxArray<GdxArray<ModifyCellAction>> = GdxArray()
	private var undidActionCount: Int = 0
	
	init
	{
		updateUndoRedoButtons()
	}
	
	fun addActions(actions: GdxArray<ModifyCellAction>)
	{
		if (undidActionCount > 0)
		{
			actionHistory.removeRange(actionHistory.size - undidActionCount, actionHistory.size - 1)
			undidActionCount = 0
		}
		actionHistory.add(actions)
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
	}
	
	private fun updateUndoRedoButtons()
	{
		println("update undoredo buttons undid=$undidActionCount actionhistorysize=${actionHistory.size}")
		grid.screen.undoButton.isDisabled = undidActionCount == actionHistory.size
		grid.screen.redoButton.isDisabled = undidActionCount == 0
	}
}
