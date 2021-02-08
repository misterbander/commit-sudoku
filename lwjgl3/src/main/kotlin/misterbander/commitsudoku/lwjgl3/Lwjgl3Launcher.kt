package misterbander.commitsudoku.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.DarkModeSettingsProvider
import java.time.LocalTime

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher
{
	@JvmStatic
	fun main(args: Array<String>)
	{
		createApplication()
	}
	
	private fun createApplication(): Lwjgl3Application
	{
		return Lwjgl3Application(CommitSudoku(DesktopDarkModeSettingsProvider()), defaultConfiguration)
	}
	
	private val defaultConfiguration: Lwjgl3ApplicationConfiguration
		get()
		{
			val configuration = Lwjgl3ApplicationConfiguration()
			configuration.setTitle("Commit Sudoku")
			configuration.setWindowedMode(1280, 720)
			configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
			configuration.setBackBufferConfig(8, 8, 8,8, 16, 0, 4)
			return configuration
		}
	
	private class DesktopDarkModeSettingsProvider : DarkModeSettingsProvider
	{
		override val defaultDarkModeEnabled: Boolean
			get()
			{
				val now: LocalTime = LocalTime.now()
				return now.hour >= 22 || now.hour < 8
			}
	}
}