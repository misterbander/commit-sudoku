package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.txt
import misterbander.gframework.util.formatDuration

class SudokuTimer(private val timerLabel: Label)
{
	var isRunning = false
	private var elapsedSeconds = 0F
	private var roundedElapsedSeconds = 0L
	
	fun update(delta: Float)
	{
		if (!isRunning)
			return
		elapsedSeconds += delta
		val currentRoundedElapsedSeconds = elapsedSeconds.toLong()
		if (roundedElapsedSeconds != currentRoundedElapsedSeconds)
		{
			roundedElapsedSeconds = currentRoundedElapsedSeconds
			timerLabel.txt = formatDuration(currentRoundedElapsedSeconds)
		}
	}
}
