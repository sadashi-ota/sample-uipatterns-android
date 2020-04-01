package com.sadashi.apps.ui.samples.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sadashi.apps.ui.samples.R
import kotlinx.android.synthetic.main.activity_input_password.*

class InputPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_password)

        done.setOnClickListener {
            val message = "${username.text} ${password.text}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}