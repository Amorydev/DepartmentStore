package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.manager.DetailProductImages
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.ProductImages

class DescriptionProductViewModel : ViewModel() {
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _productImages = MutableLiveData<ProductImages?>()
    val productImages: LiveData<ProductImages?> get() = _productImages

    private val _productPairs = MutableLiveData<List<Pair<String, String>>>()
    val productPairs: LiveData<List<Pair<String, String>>> get() = _productPairs

    private val _additionalText = MutableLiveData<MutableList<String>?>()
    val additionalText: LiveData<MutableList<String>?> get() = _additionalText

    private val _cartItems = MutableLiveData<MutableList<GioHang>>()
    val cartItems: LiveData<MutableList<GioHang>> get() = _cartItems

    private val _qualityProduct = MutableLiveData<Int>().apply { value = 1 }
    val qualityProduct: LiveData<Int> get() = _qualityProduct

    private var totalValue : Double = 0.0

    fun fetchDataProduct(id: Int) {
        DetailProductImages.getDetailProductImages(id, { product ->
            _product.postValue(product)
            _productImages.postValue(product.productImages)
        }, { _ ->
            _product.postValue(null)
            _productImages.postValue(null)
        })
    }

    fun handleDataToTable(description: String) {
        val productPair = mutableListOf<Pair<String, String>>()
        val additionalText = mutableListOf<String>()

        if (description.isEmpty()) {
            _productPairs.postValue(emptyList())
            return
        }

        if (description.contains(":")) {
            val lines = description.lines()
            var currentKey: String? = null
            val currentValue = StringBuilder()

            for (line in lines) {
                val trimmedLine = line.trim()
                if (trimmedLine.isEmpty()) continue

                if (':' in trimmedLine) {
                    if (currentKey != null) {
                        productPair.add(Pair(currentKey, currentValue.toString().trim()))
                        currentValue.setLength(0)
                    }

                    val parts = trimmedLine.split(':', limit = 2)
                    if (parts.size == 2) {
                        currentKey = parts[0].trim()
                        currentValue.append(parts[1].trim())
                    }
                } else {
                    if (currentKey != null) {
                        if (currentValue.isNotEmpty()) {
                            currentValue.append("\n")
                        }
                        currentValue.append(trimmedLine)
                    } else {
                        additionalText.add(trimmedLine)
                    }
                }
            }

            if (currentKey != null) {
                productPair.add(Pair(currentKey, currentValue.toString().trim()))
            }
        } else {
            val lines = description.lines()
            if (lines[0].length > 50) {
                additionalText.add(lines[0])
                for (i in 1 until lines.size step 2) {
                    if (i + 1 < lines.size) {
                        productPair.add(Pair(lines[i], lines[i + 1]))
                    }
                }
            } else {
                for (i in lines.indices step 2) {
                    if (i + 1 < lines.size) {
                        productPair.add(Pair(lines[i], lines[i + 1]))
                    }
                }
            }
        }
        _productPairs.postValue(productPair)
        _additionalText.postValue(additionalText)
    }

    fun addProductToCart(product: Product) {
        val currentCart = Utils.manggiohang.toMutableList()
        if (Utils.manggiohang.size > 0) {
            val quality = _qualityProduct.value ?: 1
            var flags = false

            for (i in 0 until currentCart.size) {
                if (currentCart[i].idsanphamgiohang == product.id) {
                    currentCart[i].soluongsanphamgiohang += quality
                    val totalMoneyProduct =
                        product.price * currentCart[i].soluongsanphamgiohang
                    currentCart[i].giasanphamgiohang = totalMoneyProduct
                    flags = true
                    break
                }
            }
            if (!flags) {
                val tongGiaTriSanPham = product.price * quality
                val gioHang = GioHang(
                    idsanphamgiohang = product.id,
                    tensanphamgiohang = product.name,
                    giasanphamgiohang = tongGiaTriSanPham,
                    hinhanhsanphamgiohang = product.thumbnail,
                    soluongsanphamgiohang = quality
                )
                currentCart.add(gioHang)
            }
        } else {
            val soluong =  _qualityProduct.value ?: 1
            val tongGiaTriSanPham = product.price * soluong
            val gioHang = GioHang(
                idsanphamgiohang = product.id,
                tensanphamgiohang = product.name,
                giasanphamgiohang = tongGiaTriSanPham,
                hinhanhsanphamgiohang = product.thumbnail,
                soluongsanphamgiohang = soluong
            )
            currentCart.add(gioHang)
        }
        Utils.manggiohang = currentCart
        _cartItems.postValue(currentCart)
    }

    fun plusProduct() {
        val currentQuality = _qualityProduct.value ?: 1
        _qualityProduct.value = currentQuality + 1
    }

    fun minusProduct() {
        val currentQuality = _qualityProduct.value ?: 1
        if (currentQuality > 1) {
            _qualityProduct.value = currentQuality - 1
        }
    }

    fun buyNowProduct(product: Product){
        val quality = _qualityProduct.value ?: 1
        totalValue = product.price * quality
        val gioHang = GioHang(
            idsanphamgiohang = product.id,
            tensanphamgiohang = product.name,
            giasanphamgiohang = totalValue,
            hinhanhsanphamgiohang = product.thumbnail,
            soluongsanphamgiohang = quality
        )
        Utils.mangmuahang.add(gioHang)
    }

}