package com.example.wallet

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private lateinit var txtWalletBalance: TextView
    private lateinit var txtInvestmentBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtWalletBalance = findViewById(R.id.txt_wallet_balance)
        txtInvestmentBalance = findViewById(R.id.txt_investment_balance)

        // بارگذاری موجودی‌ها
        loadBalances()

        val operationButton: Button = findViewById(R.id.operation_button)
        operationButton.setOnClickListener {
            showOperationsDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // بروزرسانی موجودی‌ها در بازگشت به صفحه اصلی
        loadBalances()
    }

    private fun loadBalances() {
        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
        val walletBalance = sharedPref.getInt("wallet_balance", 0)
        val investmentBalance = sharedPref.getInt("investment_balance", 0)

        txtWalletBalance.text = "موجودی کیف پول: $walletBalance تومان"
        txtInvestmentBalance.text = "موجودی صندوق: $investmentBalance تومان"
    }

    private fun showOperationsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_operations)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        val buttons = listOf(
            dialog.findViewById<Button>(R.id.btn_add_money),
            dialog.findViewById<Button>(R.id.btn_new_purchase),
            dialog.findViewById<Button>(R.id.btn_view_completed_purchases),
            dialog.findViewById<Button>(R.id.btn_transfer_investment),
            dialog.findViewById<Button>(R.id.btn_future_purchases),
            dialog.findViewById<Button>(R.id.btn_monthly_yearly_stats)
        )

        val labels = listOf(
            dialog.findViewById<TextView>(R.id.label_add_money),
            dialog.findViewById<TextView>(R.id.label_new_purchase),
            dialog.findViewById<TextView>(R.id.label_completed_purchases),
            dialog.findViewById<TextView>(R.id.label_transfer_investment),
            dialog.findViewById<TextView>(R.id.label_future_purchases),
            dialog.findViewById<TextView>(R.id.label_monthly_yearly_stats)
        )

        val container = dialog.findViewById<FrameLayout>(R.id.circle_buttons_container)

        // محاسبه موقعیت دکمه‌ها پس از ترسیم
        container.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                container.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val centerX = container.width / 2f
                val centerY = container.height / 2f
                val radius = (min(centerX, centerY) * 0.7).toFloat()

                buttons.forEachIndexed { index, button ->
                    val angle = Math.toRadians((index * 360.0 / buttons.size) - 90)
                    val x = centerX + radius * cos(angle) - button.width / 2
                    val y = centerY + radius * sin(angle) - button.height / 2
                    button.x = x.toFloat()
                    button.y = y.toFloat()

                    val label = labels[index]
                    label.x = x + button.width / 2 - label.width / 2
                    label.y = y + button.height + 8

                    // افکت ورود
                    val anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.button_fade_slide)
                    anim.startOffset = (index * 100L)
                    button.startAnimation(anim)
                    label.startAnimation(anim)
                }
            }
        })

        // اتصال دکمه‌ها به صفحات مربوطه
        buttons[0].setOnClickListener {
            startActivity(Intent(this, AddMoneyActivity::class.java))
            dialog.dismiss()
        }

        buttons[1].setOnClickListener {
            startActivity(Intent(this, NewPurchaseActivity::class.java))
            dialog.dismiss()
        }

        buttons[2].setOnClickListener {
            startActivity(Intent(this, ViewPurchasesActivity::class.java))
            dialog.dismiss()
        }

        buttons[3].setOnClickListener {
            startActivity(Intent(this, TransferInvestmentActivity::class.java))
            dialog.dismiss()
        }

        buttons[4].setOnClickListener {
            startActivity(Intent(this, FuturePurchasesActivity::class.java))
            dialog.dismiss()
        }

        buttons[5].setOnClickListener {
            startActivity(Intent(this, MonthlyYearlyStatsActivity::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }
}
