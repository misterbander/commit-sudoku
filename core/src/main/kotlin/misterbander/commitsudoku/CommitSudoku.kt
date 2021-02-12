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
			minFilter = Texture.TextureFilter.Linear
			magFilter = Texture.TextureFilter.Linear
		}
	}
	
	// Skins
	private val highlightColors: GdxMap<Int, Color> = gdxMapOf(
		0 to Color.CLEAR,
		1 to Color(0xFF000050.toInt()), // Red
		2 to Color(0xFF911450.toInt()), // Orange
		3 to Color(0xF7FF1450.toInt()), // Yellow
		4 to Color(0x81FF1450.toInt()), // Green
		5 to Color(0x14F7FF50), // Blue
		6 to Color(0x1481FF50), // Dark blue
		7 to Color(0x9114FF50.toInt()), // Purple
		8 to Color(0xB4B4B450.toInt()) // Gray
	)
	val lightSkin by lazy {
		skin {
			add(Color.BLACK, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color.WHITE, "backgroundcolor")
			add(Color(0xF0F0F0FF.toInt()), "toolbarbackgroundcolor")
			add(Color(0x00DB15FF), "nongivencolor")
			add(Color(0x7F92FFFF), "markcolor")
			add(Color(0xFFF27F78.toInt()), "selectedcolor")
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
			imageButton("toolbarbuttonstylebase") {
				over = this@skin["toolbarbuttonover"]
				down = this@skin["toolbarbuttondown"]
				checked = down
			}
			
			// Light derived styles
			imageButton("editbuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["editicon"]
				imageDisabled = this@skin["editdisabledicon"]
			}
			imageButton("playbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["playicon"] }
			imageButton("pausebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["pauseicon"] }
			imageButton("clearbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["clearicon"] }
			imageButton("darkmodebuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["moonicon"] }
			imageButton("deletebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["deleteicon"] }
			imageButton("undobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["undoicon"]
				imageDisabled = this@skin["undodisabledicon"]
			}
			imageButton("redobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["redoicon"]
				imageDisabled = this@skin["redodisabledicon"]
			}
			imageButton("colorbuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["coloricon"] }
			imageButton("redbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["redicon"] }
			imageButton("orangebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["orangeicon"] }
			imageButton("yellowbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["yellowicon"] }
			imageButton("greenbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["greenicon"] }
			imageButton("bluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["blueicon"] }
			imageButton("darkbluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkblueicon"] }
			imageButton("purplebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["purpleicon"] }
			imageButton("graybuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["grayicon"] }
			imageButton("setgivensbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["setgivensicon"] }
			imageButton("addthermobuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addthermoicon"] }
			imageButton("addsandwichbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addsandwichicon"] }
			imageButton("addtextdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addtextdecorationicon"] }
			imageButton("addsmalltextdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addsmalltextdecorationicon"] }
			imageButton("addcircledecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addcircledecorationicon"] }
			imageButton("addlinedecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addlinedecorationicon"] }
			imageButton("addarrowdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addarrowdecorationicon"] }
			imageButton("addcagedecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["addcagedecorationicon"] }
			imageButton("xbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["xicon"] }
			imageButton("antikingbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["antikingicon"] }
			imageButton("antiknightbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["antiknighticon"] }
			imageButton("nonconsecutivebuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["nonconsecutiveicon"] }
		}
	}
	val darkSkin by lazy {
		skin {
			add(Color.WHITE, "primarycolor")
			add(Color.GRAY, "secondarycolor")
			add(Color(0x252525FF), "backgroundcolor")
			add(Color(0x0F0F0FFF), "toolbarbackgroundcolor")
			add(Color(0x00DB15FF), "nongivencolor")
			add(Color(0x7F92FFFF), "markcolor")
			add(Color(0xFFF27F78.toInt()), "selectedcolor")
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
			imageButton("toolbarbuttonstylebase") {
				over = this@skin["toolbarbuttonover"]
				down = this@skin["toolbarbuttondown"]
				checked = down
			}
			
			// Dark derived styles
			imageButton("editbuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkediticon"]
				imageDisabled = this@skin["editdisabledicon"]
			}
			imageButton("playbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkplayicon"] }
			imageButton("pausebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkpauseicon"] }
			imageButton("clearbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkclearicon"] }
			imageButton("darkmodebuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["darkmoonicon"] }
			imageButton("deletebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkdeleteicon"] }
			imageButton("undobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkundoicon"]
				imageDisabled = this@skin["undodisabledicon"]
			}
			imageButton("redobuttonstyle", "imagebuttonstylebase") {
				imageUp = this@skin["darkredoicon"]
				imageDisabled = this@skin["redodisabledicon"]
			}
			imageButton("colorbuttonstyle", "checkableimagebuttonstylebase") { imageUp = this@skin["coloricon"] }
			imageButton("redbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["redicon"] }
			imageButton("orangebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["orangeicon"] }
			imageButton("yellowbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["yellowicon"] }
			imageButton("greenbuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["greenicon"] }
			imageButton("bluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["blueicon"] }
			imageButton("darkbluebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["darkblueicon"] }
			imageButton("purplebuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["purpleicon"] }
			imageButton("graybuttonstyle", "imagebuttonstylebase") { imageUp = this@skin["grayicon"] }
			imageButton("setgivensbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darksetgivensicon"] }
			imageButton("addthermobuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddthermoicon"] }
			imageButton("addsandwichbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddsandwichicon"] }
			imageButton("addtextdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddtextdecorationicon"] }
			imageButton("addsmalltextdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddsmalltextdecorationicon"] }
			imageButton("addcircledecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddcircledecorationicon"] }
			imageButton("addlinedecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddlinedecorationicon"] }
			imageButton("addarrowdecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddarrowdecorationicon"] }
			imageButton("addcagedecorationbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkaddcagedecorationicon"] }
			imageButton("xbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkxicon"] }
			imageButton("antikingbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkantikingicon"] }
			imageButton("antiknightbuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darkantiknighticon"] }
			imageButton("nonconsecutivebuttonstyle", "toolbarbuttonstylebase") { imageUp = this@skin["darknonconsecutiveicon"] }
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
