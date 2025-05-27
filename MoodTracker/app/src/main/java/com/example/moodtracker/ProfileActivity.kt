package com.example.moodtracker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.moodtracker.databinding.ActivityProfileBinding
import com.example.moodtracker.databinding.DialogSupportBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefsHelper: SharedPrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPrefsHelper(this)

        setupToolbar()
        setupProfile()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupProfile() {
        binding.tvUsername.text = prefsHelper.getUsername()
        binding.switchDarkTheme.isChecked = prefsHelper.isDarkMode()
        binding.switchNotifications.isChecked = prefsHelper.getNotificationsEnabled()

        // Apply current theme
        if (prefsHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, MoodHistoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile page
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnChangeUsername.setOnClickListener {
            showChangeUsernameDialog()
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            prefsHelper.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Snackbar.make(
                binding.root,
                if (isChecked) "Dark theme enabled 🌙" else "Light theme enabled ☀️",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefsHelper.setNotificationsEnabled(isChecked)

            Snackbar.make(
                binding.root,
                if (isChecked) "Notifications enabled 🔔" else "Notifications disabled 🔕",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.btnSupport.setOnClickListener {
            showSupportDialog()
        }

        binding.btnSignOut.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showChangeUsernameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_username, null)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editTextUsername)

        editText.setText(prefsHelper.getUsername())

        MaterialAlertDialogBuilder(this)
            .setTitle("✏️ Change Username")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUsername = editText.text.toString().trim()
                if (newUsername.isNotEmpty()) {
                    if (newUsername.length >= 3) {
                        prefsHelper.updateUsername(newUsername)
                        binding.tvUsername.text = newUsername

                        Snackbar.make(
                            binding.root,
                            "Username updated successfully! ✅",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        textInputLayout.error = "Username must be at least 3 characters"
                    }
                } else {
                    textInputLayout.error = "Username cannot be empty"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSupportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_support, null)
        val subjectEditText = dialogView.findViewById<TextInputEditText>(R.id.editTextSubject)
        val messageEditText = dialogView.findViewById<TextInputEditText>(R.id.editTextMessage)
        val subjectLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayoutSubject)
        val messageLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayoutMessage)

        MaterialAlertDialogBuilder(this)
            .setTitle("💬 Contact Support")
            .setMessage("We're here to help! Please describe your issue or feedback.")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val subject = subjectEditText.text.toString().trim()
                val message = messageEditText.text.toString().trim()

                var hasError = false

                if (subject.isEmpty()) {
                    subjectLayout.error = "Subject is required"
                    hasError = true
                } else {
                    subjectLayout.error = null
                }

                if (message.isEmpty()) {
                    messageLayout.error = "Message is required"
                    hasError = true
                } else {
                    messageLayout.error = null
                }

                if (!hasError) {
                    // Here you would typically send the support request to your backend
                    // For now, we'll just show a success message
                    submitSupportRequest(subject, message)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun submitSupportRequest(subject: String, message: String) {
        // Simulate API call or email intent
        Snackbar.make(
            binding.root,
            "Support request sent successfully! We'll get back to you soon. 📧",
            Snackbar.LENGTH_LONG
        ).setAction("OK") { }
            .show()

        // You can also open email client
        // val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        //     data = Uri.parse("mailto:support@moodtracker.com")
        //     putExtra(Intent.EXTRA_SUBJECT, subject)
        //     putExtra(Intent.EXTRA_TEXT, message)
        // }
        // if (emailIntent.resolveActivity(packageManager) != null) {
        //     startActivity(emailIntent)
        // }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🚪 Sign Out")
            .setMessage("Are you sure you want to sign out? Your mood data will be saved.")
            .setIcon(R.drawable.ic_logout)
            .setPositiveButton("Yes, Sign Out") { _, _ ->
                prefsHelper.logout()

                Snackbar.make(
                    binding.root,
                    "Signed out successfully! See you soon! 👋",
                    Snackbar.LENGTH_SHORT
                ).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}