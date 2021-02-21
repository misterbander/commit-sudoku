package misterbander.commitsudoku.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import java.io.Serializable

abstract class ModifyCellAction : RunnableAction()
{
	var inverse: Boolean = false
	abstract val dataObject: HashMap<String, Serializable>
	
	enum class Type
	{
		DIGIT, CORNER, CENTER, COLOR
	}
}
