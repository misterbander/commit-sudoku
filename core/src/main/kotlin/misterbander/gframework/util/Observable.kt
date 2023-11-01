package misterbander.gframework.util

import ktx.collections.*
import kotlin.reflect.KProperty

class Observable<T>(private var value: T, observer: ((T) -> Unit)? = null)
{
	private val observers = GdxArray<(T) -> Unit>()

	init
	{
		if (observer != null)
			observers += observer
	}

	fun addObserver(observer: (T) -> Unit)
	{
		observers += observer
	}

	operator fun getValue(from: Any?, property: KProperty<*>): T = value

	operator fun setValue(from: Any?, property: KProperty<*>, value: T)
	{
		if (this.value == value)
			return
		for (i in 0 until observers.size)
			observers[i](value)
		this.value = value
	}
}
