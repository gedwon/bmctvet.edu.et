package com.example.composestarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composestarter.ui.VideoFeedScreen

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent { ComposeStarterApp() }
	}
}

@Composable
fun ComposeStarterApp() {
	MaterialTheme {
		Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
			VideoFeedScreen()
		}
	}
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	ComposeStarterApp()
}

