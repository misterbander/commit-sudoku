package misterbander.commitsudoku

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.freetype.generateFont
import ktx.log.info
import ktx.scene2d.*
import ktx.style.*
import misterbander.gframework.GFramework
import misterbander.gframework.util.gdxIntMapOf

class CommitSudoku(
	private val args: Array<String> = emptyArray(),
	private val darkModeSettingsProvider: DarkModeSettingsProvider
) : GFramework()
{
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
			val guiAtlasDeferred = assetStorage.loadAsync(TextureAtlases.gui)
			val generatorDeferred = assetStorage.loadAsync(Fonts.notoSans)
			val guiAtlas = guiAtlasDeferred.await()
			val notoSansGenerator = generatorDeferred.await()
			val highlightColors = gdxIntMapOf(
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
			val notoSans = notoSansGenerator.generateFont {
				size = 18
				minFilter = Texture.TextureFilter.Linear
				magFilter = Texture.TextureFilter.Linear
			}.alsoRegister()
			val notoSansLarge = notoSansGenerator.generateFont {
				size = 32
				minFilter = Texture.TextureFilter.Linear
				magFilter = Texture.TextureFilter.Linear
			}.alsoRegister()

			val lightSkin = skin {
				addRegions(guiAtlas)
				add(PRIMARY_COLOR, Color.BLACK)
				add(SECONDARY_COLOR, Color.GRAY)
				add(BACKGROUND_COLOR, Color.WHITE)
				add(TOOLBAR_BACKGROUND_COLOR, Color(0xEDEDEDFF.toInt()))
				add(NON_GIVEN_COLOR, Color(0x00DB15FF))
				add(MARK_COLOR, Color(0x7F92FFFF))
				add(SELECTED_COLOR, Color(0xFFF27F78.toInt()))
				add(HIGHLIGHT_COLORS, highlightColors)
				add(DECORATION_COLOR_1, Color(0.4822198F, 0.4822198F, 0.4822198F, 0.266055F))
				add(DECORATION_COLOR_2, Color(0.39875F, 0.39875F, 0.39875F, 0.417431F))
				add(NOTO_SANS, notoSans)
				add(NOTO_SANS_LARGE, notoSansLarge)
				load(Gdx.files.internal("textures/light_skin.json"))
			}
			val darkSkin = skin {
				addRegions(guiAtlas)
				add(PRIMARY_COLOR, Color.WHITE)
				add(SECONDARY_COLOR, Color.GRAY)
				add(BACKGROUND_COLOR, Color(0x252525FF))
				add(TOOLBAR_BACKGROUND_COLOR, Color(0x0F0F0FFF))
				add(NON_GIVEN_COLOR, Color(0x00DB15FF))
				add(MARK_COLOR, Color(0x7F92FFFF))
				add(SELECTED_COLOR, Color(0xFFF27F60.toInt()))
				add(HIGHLIGHT_COLORS, highlightColors)
				add(DECORATION_COLOR_1, Color(0.4822198F, 0.4822198F, 0.4822198F, 0.266055F))
				add(DECORATION_COLOR_2, Color(0.6F, 0.6F, 0.6F, 0.5F))
				add(NOTO_SANS, notoSans)
				add(NOTO_SANS_LARGE, notoSansLarge)
				load(Gdx.files.internal("textures/dark_skin.json"))
			}
			Scene2DSkin.defaultSkin = if (darkModeSettingsProvider.defaultDarkModeEnabled) darkSkin else lightSkin

			shapeDrawer.setTextureRegion(guiAtlas.findRegion("pixel"))

			info("CommitSudoku          | INFO") { "Finished loading assets!" }
			info("CommitSudoku          | INFO") { "Resolution = ${Gdx.graphics.width}x${Gdx.graphics.height}" }

			addScreen(CommitSudokuScreen(this@CommitSudoku, lightSkin, darkSkin))
			setScreen<CommitSudokuScreen>()
		}
	}

	override fun resize(width: Int, height: Int)
	{
		super.resize(width, height)
		info("CommitSudoku          | INFO") { "Resizing to = ${width}x${height}" }
	}
}
