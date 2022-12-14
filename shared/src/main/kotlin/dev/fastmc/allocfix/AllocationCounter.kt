package dev.fastmc.allocfix

import java.util.*
import kotlin.math.max

object AllocationCounter {
    private val allocations = ArrayDeque<Entry>()
    private var lastUsed = 0L

    fun update() {
        val current = getUsed()

        if (lastUsed != 0L) {
            val diff = current - lastUsed
            if (diff > 0L) {
                allocations.add(Entry(System.nanoTime() + 3_000_000_000L, diff.toFloat() / 1024.0f))
            }
        }

        lastUsed = current
    }

    fun getRenderText(): String {
        val current = System.nanoTime()
        while (allocations.isNotEmpty() && allocations.peek().time < current) {
            allocations.poll()
        }

        if (allocations.isEmpty()) {
            return "0.0 MB/s"
        }

        var allocation = 0.0f
        for (entry in allocations) {
            allocation += entry.allocation
        }
        val timeLength = (allocations.last.time - allocations.first.time) / 1_000_000_000.0f

        return "%.2f MB/s".format(allocation / max(timeLength, 0.1f) / 1024.0f)
    }

    fun reset() {
        allocations.clear()
        lastUsed = 0L
    }

    private fun getUsed(): Long {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    private class Entry(val time: Long, val allocation: Float)
}