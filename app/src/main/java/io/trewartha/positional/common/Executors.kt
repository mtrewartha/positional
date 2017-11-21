package io.trewartha.positional.common

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object Executors {

    private val processors = Runtime.getRuntime().availableProcessors()

    val STORAGE: ScheduledExecutorService = Executors.newScheduledThreadPool(processors)
}