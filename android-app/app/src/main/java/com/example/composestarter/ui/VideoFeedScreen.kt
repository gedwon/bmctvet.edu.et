package com.example.composestarter.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.composestarter.media.MediaStoreRepository
import com.example.composestarter.media.LocalVideo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeedScreen() {
	val context = LocalContext.current
	var hasPermission by remember { mutableStateOf(false) }
	val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
	val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
		hasPermission = granted
	}

	LaunchedEffect(Unit) {
		hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
		if (!hasPermission) launcher.launch(permission)
	}

	Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
		if (!hasPermission) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text("Permission required to show videos")
			}
			return@Surface
		}

		val repository = remember { MediaStoreRepository(context) }
		var videos by remember { mutableStateOf<List<LocalVideo>?>(null) }
		LaunchedEffect(hasPermission) {
			if (hasPermission) {
				videos = repository.loadDeviceVideos()
			}
		}

		if (videos == null) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
			return@Surface
		}

		val pagerState = rememberPagerState(pageCount = { videos!!.size })
		val player = remember {
			ExoPlayer.Builder(context).build().apply {
				repeatMode = Player.REPEAT_MODE_ONE
			}
		}

		LaunchedEffect(pagerState.currentPage, videos) {
			val current: LocalVideo? = videos?.getOrNull(pagerState.currentPage)
			if (current != null) {
				player.setMediaItem(MediaItem.fromUri(current.contentUri))
				player.prepare()
				player.playWhenReady = true
			}
		}

		VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
			val item = videos!![page]
			VideoPlayer(modifier = Modifier.fillMaxSize(), player = player, uri = item.contentUri, isActive = page == pagerState.currentPage)
		}
	}
}

@Composable
private fun VideoPlayer(modifier: Modifier, player: ExoPlayer, uri: Uri, isActive: Boolean) {
	AndroidPlayer(modifier = modifier, player = player)
}

@Composable
private fun AndroidPlayer(modifier: Modifier, player: ExoPlayer) {
	androidx.compose.ui.viewinterop.AndroidView(
		modifier = modifier,
		factory = { context ->
			PlayerView(context).apply {
				useController = false
				this.player = player
			}
		}
	)
}

