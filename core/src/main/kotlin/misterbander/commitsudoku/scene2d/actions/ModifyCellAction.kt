package misterbander.commitsudoku.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction

abstract class ModifyCellAction : RunnableAction()
{
	var inverse: Boolean = false
}
