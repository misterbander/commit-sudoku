package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import ktx.actors.onChange
import ktx.scene2d.*

class ModifyColorButton(grid: SudokuGrid, digit: Int, styleName: String)
	: ImageButton(Scene2DSkin.defaultSkin, styleName)
{
	init
	{
		onChange { grid.typedDigit(digit, true) }
	}
}
