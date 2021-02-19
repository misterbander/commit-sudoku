package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.onChange

class ModifyColorButton(grid: SudokuGrid, digit: Int, skin: Skin, styleName: String)
	: ImageButton(skin, styleName)
{
	init
	{
		onChange {
			grid.typedDigit(digit, true)
			isChecked = false
		}
	}
}
