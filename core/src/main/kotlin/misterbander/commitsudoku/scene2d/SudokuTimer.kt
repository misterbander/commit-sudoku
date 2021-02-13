package misterbander.commitsudoku.scene2d

import ktx.actors.txt
import ktx.style.get
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.formatDuration

class SudokuTimer(private val panel: SudokuPanel) : PersistentState
{
	private var elapsedSeconds = 0F
	private var roundedElapsedSeconds = 0L
		set(value)
		{
			if (field != value)
			{
				field = value
				panel.timerLabel.txt = formatDuration(value)
			}
		}
	
	var isRunning = false
		set(value)
		{
			field = value
			panel.playButton.style = panel.screen.game.skin[if (value) "pausebuttonstyle" else "playbuttonstyle"]
		}
	
	fun update(delta: Float)
	{
		if (!isRunning)
			return
		elapsedSeconds += delta
		roundedElapsedSeconds = elapsedSeconds.toLong()
	}
	
	fun reset()
	{
		elapsedSeconds = 0F
		roundedElapsedSeconds = 0
	}
	
	override fun readState(mapper: PersistentStateMapper)
	{
		elapsedSeconds = mapper["elapsedSeconds"] ?: elapsedSeconds
	}
	
	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["elapsedSeconds"] = elapsedSeconds
	}
}
