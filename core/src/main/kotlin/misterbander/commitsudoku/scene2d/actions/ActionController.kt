package misterbander.commitsudoku.scene2d.actions

import ktx.collections.*
import misterbander.commitsudoku.scene2d.SudokuGrid
import misterbander.gframework.util.PersistentStateMapper
import java.io.Serializable

class ActionController
{
	private val history = GdxArray<Array<ModifyCellAction>>()
	private var undidActionCount: Int = 0
	private val onChangeListeners = GdxArray<(Int, Int) -> Unit>()

	fun addActions(actions: Array<ModifyCellAction>)
	{
		if (undidActionCount > 0)
		{
			history.removeRange(history.size - undidActionCount, history.size - 1)
			undidActionCount = 0
		}
		history += actions
		for (i in 0 until onChangeListeners.size)
			onChangeListeners[i](history.size, undidActionCount)
	}

	fun undo()
	{
		if (undidActionCount == history.size)
			return
		undidActionCount++
		val lastActions: Array<ModifyCellAction> = history[history.size - undidActionCount]
		for (action in lastActions)
			action.run(true)
		for (i in 0 until onChangeListeners.size)
			onChangeListeners[i](history.size, undidActionCount)
	}

	fun redo()
	{
		if (undidActionCount == 0)
			return
		val nextActions: Array<ModifyCellAction> = history[history.size - undidActionCount]
		undidActionCount--
		for (action: ModifyCellAction in nextActions)
			action.run()
		for (i in 0 until onChangeListeners.size)
			onChangeListeners[i](history.size, undidActionCount)
	}

	fun clearHistory()
	{
		history.clear()
		undidActionCount = 0
		for (i in 0 until onChangeListeners.size)
			onChangeListeners[i](history.size, undidActionCount)
	}

	fun onChange(callback: (historySize: Int, undidActionCount: Int) -> Unit)
	{
		onChangeListeners += callback
	}

	fun readState(grid: SudokuGrid, mapper: PersistentStateMapper)
	{
		val actionHistoryDataObjects: Array<Array<HashMap<String, Serializable>>>? = mapper["actionHistory"]
		if (actionHistoryDataObjects != null)
		{
			for (dataObjectGroup in actionHistoryDataObjects)
			{
				val actionHistoryGroup = GdxArray<ModifyCellAction>()
				for (dataObject in dataObjectGroup)
				{
					val type = dataObject["type"] as ModifyCellAction.Type
					val row = dataObject["row"] as Int
					val col = dataObject["col"] as Int
					val cell = grid.cells[row][col]
					when (type)
					{
						ModifyCellAction.Type.DIGIT ->
							actionHistoryGroup += ModifyDigitAction(
								cell,
								dataObject["from"] as Int,
								dataObject["to"] as Int
							)
						ModifyCellAction.Type.CORNER ->
							actionHistoryGroup += ModifyMarkAction(
								cell,
								ModifyCellAction.Type.CORNER,
								dataObject["digit"] as Int,
								dataObject["from"] as Boolean,
								dataObject["to"] as Boolean
							)
						ModifyCellAction.Type.CENTER ->
							actionHistoryGroup += ModifyMarkAction(
								cell,
								ModifyCellAction.Type.CENTER,
								dataObject["digit"] as Int,
								dataObject["from"] as Boolean,
								dataObject["to"] as Boolean
							)
						ModifyCellAction.Type.COLOR ->
							actionHistoryGroup += ModifyColorAction(
								cell,
								dataObject["from"] as Int,
								dataObject["to"] as Int
							)
					}
				}
				history += actionHistoryGroup.toArray(ModifyCellAction::class.java)
			}
		}
		undidActionCount = mapper["undidActionCount"] ?: undidActionCount
		for (i in 0 until onChangeListeners.size)
			onChangeListeners[i](history.size, undidActionCount)
	}

	fun writeState(mapper: PersistentStateMapper)
	{
		val actionHistoryDataObjects = Array(history.size) { i ->
			Array(history[i].size) { j -> history[i][j].dataObject }
		}
		mapper["actionHistory"] = actionHistoryDataObjects
		mapper["undidActionCount"] = undidActionCount
	}
}
