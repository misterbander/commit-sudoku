package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Timer
import ktx.actors.txt
import ktx.scene2d.*
import ktx.style.*
import misterbander.commitsudoku.PAUSE_BUTTON_STYLE
import misterbander.commitsudoku.PLAY_BUTTON_STYLE
import misterbander.gframework.util.PersistentState
import misterbander.gframework.util.PersistentStateMapper
import misterbander.gframework.util.formatDuration

class SudokuTimer(private val timerLabel: Label, private val playButton: ImageButton) : PersistentState
{
	private var seconds = 0L
		set(value)
		{
			field = value
			timerLabel.txt = formatDuration(seconds)
		}
	private var lastSecondMillis = 0L
	private var stopTimerMillis = 0L

	private val incrementSeconds: Timer.Task = object : Timer.Task()
	{
		override fun run()
		{
			lastSecondMillis = System.currentTimeMillis()
			seconds++
		}
	}
	var isRunning = false
		set(value)
		{
			playButton.style = Scene2DSkin.defaultSkin[if (value) PAUSE_BUTTON_STYLE else PLAY_BUTTON_STYLE]
			if (value && !field)
				Timer.schedule(incrementSeconds, 1 - (stopTimerMillis - lastSecondMillis)/1000F, 1F)
			else
			{
				stopTimerMillis = System.currentTimeMillis()
				incrementSeconds.cancel()
			}
			field = value
		}

	fun reset()
	{
		seconds = 0
		lastSecondMillis = 0
		stopTimerMillis = 0
	}

	override fun readState(mapper: PersistentStateMapper)
	{
		seconds = mapper["seconds"] ?: seconds
		lastSecondMillis = mapper["lastSecondMillis"] ?: lastSecondMillis
		stopTimerMillis = mapper["stopTimerMillis"] ?: stopTimerMillis
	}

	override fun writeState(mapper: PersistentStateMapper)
	{
		mapper["seconds"] = seconds
		mapper["lastSecondMillis"] = lastSecondMillis
		mapper["stopTimerMillis"] = stopTimerMillis
	}
}
