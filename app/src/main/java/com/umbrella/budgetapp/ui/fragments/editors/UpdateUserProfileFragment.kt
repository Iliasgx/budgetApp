package com.umbrella.budgetapp.ui.fragments.editors

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.MainActivity
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.User
import com.umbrella.budgetapp.database.viewmodels.UserViewModel
import com.umbrella.budgetapp.databinding.DataUserBinding
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import java.util.*

class UpdateUserProfileFragment : ExtendedFragment(R.layout.data_user) {
    private val binding by viewBinding(DataUserBinding::bind)

    //ViewModel for holding the user data.
    private lateinit var model : UserViewModel

    //The current user, null when it's a new user.
    private var user: User? = null

    //Used to save the edited arguments. Used do determine of data has to be updated in the Database.
    private var editedUser = User(id = 0)

    //Arguments received to identify a new user.
    private var newUser : Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newUser = UpdateUserProfileFragmentArgs.fromBundle(requireArguments()).newUser

        model = ViewModelProvider(this).get(UserViewModel::class.java)

        if (!newUser) {
            //Only execute query when user exist.
            user = model.getUserById(Memory.loggedUser.id)
            //Set defaultData of editedUser to user (safety reasons).
            editedUser = user!!

            initData()
        } else {
            binding.dataCardUserAction.text = getString(R.string.data_User_CreateAccount)
        }

        setListeners()
    }

    //SetUp data from User. Only called when user exist already.
    private fun initData() {
        with(binding) {
            dataCardUserNameFirst.setText(user!!.firstName, EDITABLE)
            dataCardUserNameLast.setText(user!!.lastName, EDITABLE)
            dataCardUserEmail.setText(user!!.email, EDITABLE)
            dataCardUserGender.setSelection(user!!.gender)

            val tempDate = if (user!!.birthday != 0L) user!!.birthday else Calendar.getInstance().timeInMillis
            dataCardUserBirthDay.text = DateTimeFormatter().dateFormat(tempDate)
        }
    }

    private fun setListeners() {
        with(binding) {
            binding.dataCardUserAction.setOnClickListener { actionButton() }

            //Update edited user after a short delay.
            dataCardUserNameFirst.afterTextChangedDelayed { editedUser.firstName = it }
            dataCardUserNameLast.afterTextChangedDelayed { editedUser.lastName = it }
            dataCardUserEmail.afterTextChangedDelayed { editedUser.email = it }

            //Start DatePickerDialog on Birthday click. Returns the date and is updated in the variable and in the UI.
            dataCardUserBirthDay.setOnClickListener {
                val cal = Calendar.getInstance()

                DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    cal.set(year, month, dayOfMonth)
                    dataCardUserBirthDay.text = DateTimeFormatter().dateFormat(cal.timeInMillis)
                    editedUser.birthday = cal.timeInMillis

                    //Set initial date (now)
                }, cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH])
                .apply { datePicker.maxDate = cal.timeInMillis } // No future date
                .show()
            }
        }
    }

    //Create user when not existing, else identify possible changes and save.
    private fun saveData() {
        if (newUser) {
            model.addUser(editedUser)
        } else if (editedUser !== user) {
            model.updateUser(editedUser)
        }
    }

    /**
     * ActionButton actions:
     *  - new user: createUser
     *  - Existing user: save possible changes and return the user back to the login screen due to logout.
     */
    private fun actionButton() {
        saveData()

        if (!newUser) {
            (requireActivity() as MainActivity).logUserOut()
            findNavController().navigate(UpdateUserProfileFragmentDirections.globalLogin())
        } else {
            findNavController().navigateUp()
        }
    }
}