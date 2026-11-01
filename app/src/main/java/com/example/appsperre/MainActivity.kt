package com.example.appsperre

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }

        layout.addView(TextView(this).apply {
            text = "Erlaubte WhatsApp-Kontakte (Name genau wie in WhatsApp):"
            textSize = 16f
        })

        val fields = (0..2).map { i ->
            EditText(this).apply {
                hint = "Kontakt ${i + 1}"
                inputType = InputType.TYPE_CLASS_TEXT
                setText(Config.get(this@MainActivity, i))
            }.also { layout.addView(it, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) }
        }

        layout.addView(Button(this).apply {
            text = "Speichern"
            setOnClickListener {
                val e = prefs.edit()
                fields.forEachIndexed { i, f -> e.putString("c$i", f.text.toString().trim()) }
                e.apply()
                Toast.makeText(this@MainActivity, "Gespeichert", Toast.LENGTH_SHORT).show()
            }
        })

        layout.addView(Button(this).apply {
            text = "1. Löschschutz aktivieren"
            setOnClickListener {
                val admin = ComponentName(this@MainActivity, AdminReceiver::class.java)
                startActivity(Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Schützt die App vor dem Löschen.")
                })
            }
        })

        layout.addView(Button(this).apply {
            text = "2. Sperre aktivieren (Bedienungshilfen)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })

        setContentView(layout)
    }
}
