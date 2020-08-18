package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.database.viewmodels.GoalViewModel
import com.umbrella.budgetapp.databinding.FragmentGoalDetailsBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit

class GoalDetailsFragment : ExtendedFragment(R.layout.fragment_goal_details) {
    private val binding by viewBinding(FragmentGoalDetailsBinding::bind)

    private val model by viewModels<GoalViewModel>()

    private var goal : Goal

    init {
        val args : GoalDetailsFragmentArgs by navArgs()

        goal = model.getGoalById(args.goalId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpData()

        binding.apply {
            goalDetailsAddAmount.setOnClickListener { addSavedAmount() }
            goalDetailsSetReached.setOnClickListener { setAsReached() }
        }
    }

    private fun setUpData() {
        with(binding) {
            goalDetailsImg.apply {
                setImageResource(resources.getIntArray(R.array.icons)[goal.icon ?: 0])
                setBackgroundColor(resources.getIntArray(R.array.colors)[goal.color ?: 0])
            }

            goalDetailsName.text = goal.name
            goalDetailsTargetDate.text = DateTimeFormatter().dateFormat(goal.desiredDate!!)

            goalDetailsSavedAmount.text = goal.savedAmount.toString()
            goalDetailsSavable.text = goal.targetAmount.toString()
            goalDetailsCurrency.text = Memory.lastUsedCountry.name
            goalDetailsLastAddedAmount.currencyText(Memory.lastUsedCountry.symbol.toString(), goal.lastAmount ?: BigDecimal.ZERO)

            calculateProgress().let {
                goalDetailsSaved.text = getString(R.string.percentage, it.toString())
                goalDetailsProgress.progress = it
            }

            goalDetailsEstimatedPeriod.text = getString(R.string.goals_Details_EstimatedPeriod, calculateEstimatedTime())
        }
    }

    /**
     * Calculates the progress between the saved amount and the target goal.
     */
    private fun calculateProgress() : Int = goal.savedAmount!!.divide(goal.targetAmount!!).toInt()

    /**
     * Calculates an estimation of how long it will take to reach the goal.
     */
    private fun calculateEstimatedTime() : Int {

        //The time between now and the start date of the goal in days.
        val untilNow = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - goal.startDate!!)

        // How many cycles we need from the current saved amount till the target amount.
        val cycles = goal.targetAmount!!.divide(goal.savedAmount!!).toInt()

        // Return a multiplication of the number of cycles needed by the time that has passed since the start.
        return untilNow.times(cycles).toInt()
    }

    /**
     * Add a new amount. One step closer to the goal.
     */
    private fun addSavedAmount() {
        // TODO: 18/08/2020 Dialog
        // Add value in Goal and in field LastAddedWeekAmount
    }

    /**
     * Set the current goal as reached. Even when the target amount is not yet reached.
     */
    private fun setAsReached() {
        goal.status = GoalStatus.REACHED
        // TODO: 18/08/2020 update layout after set
    }
}