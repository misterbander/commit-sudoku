package misterbander.commitsudoku

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.IntMap
import ktx.scene2d.*
import ktx.style.*

const val PRIMARY_COLOR = "primary_color"
const val SECONDARY_COLOR = "secondary_color"
const val BACKGROUND_COLOR = "background_color"
const val TOOLBAR_BACKGROUND_COLOR = "toolbar_background_color"
const val NON_GIVEN_COLOR = "non_given_color"
const val MARK_COLOR = "mark_color"
const val SELECTED_COLOR = "selected_color"
const val HIGHLIGHT_COLORS = "highlight_colors"
const val DECORATION_COLOR_1 = "decoration_color_1"
const val DECORATION_COLOR_2 = "decoration_color_2"
const val NOTO_SANS = "noto_sans"
const val NOTO_SANS_LARGE = "noto_sans_large"
const val TEXT_BUTTON_LARGE_STYLE = "text_button_large"
const val CHECKABLE_TEXT_BUTTON_STYLE = "checkable_text_button"
const val CHECKABLE_TEXT_BUTTON_LARGE_STYLE = "checkable_text_button_large"
const val NEW_BUTTON_STYLE = "new_button"
const val EDIT_BUTTON_STYLE = "edit_button"
const val PLAY_BUTTON_STYLE = "play_button"
const val PAUSE_BUTTON_STYLE = "pause_button"
const val CLEAR_BUTTON_STYLE = "clear_button"
const val DARK_MODE_BUTTON_STYLE = "dark_mode_button"
const val SYNC_BUTTON_STYLE = "sync_button"
const val DELETE_BUTTON_STYLE = "delete_button"
const val UNDO_BUTTON_STYLE = "undo_button"
const val REDO_BUTTON_STYLE = "redo_button"
const val COLOR_BUTTON_STYLE = "color_button"
const val RED_BUTTON_STYLE = "red_button"
const val ORANGE_BUTTON_STYLE = "orange_button"
const val YELLOW_BUTTON_STYLE = "yellow_button"
const val GREEN_BUTTON_STYLE = "green_button"
const val BLUE_BUTTON_STYLE = "blue_button"
const val DARK_BLUE_BUTTON_STYLE = "dark_blue_button"
const val PURPLE_BUTTON_STYLE = "purple_button"
const val PINK_BUTTON_STYLE = "pink_button"
const val GRAY_BUTTON_STYLE = "gray_button"
const val SET_GIVENS_BUTTON_STYLE = "set_givens_button"
const val THERMO_BUTTON_STYLE = "thermo_button"
const val SLOW_THERMO_BUTTON_STYLE = "slow_thermo_button"
const val EMPTY_THERMO_BUTTON_STYLE = "empty_thermo_button"
const val SANDWICH_BUTTON_STYLE = "sandwich_button"
const val TEXT_DECORATION_BUTTON_STYLE = "text_decoration_button"
const val CORNER_TEXT_DECORATION_BUTTON_STYLE = "corner_text_decoration_button"
const val CIRCLE_DECORATION_BUTTON_STYLE = "circle_decoration_button"
const val LINE_DECORATION_BUTTON_STYLE = "line_decoration_button"
const val ARROW_DECORATION_BUTTON_STYLE = "arrow_decoration_button"
const val LITTLE_ARROW_DECORATION_BUTTON_STYLE = "little_arrow_decoration_button"
const val KILLER_CAGE_BUTTON_STYLE = "killer_cage_button"
const val CAGE_DECORATION_BUTTON_STYLE = "cage_decoration_button"
const val BORDER_DECORATION_BUTTON_STYLE = "border_decoration_button"
const val X_BUTTON_STYLE = "x_button"
const val ANTIKING_BUTTON_STYLE = "antiking_button"
const val ANTIKNIGHT_BUTTON_STYLE = "antiknight_button"
const val NON_CONSECUTIVE_BUTTON_STYLE = "non_consecutive_button"
const val CLOSE_BUTTON_STYLE = "close_button"

val primaryColor: Color
	get() = Scene2DSkin.defaultSkin[PRIMARY_COLOR]
val secondaryColor: Color
	get() = Scene2DSkin.defaultSkin[SECONDARY_COLOR]
val backgroundColor: Color
	get() = Scene2DSkin.defaultSkin[BACKGROUND_COLOR]
val toolbarBackgroundColor: Color
	get() = Scene2DSkin.defaultSkin[TOOLBAR_BACKGROUND_COLOR]
val nonGivenColor: Color
	get() = Scene2DSkin.defaultSkin[NON_GIVEN_COLOR]
val markColor: Color
	get() = Scene2DSkin.defaultSkin[MARK_COLOR]
val selectedColor: Color
	get() = Scene2DSkin.defaultSkin[SELECTED_COLOR]
val highlightColors: IntMap<Color>
	get() = Scene2DSkin.defaultSkin[HIGHLIGHT_COLORS]
val decorationColor1: Color
	get() = Scene2DSkin.defaultSkin[DECORATION_COLOR_1]
val decorationColor2: Color
	get() = Scene2DSkin.defaultSkin[DECORATION_COLOR_2]
val notoSans: BitmapFont
	get() = Scene2DSkin.defaultSkin[NOTO_SANS]
val notoSansLarge: BitmapFont
	get() = Scene2DSkin.defaultSkin[NOTO_SANS_LARGE]
