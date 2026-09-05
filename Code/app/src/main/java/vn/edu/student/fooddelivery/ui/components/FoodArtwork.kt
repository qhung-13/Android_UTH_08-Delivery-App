package vn.edu.student.fooddelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FoodArtwork(name: String, modifier: Modifier = Modifier) {
    val symbol = when {
        name.contains("trà", ignoreCase = true) -> "🧋"
        name.contains("phở", ignoreCase = true) || name.contains("bún", ignoreCase = true) -> "🍜"
        else -> "🍱"
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, fontSize = 54.sp, fontWeight = FontWeight.Bold)
    }
}
