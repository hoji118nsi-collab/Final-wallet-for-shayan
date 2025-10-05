package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NewPurchaseActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var spinnerTitle: Spinner
    private lateinit var btnSelectDate: Button
    private lateinit var editAmount: EditText
    private lateinit var btnConfirm: Button
    private lateinit var btnCancel: Button
    private lateinit var btnThanks: Button

    private var selectedDate: Calendar = Calendar.getInstance() // تاریخ انتخاب شده

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_purchase)

        // بارگذاری المان‌ها
        spinnerTitle = findViewById(R.id.spinner_purchase_title)
        btnSelectDate = findViewById(R.id.btn_select_date)
        editAmount = findViewById(R.id.edit_purchase_amount)
        btnConfirm = findViewById(R.id.btn_confirm_purchase)
        btnCancel = findViewById(R.id.btn_cancel_purchase)
        btnThanks = findViewById(R.id.btn_thanks_purchase)

        val sharedPref = getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)

        // دکمه تایید در ابتدا غیرفعال باشد
        btnConfirm.isEnabled = false
        btnConfirm.alpha = 0.5f

        // فعال/غیرفعال کردن دکمه تایید بر اساس مقدار وارد شده
        editAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = !s.isNullOrEmpty()
                btnConfirm.isEnabled = isNotEmpty
                btnConfirm.alpha = if (isNotEmpty) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // دکمه انتخاب تاریخ خرید
        btnSelectDate.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(supportFragmentManager, "Datepickerdialog")
        }

        // دکمه تایید
        btnConfirm.setOnClickListener {
            val amountText = editAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toInt()
                val currentBalance = sharedPref.getInt("wallet_balance", 0)
                if (amount <= currentBalance) {
                    // کاهش موجودی کیف پول
                    sharedPref.edit().putInt("wallet_balance", currentBalance - amount).apply()

                    // ذخیره خرید جدید در SharedPreferences به صورت JSON
                    val purchasesJson = sharedPref.getString("purchases_list", "[]")
                    val purchasesArray = JSONArray(purchasesJson)
                    val purchaseObject = JSONObject()
                    purchaseObject.put("title", spinnerTitle.selectedItem.toString())
                    purchaseObject.put("amount", amount)
                    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    purchaseObject.put("date", sdf.format(selectedDate.time))
                    purchasesArray.put(purchaseObject)
                    sharedPref.edit().putString("purchases_list", purchasesArray.toString()).apply()

                    // نمایش پیغام موفقیت و دکمه خدایا شکرت
                    btnThanks.visibility = View.VISIBLE
                    btnConfirm.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                    Toast.makeText(this, "با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "مبلغ وارد شده بیشتر از موجودی است", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // دکمه انصراف همیشه فعال
        btnCancel.setOnClickListener {
            finish()
        }

        // دکمه خدایا شکرت
        btnThanks.setOnClickListener {
            finish()
        }
    }

    // فراخوانی وقتی تاریخ انتخاب شد
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDate.set(year, monthOfYear, dayOfMonth)
        btnSelectDate.text = "${year}/${monthOfYear + 1}/${dayOfMonth}" // نمایش تاریخ انتخاب شده روی دکمه
    }
}
