package com.android.hwsystemmanager.utils

import kotlin.ArithmeticException

@JvmOverloads
public fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null,
): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}

fun <T> List<T>.withIndexStep(step: Int): Iterable<IndexedValue<T>> {
    return IndexingIterable(step) { this }
}

internal class IndexingIterable<out T>(private val step: Int, private val iteratorFactory: () -> List<T>) : Iterable<IndexedValue<T>> {
    override fun iterator(): Iterator<IndexedValue<T>> = IndexingIterator(iteratorFactory(), step)
}

internal class IndexingIterator<out T>(private val iterator: List<T>, private val step: Int) : Iterator<IndexedValue<T>> {
    private var index = 0

    override fun hasNext(): Boolean {
        return index < iterator.size
    }

    override fun next(): IndexedValue<T> {
        val oldIndex = index
        index = index + step
        return IndexedValue(checkIndexOverflow(oldIndex), iterator[oldIndex])
    }
}

@PublishedApi
@SinceKotlin("1.3")
internal fun checkIndexOverflow(index: Int): Int {
    if (index < 0) {
        throw ArithmeticException("Index overflow has happened.")
    }
    return index
}


public infix fun <T> List<T>.step(step: Int): ListProgression<T> {
    return ListProgression(this, step)
}


public open class ListProgression<T>
internal constructor(private val iterable: List<T>, step: Int) : Iterable<T> {
    init {
        if (step == 0) throw kotlin.IllegalArgumentException("Step must be non-zero.")
        if (step == iterable.size - 1) throw kotlin.IllegalArgumentException("Step must be greater than Long.MIN_VALUE to avoid overflow on negation.")
    }

    /**
     * The first element in the progression.
     */
    public val first: T = iterable.first()

    /**
     * The last element in the progression.
     */
    public val last: T = iterable.last()

    /**
     * The step of the progression.
     */
    public val step: Int = step

    override fun iterator(): ListIterator<T> = ListProgressionIterator(iterable, step)

    /**
     * Checks if the progression is empty.
     *
     * Progression with a positive step is empty if its first element is greater than the last element.
     * Progression with a negative step is empty if its first element is less than the last element.
     */
    public open fun isEmpty(): Boolean = iterable.isEmpty()

}

internal class ListProgressionIterator<T>(private val iterable: List<T>, private val step: Int) : ListIterator<T> {
    private var index = 0
    override fun hasNext(): Boolean {
        return index <= iterable.size
    }

    override fun hasPrevious(): Boolean {
        return index - step >= 0
    }

    override fun next(): T {
        val oldIndex = index
        index += step
        Logcat.d("calendar4>>>>>>>oldIndex:$oldIndex,index:$index")
        if (oldIndex >= iterable.size) {
            return iterable.last()
        }
        return iterable[oldIndex]
    }

    override fun nextIndex(): Int {
        return index + step
    }

    override fun previous(): T {
        return iterable[index - step]
    }

    override fun previousIndex(): Int {
        return index - step
    }
}