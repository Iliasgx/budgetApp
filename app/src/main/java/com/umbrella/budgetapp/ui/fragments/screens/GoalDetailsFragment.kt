package com.umbrella.budgetapp.ui.fragments.screens

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.database.viewmodels.GoalViewModel
import com.umbrella.budgetapp.databinding.FragmentGoalDetailsBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.android.synthetic.main._activity.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit

class GoalDetailsFragment : ExtendedFragment(R.layout.fragment_goal_details) {
    private val binding by viewBinding(FragmentGoalDetailsBinding::bind)

    private val model by viewModels<GoalViewModel>()

    val args : GoalDetailsFragmentArgs by navArgs()

    private lateinit var goal : Goal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getGoalById(args.goalId).observe(viewLifecycleOwner, Observer {
            goal = it
            setUpData()

            updateFields()
        })

        binding.apply {
            goalDetailsAddAmount.setOnClickListener {
                if (goal.status == GoalStatus.PAUSED) {
                    Toast.makeText(context, getString(R.string.goals_Details_AddAmount_error), Toast.LENGTH_SHORT).show()
                } else {
                    addSavedAmount()
                }
            }
            goalDetailsSetReached.setOnClickListener {
                goal.status = GoalStatus.REACHED
                model.updateGoal(goal)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.goal_more_options, menu)
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menuLayout_GoalMoreOptions_Edit).isVisible = true
    }

    private fun setUpData() {
        with(binding) {
            goalDetailsImg.apply {
                setImageResource(goal.icon!!)
                backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[goal.color!!])
            }

            goalDetailsName.text = goal.name
            goalDetailsTargetDate.text = DateTimeFormatter().dateFormat(goal.desiredDate!!)

            @SuppressLint("SetTextI18n")
            goalDetailsAmount.text = "${String.format("%.2f", goal.savedAmount)} / ${String.format("%.2f", goal.targetAmount)}"
            goalDetailsCurrency.text = Memory.lastUsedCountry.name
            goalDetailsLastAddedAmount.currencyText(Memory.lastUsedCountry.symbol, goal.lastAmount ?: BigDecimal.ZERO)

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
    private fun calculateProgress() = goal.savedAmount!!.divide(goal.targetAmount!!, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100)).toInt()

    /**
     * Calculates an estimation of how long it will take to reach the goal.
     */
    private fun calculateEstimatedTime() : Int {

        //The time between now and the start date of the goal in days.
        val untilNow = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - goal.startDate!!)

        // How many cycles we need from the current saved amount till the target amount.
        val cycles = goal.targetAmount!!.divide(goal.savedAmount!!, 2, RoundingMode.HALF_UP).toInt()

        // Return a multiplication of the number of cycles needed by the time that has passed since the start.
        return untilNow.times(cycles).toInt()
    }

    /**
     * Add a new amount to the goal.
     */
    private fun addSavedAmount() {
        findNavController().navigate(GoalDetailsFragmentDirections.globalDialogAmount())

        getNavigationResult<String>(R.id.goalDetails, "amount") { result ->
            // !! Shortening this is not working. Keep it
            goal.savedAmount = goal.savedAmount?.add(BigDecimal(result))
            goal.lastAmount = BigDecimal(result)

            model.updateGoal(goal)
        }
    }

    private fun updateFields() {
        val isReached = (goal.status == GoalStatus.REACHED)

        binding.goalDetailsOptionsMenu.isVisible = !isReached
        binding.goalDetailsAddAmount.alpha = if (goal.status == GoalStatus.PAUSED) 0.6f else 1f
        requireActivity().toolbar?.menu?.findItem(R.id.menuLayout_GoalMoreOptions_Delete)?.isVisible = !isReached
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuLayout_GoalMoreOptions_Delete -> {
                model.removeGoal(goal)
                navigateUp()
                return true
            }
            R.id.menuLayout_GoalMoreOptions_Edit -> {
                findNavController().navigate(GoalDetailsFragmentDirections.goalDetailsToUpdateGoalDetails(goal.id!!))
                return true
            }
        }
        return false
    }
}