package io.trewartha.positional.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import kotlinx.android.synthetic.main.profile_activity.*


class ProfileActivity : FragmentActivity() {

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        loadUserPhoto()
        loadUserInfo()

        signOutButton.setOnClickListener { onSignOutClick() }
    }

    private fun loadUserInfo() {
        nameTextView.text = user?.displayName
        emailTextView.text = user?.email
    }

    private fun loadUserPhoto() {
        val unknownDrawable = TextDrawable(
                this,
                "?",
                ContextCompat.getColor(this, R.color.gray3),
                ContextCompat.getColor(this, R.color.white)
        )
        GlideApp.with(this)
                .load(user?.photoUrl)
                .placeholder(unknownDrawable)
                .error(unknownDrawable)
                .circleCrop()
                .into(photoImageView)
    }

    private fun onSignOutClick() {
        signOutButton.isEnabled = false
        signOutButton.text = getString(R.string.signing_out)
        progressBar.visibility = View.VISIBLE
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                .addOnFailureListener {
                    signOutButton.text = getString(R.string.sign_out)
                    signOutButton.isEnabled = true
                    Snackbar.make(profileLayout, R.string.sign_out_failed, Snackbar.LENGTH_LONG)
                }
    }

    class IntentBuilder(val context: Context) {

        private var intent = Intent(context, ProfileActivity::class.java)

        fun build(): Intent {
            return intent
        }
    }
}