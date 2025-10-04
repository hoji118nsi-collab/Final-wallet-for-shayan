package com.example.wallet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // دکمه عملیات صفحه اصلی
        val operationButton: Button = findViewById(R.id.operation_button)
        operationButton.setOnClickListener {
            showOperationsDialog()
        }
    }

    private fun showOperationsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_operations)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // مهم: اجازه بسته شدن دیالوگ با لمس بیرون صفحه عملیات
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        // دکمه‌ها و لیبل‌ها
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

        // محاسبه موقعیت دایره‌ای دکمه‌ها و لیبل‌ها بعد از layout
        container.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                container.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val centerX = container.width / 2f
                val centerY = container.height / 2f
                val radius = (minOf(centerX, centerY) * 0.7).toFloat() // شعاع 70٪ از نیمه کوچک

                buttons.forEachIndexed { index, button ->
                    val angle = Math.toRadians((index * 360.0 / buttons.size) - 90)
                    val x = centerX + radius * cos(angle) - button.width / 2
                    val y = centerY + radius * sin(angle) - button.height / 2
                    button.x = x.toFloat()
                    button.y = y.toFloat()

                    val label = labels[index]
                    label.x = x + button.width / 2 - label.width / 2
                    label.y = y + button.height + 4 // کمی پایین‌تر از دکمه

                    // انیمیشن Fade + Slide
                    val anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.button_fade_slide)
                    anim.startOffset = (index * 100).toLong()
                    button.startAnimation(anim)
                    label.startAnimation(anim)
                }
            }
        })

        // اتصال Toast تستی به دکمه‌ها
        buttons[0].setOnClickListener { showToastAndDismiss(dialog, "ثبت ورود پول انتخاب شد") }
        buttons[1].setOnClickListener { showToastAndDismiss(dialog, "ثبت خرید جدید انتخاب شد") }
        buttons[2].setOnClickListener { showToastAndDismiss(dialog, "مشاهده خریدهای انجام شده انتخاب شد") }
        buttons[3].setOnClickListener { showToastAndDismiss(dialog, "انتقال به صندوق سرمایه گذاری انتخاب شد") }
        buttons[4].setOnClickListener { showToastAndDismiss(dialog, "لیست خریدهای آتی انتخاب شد") }
        buttons[5].setOnClickListener { showToastAndDismiss(dialog, "مشاهده آمار ماهانه و سالانه انتخاب شد") }

        dialog.show()
    }

    // تابع کمکی برای نمایش Toast و بستن دیالوگ
    private fun showToastAndDismiss(dialog: Dialog, message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }
}
