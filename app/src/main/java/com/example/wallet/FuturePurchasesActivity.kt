package com.example.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class FuturePurchasesActivity : AppCompatActivity() {

    private lateinit var btnAddNew: Button
    private lateinit var containerLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_purchases)

        btnAddNew = findViewById(R.id.btn_add_new_future_purchase)
        containerLayout = findViewById(R.id.container_future_purchases)

        // بارگذاری لیست خریدهای آتی از SharedPreferences
        loadFuturePurchases()

        // دکمه ثبت مورد جدید
        btnAddNew.setOnClickListener {
            showAddNewDialog()
        }
    }

    private fun loadFuturePurchases() {
        containerLayout.removeAllViews()
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val futureJson = sharedPref.getString("future_purchases_list", "[]")
        val futureArray = JSONArray(futureJson)

        if (futureArray.length() == 0) {
            val emptyText = TextView(this).apply {
                text = "هیچ خرید آتی ثبت نشده است."
                textSize = 16f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 16, 0, 16)
                gravity = Gravity.CENTER
            }
            containerLayout.addView(emptyText)
            return
        }

        for (i in 0 until futureArray.length()) {
            val purchase = futureArray.getJSONObject(i)
            val title = purchase.getString("title")
            val amount = purchase.getInt("amount")

            val purchaseText = TextView(this).apply {
                text = "$title - مبلغ: $amount تومان"
                textSize = 16f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(8, 8, 8, 8)
            }
            containerLayout.addView(purchaseText)
        }
    }

    private fun showAddNewDialog() {
        val dialogLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val editTitle = EditText(this).apply {
            hint = "عنوان خرید"
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0x00000000)
        }
        val editAmount = EditText(this).apply {
            hint = "مبلغ"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0x00000000)
        }

        val btnRegister = Button(this).apply {
            text = "ثبت"
            setBackgroundColor(0x00000000)
            setTextColor(0xFFFFFFFF.toInt())
            isEnabled = false
            alpha = 0.5f
        }

        val btnCancel = Button(this).apply {
            text = "انصراف"
            setBackgroundColor(0x00000000)
            setTextColor(0xFFFFFFFF.toInt())
        }

        dialogLayout.addView(editTitle)
        dialogLayout.addView(editAmount)
        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(btnRegister, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(btnCancel, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(16,0,0,0) })
        }
        dialogLayout.addView(btnLayout)

        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogLayout)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        // فعال‌سازی دکمه ثبت وقتی هر دو فیلد پر شدند
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnRegister.isEnabled = editTitle.text.isNotEmpty() && editAmount.text.isNotEmpty()
                btnRegister.alpha = if (btnRegister.isEnabled) 1f else 0.5f
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        editTitle.addTextChangedListener(textWatcher)
        editAmount.addTextChangedListener(textWatcher)

        btnRegister.setOnClickListener {
            val title = editTitle.text.toString()
            val amount = editAmount.text.toString().toIntOrNull() ?: 0
            if (title.isNotEmpty() && amount > 0) {
                saveNewFuturePurchase(title, amount)
                loadFuturePurchases()
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveNewFuturePurchase(title: String, amount: Int) {
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val futureJson = sharedPref.getString("future_purchases_list", "[]")
        val futureArray = JSONArray(futureJson)

        val newPurchase = JSONObject().apply {
            put("title", title)
            put("amount", amount)
        }

        futureArray.put(newPurchase)
        sharedPref.edit().putString("future_purchases_list", futureArray.toString()).apply()
    }

    // اضافه کردن قابلیت بازگشت به صفحه عملیات با دکمه بک گوشی
    override fun onBackPressed() {
        val intent = Intent(this, OperationsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
