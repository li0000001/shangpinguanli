package com.expirytracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expirytracker.data.database.AppDatabase
import com.expirytracker.data.model.Product
import com.expirytracker.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao, application)
    }

    val products: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(
        name: String,
        productionDate: LocalDate,
        shelfLifeDays: Int
    ) {
        viewModelScope.launch {
            val expiryDate = productionDate.plusDays(shelfLifeDays.toLong())
            val product = Product(
                name = name,
                productionDate = productionDate,
                shelfLifeDays = shelfLifeDays,
                expiryDate = expiryDate
            )
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}
