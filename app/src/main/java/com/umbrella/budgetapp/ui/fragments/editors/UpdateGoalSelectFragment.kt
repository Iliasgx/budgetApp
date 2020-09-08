package com.umbrella.budgetapp.ui.fragments.editors

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.DataGoalSelectBinding
import com.umbrella.budgetapp.enums.GoalPrefabs
import com.umbrella.budgetapp.extensions.inflate
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.android.synthetic.main.list_goal_prefab.view.*

class UpdateGoalSelectFragment : ExtendedFragment(R.layout.data_goal_select) {
    private val binding by viewBinding(DataGoalSelectBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(R.string.title_add_goal_select)

        binding.dataCardGoalSelectGrid.adapter =
                GoalPrefabAdapter(object : GoalPrefabAdapter.CallBack {
                    override fun onSelectPrefab(prefab: GoalPrefabs) {
                        val tempName = checkRequirements()

                        val navDir = if (tempName.isNullOrBlank()) {
                            UpdateGoalSelectFragmentDirections.goalSelectToUpdateGoalDetails(prefab = prefab)
                        } else {
                            UpdateGoalSelectFragmentDirections.goalSelectToUpdateGoalDetails(name = tempName, prefab = prefab)
                        }
                        findNavController().navigate(navDir)
                    }
                })

        binding.dataCardGoalSelectCreate.setOnClickListener {
            with(binding.dataCardGoalSelectName.text.trim()) {
                when {
                    isBlank() -> binding.dataCardGoalSelectCreate.error = getString(R.string.data_Goal_Select_Name_ErrorMsg_empty)
                    length < MIN_NAME_LENGTH -> binding.dataCardGoalSelectCreate.error = getString(R.string.data_Goal_Select_Name_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                    else -> findNavController().navigate(UpdateGoalSelectFragmentDirections.goalSelectToUpdateGoalDetails(name = this.toString()))
                }
            }
        }
    }

    private fun checkRequirements() : String? {
        with(binding.dataCardGoalSelectName.text.trim()) {
            when {
                isBlank() -> binding.dataCardGoalSelectCreate.error = getString(R.string.data_Goal_Select_Name_ErrorMsg_empty)
                length < MIN_NAME_LENGTH -> binding.dataCardGoalSelectCreate.error = getString(R.string.data_Goal_Select_Name_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> return this.toString()
            }
            return null
        }
    }

    class GoalPrefabAdapter(val callBack: CallBack) : RecyclerView.Adapter<GoalPrefabAdapter.PrefabViewHolder>() {
        interface CallBack { fun onSelectPrefab(prefab: GoalPrefabs) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefabViewHolder {
            return PrefabViewHolder(parent.inflate(R.layout.list_goal_prefab))
        }

        override fun onBindViewHolder(holder: PrefabViewHolder, position: Int) = holder.bind(GoalPrefabs.values()[position])

        override fun getItemCount() = GoalPrefabs.values().size - 1 // None is not needed.

        inner class PrefabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(prefab: GoalPrefabs) {
                with(itemView) {
                    val arrIcon = resources.obtainTypedArray(R.array.goalPrefab_icons)

                    listGoalPrefab_img.setImageResource(arrIcon.getResourceId(prefab.ordinal, 0))
                    ViewCompat.setBackgroundTintList(listGoalPrefab_img, ColorStateList.valueOf(resources.getIntArray(R.array.goalPrefab_colors)[prefab.ordinal]))
                    listGoalPrefab_name.text = resources.getStringArray(R.array.goalPrefab_names)[prefab.ordinal]

                    arrIcon.recycle()

                    setOnClickListener { callBack.onSelectPrefab(prefab) }
                }
            }
        }
    }
}