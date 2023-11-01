package misterbander.commitsudoku.scene2d.actions

import java.io.Serializable

abstract class ModifyCellAction
{
	abstract val dataObject: HashMap<String, Serializable>

	abstract fun run(inverse: Boolean = false)

	enum class Type
	{
		DIGIT, CORNER, CENTER, COLOR
	}
}
