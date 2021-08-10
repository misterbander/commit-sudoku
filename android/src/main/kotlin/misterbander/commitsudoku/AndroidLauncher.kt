package misterbander.commitsudoku

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
		initialize(CommitSudoku(), configuration)
	}
}