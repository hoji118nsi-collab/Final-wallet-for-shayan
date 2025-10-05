package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TransferInvestmentActivity : AppCompatActivity() {

    private lateinit var editAmount: EditText
    private lateinit var btnTransfer: Button
    private lateinit var btnCancel: Button
    private lateinit var successLayout: LinearLayout
    private lateinit var btnConfirmSuccess: Button
    private lateinit var tvSuccessMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_investment)

        editAmount = findViewById(R.id.edit_investment_amount)
        btnTransfer = findViewById(R.id.btn_transfer_investment)
        btnCancel = findViewById(R.id.btn_cancel_investment)

        // ساخت و اضافه کردن باکس پیام موفقیت به صورت برنامه‌ای
        successLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0x88000000.toInt()) // باکس شفاف مشکی
            setPadding(16, 16, 16, 16)
            visibility = View.GONE
        }
        tvSuccessMessage = TextView(this).apply {
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
        }
        btnConfirmSuccess = Button(this).apply {
            text = "تایید"
            setBackgroundColor(0x00000000)
            setTextColor(0xFFFFFFFF.toInt())
        }
        successLayout.addView(tvSuccessMessage)
        successLayout.addView(btnConfirmSuccess)
        (findViewById<LinearLayout>(R.id.root_layout) ?: findViewById(android.R.id.content) as LinearLayout)
            .addView(successLayout)

        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        var currentWallet = sharedPref.getInt("wallet_balance", 0)
        var currentInvestment = sharedPref.getInt("investment_balance", 0)

        // دکمه انتقال در ابتدا غیرفعال باشد
        btnTransfer.isEnabled = false
        btnTransfer.alpha = 0.5f

        // فعال/غیرفعال کردن دکمه انتقال بر اساس مقدار وارد شده
        editAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = !s.isNullOrEmpty() && s.toString().toIntOrNull() ?: 0 > 0
                btnTransfer.isEnabled = isNotEmpty
                btnTransfer.alpha = if (isNotEmpty) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnTransfer.setOnClickListener {
            val amountText = editAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toInt()
                if (amount <= currentWallet) {
                    // کاهش موجودی کیف پول
                    currentWallet -= amount
                    sharedPref.edit().putInt("wallet_balance", currentWallet).apply()

                    // افزایش سرمایه گذاری (دو برابر مبلغ منتقل شده)
                    currentInvestment += (amount * 2)
                    sharedPref.edit().putInt("investment_balance", currentInvestment).apply()

                    // نمایش پیام موفقیت و دکمه تایید
                    tvSuccessMessage.text = "انتقال با موفقیت انجام شد.\nموجودی صندوق: $currentInvestment تومان"
                    successLayout.visibility = View.VISIBLE
                    btnTransfer.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                } else {
                    Toast.makeText(this, "مبلغ انتخابی بیشتر از موجودی کیف پول شماست", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            finish() // بازگشت به صفحه عملیات
        }

        btnConfirmSuccess.setOnClickListener {
            finishAffinity() // بازگشت به صفحه اصلی
        }
    }
}
