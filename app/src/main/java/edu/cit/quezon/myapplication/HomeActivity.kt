package edu.cit.quezon.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvUserDetail = findViewById<TextView>(R.id.tvUserDetail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Retrieve data passed from Intent
        val username = intent.getStringExtra("logged_in_user") ?: "User"
        tvUserDetail.text = getString(R.string.logged_in_as, username)

        btnLogout.setOnClickListener {
            // Logout and return to Login screen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
