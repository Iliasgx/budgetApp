package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.MainActivity
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.viewmodels.UserViewModel
import com.umbrella.budgetapp.databinding.DataUserBinding
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.Dialogs
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.util.*

class UpdateUserProfileFragment : ExtendedFragment(R.layout.data_user), Edit {
    private val binding by viewBinding(DataUserBinding::bind)

    //ViewModel for holding the user data.
    private val model by viewModels<UserViewModel>()

    //The current user, null when it's a new user.
    private var user: User? = null

    //Used to save the edited arguments. Used do determine of data has to be updated in the Database.
    private var editedUser = User(id = 0L)

    //Arguments received to identify a new user.
    private var type : Type

    init {
        val args : UpdateUserProfileFragmentArgs by navArgs()

        type = checkType(args.userId)

        if (type == Type.EDIT) {
            user = model.getUserById(args.userId)
            //Set defaultData of editedUser to user (safety reasons).
            editedUser = user!!
        } else {
            binding.dataCardUserAction.text = getString(R.string.data_User_CreateAccount)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (type == Type.EDIT) initData()
        setListeners()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            dataCardUserNameFirst.setText(user!!.firstName, EDITABLE)
            dataCardUserNameLast.setText(user!!.lastName, EDITABLE)
            dataCardUserEmail.setText(user!!.email, EDITABLE)
            dataCardUserGender.setSelection(user!!.gender)

            val tempDate = if (user!!.birthday != 0L) user!!.birthday else Calendar.getInstance().timeInMillis
            dataCardUserBirthDay.text = DateTimeFormatter().dateFormat(tempDate)
        }
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardUserAction.setOnClickListener { actionButton() }

            //Update edited user after a short delay.
            dataCardUserNameFirst.afterTextChangedDelayed { editedUser.firstName = it }
            dataCardUserNameLast.afterTextChangedDelayed { editedUser.lastName = it }
            dataCardUserEmail.afterTextChangedDelayed { editedUser.email = it }

            //Start DatePickerDialog on Birthday click. Returns the date and is updated in the variable and in the UI.
            dataCardUserBirthDay.setOnClickListener {
                Dialogs.DatePicker(requireContext(),  object : Dialogs.DatePicker.OnSelectDate {
                    override fun dateSelected(timeInMillis: Long) {
                        binding.dataCardUserBirthDay.text = DateTimeFormatter().dateFormat(timeInMillis)
                        editedUser.birthday = timeInMillis
                    }
                }).apply {
                    if (editedUser.birthday != 0L) initialDate(editedUser.birthday)
                    beforeToday()
                    show()
                }

            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {}

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addUser(editedUser)
        } else if (hasChanges(user!!, editedUser)) {
            model.updateUser(editedUser)
        }
    }

    private fun actionButton() {
        saveData()

        if (type == Type.EDIT) {
            (requireActivity() as MainActivity).logUserOut()
            findNavController().navigate(UpdateUserProfileFragmentDirections.globalLogin())
        } else {
            findNavController().navigateUp()
        }
    }
}