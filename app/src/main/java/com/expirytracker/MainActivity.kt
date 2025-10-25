package com.expirytracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expirytracker.ui.screens.AddProductScreen
import com.expirytracker.ui.screens.ProductListScreen
import com.expirytracker.ui.theme.ExpiryTrackerTheme
import com.expirytracker.ui.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.READ_CALENDAR] == true &&
                permissions[Manifest.permission.WRITE_CALENDAR] == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpiryTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpiryTrackerApp(
                        hasCalendarPermission = { hasCalendarPermission() },
                        requestCalendarPermission = { requestCalendarPermission() }
                    )
                }
            }
        }
    }

    private fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCalendarPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            )
        )
    }
}

@Composable
fun ExpiryTrackerApp(
    hasCalendarPermission: () -> Boolean,
    requestCalendarPermission: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val navController = rememberNavController()
    val products by viewModel.products.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "product_list"
    ) {
        composable("product_list") {
            ProductListScreen(
                products = products,
                onAddProduct = {
                    navController.navigate("add_product")
                },
                onDeleteProduct = { product ->
                    viewModel.deleteProduct(product)
                }
            )
        }

        composable("add_product") {
            AddProductScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveProduct = { name, productionDate, shelfLifeDays ->
                    if (!hasCalendarPermission()) {
                        requestCalendarPermission()
                    }
                    viewModel.addProduct(name, productionDate, shelfLifeDays)
                }
            )
        }
    }
}
