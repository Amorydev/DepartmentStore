package com.amory.departmentstore.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.amory.departmentstore.databinding.FragmentDoanhThuTheoThoiGianBinding
import com.amory.departmentstore.model.RevenueByTimeModel
import com.amory.departmentstore.model.RevenueByYearModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallDoanhThu
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random

class DoanhThuTheoThoiGianFragment : Fragment() {

    private var _binding: FragmentDoanhThuTheoThoiGianBinding? = null
    private val binding get() = _binding!!
    private lateinit var colors: List<Int>
    private lateinit var listBarChart: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoanhThuTheoThoiGianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listBarChart = arrayListOf()
        showBarChart()
        onCLickChonNgay()
    }

    private fun showBarChart() {
        val service = RetrofitClient.retrofitInstance.create(APICallDoanhThu::class.java)
        val call = service.layDoanhThuTheoNam(2024)
        call.enqueue(object : Callback<RevenueByYearModel> {
            override fun onResponse(
                call: Call<RevenueByYearModel>,
                response: Response<RevenueByYearModel>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    val currentDate = LocalDate.now()
                    val startOfYear = LocalDate.of(currentDate.year, 1, 1)
                    val months = ChronoUnit.MONTHS.between(startOfYear, currentDate).toInt() + 1

                    if (data != null) {
                        colors = getColorList(months).map { Color.parseColor(it) }
                        for (entry in 0..months) {
                            listBarChart.add(
                                BarEntry(
                                    (entry + 1).toFloat(),
                                    data[entry].total_revenue.toFloat()
                                )
                            )
                        }
                        updateBarChart()
                    }
                }
            }

            override fun onFailure(call: Call<RevenueByYearModel>, t: Throwable) {
            }
        })
    }

    private fun updateBarChart() {
        val barDataSet = BarDataSet(listBarChart, "")
        barDataSet.colors = colors
        barDataSet.barBorderWidth = 0.4f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barDataSet.valueTextSize = 16f
        barDataSet.setDrawValues(false)

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            description.text = "Doanh Thu Theo Tháng (2024)"
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            val monthNames = listOf(
                "Tháng 1",
                "Tháng 2",
                "Tháng 3",
                "Tháng 4",
                "Tháng 5",
                "Tháng 6",
                "Tháng 7",
                "Tháng 8",
                "Tháng 9",
                "Tháng 10",
                "Tháng 11",
                "Tháng 12"
            )

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val monthIndex = value.toInt() - 1
                    return if (monthIndex in monthNames.indices) {
                        monthNames[monthIndex]
                    } else {
                        ""
                    }
                }
            }
            legend.isEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun generateRandomBrightColor(): String {
        val random = Random()
        val red = random.nextInt(128) + 128
        val green = random.nextInt(128) + 128
        val blue = random.nextInt(128) + 128
        return String.format("#%02X%02X%02X", red, green, blue)
    }

    private fun getColorList(size: Int): List<String> {
        val colors = mutableListOf(
            "#FF5733",
            "#33FF57",
            "#3357FF",
            "#FF33A8",
            "#FF8C33",
            "#33FFF6",
            "#8D33FF",
            "#FF3333",
            "#33FF8D",
            "#3333FF"
        )

        if (size > colors.size) {
            repeat(size - colors.size) {
                var newColor: String
                do {
                    newColor = generateRandomBrightColor()
                } while (colors.contains(newColor))
                colors.add(newColor)
            }
        }

        return colors
    }

    @SuppressLint("SimpleDateFormat")
    private fun onCLickChonNgay() {
        setUpDatePicker(binding.startDateET)
        setUpDatePicker(binding.endDateET)
        binding.btnXemKetQua.setOnClickListener {
            val startDate = binding.startDateET.text?.trim().toString()
            val endDate = binding.endDateET.text?.trim().toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val startDateObj = sdf.parse(startDate)
                val endDateObj = sdf.parse(endDate)
                if (startDateObj != null && endDateObj != null && endDateObj.before(startDateObj)) {

                    Toast.makeText(
                        context,
                        "Ngày kết thúc không thể nhỏ hơn ngày bắt đầu",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            /*Log.d("startDate",startDate.toString())
            Log.d("startDate",endDate.toString())*/
            val service = RetrofitClient.retrofitInstance.create(APICallDoanhThu::class.java)
            val call = service.layDoanhThuTheoThoiGian(startDate, endDate)
            call.enqueue(object : Callback<RevenueByTimeModel> {
                override fun onResponse(
                    call: Call<RevenueByTimeModel>,
                    response: Response<RevenueByTimeModel>
                ) {
                    if (response.isSuccessful) {
                        binding.txtTong.text = formatAmount(response.body()?.data)
                    }
                }

                override fun onFailure(call: Call<RevenueByTimeModel>, t: Throwable) {
                }
            })
        }
    }

    private fun formatAmount(amount: Double?): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(amount)}đ"
    }

    private fun setUpDatePicker(btn: Button) {
        // Tạo một MaterialDatePicker với loại ngày là RANGE_SELECTION
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Khi người dùng chọn một ngày, cập nhật giá trị của EditText
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val selectedDate = calendar.time
            btn.setText(formatDate(selectedDate))
        }

        btn.setOnClickListener {
            datePicker.show(childFragmentManager, "DATE_PICKER")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date.time)
    }

}