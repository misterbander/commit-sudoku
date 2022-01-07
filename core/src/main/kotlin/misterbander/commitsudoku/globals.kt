package misterbander.commitsudoku

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.IntMap
import ktx.scene2d.*
import ktx.style.*

const val PRIMARY_COLOR = "primarycolor"
const val SECONDARY_COLOR = "secondarycolor"
const val BACKGROUND_COLOR = "backgroundcolor"
const val TOOLBAR_BACKGROUND_COLOR = "toolbarbackgroundcolor"
const val NON_GIVEN_COLOR = "nongivencolor"
const val MARK_COLOR = "markcolor"
const val SELECTED_COLOR = "selectedcolor"
const val HIGHLIGHT_COLORS = "highlightcolors"
const val DECORATION_COLOR_1 = "decorationcolor1"
const val DECORATION_COLOR_2 = "decorationcolor2"
const val INFO_LABEL_STYLE = "infolabelstyle"
const val TEXT_BUTTON_STYLE_BASE = "textbuttonstylebase"
const val TEXT_BUTTON_STYLE = "textbuttonstyle"
const val TEXT_BUTTON_STYLE_L = "textbuttonstylel"
const val CHECKABLE_TEXT_BUTTON_STYLE_BASE = "checkabletextbuttonstylebase"
const val CHECKABLE_TEXT_BUTTON_STYLE = "checkabletextbuttonstyle"
const val CHECKABLE_TEXT_BUTTON_STYLE_L = "checkabletextbuttonstylel"
const val IMAGE_BUTTON_STYLE_BASE = "imagebuttonstylebase"
const val CHECKABLE_IMAGE_BUTTON_STYLE_BASE = "checkableimagebuttonstylebase"
const val TOOLBAR_BUTTON_STYLE_BASE = "toolbarbuttonstylebase"
const val NEW_BUTTON_STYLE = "newbuttonstyle"
const val EDIT_BUTTON_STYLE = "editbuttonstyle"
const val PLAY_BUTTON_STYLE = "playbuttonstyle"
const val PAUSE_BUTTON_STYLE = "pausebuttonstyle"
const val CLEAR_BUTTON_STYLE = "clearbuttonstyle"
const val DARK_MODE_BUTTON_STYLE = "darkmodebuttonstyle"
const val CONNECT_BUTTON_STYLE = "connectbuttonstyle"
const val DELETE_BUTTON_STYLE = "deletebuttonstyle"
const val UNDO_BUTTON_STYLE = "undobuttonstyle"
const val REDO_BUTTON_STYLE = "redobuttonstyle"
const val COLOR_BUTTON_STYLE = "colorbuttonstyle"
const val RED_BUTTON_STYLE = "redbuttonstyle"
const val ORANGE_BUTTON_STYLE = "orangebuttonstyle"
const val YELLOW_BUTTON_STYLE = "yellowbuttonstyle"
const val GREEN_BUTTON_STYLE = "greenbuttonstyle"
const val BLUE_BUTTON_STYLE = "bluebuttonstyle"
const val DARK_BLUE_BUTTON_STYLE = "darkbluebuttonstyle"
const val PURPLE_BUTTON_STYLE = "purplebuttonstyle"
const val PINK_BUTTON_STYLE = "pinkbuttonstyle"
const val GRAY_BUTTON_STYLE = "graybutonstyle"
const val SET_GIVENS_BUTTON_STYLE = "setgivensbuttonstyle"
const val ADD_THERMO_BUTTON_STYLE = "addthermobuttonstyle"
const val SOFT_THERMO_BUTTON_STYLE = "softthermobuttonstyle"
const val EMPTY_THERMO_BUTTON_STYLE = "emptythermobuttonstyle"
const val ADD_SANDWICH_BUTTON_STYLE = "addsandwichbuttonstyle"
const val ADD_TEXT_DECORATION_BUTTON_STYLE = "addtextdecorationbuttonstyle"
const val ADD_CORNER_TEXT_DECORATION_BUTTON_STYLE = "addcornertextdecorationbuttonstyle"
const val ADD_CIRCLE_DECORATION_BUTTON_STYLE = "addcircledecorationbuttonstyle"
const val ADD_LINE_DECORATION_BUTTON_STYLE = "addlinedecorationbuttonstyle"
const val ADD_ARROW_DECORATION_BUTTON_STYLE = "addarrowdecorationbuttonstyle"
const val ADD_LITTLE_ARROW_DECORATION_BUTTON_STYLE = "addlittlearrowdecorationbuttonstyle"
const val ADD_KILLER_CAGE_BUTTON_STYLE = "addkillercagebuttonstyle"
const val ADD_CAGE_DECORATION_BUTTON_STYLE = "addcagedecorationbuttonstyle"
const val ADD_BORDER_DECORATION_BUTTON_STYLE = "addborderdecorationbuttonstyle"
const val X_BUTTON_STYLE = "xbuttonstyle"
const val ANTIKING_BUTTON_STYLE = "antikingbuttonstyle"
const val ANTIKNIGHT_BUTTON_STYLE = "antiknightbuttonstyle"
const val NON_CONSECUTIVE_BUTTON_STYLE = "nonconsecutivebuttonstyle"
const val WINDOW_STYLE = "windowstyle"
const val CLOSE_BUTTON_STYLE = "closebuttonstyle"
const val TEXT_FIELD_STYLE = "textfieldstyle"

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
