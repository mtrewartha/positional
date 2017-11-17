package io.trewartha.positional.storage

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object Executors {

    val STORAGE: ScheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
}