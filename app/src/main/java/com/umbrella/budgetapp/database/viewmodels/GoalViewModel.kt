package com.umbrella.budgetapp.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.database.repositories.GoalRepository
import com.umbrella.budgetapp.enums.GoalStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = GoalRepository()

    fun getGoalById(id: Long) = repos.getGoalById(id).asLiveData()

    fun getAllGoalsUnreached(status: GoalStatus) = repos.getAllGoalsUnreached(status).asLiveData()

    fun getAllGoalsReached() : LiveData<List<Goal>> = repos.getAllGoalsReached().asLiveData()

    fun addGoal(goal: Goal) = viewModelScope.launch(Dispatchers.IO) { repos.addGoal(goal) }

    fun updateGoal(goal: Goal) = viewModelScope.launch(Dispatchers.IO) { repos.updateGoal(goal) }

    fun removeGoal(goal: Goal) = viewModelScope.launch(Dispatchers.IO) { repos.removeGoal(goal) }
}