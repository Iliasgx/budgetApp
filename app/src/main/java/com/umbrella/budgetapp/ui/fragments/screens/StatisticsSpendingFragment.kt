package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.RecordsAdapter
import com.umbrella.budgetapp.database.viewmodels.submodels.StatisticsViewModel
import com.umbrella.budgetapp.databinding.FragmentStatisticsSpendingBinding
import com.umbrella.budgetapp.extensions.inflate
import com.umbrella.budgetapp.extensions.sumByBigDecimal
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.android.synthetic.main.list_stats_prgb.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class StatisticsSpendingFragment : ExtendedFragment(R.layout.fragment_statistics_spending) {
    private val binding by viewBinding(FragmentStatisticsSpendingBinding::bind)

    private val model by viewModels<StatisticsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
    }

    private fun setAdapters() {
        val expenseRecordAdapter = RecordsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {}
        })
        val spendingCategoryAdapter = SpendingCategoryAdapter()

        binding.statisticsCardExpensesRecyclerView.apply {
            adapter = expenseRecordAdapter
            setHasFixedSize(false)
        }

        model.topExpenses.observe(viewLifecycleOwner, Observer { expenseRecordAdapter.setData(it) })

        model.topCategories.observe(viewLifecycleOwner, Observer { spendingCategoryAdapter.setData(it) })
    }

    private class SpendingCategoryAdapter : BaseAdapter<Triple<String, BigDecimal, Int>>() {

        private var categories = listOf<Triple<String, BigDecimal, Int>>()

        private var total: BigDecimal = BigDecimal.ONE

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(parent.inflate(R.layout.list_stats_prgb))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) { holder.bind(categories[position]) }

        override fun getItemCount() = categories.size

        override fun setData(list: List<Triple<String, BigDecimal, Int>>) {
            if (categories == list) return
            total = list.sumByBigDecimal { item -> item.second }
            categories = list
            notifyDataSetChanged()
        }

        init {
            onBind(object : Bind<Triple<String, BigDecimal, Int>> {
                override fun onBinding(item: Triple<String, BigDecimal, Int>, itemView: View, adapterPosition: Int) {
                    itemView.list_stats_prgb_bar.apply {
                        title = item.first
                        max = total.toInt()
                        value = item.second
                        progress = item.second.divide(total, 0, RoundingMode.UP).toInt()
                        progressBarColor = item.third
                    }
                }
            })
        }
    }
}