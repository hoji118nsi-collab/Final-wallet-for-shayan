package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddMoneyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)

        val spinnerTitle: Spinner = findViewById(R.id.spinner_title)
        val editAmount: EditText = findViewById(R.id.edit_amount)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnCancel: Button = findViewById(R.id.btn_cancel)
        val btnThanks: Button = findViewById(R.id.btn_thanks)

        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)

        // اول دکمه تایید غیرفعال باشه
        btnConfirm.isEnabled = false
        btnConfirm.alpha = 0.5f

        // بررسی تغییر متن برای فعال/غیرفعال کردن دکمه تایید
        editAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amountValid = !s.isNullOrEmpty() && s.toString().toIntOrNull() != null
                btnConfirm.isEnabled = amountValid
                btnConfirm.alpha = if (amountValid) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // دکمه تایید
        btnConfirm.setOnClickListener {
            val amountText = editAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toInt()
                val currentBalance = sharedPref.getInt("wallet_balance", 0)
                sharedPref.edit()
                    .putInt("wallet_balance", currentBalance + amount)
                    .apply()

                Toast.makeText(this, "با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                btnThanks.visibility = View.VISIBLE
            }
        }

        // دکمه انصراف
        btnCancel.setOnClickListener {
            finish()
        }

        // دکمه خدایا شکرت
        btnThanks.setOnClickListener {
            finish()
        }
    }

    companion object {
        // تابع کمکی برای گرفتن موجودی از SharedPreferences
        fun getWalletBalance(context: Context): Int {
            val prefs = context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
            return prefs.getInt("wallet_balance", 0)
        }
    }
}
