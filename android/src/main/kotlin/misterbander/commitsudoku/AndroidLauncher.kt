package misterbander.commitsudoku

import android.content.res.Configuration
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		val configuration = AndroidApplicationConfiguration()
		val darkModeSettingsProvider = object : DarkModeSettingsProvider
		{
			override val defaultDarkModeEnabled: Boolean
				get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		}
		initialize(CommitSudoku(darkModeSettingsProvider), configuration)
	}
}