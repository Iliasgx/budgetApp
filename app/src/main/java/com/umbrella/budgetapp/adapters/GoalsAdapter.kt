package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_goals_active_paused.view.*
import kotlinx.android.synthetic.main.list_goals_reached.view.*
import kotlin.properties.Delegates

class GoalsAdapter(private val status: GoalStatus, val callBack: CallBack) : BaseAdapter<Goal>() {

    var goals: List<Goal> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (status) {
            GoalStatus.ACTIVE, GoalStatus.PAUSED -> BaseViewHolder(parent.inflate(R.layout.list_goals_active_paused))
            GoalStatus.REACHED                   -> BaseViewHolder(parent.inflate(R.layout.list_goals_reached))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount() = goals.size

    override fun setData(list: List<Goal>) {
        goals = list
    }

    init {
        onBind(object : Bind<Goal> {
            override fun onBinding(item: Goal, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    when (status) {
                        GoalStatus.ACTIVE, GoalStatus.PAUSED -> {
                            list_Goals_ActivePaused_Img.apply {
                                setImageResource(item.icon!!)
                                backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.color!!])
                            }
                            list_Goals_ActivePaused_Name.text = item.name!!
                            list_Goals_ActivePaused_Saved.currencyText(Memory.lastUsedCountry.symbol, item.savedAmount!!)
                            list_Goals_ActivePaused_Goal.currencyText(Memory.lastUsedCountry.symbol, item.targetAmount!!)
                            list_Goals_ActivePaused_Date.text = DateTimeFormatter().dateFormat(item.desiredDate!!, '/')
                            list_Goals_ActivePaused_Progressbar.max = item.targetAmount!!.toInt()
                            list_Goals_ActivePaused_Progressbar.progress = item.savedAmount!!.toInt()
                        }
                        GoalStatus.REACHED -> {
                            list_Goals_Reached_Img.apply {
                                setImageResource(item.icon!!)
                                backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.color!!])
                            }
                            list_Goals_Reached_Name.text = item.name!!
                            list_Goals_Reached_Saved.currencyText(Memory.lastUsedCountry.symbol, item.savedAmount!!)
                            list_Goals_Reached_Date.text = DateTimeFormatter().dateFormat(item.desiredDate!!, '/')
                        }
                    }
                    setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(goals[adapterPosition].id!!)
                    }
                }
            }
        })
    }
}