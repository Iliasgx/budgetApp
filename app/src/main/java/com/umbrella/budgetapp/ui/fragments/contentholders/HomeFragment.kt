package com.umbrella.budgetapp.ui.fragments.contentholders

import android.os.Bundle
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.ContentHomeBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.screens.HomeAccountsFragment
import com.umbrella.budgetapp.ui.fragments.screens.HomeInformationFragment

class HomeFragment: ExtendedFragment(R.layout.content_home), View.OnClickListener {
    private val binding by viewBinding(ContentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()

        binding.homeFAB.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        /*if (v!!.tag == "record") {
            v.findNavController().navigate(HomeFragmentDirections.homeToNewRecord())
        } else {

        }*/
    }

    private fun setUpAdapter() {
        with(binding.homeViewPager) {
            adapter = HomeStateAdapter(this@HomeFragment)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        TabLayoutMediator(binding.homeTabLayout, binding.homeViewPager) { tab, position ->
            tab.text = when (position) {
                1 -> getString(R.string.home_Tab2)
                else -> getString(R.string.home_Tab1)
            }
        }.attach()
    }

    class HomeStateAdapter(fragment: ExtendedFragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): ExtendedFragment {
            return when (position) {
                1 -> HomeInformationFragment()
                else -> HomeAccountsFragment()
            }
        }

        override fun getItemCount() = 2
    }
}