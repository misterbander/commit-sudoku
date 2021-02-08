package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.getValue
import ktx.assets.load
import ktx.async.KtxAsync
import ktx.collections.GdxMap
import ktx.collections.gdxMapOf
import ktx.freetype.generateFont
import ktx.style.*
import misterbander.gframework.GFramework

class CommitSudoku(private val darkModeSettingsProvider: DarkModeSettingsProvider) : GFramework()
{
	// Assets
	
	private val guiAtlas by assetManager.load<TextureAtlas>("sprites/gui.atlas")
	
	// Fonts
	private val generator by assetManager.load<FreeTypeFontGenerator>("fonts/segoeui.ttf")
	val segoeui by lazy {
		generator.generateFont {
			size = 18
			padLeft = 4 // Padding to ensure font doesn't get clipped
			padRight = 4
			padBottom = 4
			padTop = 4
			minFilter = Texture.TextureFilter.Linear
			magFilter = Texture.TextureFilter.Linear
		}
	}
	val segoeui2 by lazy {
		generator.generateFont {
			size = 32
			padLeft = 4 // Padding to ensure font doesn't get clipped
			padRight = 4
			padBottom = 4
			padTop = 4
			minFilter = Texture.TextureFilter.Linear
			magFilter = Texture.TextureFilter.Linear
		}
	}
	
	// Skins
	private val highlightColors: GdxMap<Int, Color> = gdxMapOf(
		0 to Color.CLEAR,
		1 to Color(1F, 0F, 0F, 0.313725F), // Red
		2 to Color(1F, 0.568627F, 0.078431F, 0.313725F), // Orange
		3 to Color(0.968627F, 1F, 0.078431F, 0.313725F), // Yellow
		4 to Color(0.501960F, 1F, 0.078431F, 0.313725F), // Green
		5 to Color(0.078431F, 0.968627F, 1F, 0.313725F), // Blue
		6 to Color(0.078431F, 0.501960F, 1F, 0.313725F), // Dark blue
		7 to Color(0.568627F, 0.078431F, 1F, 0.313725F), // Purple
		8 to Color(0.705882F, 0.705882F, 0.705882F, 0.313725F) // Gray
	)
	private val lightSkin by lazy {
		skin {
			add(Color.BLACK, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color.WHITE, "backgroundcolor")
			color("nongivencolor", 0F, 0.858824F, 0.082353F)
			color("markcolor", 0.5F, 0.572549F, 1F)
			color("selectedcolor", 1F, 0.949019F, 0.5F, 0.470588F)
			add(highlightColors, "highlightcolors")
			
			// Style bases
			addRegions(guiAtlas)
			label("infolabelstyle") { font = segoeui; fontColor = Color.BLACK }
			textButton("textbuttonstylebase") {
				up = this@skin["button"]
				over = this@skin["buttonover"]
				down = this@skin["buttondown"]
				fontColor = Color.BLACK
			}
			textButton("checkabletextbuttonstylebase", "textbuttonstylebase") { checked = down }
			textButton("textbuttonstyle", "textbuttonstylebase") { font = segoeui; }
			textButton("checkabletextbuttonstyle", "checkabletextbuttonstylebase") { font = segoeui; }
			textButton("textbuttonstyle2", "textbuttonstylebase") { font = segoeui2; }
			textButton("checkabletextbuttonstyle2", "checkabletextbuttonstylebase") { font = segoeui2; }
			imageButton("imagebuttonstylebase") {
				up = this@skin["button"]
				over = this@skin["buttonover"]
				down = this@skin["buttondown"]
				disabled = this@skin["buttondisabled"]
			}
			imageButton("checkableimagebuttonstylebase", "imagebuttonstylebase") { checked = down }
			
			// Light derived styles
			imageButton("editbuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["edit"]
				imageDisabled = this@skin["editdisabled"]
			}
			imageButton("playbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["play"] }
			imageButton("pausebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["pause"] }
			imageButton("clearbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["clear"] }
			imageButton("deletebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["delete"] }
			imageButton("undobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["undo"]
				imageDisabled = this@skin["undodisabled"]
			}
			imageButton("redobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["redo"]
				imageDisabled = this@skin["redodisabled"]
			}
			imageButton("colorbuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["color"] }
			imageButton("redbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["red"] }
			imageButton("orangebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["orange"] }
			imageButton("yellowbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["yellow"] }
			imageButton("greenbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["green"] }
			imageButton("bluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["blue"] }
			imageButton("darkbluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkblue"] }
			imageButton("purplebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["purple"] }
			imageButton("graybuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["gray"] }
		}
	}
	private val darkSkin by lazy {
		skin {
			add(Color.WHITE, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			color("backgroundcolor", 0.15F, 0.15F, 0.15F, 1F)
			color("nongivencolor", 0F, 0.858824F, 0.082353F)
			color("markcolor", 0.5F, 0.572549F, 1F)
			color("selectedcolor", 1F, 0.949019F, 0.5F, 0.470588F)
			add(highlightColors, "highlightcolors")
			
			// Style bases
			addRegions(guiAtlas)
			label("infolabelstyle") { font = segoeui; fontColor = Color.WHITE }
			textButton("textbuttonstylebase") {
				up = this@skin["darkbutton"]
				over = this@skin["darkbuttonover"]
				down = this@skin["darkbuttondown"]
				fontColor = Color.WHITE
			}
			textButton("checkabletextbuttonstylebase", "textbuttonstylebase") { checked = down }
			textButton("textbuttonstyle", "textbuttonstylebase") { font = segoeui }
			textButton("checkabletextbuttonstyle", "checkabletextbuttonstylebase") { font = segoeui }
			textButton("textbuttonstyle2", "textbuttonstylebase") { font = segoeui2 }
			textButton("checkabletextbuttonstyle2", "checkabletextbuttonstylebase") { font = segoeui2 }
			imageButton("imagebuttonstylebase") {
				up = this@skin["darkbutton"]
				over = this@skin["darkbuttonover"]
				down = this@skin["darkbuttondown"]
				disabled = this@skin["darkbuttondisabled"]
			}
			imageButton("checkableimagebuttonstylebase", "imagebuttonstylebase") { checked = down }
			
			// Dark derived styles
			imageButton("editbuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkedit"]
				imageDisabled = this@skin["editdisabled"]
			}
			imageButton("playbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkplay"] }
			imageButton("pausebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkpause"] }
			imageButton("clearbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkclear"] }
			imageButton("deletebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkdelete"] }
			imageButton("undobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkundo"]
				imageDisabled = this@skin["undodisabled"]
			}
			imageButton("redobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkredo"]
				imageDisabled = this@skin["redodisabled"]
			}
			imageButton("colorbuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["color"] }
			imageButton("redbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["red"] }
			imageButton("orangebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["orange"] }
			imageButton("yellowbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["yellow"] }
			imageButton("greenbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["green"] }
			imageButton("bluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["blue"] }
			imageButton("darkbluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkblue"] }
			imageButton("purplebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["purple"] }
			imageButton("graybuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["gray"] }
		}
	}
	lateinit var skin: Skin
	
	override fun create()
	{
		KtxAsync.initiate()
		
		assetManager.finishLoading()
		println("Finished loading assets!")
		
		skin = if (darkModeSettingsProvider.defaultDarkModeEnabled) darkSkin else lightSkin
		
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
