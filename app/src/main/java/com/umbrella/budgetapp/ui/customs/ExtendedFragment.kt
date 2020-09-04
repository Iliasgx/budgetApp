package com.umbrella.budgetapp.ui.customs

import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
     * Set Toolbar for the current fragment with NavIcon.
     * @param icon implement icon of ToolbarNavIcon
     *
     * @see ToolBarNavIcon
     */
    fun setToolbar(icon: ToolBarNavIcon) {
        val tb = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        tb?.navigationIcon = ContextCompat.getDrawable(requireContext(), icon.id)
    }

    /**
     * Set Title of the current fragment.
     *
     * @param titleId The id corresponding with the String title.
     */
    fun setTitle(@StringRes titleId: Int) {
        requireActivity().findViewById<Toolbar>(R.id.toolbar)?.title = getString(titleId)
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

    /**
     * Class for adding a Drag & Swipe ability to the RecyclerView.
     *
     * @param dragDirs The directions to where the user can drag. [UP, DOWN]
     * @param swipeDirs The directions to where the user can swipe. [LEFT, RIGHT]
     * @param callback The interface used to interact with the DragManager.
     */
    class DragManageAdapter(dragDirs: Int, swipeDirs: Int, val callback: OnAction) : SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
            callback.onMoved(viewHolder.itemView.id.toLong(), viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            callback.onSwiped(viewHolder.itemView.id.toLong())
        }

        interface OnAction {
            fun onMoved(itemId: Long, oldPosition: Int, newPosition: Int)
            fun onSwiped(itemId: Long)
        }
    }
}