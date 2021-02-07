package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick

class ModifyCellButton(grid: SudokuGrid, digit: Int, skin: Skin, styleName: String, alignLabelTopLeft: Boolean = false)
	: TextButton(digit.toString(), skin, styleName)
{
	init
	{
		if (alignLabelTopLeft)
		{
			label.setAlignment(Align.topLeft)
			padLeft(5F)
		}
		onClick {
			grid.typedDigit(digit)
			isChecked = false
		}
	}
}