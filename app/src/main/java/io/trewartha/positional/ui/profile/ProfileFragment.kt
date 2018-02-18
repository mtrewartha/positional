package io.trewartha.positional.ui.profile

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.ui.MainActivity
import io.trewartha.positional.ui.TextDrawable
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : Fragment() {

    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadUserPhoto()
        loadUserInfo()

        signOutButton.setOnClickListener { onSignOutClick() }
    }

    private fun loadUserInfo() {
        nameTextInputLayout.text = user?.displayName
        emailTextView.text = user?.email
    }

    private fun loadUserPhoto() {
        val unknownDrawable = TextDrawable(
                context,
                "?",
                ContextCompat.getColor(context, R.color.gray4),
                ContextCompat.getColor(context, R.color.white)
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
                .signOut(activity)
                .addOnCompleteListener {
                    startActivity(Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    activity.finish()
                }
                .addOnFailureListener {
                    signOutButton.text = getString(R.string.sign_out)
                    signOutButton.isEnabled = true
                    Snackbar.make(
                            profileLayout,
                            R.string.sign_out_failed,
                            Snackbar.LENGTH_LONG
                    ).show()
                }
    }
}