package com.umbrella.budgetapp.adapters

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

class GoalsAdapter(private val status: GoalStatus, val callBack: CallBack) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var goals: List<Goal> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (status) {
            GoalStatus.ACTIVE, GoalStatus.PAUSED -> GoalUnreachedViewHolder(parent.inflate(R.layout.list_goals_active_paused))
            GoalStatus.REACHED                   -> GoalReachedViewHolder(parent.inflate(R.layout.list_goals_reached))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (status) {
            GoalStatus.ACTIVE, GoalStatus.PAUSED -> (holder as GoalUnreachedViewHolder).bind(goals[position])
            GoalStatus.REACHED                   -> (holder as GoalReachedViewHolder).bind(goals[position])
        }
    }

    override fun getItemCount() = goals.size

    fun setData(list: List<Goal>) {
        this.goals = list
    }

    interface CallBack {
        fun onItemClick(itemId: Long)
    }

    private inner class GoalUnreachedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(goal: Goal) {
           with(itemView) {
               list_Goals_ActivePaused_Img.setImageResource(goal.icon!!)
               list_Goals_ActivePaused_Img.setBackgroundColor(goal.color!!)
               list_Goals_ActivePaused_Name.text = goal.name!!
               list_Goals_ActivePaused_Saved.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(goal.savedAmount)}")
               list_Goals_ActivePaused_Goal.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(goal.targetAmount)}")
               list_Goals_ActivePaused_Date.text = SimpleDateFormat("DD/MM/YYYY", Locale.getDefault()).format(Date(goal.desiredDate!!))
               list_Goals_ActivePaused_Progressbar.max = goal.targetAmount!!.toInt()
               list_Goals_ActivePaused_Progressbar.progress = goal.savedAmount!!.toInt()
           }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(goals[adapterPosition].id!!)
            }
        }
    }

    private inner class GoalReachedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(goal: Goal) {
            with(itemView) {
                list_Goals_Reached_Img.setImageResource(goal.icon!!)
                list_Goals_Reached_Img.setBackgroundColor(goal.color!!)
                list_Goals_Reached_Name.text = goal.name!!
                list_Goals_Reached_Saved.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(goal.savedAmount)}")
                list_Goals_Reached_Date.text = SimpleDateFormat("DD-MM-YYYY hh:mm", Locale.getDefault()).format(Date(goal.desiredDate!!))
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(goals[adapterPosition].id!!)
            }
        }
    }
}