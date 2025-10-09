package com.example.wallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OperationActivity : AppCompatActivity() {

    private lateinit var btnAddMoney: Button
    private lateinit var btnNewPurchase: Button
    private lateinit var btnViewCompletedPurchases: Button
    private lateinit var btnTransferInvestment: Button
    private lateinit var btnFuturePurchases: Button
    private lateinit var btnMonthlyYearlyStats: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operations)

        // اتصال دکمه‌ها به المان‌های XML
        btnAddMoney = findViewById(R.id.btn_add_money)
        btnNewPurchase = findViewById(R.id.btn_new_purchase)
        btnViewCompletedPurchases = findViewById(R.id.btn_view_completed_purchases)
        btnTransferInvestment = findViewById(R.id.btn_transfer_investment)
        btnFuturePurchases = findViewById(R.id.btn_future_purchases)
        btnMonthlyYearlyStats = findViewById(R.id.btn_monthly_yearly_stats)

        // تعریف رویداد کلیک‌ها
        btnAddMoney.setOnClickListener {
            val intent = Intent(this, AddMoneyActivity::class.java)
            startActivity(intent)
        }

        btnNewPurchase.setOnClickListener {
            val intent = Intent(this, NewPurchaseActivity::class.java)
            startActivity(intent)
        }

        btnViewCompletedPurchases.setOnClickListener {
            val intent = Intent(this, ViewPurchasesActivity::class.java)
            startActivity(intent)
        }

        btnTransferInvestment.setOnClickListener {
            val intent = Intent(this, TransferInvestmentActivity::class.java)
            startActivity(intent)
        }

        btnFuturePurchases.setOnClickListener {
            val intent = Intent(this, FuturePurchasesActivity::class.java)
            startActivity(intent)
        }

        btnMonthlyYearlyStats.setOnClickListener {
            val intent = Intent(this, MonthlyYearlyStatsActivity::class.java)
            startActivity(intent)
        }
    }
}
