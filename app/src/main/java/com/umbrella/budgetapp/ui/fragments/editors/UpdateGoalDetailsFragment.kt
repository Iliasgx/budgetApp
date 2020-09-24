package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Goal
import com.umbrella.budgetapp.database.viewmodels.GoalViewModel
import com.umbrella.budgetapp.databinding.DataGoalDetailsBinding
import com.umbrella.budgetapp.enums.GoalPrefabs
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.Dialogs
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.customs.Spinners.Colors.Size.SMALL
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal
import java.util.*

class UpdateGoalDetailsFragment : ExtendedFragment(R.layout.data_goal_details), Edit {
    private val binding by viewBinding(DataGoalDetailsBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 4
        const val MIN_TARGET_AMOUNT = 1.0f
    }

    val args : UpdateGoalDetailsFragmentArgs by navArgs()

    private val model by viewModels<GoalViewModel>()

    private lateinit var goal : Goal

    private lateinit var type: Type

    private lateinit var menu: Menu

    // The name of the current goal. Only set when the user is creating a new goal and chose to set a name.
    private var name : String? = null

    // A prefab of the chosen goal when the user created the goal. Only set when user is creating a new goal.
    private lateinit var goalPrefab : GoalPrefabs

    private var editData : Goal = Goal()

    // Identifier for the result callback from the AmountDialog
    // True - TargetAmount
    // False - SavedAmount
    private var clickedTargetAmount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.goalId)

        name = args.name
        goalPrefab = args.prefab

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> R.string.title_add_goal
            Type.EDIT -> R.string.title_edit_goal
        })

        model.getGoalById(args.goalId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                goal = it
                editData = goal.copy()
            }
            updateMenu()
            initData()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.goal_more_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menuLayout_GoalMoreOptions_Save).isVisible = true
        menu.findItem(R.id.menuLayout_GoalMoreOptions_Delete).isVisible = true
        this.menu = menu
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            //Populate the Colors and Icons spinners.
            Spinners.Colors(this@UpdateGoalDetailsFragment, dataCardGoalDetailsColor, SMALL)
            Spinners.Icons(this@UpdateGoalDetailsFragment, dataCardGoalDetailsIcon)

            if (type == Type.NEW) {
                //Set default data
                dataCardGoalDetailsAmount.currencyText(Memory.lastUsedCountry.symbol, BigDecimal.ZERO)
                dataCardGoalDetailsSaved.currencyText(Memory.lastUsedCountry.symbol, BigDecimal.ZERO)
                dataCardGoalDetailsDate.text = DateTimeFormatter().dateFormat(Calendar.getInstance().timeInMillis)

                //Set data of name or prefab.
                if (goalPrefab == GoalPrefabs.NONE) {
                    dataCardGoalDetailsName.setText(name, EDITABLE)
                } else {
                    dataCardGoalDetailsName.setText(name ?: resources.getStringArray(R.array.goalPrefab_names)[goalPrefab.ordinal])
                    // TODO: 16/09/2020 Find out how to select right one
                   /* dataCardGoalDetailsIcon.setSelection(goalPrefab.iconIndex)
                    dataCardGoalDetailsColor.setSelection(goalPrefab.colorIndex)*/
                }
            } else {
                //Load all the data from the database. Defaults are in place in case the field was empty.
                dataCardGoalDetailsName.setText(goal.name)
                dataCardGoalDetailsAmount.currencyText(Memory.lastUsedCountry.symbol, goal.targetAmount ?: BigDecimal.ZERO)
                dataCardGoalDetailsSaved.currencyText(Memory.lastUsedCountry.symbol, goal.savedAmount ?: BigDecimal.ZERO)
                dataCardGoalDetailsDate.text = DateTimeFormatter().dateFormat(goal.desiredDate ?: Calendar.getInstance().timeInMillis)
                dataCardGoalDetailsNote.setText(goal.note, EDITABLE)
                // TODO: 16/09/2020 Find out how to select right one
                /*dataCardGoalDetailsIcon.setSelection(goal.icon ?: 0)
                dataCardGoalDetailsColor.setSelection(goal.color ?: 0)*/

            }
        }
        //Set listeners after initializing the data so no listeners would be called already.
        setListeners()
    }

    private fun updateMenu() {
        menu.apply {
            if (type == Type.EDIT) {
                val isActive = editData.status == GoalStatus.ACTIVE

                findItem(R.id.menuLayout_GoalMoreOptions_pause).isVisible = isActive
                findItem(R.id.menuLayout_GoalMoreOptions_start).isVisible = !isActive
            } else {
                findItem(R.id.menuLayout_GoalMoreOptions_Save).isVisible = true
            }
        }
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardGoalDetailsAmount.setOnClickListener {
                clickedTargetAmount = true
                findNavController().navigate(UpdateGoalDetailsFragmentDirections.globalDialogAmount(editData.targetAmount?.toEngineeringString(), MIN_TARGET_AMOUNT))
            }

            dataCardGoalDetailsSaved.setOnClickListener {
                clickedTargetAmount = false
                findNavController().navigate(UpdateGoalDetailsFragmentDirections.globalDialogAmount(editData.savedAmount?.toEngineeringString()))
            }

            getNavigationResult<String>(R.id.updateGoalDetails, "amount") { result ->
                if (clickedTargetAmount) {
                    editData.targetAmount = BigDecimal(result)
                    dataCardGoalDetailsAmount.text = result
                } else {
                    editData.savedAmount = BigDecimal(result)
                    dataCardGoalDetailsSaved.text = result
                }
            }

            //Open a DatePickerDialog with the current date.
            dataCardGoalDetailsDate.setOnClickListener {
                Dialogs.DatePicker(requireContext(), object: Dialogs.DatePicker.OnSelectDate {
                    override fun dateSelected(timeInMillis: Long) {
                        dataCardGoalDetailsDate.text = DateTimeFormatter().dateFormat(timeInMillis)
                        editData.desiredDate = timeInMillis
                    }
                }).apply {
                    initialDate(editData.desiredDate ?: Calendar.getInstance().timeInMillis)
                    fromToday()
                    show()
                }
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var nameCorrect  = false

        with(binding.dataCardGoalDetailsName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Goal_Details_Name_ErrorMsg_empty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Goal_Details_Name_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> nameCorrect = true
            }
        }

        //If this field has not met de requirements, already return so the user won't have a combination of errors and toasts.
        if (!nameCorrect) return

        //Check if the user hasn't saved already more than the target was. (Then a Goal would not be needed.) Return if not met requirements.
        if (editData.savedAmount!! >= editData.targetAmount!!) {
            Toast.makeText(context, getString(R.string.data_Goal_Details_Saved_ErrorMsg_tooLarge), Toast.LENGTH_SHORT).show()
            return
        }

        //Update EditGoal with data that is set without listeners.
        editData.apply {
            name = binding.dataCardGoalDetailsName.text.trim().toString()
            note = binding.dataCardGoalDetailsNote.text.trim().toString()
            color = binding.dataCardGoalDetailsColor.selectedItemPosition
            icon = binding.dataCardGoalDetailsIcon.selectedItemPosition
        }

        //All requirements are met, saving the data to the Database is now allowed.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addGoal(editData)
        } else if (hasChanges(goal, editData)) {
            model.updateGoal(editData)
        }
        findNavController().navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.menuLayout_GoalMoreOptions_Save -> {
                checkData()
                return true
            }
            R.id.menuLayout_GoalMoreOptions_Delete -> {
                model.removeGoal(goal)
                //Cannot return to details screen because the details of a deleted goal is not available.
                findNavController().navigate(UpdateGoalDetailsFragmentDirections.updateGoalDetailsToGoals())
                return true
            }
            R.id.menuLayout_GoalMoreOptions_start -> {
                editData.status = GoalStatus.ACTIVE
                updateMenu()
                return true
            }
            R.id.menuLayout_GoalMoreOptions_pause -> {
                editData.status = GoalStatus.PAUSED
                updateMenu()
                return true
            }
        }
        return false
    }
}
