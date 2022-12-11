package misterbander.commitsudoku.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.DarkModeSettingsProvider
import misterbander.gframework.GFrameworkDelegator
import java.time.LocalTime

/** Launches the desktop (LWJGL3) application. */
fun main(args: Array<String>)
{
	Lwjgl3Application(
		GFrameworkDelegator {
			CommitSudoku(args, object : DarkModeSettingsProvider
			{
				override val defaultDarkModeEnabled = LocalTime.now().let { it.hour >= 22 || it.hour < 8 }
			})
		},
		Lwjgl3ApplicationConfiguration().apply {
			setTitle("Commit Sudoku")
			setWindowedMode(1280, 720)
			setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
			setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)
		}
	)
}
