package misterbander.commitsudoku

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.freetype.generateFont
import ktx.log.info
import ktx.scene2d.*
import ktx.style.*
import misterbander.gframework.GFramework
import misterbander.gframework.scene2d.gTextField
import misterbander.gframework.util.gdxIntMapOf

class CommitSudoku(
	private val args: Array<String> = emptyArray(),
	private val darkModeSettingsProvider: DarkModeSettingsProvider
) : GFramework()
{
	// Assets
	private val guiAtlas by lazy { assetStorage[TextureAtlases.gui] }
	
	// Fonts
	private val generator by lazy { assetStorage[Fonts.segoeui] }
	val segoeui by lazy {
		generator.generateFont {
			size = 18
			padLeft = 1 // Padding to ensure font doesn't get clipped
			padRight = 1
			padBottom = 4
			minFilter = Texture.TextureFilter.Linear
			magFilter = Texture.TextureFilter.Linear
		}
	}
	val segoeuil by lazy {
		generator.generateFont {
			size = 32
			padLeft = 1 // Padding to ensure font doesn't get clipped
			padRight = 1
			padBottom = 4
			minFilter = Texture.TextureFilter.Linear
			magFilter = Texture.TextureFilter.Linear
		}
	}
	
	// Skins
	private val highlightColors = gdxIntMapOf(
		0 to Color.CLEAR,
		1 to Color(0xFF000050.toInt()), // Red
		2 to Color(0xFF911450.toInt()), // Orange
		3 to Color(0xFFFF1450.toInt()), // Yellow
		4 to Color(0x81FF1450.toInt()), // Green
		5 to Color(0x14F7FF50), // Blue
		6 to Color(0x1481FF50), // Dark blue
		7 to Color(0x9114FF50.toInt()), // Purple
		8 to Color(0xFF00BB50.toInt()), // Pink
		9 to Color(0xB4B4B450.toInt()) // Gray
	)
	private val markColor = Color(0x7F92FFFF)
	private val decorationColor1 = Color(0.4822198F, 0.4822198F, 0.4822198F, 0.266055F)
	val lightSkin by lazy {
		skin {
			add(Color.BLACK, PRIMARY_COLOR)
			add(Color.GRAY, SECONDARY_COLOR)
			add(Color.WHITE, BACKGROUND_COLOR)
			add(Color(0xF0F0F0FF.toInt()), TOOLBAR_BACKGROUND_COLOR)
			add(Color(0x00DB15FF), NON_GIVEN_COLOR)
			add(markColor, MARK_COLOR)
			add(Color(0xFFF27F78.toInt()), SELECTED_COLOR)
			add(highlightColors, HIGHLIGHT_COLORS)
			add(decorationColor1, DECORATION_COLOR_1)
			add(Color(0.39875F, 0.39875F, 0.39875F, 0.417431F), DECORATION_COLOR_2)
			
			// Style bases
			addRegions(guiAtlas)
			label(INFO_LABEL_STYLE) { font = segoeui; fontColor = Color.BLACK }
			textButton(TEXT_BUTTON_STYLE_BASE) {
				up = this@skin["button"]
				over = this@skin["buttonover"]
				down = this@skin["buttondown"]
				disabled = this@skin["buttondisabled"]
				fontColor = Color.BLACK
				disabledFontColor = Color(0x868686FF.toInt())
			}
			textButton(CHECKABLE_TEXT_BUTTON_STYLE_BASE, TEXT_BUTTON_STYLE_BASE) { checked = down }
			textButton(TEXT_BUTTON_STYLE, TEXT_BUTTON_STYLE_BASE) { font = segoeui }
			textButton(CHECKABLE_TEXT_BUTTON_STYLE, CHECKABLE_TEXT_BUTTON_STYLE_BASE) { font = segoeui }
			textButton(TEXT_BUTTON_STYLE_L, TEXT_BUTTON_STYLE_BASE) { font = segoeuil }
			textButton(CHECKABLE_TEXT_BUTTON_STYLE_L, CHECKABLE_TEXT_BUTTON_STYLE_BASE) { font = segoeuil }
			imageButton(IMAGE_BUTTON_STYLE_BASE) {
				up = this@skin["button"]
				over = this@skin["buttonover"]
				down = this@skin["buttondown"]
				disabled = this@skin["buttondisabled"]
			}
			imageButton(CHECKABLE_IMAGE_BUTTON_STYLE_BASE, IMAGE_BUTTON_STYLE_BASE) { checked = down }
			imageButton(TOOLBAR_BUTTON_STYLE_BASE) {
				over = this@skin["toolbarbuttonover"]
				down = this@skin["toolbarbuttondown"]
				checked = down
			}
			
			// Light derived styles
			imageButton(NEW_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["newicon"] }
			imageButton(EDIT_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["editicon"] }
			imageButton(PLAY_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["playicon"] }
			imageButton(PAUSE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["pauseicon"] }
			imageButton(CLEAR_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["clearicon"] }
			imageButton(DARK_MODE_BUTTON_STYLE, CHECKABLE_IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["moonicon"] }
			imageButton(CONNECT_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["connecticon"] }
			imageButton(DELETE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["deleteicon"] }
			imageButton(UNDO_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) {
				imageUp = this@skin["undoicon"]
				imageDisabled = this@skin["undodisabledicon"]
			}
			imageButton(REDO_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) {
				imageUp = this@skin["redoicon"]
				imageDisabled = this@skin["redodisabledicon"]
			}
			imageButton(COLOR_BUTTON_STYLE, CHECKABLE_IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["coloricon"] }
			imageButton(RED_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["redicon"] }
			imageButton(ORANGE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["orangeicon"] }
			imageButton(YELLOW_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["yellowicon"] }
			imageButton(GREEN_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["greenicon"] }
			imageButton(BLUE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["blueicon"] }
			imageButton(DARK_BLUE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkblueicon"] }
			imageButton(PURPLE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["purpleicon"] }
			imageButton(PINK_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["pinkicon"] }
			imageButton(GRAY_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["grayicon"] }
			imageButton(SET_GIVENS_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["setgivensicon"] }
			imageButton(ADD_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addthermoicon"] }
			imageButton(SOFT_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["softthermoicon"] }
			imageButton(EMPTY_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["emptythermoicon"] }
			imageButton(ADD_SANDWICH_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addsandwichicon"] }
			imageButton(ADD_TEXT_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addtextdecorationicon"] }
			imageButton(ADD_CORNER_TEXT_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addcornertextdecorationicon"] }
			imageButton(ADD_CIRCLE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addcircledecorationicon"] }
			imageButton(ADD_LINE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addlinedecorationicon"] }
			imageButton(ADD_ARROW_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addarrowdecorationicon"] }
			imageButton(ADD_LITTLE_ARROW_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addlittlearrowdecorationicon"] }
			imageButton(ADD_KILLER_CAGE_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addkillercageicon"] }
			imageButton(ADD_CAGE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addcagedecorationicon"] }
			imageButton(ADD_BORDER_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["addborderdecorationicon"] }
			imageButton(X_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["xicon"] }
			imageButton(ANTIKING_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["antikingicon"] }
			imageButton(ANTIKNIGHT_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["antiknighticon"] }
			imageButton(NON_CONSECUTIVE_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["nonconsecutiveicon"] }
			window(WINDOW_STYLE) {
				background = this@skin["window"]
				titleFont = segoeui
				titleFontColor = this@skin["primarycolor"]
			}
			button(CLOSE_BUTTON_STYLE) {
				up = this@skin["closebutton"]
				over = this@skin["closebuttonover"]
				down = this@skin["closebuttondown"]
			}
			gTextField(TEXT_FIELD_STYLE) {
				background = this@skin["textfield"]
				background!!.leftWidth = 16F
				background!!.rightWidth = 16F
				font = segoeui
				fontColor = this@skin["primarycolor"]
				selectionFontColor = Color.WHITE
				cursor = this@skin["textcursor"]
				cursor!!.leftWidth = 32F
				selection = this@skin["textselection"]
			}
		}
	}
	val darkSkin by lazy {
		skin {
			add(Color.WHITE, PRIMARY_COLOR)
			add(Color.GRAY, SECONDARY_COLOR)
			add(Color(0x252525FF), BACKGROUND_COLOR)
			add(Color(0x0F0F0FFF), TOOLBAR_BACKGROUND_COLOR)
			add(Color(0x00DB15FF), NON_GIVEN_COLOR)
			add(markColor, MARK_COLOR)
			add(Color(0xFFF27F60.toInt()), SELECTED_COLOR)
			add(highlightColors, HIGHLIGHT_COLORS)
			add(decorationColor1, DECORATION_COLOR_1)
			add(Color(0.6F, 0.6F, 0.6F, 0.5F), DECORATION_COLOR_2)
			
			// Style bases
			addRegions(guiAtlas)
			label(INFO_LABEL_STYLE) { font = segoeui; fontColor = Color.WHITE }
			textButton(TEXT_BUTTON_STYLE_BASE) {
				up = this@skin["darkbutton"]
				over = this@skin["darkbuttonover"]
				down = this@skin["darkbuttondown"]
				disabled = this@skin["darkbuttondisabled"]
				fontColor = Color.WHITE
				disabledFontColor = Color(0x868686FF.toInt())
			}
			textButton(CHECKABLE_TEXT_BUTTON_STYLE_BASE, TEXT_BUTTON_STYLE_BASE) { checked = down }
			textButton(TEXT_BUTTON_STYLE, TEXT_BUTTON_STYLE_BASE) { font = segoeui }
			textButton(CHECKABLE_TEXT_BUTTON_STYLE, CHECKABLE_TEXT_BUTTON_STYLE_BASE) { font = segoeui }
			textButton(TEXT_BUTTON_STYLE_L, TEXT_BUTTON_STYLE_BASE) { font = segoeuil }
			textButton(CHECKABLE_TEXT_BUTTON_STYLE_L, CHECKABLE_TEXT_BUTTON_STYLE_BASE) { font = segoeuil }
			imageButton(IMAGE_BUTTON_STYLE_BASE) {
				up = this@skin["darkbutton"]
				over = this@skin["darkbuttonover"]
				down = this@skin["darkbuttondown"]
				disabled = this@skin["darkbuttondisabled"]
			}
			imageButton(CHECKABLE_IMAGE_BUTTON_STYLE_BASE, IMAGE_BUTTON_STYLE_BASE) { checked = down }
			imageButton(TOOLBAR_BUTTON_STYLE_BASE) {
				over = this@skin["toolbarbuttonover"]
				down = this@skin["toolbarbuttondown"]
				checked = down
			}
			
			// Dark derived styles
			imageButton(NEW_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darknewicon"] }
			imageButton(EDIT_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkediticon"] }
			imageButton(PLAY_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkplayicon"] }
			imageButton(PAUSE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkpauseicon"] }
			imageButton(CLEAR_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkclearicon"] }
			imageButton(DARK_MODE_BUTTON_STYLE, CHECKABLE_IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkmoonicon"] }
			imageButton(CONNECT_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkconnecticon"] }
			imageButton(DELETE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkdeleteicon"] }
			imageButton(UNDO_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) {
				imageUp = this@skin["darkundoicon"]
				imageDisabled = this@skin["undodisabledicon"]
			}
			imageButton(REDO_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) {
				imageUp = this@skin["darkredoicon"]
				imageDisabled = this@skin["redodisabledicon"]
			}
			imageButton(COLOR_BUTTON_STYLE, CHECKABLE_IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["coloricon"] }
			imageButton(RED_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["redicon"] }
			imageButton(ORANGE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["orangeicon"] }
			imageButton(YELLOW_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["yellowicon"] }
			imageButton(GREEN_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["greenicon"] }
			imageButton(BLUE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["blueicon"] }
			imageButton(DARK_BLUE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["darkblueicon"] }
			imageButton(PURPLE_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["purpleicon"] }
			imageButton(PINK_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["pinkicon"] }
			imageButton(GRAY_BUTTON_STYLE, IMAGE_BUTTON_STYLE_BASE) { imageUp = this@skin["grayicon"] }
			imageButton(SET_GIVENS_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darksetgivensicon"] }
			imageButton(ADD_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddthermoicon"] }
			imageButton(SOFT_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darksoftthermoicon"] }
			imageButton(EMPTY_THERMO_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkemptythermoicon"] }
			imageButton(ADD_SANDWICH_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddsandwichicon"] }
			imageButton(ADD_TEXT_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddtextdecorationicon"] }
			imageButton(ADD_CORNER_TEXT_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddcornertextdecorationicon"] }
			imageButton(ADD_CIRCLE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddcircledecorationicon"] }
			imageButton(ADD_LINE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddlinedecorationicon"] }
			imageButton(ADD_ARROW_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddarrowdecorationicon"] }
			imageButton(ADD_LITTLE_ARROW_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddlittlearrowdecorationicon"] }
			imageButton(ADD_KILLER_CAGE_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddkillercageicon"] }
			imageButton(ADD_CAGE_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddcagedecorationicon"] }
			imageButton(ADD_BORDER_DECORATION_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkaddborderdecorationicon"] }
			imageButton(X_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkxicon"] }
			imageButton(ANTIKING_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkantikingicon"] }
			imageButton(ANTIKNIGHT_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darkantiknighticon"] }
			imageButton(NON_CONSECUTIVE_BUTTON_STYLE, TOOLBAR_BUTTON_STYLE_BASE) { imageUp = this@skin["darknonconsecutiveicon"] }
			window(WINDOW_STYLE) {
				background = this@skin["darkwindow"]
				titleFont = segoeui
				titleFontColor = this@skin["primarycolor"]
			}
			button(CLOSE_BUTTON_STYLE) {
				up = this@skin["darkclosebutton"]
				over = this@skin["closebuttonover"]
				down = this@skin["closebuttondown"]
			}
			gTextField(TEXT_FIELD_STYLE) {
				background = this@skin["darktextfield"]
				background!!.leftWidth = 16F
				background!!.rightWidth = 16F
				font = segoeui
				fontColor = this@skin["primarycolor"]
				selectionFontColor = Color.WHITE
				cursor = this@skin["darktextcursor"]
				cursor!!.leftWidth = 32F
				selection = this@skin["textselection"]
			}
		}
	}
	val isDarkMode
		get() = Scene2DSkin.defaultSkin == darkSkin
	
	override fun create()
	{
		Gdx.app.logLevel = when (args.firstOrNull())
		{
			"--debug" -> Application.LOG_DEBUG
			"--info" -> Application.LOG_INFO
			else -> Application.LOG_ERROR
		}
		Gdx.graphics.isContinuousRendering = false
		KtxAsync.initiate()
		
		KtxAsync.launch {
			val assets = listOf(
				assetStorage.loadAsync(TextureAtlases.gui),
				assetStorage.loadAsync(Fonts.segoeui)
			)
			assets.joinAll()
			
			shapeDrawer.setTextureRegion(guiAtlas.findRegion("pixel"))
			
			Scene2DSkin.defaultSkin = if (darkModeSettingsProvider.defaultDarkModeEnabled) darkSkin else lightSkin
			info("CommitSudoku          | INFO") { "Finished loading assets!" }
			info("CommitSudoku          | INFO") { "Resolution = ${Gdx.graphics.width}x${Gdx.graphics.height}" }
			addScreen(CommitSudokuScreen(this@CommitSudoku))
			setScreen<CommitSudokuScreen>()
		}
	}
	
	override fun resize(width: Int, height: Int)
	{
		super.resize(width, height)
		info("CommitSudoku          | INFO") { "Resizing to = ${width}x${height}" }
	}
	
	override fun dispose()
	{
		segoeui.dispose()
		segoeuil.dispose()
		lightSkin.dispose()
		darkSkin.dispose()
		super.dispose()
	}
}
