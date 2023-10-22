package misterbander.gframework.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface KNestedTable : KTable
{
	fun defaults(): Cell<*>

	fun row(): Cell<*>
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun Table.scene2d(
	init: KNestedTable.() -> Unit = {}
): Table
{
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	val proxy = object : KNestedTable
	{
		override fun <T : Actor> add(actor: T): Cell<T> = this@scene2d.add(actor)

		override fun defaults() = this@scene2d.defaults()

		override fun row(): Cell<*> = this@scene2d.row()
	}
	proxy.init()
	return this
}
