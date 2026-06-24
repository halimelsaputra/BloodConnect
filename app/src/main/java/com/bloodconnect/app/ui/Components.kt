package com.bloodconnect.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloodconnect.app.data.Donor
import com.bloodconnect.app.ui.theme.Hairline
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch
import com.bloodconnect.app.ui.theme.RauschSoft
import com.bloodconnect.app.ui.theme.Success
import com.bloodconnect.app.ui.theme.SurfaceStrong

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Text(subtitle, fontSize = 14.sp, color = Muted)
        }
    }
}

@Composable
fun BloodBadge(type: String, modifier: Modifier = Modifier) {
    Surface(
        color = Rausch,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Text(
            type,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun StatusPill(text: String, positive: Boolean) {
    val bg = if (positive) Color(0xFFE7F8EE) else SurfaceStrong
    val fg = if (positive) Success else Muted
    Surface(color = bg, shape = RoundedCornerShape(999.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(fg))
            Spacer(Modifier.width(6.dp))
            Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DonorCard(
    donor: Donor,
    statusText: String,
    statusPositive: Boolean,
    tierLabel: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(52.dp).clip(CircleShape).background(RauschSoft)
            ) {
                Text(donor.bloodType, color = Rausch, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(donor.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Muted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${donor.location.district}, ${donor.location.city}", color = Muted, fontSize = 13.sp)
                }
                if (!tierLabel.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(tierLabel, color = Rausch, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.width(10.dp))
            StatusPill(statusText, statusPositive)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (selected.isBlank()) placeholder else selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(if (option.isBlank()) placeholder else option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PrimaryButtonContent(label: String) {
    Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
}
