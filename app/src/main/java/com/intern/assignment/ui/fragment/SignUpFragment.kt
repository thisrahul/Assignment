package com.intern.assignment.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.intern.assignment.R
import com.intern.assignment.databinding.FragmentSignUpBinding
import com.intern.assignment.models.User
import com.intern.assignment.util.ChangePageInterface
import com.intern.assignment.util.Utils
import java.util.*


class SignUpFragment : Fragment(), View.OnClickListener {

    private val TAG = "SignUpFragment"

    //binding instance
    private lateinit var binding: FragmentSignUpBinding

    //firbaseAuth instance
    private lateinit var mAuth: FirebaseAuth

    // interface instance
    private lateinit var mInterface: ChangePageInterface

    //progressDialog instance
    private lateinit var progressDialog: ProgressDialog

    //set interface
    fun setInterface(i: ChangePageInterface?) {
        if (i != null) {
            mInterface = i
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //binding
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance()

        //initialize all views
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.checkBox.text = ""


//        //spinner setup of country code selection start here
//        val years = arrayOf("+91", "+92", "+93")
//        val langAdapter = ArrayAdapter<CharSequence>(context.pare, R.layout.spinner_text, years)
//        langAdapter.setDropDownViewResource(R.layout.spinner_layout)
//        binding!!.spinner2.adapter = langAdapter
//        //end

        //set click listener to signup button
        binding.btnSignUp.setOnClickListener(this)
        binding.txtAlreadyAcount.setOnClickListener(this)
    }

    //create acount
    private fun createAccount(email: String, password: String, number: String, name: String) {
        // [START create_user_with_email]
        activity?.let {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    it
                ) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        uploadData(email, password, number, name)
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss()
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //   updateUI(null);
                    }
                }
        }
        // [END create_user_with_email]
    }

    //upload data to firebase database
    private fun uploadData(email: String, password: String, number: String, name: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.reference
        val user = User(name, number, email, password, "EmailPassword")
        myRef.child("Users").child("EmailPassword").child(UUID.randomUUID().toString())
            .setValue(user)
            .addOnSuccessListener { unused ->
                progressDialog.dismiss()
                Toast.makeText(context, "Acount Successfully created!!", Toast.LENGTH_SHORT).show()
                mInterface.openHomeActivity()
            }
            .addOnFailureListener(OnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Something is wrong!!", Toast.LENGTH_SHORT).show()
            })
    }

    //check validation
    private fun isValidate(): Boolean {
        if (binding.etName.text.toString().isEmpty()) {
            binding.etName.error = "Please Enter Valid Name"
            binding.etName.requestFocus()
        } else if (binding.etEmailId.text.toString().isEmpty()
            || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmailId.text.toString()).matches()
        ) {
            binding.etEmailId.error = "Please Enter Valid email id"
            binding.etEmailId.requestFocus()
        } else if (binding.etNumber.text.toString().isEmpty()
            || binding.etNumber.text.toString().length !== 10
        ) {
            binding.etNumber.error = "Please Enter Valid Number"
            binding.etNumber.requestFocus()
        } else if (binding.etPassword.text.toString().isEmpty()
            || binding.etPassword.text.toString().length < 6
        ) {
            binding.etPassword.error = "Please Enter Password"
            binding.etPassword.requestFocus()
        } else if (!binding.checkBox.isChecked) {
            Toast.makeText(context, "Agree to Term and Conditions", Toast.LENGTH_SHORT).show()
        } else {
            return true
        }
        return false
    }

    //click events
    override fun onClick(v: View) {
        if (v.id == R.id.btnSignUp) {
            if (isValidate()) {
                progressDialog = Utils.myDialog(context)!!
                progressDialog.show()
                createAccount(
                    binding.etEmailId.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etNumber.text.toString(),
                    binding.etName.text.toString()
                )
            }
        }

        if (v.id == R.id.txtAlreadyAcount) {
            mInterface.openSignInFragment()
        }
    }
}