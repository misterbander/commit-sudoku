package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.getValue
import ktx.assets.load
import ktx.async.KtxAsync
import ktx.freetype.generateFont
import ktx.style.add
import ktx.style.color
import ktx.style.label
import ktx.style.skin
import misterbander.gframework.GFramework

class CommitSudoku : GFramework()
{
	// Fonts
	private val generator by assetManager.load<FreeTypeFontGenerator>("fonts/segoeui.ttf")
	val segoeui by lazy {
		generator.generateFont {
			size = 16; minFilter = Texture.TextureFilter.Linear; magFilter = Texture.TextureFilter.Linear
		}
	}
	val segoeui2 by lazy {
		generator.generateFont {
			size = 32; minFilter = Texture.TextureFilter.Linear; magFilter = Texture.TextureFilter.Linear
		}
	}
	
	// Skins
	private val lightSkin by lazy {
		skin {
			label("infolabel") { font = segoeui; fontColor = Color.BLACK }
			add(Color.BLACK, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color.WHITE, "backgroundcolor")
			color("nongivencolor", 0F, 0.858824F, 0.082353F)
			color("markcolor", 0.5F, 0.572549F, 1F)
			color("selectedcolor", 1F, 0.949019F, 0.5F, 0.470588F)
		}
	}
	private val darkSkin by lazy {
		skin {
			label("infolabel") { font = segoeui; fontColor = Color.WHITE }
			add(Color.WHITE, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			color("backgroundcolor", 0.15F, 0.15F, 0.15F, 1F)
			color("nongivencolor", 0F, 0.858824F, 0.082353F)
			color("markcolor", 0.5F, 0.572549F, 1F)
			color("selectedcolor", 1F, 0.949019F, 0.5F, 0.470588F)
		}
	}
	lateinit var skin: Skin
	
	override fun create()
	{
		KtxAsync.initiate()
		
		assetManager.finishLoading()
		println("Finished loading assets!")
		skin = lightSkin
		
		println("Resolution = ${Gdx.graphics.width}x${Gdx.graphics.height}")
		
		addScreen(CommitSudokuScreen(this))
		setScreen<CommitSudokuScreen>()
	}
	
	override fun resize(width: Int, height: Int)
	{
		super.resize(width, height)
		println("Resizing to = ${width}x${height}")
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
