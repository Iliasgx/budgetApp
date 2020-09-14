package com.umbrella.budgetapp.ui.customs

import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.database.viewmodels.CurrencyViewModel
import com.umbrella.budgetapp.extensions.inflate

sealed class Spinners {
    /**
     * Private binder for the Spinner widget.
     * @param spinner The spinner that is used for the collection.
     * @param adapter The adapter with the collection of items.
     */
    internal fun bindSpinner(spinner: Spinner, adapter: ArrayAdapter<*>) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    /**
     * Class for the color spinner.
     */
    class Colors(fragment: Fragment, spinner: Spinner, size: Size) : Spinners() {
        enum class Size {
            SMALL,
            LARGE
        }

        init {
            bindSpinner(spinner, ColorAdapter(fragment, fragment.resources.getIntArray(R.array.colors).asList(), size))
        }

        private class ColorAdapter(private val fragment: Fragment, private val items: List<Int>, private val size: Size) : ArrayAdapter<Int>(fragment.requireContext(), R.layout.custom_spinner_colors, 0, items) {

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createItemView(position, parent)
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createItemView(position, parent)
            }

            private fun createItemView(position: Int, parent: ViewGroup): View {
                val view = parent.inflate(R.layout.custom_spinner_colors, false)
                val item = view.findViewById<View>(R.id.spinner_color)
                item.layoutParams = LinearLayout.LayoutParams(if (size == Size.LARGE) fragment.resources.getDimensionPixelSize(R.dimen.spinner_colors_width_large) else fragment.resources.getDimensionPixelSize(R.dimen.spinner_colors_width_small), fragment.resources.getDimensionPixelSize(R.dimen.spinners_colors_height))
                item.foreground = ResourcesCompat.getDrawable(fragment.resources, R.drawable._spinner_color_corners, dropDownViewTheme)
                item.setBackgroundColor(items[position])
                return view
            }

        }
    }

    /**
     * Class for the icon spinner.
     */
    class Icons(fragment: Fragment, spinner: Spinner) : Spinners() {

        init {
            bindSpinner(spinner, IconAdapter(fragment, fragment.resources.obtainTypedArray(R.array.icons), (0..fragment.resources.getIntArray(R.array.icons).count()).toList().dropLast(1)))
        }

        private class IconAdapter(private val fragment: Fragment, private val array: TypedArray, indices: List<Int>) : ArrayAdapter<Int>(fragment.requireContext(), R.layout.custom_spinner_icons, 0, indices) {

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createItemView(position, parent)
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createItemView(position, parent)
            }

            private fun createItemView(position: Int, parent: ViewGroup): View {
                val view = parent.inflate(R.layout.custom_spinner_icons, false)
                (view.findViewById<View>(R.id.spinner_icon) as ImageView).setImageResource(array.getResourceId(position, 0))
                return view
            }


        }
    }

    /**
     * Class for simple spinners of Strings.
     */
    class SimpleSpinner(fragment: Fragment, spinner: Spinner, arrayId: Int) : Spinners() {

        init {
            val items : Array<String> = fragment.resources.getStringArray(arrayId)

            bindSpinner(spinner, ArrayAdapter<String>(fragment.requireContext(), android.R.layout.simple_spinner_item, items))
        }
    }

    /**
     * Internal function for initializing the adapter and binding it to the adapter.
     */
    internal fun <T> initRoom(fragment: Fragment, spinner: Spinner) : ArrayAdapter<T> {
        val adapter = ArrayAdapter<T>(fragment.requireContext(), android.R.layout.simple_spinner_item, mutableListOf())

        bindSpinner(spinner, adapter)

        return adapter
    }

    /**
     * Class for the currency spinner.
     */
    class Currencies(fragment: Fragment, spinner: Spinner) : Spinners() {

        private var list = mutableListOf<CurrencyAndName>()

        init {
            val model by fragment.viewModels<CurrencyViewModel>()

            val adapter = initRoom<String>(fragment, spinner)

            model.getBasicCurrencies().observe(fragment.viewLifecycleOwner, Observer { items ->
                list = items.toMutableList()

                val defC = DefaultCountries()
                val listOfNames = mutableListOf<String>()
                items.forEach { item -> listOfNames.add(defC.getCountryById(item.countryRef).name) }

                adapter.apply {
                    clear()
                    addAll(listOfNames)
                    notifyDataSetChanged()
                }
            })

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    spinner.tag = list[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        fun getPosition(currencyId: Long) : Int {
            return list.indexOf(list.find { curr -> curr.id == currencyId })
        }
    }

    /**
     * Class for the account spinner.
     */
    class Accounts(fragment: Fragment, spinner: Spinner) : Spinners() {

        private var list = mutableListOf<Account>()

        init {
            val model by fragment.viewModels<AccountViewModel>()

            val adapter = initRoom<String>(fragment, spinner)

            model.getAccountBasics().observe(fragment.viewLifecycleOwner, Observer { items ->
                list = items.toMutableList()

                val listOfNames = mutableListOf<String>()
                items.forEach { item -> listOfNames.add(item.name!!) }

                adapter.apply {
                    clear()
                    addAll(listOfNames)
                    notifyDataSetChanged()
                }

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        spinner.tag = list[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            })
        }

        fun getPosition(accountId: Long) : Int {
            return list.indexOf(list.find { acc -> acc.id == accountId })
        }
    }
}