package misterbander.commitsudoku.android

import android.content.res.Configuration
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import misterbander.commitsudoku.CommitSudoku
import misterbander.commitsudoku.DarkModeSettingsProvider
import misterbander.gframework.GScreen
import misterbander.gframework.scene2d.KeyboardHeightObserver


/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication(), KeyboardHeightObserver
{
	private lateinit var commitSudoku: CommitSudoku
	private lateinit var keyboardHeightProvider: KeyboardHeightProvider
	
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		val args = intent.extras?.getString("args")
		val darkModeSettingsProvider = object : DarkModeSettingsProvider
		{
			override val defaultDarkModeEnabled
				get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		}
		commitSudoku = CommitSudoku(args?.let { arrayOf(it) } ?: emptyArray(), darkModeSettingsProvider)
		initialize(commitSudoku, AndroidApplicationConfiguration())
		
		keyboardHeightProvider = KeyboardHeightProvider(this)
		
		// Make sure to start the keyboard height provider after the onResume
		// of this activity. This is because a popup window must be initialised
		// and attached to the activity root view
		val rootView = window.decorView.rootView
		rootView.post { keyboardHeightProvider.start() }
	}
	
	override fun onPause()
	{
		super.onPause()
		keyboardHeightProvider.setKeyboardHeightObserver(null)
	}
	
	override fun onResume()
	{
		super.onResume()
		keyboardHeightProvider.setKeyboardHeightObserver(this)
	}
	
	override fun onDestroy()
	{
		super.onDestroy()
		keyboardHeightProvider.close()
	}
	
	override fun onKeyboardHeightChanged(height: Int, orientation: Int)
	{
		val gScreen = commitSudoku.shownScreen as? GScreen<*> ?: return
		for (observer in gScreen.keyboardHeightObservers)
			observer.onKeyboardHeightChanged(height, orientation)
	}
}
