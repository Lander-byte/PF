package edu.cit.quezon.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PropertyListingActivity : AppCompatActivity() {
    private var etAddress: EditText? = null
    private var etPrice: EditText? = null
    private var etType: EditText? = null
    private var etSquareFootage: EditText? = null
    private var etBedrooms: EditText? = null
    private var btnSave: Button? = null
    private var tvWelcome: TextView? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_listing)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        etAddress = findViewById<EditText?>(R.id.etAddress)
        etPrice = findViewById<EditText?>(R.id.etPrice)
        etType = findViewById<EditText?>(R.id.etType)
        etSquareFootage = findViewById<EditText?>(R.id.etSquareFootage)
        etBedrooms = findViewById<EditText?>(R.id.etBedrooms)
        btnSave = findViewById<Button?>(R.id.btnSave)
        tvWelcome = findViewById<TextView?>(R.id.tvWelcome)

        // --- Intent: retrieve username passed from Login screen ---
        val loggedInUser = getIntent().getStringExtra("logged_in_user")
        if (loggedInUser != null) {
            tvWelcome!!.setText("Logged in as: " + loggedInUser)
        }

        // --- SharedPreferences: restore last saved listing (draft) ---
        restoreDraft()

        btnSave!!.setOnClickListener(View.OnClickListener { v: View? -> validateAndSave() })
    }

    /** Restore any previously saved draft from SharedPreferences  */
    private fun restoreDraft() {
        etAddress!!.setText(sharedPreferences!!.getString("draft_address", ""))
        etPrice!!.setText(sharedPreferences!!.getString("draft_price", ""))
        etType!!.setText(sharedPreferences!!.getString("draft_type", ""))
        etSquareFootage!!.setText(sharedPreferences!!.getString("draft_sqft", ""))
        etBedrooms!!.setText(sharedPreferences!!.getString("draft_bedrooms", ""))
    }

    private fun validateAndSave() {
        val address = etAddress!!.getText().toString().trim { it <= ' ' }
        val price = etPrice!!.getText().toString().trim { it <= ' ' }
        val type = etType!!.getText().toString().trim { it <= ' ' }
        val sqft = etSquareFootage!!.getText().toString().trim { it <= ' ' }
        val bedrooms = etBedrooms!!.getText().toString().trim { it <= ' ' }

        // --- Validation ---
        if (address.isEmpty()) {
            etAddress!!.setError("Property Address is required.")
            etAddress!!.requestFocus()
            return
        }
        if (price.isEmpty()) {
            etPrice!!.setError("Price is required.")
            etPrice!!.requestFocus()
            return
        }
        if (!isValidNumber(price)) {
            etPrice!!.setError("Price must be a valid number.")
            etPrice!!.requestFocus()
            return
        }
        if (type.isEmpty()) {
            etType!!.setError("Property Type is required.")
            etType!!.requestFocus()
            return
        }
        if (sqft.isEmpty()) {
            etSquareFootage!!.setError("Square Footage is required.")
            etSquareFootage!!.requestFocus()
            return
        }
        if (!isValidNumber(sqft)) {
            etSquareFootage!!.setError("Square Footage must be a valid number.")
            etSquareFootage!!.requestFocus()
            return
        }
        if (bedrooms.isEmpty()) {
            etBedrooms!!.setError("Number of Bedrooms is required.")
            etBedrooms!!.requestFocus()
            return
        }
        if (!isValidInteger(bedrooms)) {
            etBedrooms!!.setError("Bedrooms must be a whole number.")
            etBedrooms!!.requestFocus()
            return
        }

        // --- SharedPreferences: save listing as draft ---
        val editor = sharedPreferences!!.edit()
        editor.putString("draft_address", address)
        editor.putString("draft_price", price)
        editor.putString("draft_type", type)
        editor.putString("draft_sqft", sqft)
        editor.putString("draft_bedrooms", bedrooms)
        editor.apply()

        Toast.makeText(
            this,
            "Listing saved successfully! Proceeding to details...",
            Toast.LENGTH_SHORT
        ).show()

        // --- Intent: pass all property data to ModifyListingActivity ---
        val intent: Intent = Intent(this, ModifyListingActivity::class.java)
        intent.putExtra("address", address)
        intent.putExtra("price", price)
        intent.putExtra("type", type)
        intent.putExtra("sqft", sqft)
        intent.putExtra("bedrooms", bedrooms)
        startActivity(intent)
    }

    private fun isValidNumber(value: String): Boolean {
        try {
            value.toDouble()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    private fun isValidInteger(value: String): Boolean {
        try {
            val v = value.toInt()
            return v > 0
        } catch (e: NumberFormatException) {
            return false
        }
    }

    companion object {
        private const val PREFS_NAME = "RealtorPrefs"
    }
}