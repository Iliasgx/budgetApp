package com.umbrella.budgetapp.ui.customs

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.umbrella.budgetapp.R
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class ExtendedFragment(@LayoutRes layout: Int): Fragment(layout) {

    enum class ToolBarNavIcon(var id: Int) {
        MENU    (R.drawable.menu),
        BACK    (R.drawable.back),
        CANCEL  (R.drawable.cancel)
    }

    /**
     * Set Toolbar for the current fragment.
     * @param icon: implement icon of ToolbarNavIcon
     * @param menu: Inflate the menu, if -1, removes menu.
     *
     * @see ToolBarNavIcon
     */
    fun setToolbar(icon: ToolBarNavIcon, menu: Int = -1) {
        val tb = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        tb?.navigationIcon = ContextCompat.getDrawable(requireContext(), icon.id)

        if (menu != -1) {
            tb?.inflateMenu(menu)
        } else {
            tb?.menu?.clear()
        }
    }

    /*
     * Class to automate the initializer of ViewBinding the class with the Layout.
     * Delegation also observes lifecycle to thrown the onDestroy.
     */
    class ViewBindingDelegate<T : ViewBinding>(
            val fragment: Fragment,
            val viewBindingFactory: (View) -> T
    ) : ReadOnlyProperty<Fragment, T> {
        private var binding: T? = null

        init {
            fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    fragment.viewLifecycleOwnerLiveData.observe(fragment, Observer { viewLifecycleOwner ->
                        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                binding = null
                            }
                        })
                    })
                }
            })
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            val binding = binding
            if (binding != null) return binding

            val lifecycle = fragment.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
            }

            return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
        }

    }

    fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
            ViewBindingDelegate(this, viewBindingFactory)
}