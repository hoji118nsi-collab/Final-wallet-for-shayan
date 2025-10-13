package com.example.wallet

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val operationButton: Button = findViewById(R.id.operation_button)
        operationButton.setOnClickListener {
            Toast.makeText(this, "دکمه عملیات کار می‌کند ✅", Toast.LENGTH_SHORT).show()
        }
    }
}
