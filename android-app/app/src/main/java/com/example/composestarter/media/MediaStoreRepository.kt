package com.example.composestarter.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

data class LocalVideo(
	val contentUri: Uri,
	val displayName: String?,
	val durationMs: Long
)

class MediaStoreRepository(private val context: Context) {

	fun loadDeviceVideos(limit: Int = 200): List<LocalVideo> {
		val resolver: ContentResolver = context.contentResolver
		val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
		} else {
			MediaStore.Video.Media.EXTERNAL_CONTENT_URI
		}

		val projection = arrayOf(
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.DISPLAY_NAME,
			MediaStore.Video.Media.DURATION
		)

		val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"

		val videos = mutableListOf<LocalVideo>()
		resolver.query(
			collection,
			projection,
			null,
			null,
			sortOrder
		)?.use { cursor ->
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
			val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
			val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
			var count = 0
			while (cursor.moveToNext() && count < limit) {
				val id = cursor.getLong(idColumn)
				val name = cursor.getString(nameColumn)
				val duration = cursor.getLong(durationColumn)
				val contentUri = Uri.withAppendedPath(collection, id.toString())
				videos.add(LocalVideo(contentUri, name, duration))
				count++
			}
		}
		return videos
	}
}

