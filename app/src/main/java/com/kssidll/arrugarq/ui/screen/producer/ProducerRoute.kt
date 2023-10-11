package com.kssidll.arrugarq.ui.screen.producer


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
) {
    val producer: ProducerViewModel = hiltViewModel()

    ProducerScreen(

    )
}