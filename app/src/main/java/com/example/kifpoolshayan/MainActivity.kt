package com.example.kifpoolshayan

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // دکمه‌ها را پیدا می‌کنیم
        val walletButton: Button = findViewById(R.id.wallet_balance_button)
        val investmentButton: Button = findViewById(R.id.investment_balance_button)
        val operationButton: Button = findViewById(R.id.operation_button)

        // کلیک روی دکمه‌ها
        walletButton.setOnClickListener {
            Toast.makeText(this, "موجودی کیف پول نمایش داده می‌شود", Toast.LENGTH_SHORT).show()
        }

        investmentButton.setOnClickListener {
            Toast.makeText(this, "موجودی صندوق نمایش داده می‌شود", Toast.LENGTH_SHORT).show()
        }

        operationButton.setOnClickListener {
            Toast.makeText(this, "عملیات انتخاب شد", Toast.LENGTH_SHORT).show()
        }
    }
}
