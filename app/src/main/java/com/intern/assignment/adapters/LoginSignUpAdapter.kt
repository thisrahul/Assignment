package com.intern.assignment.adapters

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class LoginSignUpAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    //arraylist for fragments
    private val fragmentArrayList: ArrayList<Fragment> = ArrayList()

    //arraylist for titles
    private val titleArrayList: ArrayList<String> = ArrayList()


    //method to add fragment and title in list
    fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        titleArrayList.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return titleArrayList[position]
    }
}