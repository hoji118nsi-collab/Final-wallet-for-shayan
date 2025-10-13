package com.example.yourapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var walletBalanceText: TextView
    private lateinit var investmentBalanceText: TextView
    private lateinit var btnOperations: Button

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "wallet_prefs"
    private val KEY_WALLET = "wallet_balance"
    private val KEY_INVESTMENT = "investment_balance"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        walletBalanceText = findViewById(R.id.wallet_balance)
        investmentBalanceText = findViewById(R.id.investment_balance)
        btnOperations = findViewById(R.id.btn_operations)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // بارگذاری مقادیر ذخیره شده
        loadBalances()

        btnOperations.setOnClickListener {
            showOperationsDialog()
        }
    }

    private fun loadBalances() {
        val wallet = sharedPreferences.getInt(KEY_WALLET, 0)
        val investment = sharedPreferences.getInt(KEY_INVESTMENT, 0)

        walletBalanceText.text = wallet.toString()
        investmentBalanceText.text = investment.toString()
    }

    private fun saveBalances(wallet: Int, investment: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_WALLET, wallet)
        editor.putInt(KEY_INVESTMENT, investment)
        editor.apply()
    }

    private fun showOperationsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_operations, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // دکمه ورود پول
        val btnAddMoney: Button = dialogView.findViewById(R.id.btn_add_money)
        btnAddMoney.setOnClickListener {
            promptAmount("ورود پول") { amount ->
                val currentWallet = sharedPreferences.getInt(KEY_WALLET, 0)
                val newWallet = currentWallet + amount
                walletBalanceText.text = newWallet.toString()
                saveBalances(newWallet, sharedPreferences.getInt(KEY_INVESTMENT, 0))
                Toast.makeText(this, "پول وارد شد: $amount", Toast.LENGTH_SHORT).show()
            }
        }

        // دکمه خرید جدید
        val btnNewPurchase: Button = dialogView.findViewById(R.id.btn_new_purchase)
        btnNewPurchase.setOnClickListener {
            promptAmount("ثبت خرید جدید") { amount ->
                val currentWallet = sharedPreferences.getInt(KEY_WALLET, 0)
                val newWallet = currentWallet - amount
                walletBalanceText.text = newWallet.toString()
                saveBalances(newWallet, sharedPreferences.getInt(KEY_INVESTMENT, 0))
                Toast.makeText(this, "خرید جدید ثبت شد: $amount", Toast.LENGTH_SHORT).show()
            }
        }

        // دکمه انتقال به صندوق سرمایه گذاری
        val btnTransferInvestment: Button = dialogView.findViewById(R.id.btn_transfer_investment)
        btnTransferInvestment.setOnClickListener {
            promptAmount("انتقال به صندوق سرمایه گذاری") { amount ->
                val currentWallet = sharedPreferences.getInt(KEY_WALLET, 0)
                val currentInvestment = sharedPreferences.getInt(KEY_INVESTMENT, 0)

                val newWallet = currentWallet - amount
                val newInvestment = currentInvestment + amount * 2

                walletBalanceText.text = newWallet.toString()
                investmentBalanceText.text = newInvestment.toString()
                saveBalances(newWallet, newInvestment)
                Toast.makeText(this, "انتقال انجام شد: $amount → صندوق دو برابر", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    // تابع کمکی برای گرفتن مقدار عددی از کاربر
    private fun promptAmount(title: String, onAmountEntered: (Int) -> Unit) {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("تأیید") { _, _ ->
                val input = editText.text.toString()
                val amount = input.toIntOrNull()
                if (amount != null && amount > 0) {
                    onAmountEntered(amount)
                } else {
                    Toast.makeText(this, "مقدار معتبر وارد کنید", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("لغو", null)
            .show()
    }
}
