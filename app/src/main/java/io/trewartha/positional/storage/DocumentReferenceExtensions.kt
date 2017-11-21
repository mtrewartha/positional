package io.trewartha.positional.storage

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import io.trewartha.positional.common.Executors
import io.trewartha.positional.common.Log
import java.util.concurrent.Callable


fun DocumentReference.deleteCollection(collectionPath: String, batchSize: Int): Task<Void> {
    return Tasks.call(Executors.STORAGE, Callable<Void> {
        val collection = collection(collectionPath)

        // Get the first batch of documents in the collection
        var query = collection.orderBy(FieldPath.documentId()).limit(batchSize.toLong())

        // Get a list of deleted documents
        Log.info("Delete", "Deleting query results...")
        var deletedBatch = deleteQueryResults(query)

        // While the deleted documents in the last batch indicate that there
        // may still be more documents in the collection, page down to the
        // next batch and delete again
        while (deletedBatch.size >= batchSize) {
            // Move the query cursor to start after the last doc in the batch
            Log.info("Delete", "Might need to delete more query results...")
            val last = deletedBatch[deletedBatch.size - 1]
            query = collection.orderBy(FieldPath.documentId())
                    .startAfter(last.id)
                    .limit(batchSize.toLong())

            deletedBatch = deleteQueryResults(query)
        }
        null
    })
}

private fun DocumentReference.deleteQueryResults(query: Query): List<DocumentSnapshot> {
    val querySnapshot = Tasks.await(query.get())
    val batch = query.firestore.batch()
    for (snapshot in querySnapshot) {
        batch.delete(snapshot.reference)
        Log.info("Delete", "Adding delete to batch...")
    }
    Tasks.await<Void>(batch.commit())
    Log.info("Delete", "Committed delete batch")
    return querySnapshot.documents
}