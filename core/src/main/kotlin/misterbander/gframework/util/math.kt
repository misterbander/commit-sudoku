package misterbander.gframework.util

/**
 * Treats a range as a cycle and "wraps" an int within the cycle.
 */
infix fun Int.cycle(range: IntRange): Int
{
	var i = this
	val size = range.last - range.first + 1
	while (i > range.last)
		i -= size
	while (i < range.first)
		i += size
	return i
}

infix fun Float.cycle(range: ClosedFloatingPointRange<Float>): Float
{
	var f = this
	val size = range.endInclusive - range.start
	while (f > range.endInclusive)
		f -= size
	while (f < range.start)
		f += size
	return f
}
