package misterbander.commitsudoku

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration


/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication()
{
	private var prevWidth = 0
	private var prevHeight = 0
	
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		val configuration = AndroidApplicationConfiguration()
		val darkModeSettingsProvider = object : DarkModeSettingsProvider
		{
			override val defaultDarkModeEnabled
				get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		}
		val commitSudoku = CommitSudoku(darkModeSettingsProvider)
		initialize(commitSudoku, configuration)
		
		val rootView = window.decorView.rootView
		val rect = Rect()
		rootView.getWindowVisibleDisplayFrame(rect)
		prevWidth = rect.width()
		prevHeight = rect.height()
		
		rootView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
			val rect1 = Rect()
			rootView.getWindowVisibleDisplayFrame(rect1)
			if (!(prevWidth == rect1.width() && prevHeight == rect1.height()))
			{
				prevWidth = rect1.width()
				prevHeight = rect1.height()
				commitSudoku.notifyLayoutSizeChange(prevHeight)
			}
		}
	}
}