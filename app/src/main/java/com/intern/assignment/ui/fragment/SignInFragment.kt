package com.intern.assignment.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.intern.assignment.R
import com.intern.assignment.databinding.FragmentSignInBinding
import com.intern.assignment.models.User
import com.intern.assignment.util.ChangePageInterface
import com.intern.assignment.util.Utils
import java.util.*


class SignInFragment : Fragment(),View.OnClickListener {

    private val TAG = "SignInFragment"
    private val RC_SIGN_IN = 9001

    //binding instance
    private lateinit var binding: FragmentSignInBinding

    //firebase auth instance
    private lateinit var mAuth: FirebaseAuth

    //insterface instance
    private lateinit var mInterface: ChangePageInterface

    //progressDialog instance
    private lateinit var progressDialog: ProgressDialog

    //GoogleSignInClient instance
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    //CallbackManager instance
    private lateinit var mCallbackManager: CallbackManager

    //set interface
    fun setInterface(i: ChangePageInterface?) {
        if (i != null) {
            mInterface = i
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        //binding
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        //google signin options initialisation
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.intern.assignment.R.string.default_web_client_id))
            .requestEmail()
            .build()

      //  googlesign in client initialisation
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        //facebook
        // Initialize Facebook Login button
        FacebookSdk.sdkInitialize(context)
        mCallbackManager = CallbackManager.Factory.create()

        //firebase auth initialize

        //firebase auth initialize
        mAuth = FirebaseAuth.getInstance()

        //initialize all views

        //initialize all views
        initViews()
        return binding.root
    }

    private fun initViews() {
        //set click listener to the buttons
        binding.btnSignIn.setOnClickListener(this)
        binding.txtForgotPassword.setOnClickListener(this)
        binding.imgFacebook.setOnClickListener(this)
        binding.imgGoogle.setOnClickListener(this)
        binding.txtDontAcount.setOnClickListener(this)

        //initialise progressDialog
        progressDialog = Utils.myDialog(context)!!
    }

    //check all validation
    private fun isValidate(): Boolean {
        if (binding.etEmailId.text.toString().isEmpty()
            || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmailId.text.toString()).matches()
        ) {
            binding.etEmailId.error = "Please Enter Valid email id"
            binding.etEmailId.requestFocus()
        } else if (binding.etPassword.text.toString().isEmpty()
            || binding.etPassword.text.toString().length < 6
        ) {
            binding.etPassword.error = "Please Enter Password"
            binding.etPassword.requestFocus()
        } else {
            return true
        }
        return false
    }

    //click events for buttons
   override fun onClick(v: View) {
        if (v.id == R.id.btnSignIn) {
            if (isValidate()) {
                progressDialog.show()
                signIn(binding.etEmailId.text.toString(), binding.etPassword.text.toString())
            }
        }
        if (v.id == R.id.txtForgotPassword) {
            Toast.makeText(context, "Forgot Password!!", Toast.LENGTH_SHORT).show()
        }
        if (v.id == R.id.imgFacebook) {
          facebookSignIn()
        }
        if (v.id == R.id.imgGoogle) {
          googleSignIn()
        }

        if (v.id == R.id.txtDontAcount) {
           mInterface.openSignUpFragment()
        }
    }

    //google signin
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //sign In with email and password
    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                requireActivity()
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressDialog.dismiss()
                    Toast.makeText(context, "Sign In Succesfully", Toast.LENGTH_SHORT).show()
                    mInterface.openHomeActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.show()
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                progressDialog.dismiss()
                Toast.makeText(context, "Something is wrong!!", Toast.LENGTH_SHORT).show()
            }

            return
        }

        // Pass the activity result back to the Facebook SDK
       mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acount.idToken, null)
        activity?.let {
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(it,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            uploadData(acount.email.toString(), acount.displayName.toString(), "Google")
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            Toast.makeText(context, "Something is wrong!!", Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }

    //upload data to firebase database
    private fun uploadData(email: String, name: String, signInMethod: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val user = User(name, email, signInMethod)
        myRef.child("Users").child(signInMethod).child(UUID.randomUUID().toString()).setValue(user)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Acount Successfully created!!", Toast.LENGTH_SHORT).show()
                mInterface.openHomeActivity()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Something is wrong!!", Toast.LENGTH_SHORT).show()
            }
    }

    //facebook signIn
    private fun facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(
            this@SignInFragment, Arrays.asList("email", "public_profile")
        )
        LoginManager.getInstance()
            .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                }
            })
    }

    //handle facebook access token
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    uploadData(user!!.email!!, user.displayName!!, "Facebook")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}