package engine

import java.io.Closeable

fun <T : Comparable<T>> clamp(x: T, min: T, max: T): T {
    return when {
        x < min -> {
            min
        }
        x > max -> {
            max
        }
        else -> {
            x
        }
    }
}

val GL_FLOAT_SIZE: Int = 4

interface Useable {
    fun bind()
    fun unbind()
}

inline fun <T : Useable> T.utilise(f: () -> Unit) {
    bind()
    f()
    unbind()
}

fun close_multiple(vararg ts: Closeable) {
    for (x in ts) {
        x.close()
    }
}