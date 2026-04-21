package edu.cit.quezon.myapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var cbRememberMe: CheckBox
    private lateinit var tvRegister: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val registeredUser = data?.getStringExtra("reg_user")
            val registeredPass = data?.getStringExtra("reg_pass")

            if (registeredUser != null) {
                etUsername.setText(registeredUser)
                etPassword.setText(registeredPass)
                Toast.makeText(this, "Details updated from registration", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        tvRegister = findViewById(R.id.tvRegister)

        val isRemembered = sharedPreferences.getBoolean("remember_me", false)
        if (isRemembered) {
            etUsername.setText(sharedPreferences.getString("saved_username", ""))
            cbRememberMe.isChecked = true
        }

        btnLogin.setOnClickListener { validateLogin() }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            registerLauncher.launch(intent)
        }
    }

    private fun validateLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve registered credentials from SharedPreferences
        val registeredUser = sharedPreferences.getString("registered_user", "admin")
        val registeredPass = sharedPreferences.getString("registered_pass", "1234")

        if (username == registeredUser && password == registeredPass) {
            // Save Remember Me state
            val editor = sharedPreferences.edit()
            if (cbRememberMe.isChecked) {
                editor.putBoolean("remember_me", true)
                editor.putString("saved_username", username)
            } else {
                editor.putBoolean("remember_me", false)
                editor.remove("saved_username")
            }
            editor.apply()

            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

            // Pass data to HomeActivity using Intent
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("logged_in_user", username)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }
}
