package com.umbrella.budgetapp.ui.fragments.contentholders

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.tabs.TabLayoutMediator
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
import com.umbrella.budgetapp.database.viewmodels.TemplateViewModel
import com.umbrella.budgetapp.database.viewmodels.submodels.HomeViewModel
import com.umbrella.budgetapp.databinding.ContentHomeBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.screens.HomeAccountsFragment
import com.umbrella.budgetapp.ui.fragments.screens.HomeInformationFragment

class HomeFragment: ExtendedFragment(R.layout.content_home) {
    private val binding by viewBinding(ContentHomeBinding::bind)

    private val model by viewModels<TemplateViewModel>()

    private val homeModel by viewModels<HomeViewModel>()

    private val buttons = mutableListOf<FloatingActionButton>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.title_Home))

        setUpAdapter()
        // FIXME: 21/09/2020 Find out how to add the button, KotlinNullPointerException
        //createTemplates()
    }

    private fun createTemplates() {
        model.getAllTemplates(5).observe(viewLifecycleOwner, Observer {
            binding.homeFAB.removeAllTemplates()

            buttons.forEach { but -> binding.homeFAB.removeMenuButton(but) }

            it.forEach { extTemplate ->
                binding.homeFAB.addMenuButton(makeTemplate(extTemplate), extTemplate.template.position!!)
            }
        })

        setListeners()
    }

    private fun FloatingActionMenu.removeAllTemplates() {
        binding.homeFAB.children.forEach { child ->
            if (child is FloatingActionButton && child.tag != 0L) {
                removeMenuButton(child)
            }
        }
    }

    private fun makeTemplate(item: TemplateAndCategory) : FloatingActionButton {
        return FloatingActionButton(context).apply {
            tag = item.template.id
            labelText = getString(R.string.home_FAB_template, item.template.name)
            setImageResource(item.category.icon!!)

            setOnClickListener { navigateFromTemplateToNewRecord(item.template.id!!) }
        }
    }

    private fun navigateFromTemplateToNewRecord(templateId: Long = 0L) {
        findNavController().navigate(HomeFragmentDirections.homeToUpdateRecordBasic(templateId))
    }

    private fun setListeners() {
        with(binding) {
            homeFABNewRecord.setOnClickListener { navigateFromTemplateToNewRecord() }
            homeFABTransfer.setOnClickListener { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setUpAdapter() {
        binding.homeViewPager.adapter = HomeStateAdapter(this)

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