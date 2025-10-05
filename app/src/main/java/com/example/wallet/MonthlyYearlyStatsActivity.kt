package com.example.wallet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class MonthlyYearlyStatsActivity : AppCompatActivity() {

    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var monthlyChartContainer: FrameLayout
    private lateinit var yearlyChartContainer: FrameLayout

    private var pieChartMonthly: PieChart? = null
    private var pieChartYearly: PieChart? = null

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_yearly_stats)

        spinnerMonth = findViewById(R.id.spinner_select_month)
        spinnerYear = findViewById(R.id.spinner_select_year)
        monthlyChartContainer = findViewById(R.id.monthly_chart_container)
        yearlyChartContainer = findViewById(R.id.yearly_chart_container)

        val months = arrayOf(
            "فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور",
            "مهر","آبان","آذر","دی","بهمن","اسفند"
        )
        spinnerMonth.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)

        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val purchasesJson = sharedPref.getString("purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)
        val yearsSet = mutableSetOf<Int>()
        for (i in 0 until purchasesArray.length()) {
            val dateStr = purchasesArray.getJSONObject(i).getString("date")
            val date = dateFormat.parse(dateStr)
            if (date != null) {
                val cal = Calendar.getInstance()
                cal.time = date
                yearsSet.add(cal.get(Calendar.YEAR))
            }
        }
        val years = yearsSet.sortedDescending()
        spinnerYear.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        val updateCharts = {
            val selectedMonth = spinnerMonth.selectedItemPosition
            val selectedYear = spinnerYear.selectedItem.toString().toInt()
            updateMonthlyPieChart(selectedMonth, selectedYear)
            updateYearlyPieChart(selectedYear)
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) { updateCharts() }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) { updateCharts() }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        if (months.isNotEmpty() && years.isNotEmpty()) {
            spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH))
            spinnerYear.setSelection(years.indexOf(Calendar.getInstance().get(Calendar.YEAR)))
        }
    }

    private fun createOrUpdatePieChart(container: FrameLayout, existingChart: PieChart?, entries: List<PieEntry>, centerText: String): PieChart {
        val chart = existingChart ?: PieChart(this).also { container.addView(it) }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#F44336"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#FFEB3B"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#9C27B0")
        )
        dataSet.valueTextSize = 14f
        val data = PieData(dataSet)

        chart.data = data
        chart.invalidate()
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setEntryLabelColor(Color.WHITE)
        chart.centerText = centerText
        chart.setCenterTextSize(18f)
        chart.setHoleColor(Color.TRANSPARENT)

        return chart
    }

    private fun updateMonthlyPieChart(month: Int, year: Int) {
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val purchasesJson = sharedPref.getString("purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)

        val categorySums = mutableMapOf<String, Int>()
        for (i in 0 until purchasesArray.length()) {
            val purchase = purchasesArray.getJSONObject(i)
            val date = dateFormat.parse(purchase.getString("date")) ?: continue
            val cal = Calendar.getInstance()
            cal.time = date
            if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month) {
                val category = purchase.getString("title")
                val amount = purchase.getInt("amount")
                categorySums[category] = categorySums.getOrDefault(category, 0) + amount
            }
        }

        if (categorySums.isEmpty()) {
            monthlyChartContainer.removeAllViews()
            val tv = TextView(this)
            tv.text = "هیچ خریدی برای این ماه ثبت نشده است."
            tv.setTextColor(Color.WHITE)
            tv.textSize = 16f
            monthlyChartContainer.addView(tv)
        } else {
            val entries = categorySums.map { PieEntry(it.value.toFloat(), it.key) }
            pieChartMonthly = createOrUpdatePieChart(monthlyChartContainer, pieChartMonthly, entries, "ماه ${month + 1} / $year")
        }
    }

    private fun updateYearlyPieChart(year: Int) {
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val purchasesJson = sharedPref.getString("purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)

        val categorySums = mutableMapOf<String, Int>()
        for (i in 0 until purchasesArray.length()) {
            val purchase = purchasesArray.getJSONObject(i)
            val date = dateFormat.parse(purchase.getString("date")) ?: continue
            val cal = Calendar.getInstance()
            cal.time = date
            if (cal.get(Calendar.YEAR) == year) {
                val category = purchase.getString("title")
                val amount = purchase.getInt("amount")
                categorySums[category] = categorySums.getOrDefault(category, 0) + amount
            }
        }

        if (categorySums.isEmpty()) {
            yearlyChartContainer.removeAllViews()
            val tv = TextView(this)
            tv.text = "هیچ خریدی برای این سال ثبت نشده است."
            tv.setTextColor(Color.WHITE)
            tv.textSize = 16f
            yearlyChartContainer.addView(tv)
        } else {
            val entries = categorySums.map { PieEntry(it.value.toFloat(), it.key) }
            pieChartYearly = createOrUpdatePieChart(yearlyChartContainer, pieChartYearly, entries, "سال $year")
        }
    }
}
