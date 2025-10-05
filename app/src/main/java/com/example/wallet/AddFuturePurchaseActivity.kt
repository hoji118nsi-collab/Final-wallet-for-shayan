package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class AddFuturePurchaseActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editAmount: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_future_purchase)

        editTitle = findViewById(R.id.edit_purchase_title)
        editAmount = findViewById(R.id.edit_purchase_amount)
        btnAdd = findViewById(R.id.btn_add_purchase)
        btnCancel = findViewById(R.id.btn_cancel_purchase)

        btnAdd.isEnabled = false
        btnAdd.alpha = 0.5f

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val titleFilled = editTitle.text.toString().trim().isNotEmpty()
                val amountFilled = editAmount.text.toString().trim().isNotEmpty()
                btnAdd.isEnabled = titleFilled && amountFilled
                btnAdd.alpha = if (btnAdd.isEnabled) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editTitle.addTextChangedListener(textWatcher)
        editAmount.addTextChangedListener(textWatcher)

        btnCancel.setOnClickListener {
            finish() // بازگشت به صفحه قبل
        }

        btnAdd.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val amount = editAmount.text.toString().trim().toIntOrNull() ?: 0

            val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
            val purchasesJson = sharedPref.getString("future_purchases_list", "[]")
            val purchasesArray = JSONArray(purchasesJson)

            val newPurchase = JSONObject()
            newPurchase.put("title", title)
            newPurchase.put("amount", amount)
            purchasesArray.put(newPurchase)

            sharedPref.edit().putString("future_purchases_list", purchasesArray.toString()).apply()
            finish() // بازگشت به صفحه لیست خریدهای آتی
        }
    }
}
