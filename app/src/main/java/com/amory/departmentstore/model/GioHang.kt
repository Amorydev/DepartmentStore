package com.amory.departmentstore.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class GioHang(
    val idsanphamgiohang: Int,
    var tensanphamgiohang: String,
    var giasanphamgiohang: Double,
    var hinhanhsanphamgiohang: String,
    var soluongsanphamgiohang: Int
) : Parcelable