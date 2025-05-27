package com.example.moodtracker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : AppCompatActivity() {

    private lateinit var logoImage: ImageView
    private lateinit var appName: TextView
    private lateinit var tagline: TextView
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View
    private lateinit var versionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Make the splash screen fullscreen
        setupFullscreen()

        // Initialize views
        initViews()

        // Start animations
        startAnimations()

        val prefsHelper = SharedPrefsHelper(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (prefsHelper.isLoggedIn()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500) // Extended to 2.5 seconds to accommodate animations
    }

    private fun setupFullscreen() {
        // Hide system bars for immersive experience
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun initViews() {
        logoImage = findViewById(R.id.logo_image)
        appName = findViewById(R.id.app_name)
        tagline = findViewById(R.id.tagline)
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)
        versionText = findViewById(R.id.version_text)
    }

    private fun startAnimations() {
        // Logo animation - scale and fade in
        animateLogo()

        // Text animations - slide up and fade in
        animateTexts()

        // Loading dots animation
        animateLoadingDots()
    }

    private fun animateLogo() {
        logoImage.alpha = 0f
        logoImage.scaleX = 0.3f
        logoImage.scaleY = 0.3f

        val fadeIn = ObjectAnimator.ofFloat(logoImage, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(logoImage, "scaleX", 0.3f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(logoImage, "scaleY", 0.3f, 1.1f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, scaleX, scaleY)
        animatorSet.duration = 800
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun animateTexts() {
        // App name animation
        appName.alpha = 0f
        appName.translationY = 50f

        val appNameFade = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f)
        val appNameSlide = ObjectAnimator.ofFloat(appName, "translationY", 50f, 0f)

        val appNameSet = AnimatorSet()
        appNameSet.playTogether(appNameFade, appNameSlide)
        appNameSet.duration = 600
        appNameSet.startDelay = 300
        appNameSet.interpolator = AccelerateDecelerateInterpolator()
        appNameSet.start()

        // Tagline animation
        tagline.alpha = 0f
        tagline.translationY = 30f

        val taglineFade = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f)
        val taglineSlide = ObjectAnimator.ofFloat(tagline, "translationY", 30f, 0f)

        val taglineSet = AnimatorSet()
        taglineSet.playTogether(taglineFade, taglineSlide)
        taglineSet.duration = 600
        taglineSet.startDelay = 500
        taglineSet.interpolator = AccelerateDecelerateInterpolator()
        taglineSet.start()

        // Version text animation
        versionText.alpha = 0f
        val versionFade = ObjectAnimator.ofFloat(versionText, "alpha", 0f, 1f)
        versionFade.duration = 400
        versionFade.startDelay = 800
        versionFade.start()
    }

    private fun animateLoadingDots() {
        Handler(Looper.getMainLooper()).postDelayed({
            startDotAnimation(dot1, 0)
            startDotAnimation(dot2, 200)
            startDotAnimation(dot3, 400)
        }, 1000)
    }

    private fun startDotAnimation(dot: View, delay: Long) {
        val scaleUp = ScaleAnimation(
            1f, 1.5f, 1f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        val scaleDown = ScaleAnimation(
            1.5f, 1f, 1.5f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        val alphaUp = AlphaAnimation(0.5f, 1f)
        val alphaDown = AlphaAnimation(1f, 0.5f)

        val animationSet = AnimationSet(true)
        scaleUp.duration = 300
        scaleDown.duration = 300
        alphaUp.duration = 300
        alphaDown.duration = 300

        scaleUp.startOffset = 0
        alphaUp.startOffset = 0
        scaleDown.startOffset = 300
        alphaDown.startOffset = 300

        animationSet.addAnimation(scaleUp)
        animationSet.addAnimation(scaleDown)
        animationSet.addAnimation(alphaUp)
        animationSet.addAnimation(alphaDown)
        animationSet.duration = 600
        animationSet.repeatCount = Animation.INFINITE
        animationSet.repeatMode = Animation.REVERSE

        Handler(Looper.getMainLooper()).postDelayed({
            dot.startAnimation(animationSet)
        }, delay)
    }
}