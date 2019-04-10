package com.hinterlong.kevin.swishticker.ui.modules

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.ui.adapters.TeamItem
import com.hinterlong.kevin.swishticker.utilities.setFullScreen
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.dialog_select_team.*


class SelectTeamDialog(context: Context, private val lifecycleOwner: LifecycleOwner) : Dialog(context) {
    private var adapter = FlexibleAdapter<TeamItem>(null)
    private var listener: (Long) -> Unit = {}

    init {
        setContentView(R.layout.dialog_select_team)
        setFullScreen(window)
    }

    override fun onStart() {
        super.onStart()

        Transformations.map(AppDatabase.getInstance(context)
            .teamDao()
            .getTeams(), { it.filterNot { it.generated }.map(::TeamItem).sortedBy { it.team.name } })
            .observe(lifecycleOwner, Observer {
                adapter = FlexibleAdapter(it)
                adapter.setFilter(filterTeamsInput.text.toString())
                adapter.filterItems()
                setupAdapter(adapter)
                teamsList.adapter = adapter
            })

        addToTeam.setOnClickListener {
            AppDatabase.getInstance(context).teamDao().insertTeam(Team(filterTeamsInput.text.toString()))
            missingTeam.visibility = View.GONE
        }

        filterTeamsInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val filter = s.toString()
                if (adapter.hasNewFilter(filter)) {
                    adapter.setFilter(filter)
                    adapter.filterItems()
                }
            }
        })
    }

    private fun setupAdapter(adapter: FlexibleAdapter<TeamItem>) {
        adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { _, position ->
            val teamItem = adapter.getItem(position)
            if (teamItem != null) {
                listener(teamItem.team.id)
                dismiss()
            }

            return@OnItemClickListener true
        }

        adapter.addListener(FlexibleAdapter.OnFilterListener { size ->
            if (size == 0) {
                missingTeam.visibility = View.VISIBLE
                teamDoesNotExist.text = context.getString(R.string.team_does_not_exist, filterTeamsInput.text.toString())
            } else {
                missingTeam.visibility = View.GONE
            }
        })
    }

    fun onSelectTeam(function: (Long) -> Unit): SelectTeamDialog {
        listener = function
        return this
    }
}