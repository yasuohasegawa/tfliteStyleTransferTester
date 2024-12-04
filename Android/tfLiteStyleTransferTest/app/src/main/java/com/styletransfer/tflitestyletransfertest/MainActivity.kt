package com.styletransfer.tflitestyletransfertest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.styletransfer.tflitestyletransfertest.ui.theme.TfLiteStyleTransferTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TfLiteStyleTransferTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Test()
                }
            }
        }
    }
}

@Composable
fun Test() {
    val context = LocalContext.current
    var styleTransferHelper by remember { mutableStateOf<StyleTransferHelper?>(null) }
    var selectedStyleBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var selectedContentBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var _inferenceTime by remember { mutableStateOf<Long?>(null) }

    val listener = object : StyleTransferHelper.StyleTransferListener {
        override fun onError(error: String) {
            errorMessage = error
            styleTransferHelper?.clearStyleTransferHelper()
        }

        override fun onResult(bitmap: Bitmap, inferenceTime: Long) {
            resultBitmap = bitmap
            _inferenceTime = inferenceTime
            styleTransferHelper?.clearStyleTransferHelper()
        }
    }

    // onAppear
    LaunchedEffect(Unit) {
        styleTransferHelper = StyleTransferHelper(
            numThreads = 2,
            currentDelegate = 0,
            currentModel = 0,
            context = context,
            styleTransferListener = listener
        )
    }

    val styleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Convert URI to a bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedStyleBitmap = bitmap?.asImageBitmap()

            styleTransferHelper?.setStyleImage(bitmap)
        }
    }

    val contentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Convert URI to a bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedContentBitmap = bitmap?.asImageBitmap()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                selectedStyleBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                selectedContentBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = {
                    styleLauncher.launch("image/*")
                }) {
                    Text("Select Style")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    contentLauncher.launch("image/*")
                }) {
                    Text("Select Content")
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            resultBitmap?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(onClick = {
                if(selectedStyleBitmap != null && selectedContentBitmap != null){
                    selectedContentBitmap?.let { bitmap ->
                        styleTransferHelper?.transfer(bitmap.asAndroidBitmap())
                    }
                }
            }) {
                Text("Run Style Transfer")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TfLiteStyleTransferTestTheme {
        Test()
    }
}