package com.example.wallet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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

        containerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.parseColor("#88000000"))
        }

        val scrollView = ScrollView(this)
        scrollView.addView(containerLayout)
        setContentView(scrollView)

        displayPurchases()
    }

    private fun displayPurchases() {
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)

        // خواندن خریدهای انجام شده
        val purchasesJson = sharedPref.getString("purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)

        // دریافت بازه تاریخ از Intent، اگر موجود نباشد همه را نمایش بده
        val fromMillis = intent.getLongExtra("fromDate", Long.MIN_VALUE)
        val toMillis = intent.getLongExtra("toDate", Long.MAX_VALUE)
        val fromDate = Calendar.getInstance().apply { timeInMillis = fromMillis }
        val toDate = Calendar.getInstance().apply { timeInMillis = toMillis }

        val categories = mutableMapOf<String, MutableList<JSONObject>>()
        for (i in 0 until purchasesArray.length()) {
            val purchase = purchasesArray.getJSONObject(i)
            val dateStr = purchase.optString("date", "")
            val date = try { dateFormat.parse(dateStr) } catch (e: Exception) { null }

            if (date != null && date.time in fromDate.timeInMillis..toDate.timeInMillis) {
                val category = purchase.optString("title", "نامشخص")
                categories.getOrPut(category) { mutableListOf() }.add(purchase)
            }
        }

        if (categories.isEmpty()) {
            addEmptyMessage("هیچ خریدی در بازه انتخاب شده ثبت نشده است.")
        } else {
            categories.forEach { (category, purchases) ->
                addCategoryHeader(category)
                purchases.forEach { addPurchaseItem(it) }
            }
        }
    }

    private fun addCategoryHeader(title: String) {
        val textView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 16, 0, 8)
        }
        containerLayout.addView(textView)
    }

    private fun addPurchaseItem(purchase: JSONObject) {
        val amount = purchase.optInt("amount", 0)
        val date = purchase.optString("date", "--/--/----")
        val textView = TextView(this).apply {
            text = "مبلغ: $amount تومان - تاریخ: $date"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(16, 4, 0, 4)
        }
        containerLayout.addView(textView)
    }

    private fun addEmptyMessage(message: String) {
        val textView = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 16, 0, 16)
        }
        containerLayout.addView(textView)
    }
}
