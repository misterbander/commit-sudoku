package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.getValue
import ktx.assets.load
import ktx.freetype.generateFont
import ktx.style.add
import ktx.style.label
import ktx.style.skin
import misterbander.gframework.GFramework

class CommitSudoku : GFramework()
{
	private val generator by assetManager.load<FreeTypeFontGenerator>("fonts/segoeui.ttf")
	
	// Fonts
	val segoeui by lazy {
		generator.generateFont {
			size = 16; minFilter = Texture.TextureFilter.Linear; magFilter = Texture.TextureFilter.Linear
		}
	}
	val segoeui2 by lazy {
		generator.generateFont {
			size = 36; minFilter = Texture.TextureFilter.Linear; magFilter = Texture.TextureFilter.Linear
		}
	}
	
	// Skins
	private val lightSkin by lazy {
		skin {
			label("infolabel") {
				font = segoeui
				fontColor = Color.BLACK
			}
			add(Color.BLACK, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color.WHITE, "backgroundcolor")
		}
	}
	private val darkSkin by lazy {
		skin {
			label("infolabel") {
				font = segoeui
				fontColor = Color.WHITE
			}
			add(Color.WHITE, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color(0.15F, 0.15F, 0.15F, 1F), "backgroundcolor")
		}
	}
	lateinit var skin: Skin
	
	override fun create()
	{
		assetManager.finishLoading()
		print("Finished loading assets!")
		skin = lightSkin
		
		print(Gdx.graphics.height)
		
		addScreen(CommitSudokuScreen(this))
		setScreen<CommitSudokuScreen>()
	}
	
	override fun dispose()
	{
		segoeui.dispose()
		segoeui2.dispose()
		lightSkin.dispose()
		darkSkin.dispose()
		super.dispose()
	}
}
