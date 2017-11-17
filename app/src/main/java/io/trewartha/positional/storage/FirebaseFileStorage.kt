package io.trewartha.positional.storage

import android.net.Uri
import android.support.annotation.WorkerThread
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class FirebaseFileStorage : FileStorage {

    private val firebaseStorage = FirebaseStorage.getInstance()

    @WorkerThread
    override fun upload(file: File, path: String): Uri? {
        val fileInputStream = BufferedInputStream(FileInputStream(file))
        val uploadTask = firebaseStorage.getReference(path).putStream(fileInputStream)
        return Tasks.await(uploadTask).downloadUrl
    }
}