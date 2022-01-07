package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.*

class ModifyCellButton(grid: SudokuGrid, digit: Int, styleName: String, alignLabelTopLeft: Boolean = false)
	: TextButton(digit.toString(), Scene2DSkin.defaultSkin, styleName)
{
	init
	{
		if (alignLabelTopLeft)
		{
			label.setAlignment(Align.topLeft)
			padLeft(5F)
		}
		onChange { grid.typedDigit(digit, true) }
	}
}
