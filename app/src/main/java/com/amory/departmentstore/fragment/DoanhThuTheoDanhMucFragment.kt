package com.amory.departmentstore.fragment

import RevenueByCategoriesAdapter
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.FragmentDoanhThuTheoDanhMucBinding
import com.amory.departmentstore.model.RevenueByCategoriesModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallDoanhThu
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class DoanhThuTheoDanhMucFragment : Fragment() {
    private var _binding: FragmentDoanhThuTheoDanhMucBinding? = null
    private val binding get() = _binding!!
    private lateinit var listEntries: ArrayList<PieEntry>
    private lateinit var colors: List<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoanhThuTheoDanhMucBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listEntries = arrayListOf()
        showPieChart()
    }

    private fun showPieChart() {
        val service = RetrofitClient.retrofitInstance.create(APICallDoanhThu::class.java)
        val call = service.layDoanhThuTheoDanhMuc()
        call.enqueue(object : Callback<RevenueByCategoriesModel> {
            override fun onResponse(
                call: Call<RevenueByCategoriesModel>,
                response: Response<RevenueByCategoriesModel>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        colors = getColorList(data.size).map { Color.parseColor(it) }
                        for (entry in 0 until data.size) {
                            listEntries.add(PieEntry(data[entry].totalRevenue.toFloat(), data[entry].category.name))
                        }
                        listEntries.sortByDescending { it.value }
                        updatePieChart()
                        val adapter = RevenueByCategoriesAdapter(listEntries, colors)
                        binding.rvItems.adapter = adapter
                        binding.rvItems.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
                    }
                }
            }

            override fun onFailure(call: Call<RevenueByCategoriesModel>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePieChart() {
        binding.pieChart.setUsePercentValues(true)
        if (listEntries.isNotEmpty()) {
            val pieDataSet = PieDataSet(listEntries, "Revenue by Categories")
            pieDataSet.colors = colors
            pieDataSet.setDrawValues(true)
            pieDataSet.valueTextSize = 16f
            pieDataSet.valueFormatter = PercentFormatter(binding.pieChart)
            pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            pieDataSet.valueLinePart1Length = 0.4f
            pieDataSet.valueLinePart2Length = 0.4f
            pieDataSet.valueLineWidth = 1.5f
            pieDataSet.valueLineColor = Color.BLACK
            binding.pieChart.holeRadius = 0f
            binding.pieChart.transparentCircleRadius = 0f
            val pieData = PieData(pieDataSet)

            binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                legend.isEnabled = false
                isRotationEnabled = false
                setDrawEntryLabels(false)
                setEntryLabelColor(Color.BLACK)
                animateY(1400, Easing.EaseInOutQuad)
                invalidate()
            }
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
}