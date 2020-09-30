package com.umbrella.budgetapp.ui.fragments.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.viewmodels.submodels.StatisticsViewModel
import com.umbrella.budgetapp.databinding.FragmentStatisticsReportsBinding
import com.umbrella.budgetapp.extensions.calculateBgDiff
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import com.umbrella.budgetapp.extensions.sumByBigDecimal
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.android.synthetic.main.list_reports.view.*
import java.math.BigDecimal

class StatisticsReportsFragment : ExtendedFragment(R.layout.fragment_statistics_reports) {
    private val binding by viewBinding(FragmentStatisticsReportsBinding::bind)

    private val model by viewModels<StatisticsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
    }

    private fun setAdapters() {
        val incomeAdapter = StatisticsBookAdapter()
        val expenseAdapter = StatisticsBookAdapter()

        model.filter.observe(viewLifecycleOwner, Observer {
            binding.statisticsCardSaldiPeriod.text = resources.getStringArray(R.array.filterPeriods)[Memory.statisticsFilter]
        })

        binding.statisticsCardSaldiRecyclerViewIncome.apply {
            adapter = incomeAdapter
            setHasFixedSize(false)
        }

        binding.statisticsCardSaldiRecyclerViewExpenses.apply {
            adapter = expenseAdapter
            setHasFixedSize(false)
        }

        model.categoryBook.observe(viewLifecycleOwner, Observer {
            val split = it.groupBy { triple -> triple.first }

            val currentTotal = it.sumByBigDecimal { triple -> triple.third.first }
            val previousTotal = it.sumByBigDecimal { triple -> triple.third.second }

            val income = split.getValue(0)
            val expenses = split.getValue(1)

            incomeAdapter.setData(income.map { form -> Triple(form.second, form.third.first, form.third.second) })
            expenseAdapter.setData(expenses.map { form -> Triple(form.second, form.third.first, form.third.second) })

            binding.statisticsCardSaldiAmount.currencyText(Memory.lastUsedCountry.symbol, currentTotal)
            binding.statisticsCardSaldiPrevAmount.percentage(calculateBgDiff(currentTotal, previousTotal))

            binding.statisticsCardSaldiIncomeAmount.currencyText(Memory.lastUsedCountry.symbol, income.sumByBigDecimal { splitter -> splitter.third.first })
            binding.statisticsCardSaldiExpensesAmount.currencyText(Memory.lastUsedCountry.symbol, expenses.sumByBigDecimal { splitter -> splitter.third.second })

            binding.statisticsCardSaldiPrevIncomeAmount.percentage(calculateBgDiff(income.sumByBigDecimal { splitter -> splitter.third.first }, income.sumByBigDecimal { splitter -> splitter.third.second }))
            binding.statisticsCardSaldiPrevExpensesAmount.percentage(calculateBgDiff(expenses.sumByBigDecimal { splitter -> splitter.third.first }, expenses.sumByBigDecimal { splitter -> splitter.third.second }))
        })
    }

    private inner class StatisticsBookAdapter : BaseAdapter<Triple<Category, BigDecimal, BigDecimal>>() {

        private var categories = listOf<Triple<Category, BigDecimal, BigDecimal>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(parent.inflate(R.layout.list_reports))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) { holder.bind(categories[position]) }

        override fun getItemCount() = categories.size

        override fun setData(list: List<Triple<Category, BigDecimal, BigDecimal>>) {
            if (categories == list) return
            categories = list
            notifyDataSetChanged()
        }

        init {
            onBind(object : Bind<Triple<Category, BigDecimal, BigDecimal>> {
                override fun onBinding(item: Triple<Category, BigDecimal, BigDecimal>, itemView: View, adapterPosition: Int) {
                    with(itemView) {
                        list_Reports_Img.apply {
                            setImageResource(item.first.icon!!)
                            backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.first.color!!])
                        }

                        list_Reports_Category.text = item.first.name
                        list_Reports_Amount.currencyText(Memory.lastUsedCountry.symbol, item.second)
                        list_Reports_Amount_PreviousPeriod.percentage(calculateBgDiff(item.second, item.third))
                    }
                }
            })
        }
    }

    private fun TextView.percentage(percentage: BigDecimal) {
        text = getString(R.string.percentage, percentage.toEngineeringString())
        setTextColor(resources.getColor(
                if (percentage.signum() != -1) { R.color.positiveColor } else { R.color.negativeColor },
                context?.theme)
        )
    }
}