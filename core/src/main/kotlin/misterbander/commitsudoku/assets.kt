package misterbander.commitsudoku

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.assets.assetDescriptor

object TextureAtlases
{
	val guiLight = assetDescriptor<TextureAtlas>("textures/gui_light.atlas")
	val guiDark = assetDescriptor<TextureAtlas>("textures/gui_dark.atlas")
}

object Fonts
{
	val notoSans = assetDescriptor<FreeTypeFontGenerator>("fonts/noto_sans.ttf")
}
