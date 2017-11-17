package io.trewartha.positional.storage

import android.net.Uri
import android.support.annotation.WorkerThread
import java.io.File

interface FileStorage {

    @WorkerThread
    fun upload(file: File, path: String): Uri?
}