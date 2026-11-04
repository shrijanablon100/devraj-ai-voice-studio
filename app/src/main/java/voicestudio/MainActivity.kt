
package com.devraj.voicestudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VoiceStudioScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioScreen() {
    val context = LocalContext.current

    var script by remember { mutableStateOf("") }
    var selectedAudio by remember {
        mutableStateOf("कुनै अडियो छानिएको छैन")
    }
    var status by remember {
        mutableStateOf("आफ्नो आवाजको नमुना तयार गर्नुहोस्।")
    }
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordingFile by remember { mutableStateOf<File?>(null) }

    fun startRecording() {
        try {
            val file = File(
                context.filesDir,
                "devraj_voice_${System.currentTimeMillis()}.m4a"
            )

            val newRecorder = MediaRecorder()
            newRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            newRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            newRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            newRecorder.setAudioEncodingBitRate(128000)
            newRecorder.setAudioSamplingRate(44100)
            newRecorder.setOutputFile(file.absolutePath)
            newRecorder.prepare()
            newRecorder.start()

            recorder = newRecorder
            recordingFile = file
            isRecording = true
            status = "आवाज रेकर्ड भइरहेको छ..."
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            isRecording = false
            status = "रेकर्डिङ सुरु भएन: ${e.message}"
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recordingFile?.let {
                selectedAudio = it.name
                status = "आवाज रेकर्ड भयो।"
            }
        } catch (e: Exception) {
            status = "रेकर्डिङ सुरक्षित भएन। फेरि प्रयास गर्नुहोस्।"
            recordingFile?.delete()
        } finally {
            recorder?.release()
            recorder = null
            isRecording = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startRecording()
        } else {
            status = "रेकर्डिङका लागि माइक्रोफोन अनुमति आवश्यक छ।"
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAudio = uri.lastPathSegment ?: "अडियो फाइल चयन भयो"
            status = "अडियो फाइल चयन भयो।"
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Devraj AI Voice Studio") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "तपाईंको आवाज, तपाईंको पहिचान।",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text("नेपाली समाचार र युट्युब भिडियोका लागि व्यक्तिगत भ्वाइस स्टुडियो।")

                ElevatedCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("१. आफ्नो आवाजको नमुना", style = MaterialTheme.typography.titleMedium)

                        Text("सफा आवाजमा कम्तीमा ५ मिनेट रेकर्ड गर्नुहोस्।")

                        Button(
                            onClick = {
                                if (isRecording) {
                                    stopRecording()
                                } else {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (granted) {
                                        startRecording()
                                    } else {
                                        permissionLauncher.launch(
                                            Manifest.permission.RECORD_AUDIO
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (isRecording) "रेकर्डिङ रोक्नुहोस्"
                                else "आवाज रेकर्ड सुरु गर्नुहोस्"
                            )
                        }

                        OutlinedButton(
                            onClick = { audioPicker.launch("audio/*") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRecording
                        ) {
                            Text("अडियो फाइल छान्नुहोस्")
                        }

                        Text(selectedAudio, style = MaterialTheme.typography.bodySmall)
                    }
                }

                ElevatedCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("२. नेपाली स्क्रिप्ट", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = script,
                            onValueChange = { script = it },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                            placeholder = {
                                Text("यहाँ आफ्नो समाचार वा युट्युब स्क्रिप्ट लेख्नुहोस्…")
                            },
                            minLines = 5
                        )

                        Text("${script.length} अक्षर")
                    }
                }

                ElevatedCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("३. आवाजको शैली", style = MaterialTheme.typography.titleMedium)
                        Text("दमदार • भावनात्मक • व्यावसायिक नेपाली प्रस्तोता")

                        Button(
                            onClick = {
                                status = "वास्तविक आवाज क्लोन गर्न एआई सर्भर जोड्न बाँकी छ।"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = script.isNotBlank() &&
                                selectedAudio != "कुनै अडियो छानिएको छैन" &&
                                !isRecording
                        ) {
                            Text("आफ्नै आवाजमा अडियो बनाउनुहोस्")
                        }

                        Text(status, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    "गोपनीयता: आफ्नो आवाज मात्र प्रयोग गर्नुहोस्। वास्तविक क्लोनिङका लागि सुरक्षित एआई सेवा जोड्न बाँकी छ।",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
