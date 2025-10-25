package com.expirytracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expirytracker.data.model.Product
import com.expirytracker.ui.theme.ExpiredRed
import com.expirytracker.ui.theme.SafeGreen
import com.expirytracker.ui.theme.WarningOrange
import java.time.format.DateTimeFormatter

@Composable
fun ProductItem(
    product: Product,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val daysUntilExpiry = product.daysUntilExpiry()
    
    val statusColor = when {
        daysUntilExpiry < 0 -> ExpiredRed
        daysUntilExpiry <= 7 -> WarningOrange
        else -> SafeGreen
    }
    
    val statusText = when {
        daysUntilExpiry < 0 -> "已过期 ${-daysUntilExpiry} 天"
        daysUntilExpiry == 0L -> "今天到期"
        daysUntilExpiry == 1L -> "明天到期"
        else -> "还剩 $daysUntilExpiry 天"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "生产日期: ${product.productionDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "到期日期: ${product.expiryDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
