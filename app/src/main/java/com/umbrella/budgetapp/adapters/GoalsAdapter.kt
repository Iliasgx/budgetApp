package com.umbrella.budgetapp.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_goals_active_paused.view.*
import kotlinx.android.synthetic.main.list_goals_reached.view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
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
        Log.d("_Test", "getPosition [$position] with {${goals[position].name}}");
        holder.bind(goals[position])
        holder.itemView.id = goals[position].id!!.toInt()
    }

    override fun getItemCount() = goals.size

    override fun setData(list: List<Goal>) {
        goals = list

        Log.d("_Test", "sizing: ${list.size}");
    }

    init {
        onBind(object : Bind<Goal> {
            override fun onBinding(item: Goal, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    when (status) {
                        GoalStatus.ACTIVE, GoalStatus.PAUSED -> {
                            list_Goals_ActivePaused_Img.setImageResource(item.icon!!)
                            list_Goals_ActivePaused_Img.setBackgroundColor(item.color!!)
                            list_Goals_ActivePaused_Name.text = item.name!!
                            list_Goals_ActivePaused_Saved.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(item.savedAmount)}")
                            list_Goals_ActivePaused_Goal.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(item.targetAmount)}")
                            list_Goals_ActivePaused_Date.text = SimpleDateFormat("DD/MM/YYYY", Locale.getDefault()).format(Date(item.desiredDate!!))
                            list_Goals_ActivePaused_Progressbar.max = item.targetAmount!!.toInt()
                            list_Goals_ActivePaused_Progressbar.progress = item.savedAmount!!.toInt()
                        }
                        GoalStatus.REACHED -> {
                            list_Goals_Reached_Img.setImageResource(item.icon!!)
                            list_Goals_Reached_Img.setBackgroundColor(item.color!!)
                            list_Goals_Reached_Name.text = item.name!!
                            list_Goals_Reached_Saved.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(item.savedAmount)}")
                            list_Goals_Reached_Date.text = SimpleDateFormat("DD-MM-YYYY hh:mm", Locale.getDefault()).format(Date(item.desiredDate!!))

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