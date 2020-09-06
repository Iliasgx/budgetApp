package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

    val args : UpdateUserProfileFragmentArgs by navArgs()

    private val model by viewModels<UserViewModel>()

    private var user: User? = null

    private lateinit var type : Type

    private var editedUser = User(id = 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.userId)

        // Screen also used to create a new account, can't create cancel button then because they can't continue without profile.
        if (type == Type.EDIT) setToolbar(ToolBarNavIcon.CANCEL)

        model.getUserById(args.userId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                user = it
                editedUser = user!!
            } else {
                binding.dataCardUserAction.text = getString(R.string.data_User_CreateAccount)
            }
            initData()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (type == Type.EDIT) inflater.inflate(R.menu.save, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        setListeners()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
        } else {
            findNavController().navigateUp()
        }
        return true
    }
}