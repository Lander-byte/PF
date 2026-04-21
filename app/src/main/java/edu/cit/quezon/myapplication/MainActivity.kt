package edu.cit.quezon.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var cbRememberMe: CheckBox? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        etUsername = findViewById<EditText?>(R.id.etUsername)
        etPassword = findViewById<EditText?>(R.id.etPassword)
        btnLogin = findViewById<Button?>(R.id.btnLogin)
        cbRememberMe = findViewById<CheckBox?>(R.id.cbRememberMe)

        // --- SharedPreferences: auto-fill saved username if "Remember Me" was checked ---
        val rememberMe = sharedPreferences!!.getBoolean("remember_me", false)
        if (rememberMe) {
            etUsername!!.setText(sharedPreferences!!.getString("saved_username", ""))
            cbRememberMe!!.setChecked(true)
        }

        btnLogin!!.setOnClickListener(View.OnClickListener { v: View? -> validateLogin() })
    }

    private fun validateLogin() {
        val username = etUsername!!.getText().toString().trim { it <= ' ' }
        val password = etPassword!!.getText().toString().trim { it <= ' ' }

        // --- Validation: empty fields ---
        if (username.isEmpty()) {
            etUsername!!.setError("Username is required.")
            etUsername!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword!!.setError("Password is required.")
            etPassword!!.requestFocus()
            return
        }

        // --- Credential check ---
        if (username == VALID_USERNAME && password == VALID_PASSWORD) {
            // --- SharedPreferences: save username & remember-me state ---

            val editor = sharedPreferences!!.edit()
            if (cbRememberMe!!.isChecked()) {
                editor.putString("saved_username", username)
                editor.putBoolean("remember_me", true)
            } else {
                editor.remove("saved_username")
                editor.putBoolean("remember_me", false)
            }
            editor.putBoolean("is_logged_in", true)
            editor.apply()

            Toast.makeText(
                this,
                "Login Successful! Welcome, " + username + "!",
                Toast.LENGTH_SHORT
            ).show()

            // --- Intent: navigate to PropertyListingActivity ---
            val intent: Intent = Intent(this, PropertyListingActivity::class.java)
            intent.putExtra("logged_in_user", username) // pass username via Intent
            startActivity(intent)
            finish() // prevent back-navigation to login
        } else {
            Toast.makeText(
                this,
                "Invalid username or password. Please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        // Hardcoded credentials for demo purposes
        private const val VALID_USERNAME = "admin"
        private const val VALID_PASSWORD = "1234"
        private const val PREFS_NAME = "RealtorPrefs"
    }
}