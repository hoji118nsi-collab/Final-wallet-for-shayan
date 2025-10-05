package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PurchasesListActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ساخت ScrollView با LinearLayout داخلی برای لیست خریدها
        val scrollView = ScrollView(this)
        containerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0x88000000.toInt()) // باکس شفاف مشکی برای خوانایی
        }
        scrollView.addView(containerLayout)
        setContentView(scrollView)

        // دریافت بازه تاریخ از Intent
        val fromMillis = intent.getLongExtra("fromDate", 0L)
        val toMillis = intent.getLongExtra("toDate", 0L)
        val fromDate = Calendar.getInstance().apply { timeInMillis = fromMillis }
        val toDate = Calendar.getInstance().apply { timeInMillis = toMillis }

        // بارگذاری لیست خریدها از SharedPreferences
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val purchasesJson = sharedPref.getString("purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)

        // گروه‌بندی خریدها بر اساس عنوان (دسته‌بندی)
        val categories = mutableMapOf<String, MutableList<JSONObject>>()
        for (i in 0 until purchasesArray.length()) {
            val purchase = purchasesArray.getJSONObject(i)
            val date = dateFormat.parse(purchase.getString("date"))
            if (date != null && date.time in fromDate.timeInMillis..toDate.timeInMillis) {
                val category = purchase.getString("title")
                categories.getOrPut(category) { mutableListOf() }.add(purchase)
            }
        }

        // نمایش هر دسته‌بندی و خریدهای مربوطه
        for ((category, purchases) in categories) {
            val categoryText = TextView(this).apply {
                text = category
                textSize = 18f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 16, 0, 8)
            }
            containerLayout.addView(categoryText)

            for (purchase in purchases) {
                val purchaseText = TextView(this).apply {
                    val amount = purchase.getInt("amount")
                    val dateStr = purchase.getString("date")
                    text = "مبلغ: $amount تومان - تاریخ: $dateStr"
                    textSize = 16f
                    setTextColor(0xFFFFFFFF.toInt())
                    setPadding(16, 4, 0, 4)
                }
                containerLayout.addView(purchaseText)
            }
        }

        // اگر هیچ خریدی پیدا نشد
        if (categories.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "هیچ خریدی در بازه انتخاب شده ثبت نشده است."
                textSize = 16f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 16, 0, 16)
            }
            containerLayout.addView(emptyText)
        }
    }
}
