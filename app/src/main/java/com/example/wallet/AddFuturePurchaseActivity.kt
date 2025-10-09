package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddFuturePurchaseActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editAmount: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_future_purchase)

        editTitle = findViewById(R.id.edit_purchase_title)
        editAmount = findViewById(R.id.edit_purchase_amount)
        btnAdd = findViewById(R.id.btn_add_purchase)
        btnCancel = findViewById(R.id.btn_cancel_purchase)

        // غیرفعال کردن دکمه در ابتدا
        updateButtonState()

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editTitle.addTextChangedListener(textWatcher)
        editAmount.addTextChangedListener(textWatcher)

        btnCancel.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {
            addFuturePurchase()
        }
    }

    private fun updateButtonState() {
        val titleFilled = editTitle.text.toString().trim().isNotEmpty()
        val amountFilled = editAmount.text.toString().trim().isNotEmpty()
        btnAdd.isEnabled = titleFilled && amountFilled
        btnAdd.alpha = if (btnAdd.isEnabled) 1f else 0.5f
    }

    private fun addFuturePurchase() {
        val title = editTitle.text.toString().trim()
        val amount = editAmount.text.toString().trim().toIntOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "لطفا مبلغ معتبر وارد کنید", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val purchasesJson = sharedPref.getString("future_purchases_list", "[]")
        val purchasesArray = JSONArray(purchasesJson)

        val newPurchase = JSONObject()
        newPurchase.put("title", title)
        newPurchase.put("amount", amount)
        newPurchase.put("date", dateFormat.format(Date())) // اضافه کردن تاریخ ثبت

        purchasesArray.put(newPurchase)
        sharedPref.edit().putString("future_purchases_list", purchasesArray.toString()).apply()

        Toast.makeText(this, "ثبت شد!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
