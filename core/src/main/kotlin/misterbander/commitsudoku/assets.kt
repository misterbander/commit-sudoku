package misterbander.commitsudoku

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.assets.assetDescriptor

object TextureAtlases
{
	val gui = assetDescriptor<TextureAtlas>("textures/gui.atlas")
}

object Fonts
{
	val segoeUi = assetDescriptor<FreeTypeFontGenerator>("fonts/segoe_ui.ttf")
}
