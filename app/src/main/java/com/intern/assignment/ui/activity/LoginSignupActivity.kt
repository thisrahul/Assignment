package com.intern.assignment.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.intern.assignment.adapters.LoginSignUpAdapter
import com.intern.assignment.databinding.ActivityLoginSignupBinding
import com.intern.assignment.ui.activity.news.NewsActivity
import com.intern.assignment.ui.fragment.SignInFragment
import com.intern.assignment.ui.fragment.SignUpFragment
import com.intern.assignment.util.ChangePageInterface


class LoginSignupActivity : AppCompatActivity(),ChangePageInterface {

    private lateinit var binding : ActivityLoginSignupBinding
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

       mAuth = FirebaseAuth.getInstance()
        //adapter for viewpager
        //adapter for viewpager
        val adapter = LoginSignUpAdapter(supportFragmentManager)


        //signUp fragment instance
        val signUpFragment = SignUpFragment()
        signUpFragment.setInterface(this)
        //signIn fragment instance
        //signIn fragment instance
        val signInFragment = SignInFragment()
        signInFragment.setInterface(this)

        //add fragments

        //add fragments
        adapter.addFragment(signInFragment, "Sign In")
        adapter.addFragment(signUpFragment, "Sign Up")

        //set adapter in viewpager

        //set adapter in viewpager
        binding.viewPager.adapter = adapter
        //set tabs with viewpager
        //set tabs with viewpager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onStart() {
        super.onStart()
        //check user is null or not
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            openHomeActivity()
        }
    }

    override fun openSignInFragment() {
        binding.viewPager.currentItem = 0
    }

    override fun openSignUpFragment() {
        binding.viewPager.currentItem = 1
    }

    override fun openHomeActivity() {
        val intent = Intent(this@LoginSignupActivity, NewsActivity::class.java)
        startActivity(intent)
        finish()
    }
}