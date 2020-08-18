package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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

        binding.dataCardGoalSelectGrid.adapter =
                GoalPrefabAdapter(object : GoalPrefabAdapter.CallBack {
                    override fun onSelectPrefab(prefab: GoalPrefabs) {
                        findNavController().navigate(UpdateGoalSelectFragmentDirections.goalSelectToUpdateGoalDetails(prefab = prefab))
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

    class GoalPrefabAdapter(val callBack: CallBack) : RecyclerView.Adapter<GoalPrefabAdapter.PrefabViewHolder>() {
        interface CallBack { fun onSelectPrefab(prefab: GoalPrefabs) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefabViewHolder {
            return PrefabViewHolder(parent.inflate(R.layout.list_goal_prefab))
        }

        override fun onBindViewHolder(holder: PrefabViewHolder, position: Int) = holder.bind(GoalPrefabs.values()[position])

        override fun getItemCount() = GoalPrefabs.values().size

        inner class PrefabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(prefab: GoalPrefabs) {
                with(itemView) {
                    listGoalPrefab_img.setImageResource(resources.getIntArray(R.array.goalPrefab_icons)[prefab.ordinal])
                    listGoalPrefab_img.setBackgroundColor(resources.getIntArray(R.array.goalPrefab_colors)[prefab.ordinal])
                    listGoalPrefab_name.text = resources.getStringArray(R.array.goalPrefab_names)[prefab.ordinal]

                    setOnClickListener { callBack.onSelectPrefab(prefab) }
                }
            }
        }
    }
}