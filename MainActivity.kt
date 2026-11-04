package com.devraj.voicestudio

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
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VoiceStudioScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioScreen() {
    var script by remember { mutableStateOf("") }
    var selectedAudio by remember { mutableStateOf("कुनै अडियो छानिएको छैन") }
    var status by remember { mutableStateOf("आफ्नो आवाजको नमुना र स्क्रिप्ट तयार गर्नुहोस्।") }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAudio = uri.lastPathSegment ?: "अडियो फाइल चयन भयो"
            status = "आवाजको फाइल चयन भयो। क्लोनिङका लागि एआई सर्भर जोड्न बाँकी छ।"
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Devraj AI Voice Studio") })
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
                Text("तपाईंको आवाज, तपाईंको पहिचान।", style = MaterialTheme.typography.headlineSmall)
                Text("नेपाली समाचार र युट्युब भिडियोका लागि व्यक्तिगत भ्वाइस स्टुडियो।")

                ElevatedCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("१. आफ्नो आवाजको नमुना", style = MaterialTheme.typography.titleMedium)
                        Text("सफा रेकर्डिङ वा अडियो फाइल छान्नुहोस्। ५ मिनेट वा बढीको नमुना उपयुक्त हुन्छ।")
                        OutlinedButton(onClick = { audioPicker.launch("audio/*") }) {
                            Text("अडियो फाइल छान्नुहोस्")
                        }
                        Text(selectedAudio, style = MaterialTheme.typography.bodySmall)
                    }
                }

                ElevatedCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("२. नेपाली स्क्रिप्ट", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = script,
                            onValueChange = { script = it },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                            placeholder = { Text("यहाँ आफ्नो समाचार वा युट्युब स्क्रिप्ट लेख्नुहोस्…") },
                            minLines = 5
                        )
                        Text("${script.length} अक्षर", style = MaterialTheme.typography.labelSmall)
                    }
                }

                ElevatedCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("३. आवाजको शैली", style = MaterialTheme.typography.titleMedium)
                        Text("दमदार • भावनात्मक • व्यावसायिक नेपाली प्रस्तोता")
                        Button(
                            onClick = {
                                status = "अहिले यो प्रारम्भिक एप नमुना हो। वास्तविक आवाज क्लोन गर्न सुरक्षित एआई ब्याकएन्ड/API जोड्न बाँकी छ।"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = script.isNotBlank() && selectedAudio != "कुनै अडियो छानिएको छैन"
                        ) {
                            Text("आफ्नै आवाजमा अडियो बनाउनुहोस्")
                        }
                        Text(status, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    "गोपनीयता: तपाईंको आफ्नै आवाज मात्र प्रयोग गर्नुहोस्। वास्तविक क्लोनिङ सुरु गर्नुअघि अडियो सुरक्षित रूपमा पठाउने र मेटाउने व्यवस्था जोडिनेछ।",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
