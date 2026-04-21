package edu.cit.quezon.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.realtorapp.MainActivity

class ModifyListingActivity : AppCompatActivity() {
    private var etAddress: EditText? = null
    private var etPrice: EditText? = null
    private var etType: EditText? = null
    private var etSquareFootage: EditText? = null
    private var etBedrooms: EditText? = null
    private var btnBack: Button? = null
    private var btnLogout: Button? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_listing)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        etAddress = findViewById<EditText?>(R.id.etAddressDetail)
        etPrice = findViewById<EditText?>(R.id.etPriceDetail)
        etType = findViewById<EditText?>(R.id.etTypeDetail)
        etSquareFootage = findViewById<EditText?>(R.id.etSqftDetail)
        etBedrooms = findViewById<EditText?>(R.id.etBedroomsDetail)
        btnBack = findViewById<Button?>(R.id.btnBack)
        btnLogout = findViewById<Button?>(R.id.btnLogout)

        // --- Intent: retrieve property data passed from PropertyListingActivity ---
        val intent = getIntent()
        var address = intent.getStringExtra("address")
        var price = intent.getStringExtra("price")
        var type = intent.getStringExtra("type")
        var sqft = intent.getStringExtra("sqft")
        var bedrooms = intent.getStringExtra("bedrooms")

        // Fallback: load from SharedPreferences if Intent data is missing
        if (address == null || address.isEmpty()) {
            address = sharedPreferences!!.getString("draft_address", "N/A")
            price = sharedPreferences!!.getString("draft_price", "N/A")
            type = sharedPreferences!!.getString("draft_type", "N/A")
            sqft = sharedPreferences!!.getString("draft_sqft", "N/A")
            bedrooms = sharedPreferences!!.getString("draft_bedrooms", "N/A")
        }

        // --- Populate read-only fields ---
        etAddress!!.setText(address)
        etPrice!!.setText("₱ " + price)
        etType!!.setText(type)
        etSquareFootage!!.setText(sqft + " sq ft")
        etBedrooms!!.setText(bedrooms + (if (bedrooms == "1") " Bedroom" else " Bedrooms"))

        // All fields are set as read-only in XML (focusable=false / enabled=false)
        // but enforcing here in code as well
        setFieldsReadOnly()

        // Back to Property Listing screen
        btnBack!!.setOnClickListener(View.OnClickListener { v: View? ->
            finish()
        })

        // Logout: clear session and go back to Login screen
        btnLogout!!.setOnClickListener(View.OnClickListener { v: View? -> logout() })
    }

    private fun setFieldsReadOnly() {
        etAddress!!.setFocusable(false)
        etPrice!!.setFocusable(false)
        etType!!.setFocusable(false)
        etSquareFootage!!.setFocusable(false)
        etBedrooms!!.setFocusable(false)
        etAddress!!.setClickable(false)
        etPrice!!.setClickable(false)
        etType!!.setClickable(false)
        etSquareFootage!!.setClickable(false)
        etBedrooms!!.setClickable(false)
    }

    private fun logout() {
        // --- SharedPreferences: clear login session ---
        val editor = sharedPreferences!!.edit()
        editor.putBoolean("is_logged_in", false)
        editor.remove("draft_address")
        editor.remove("draft_price")
        editor.remove("draft_type")
        editor.remove("draft_sqft")
        editor.remove("draft_bedrooms")
        editor.apply()

        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()

        // Return to Login screen and clear back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        private const val PREFS_NAME = "RealtorPrefs"
    }
}