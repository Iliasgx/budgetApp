package com.umbrella.budgetapp.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

fun <T>DialogFragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T>ExtendedFragment.getNavigationResult(@IdRes id: Int, key: String, onResult: (result: T) -> Unit) {
    val navBackStackEntry = findNavController().getBackStackEntry(id)

    // Create our observer and add it to the NavBackStackEntry's lifecycle.
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains(key)) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)

            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }

    // Add new observers
    navBackStackEntry.lifecycle.addObserver(observer)

    // As addObserver() does not automatically remove the observer, we call
    // removeObserver() manually when the view lifecycle is destroyed.
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
 }

fun <T>DialogFragment.getNavigationResult(@IdRes id: Int, key: String, onResult: (result: T) -> Unit) {
    val navBackStackEntry = findNavController().getBackStackEntry(id)

    // Create our observer and add it to the NavBackStackEntry's lifecycle.
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains(key)) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)

            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }

    // Add new observers
    navBackStackEntry.lifecycle.addObserver(observer)

    // As addObserver() does not automatically remove the observer, we call
    // removeObserver() manually when the view lifecycle is destroyed.
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}