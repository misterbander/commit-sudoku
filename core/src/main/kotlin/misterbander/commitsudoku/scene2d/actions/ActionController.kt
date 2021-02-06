package misterbander.commitsudoku.scene2d.actions

import ktx.collections.GdxArray

class ActionController
{
	val actionHistory: GdxArray<GdxArray<ModifyCellAction>> = GdxArray()
	var undidActionCount: Int = 0
}
