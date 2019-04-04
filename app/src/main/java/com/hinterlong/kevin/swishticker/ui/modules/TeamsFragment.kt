package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.ui.adapters.TeamItem
import eu.davidea.flexibleadapter.FlexibleAdapter

/**
 * A fragment representing a list of Items.
 */
class TeamsFragment : Fragment() {

    private val adapter = FlexibleAdapter<TeamItem>(null)
    @BindView(R.id.game_list)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.team_fab)
    lateinit var fab: FloatingActionButton
    private lateinit var unbinder: Unbinder


    private val teamsList: List<TeamItem>
        get() {
            val context = context ?: return listOf()
            return AppDatabase.getInstance(context)
                .teamDao()
                .getTeams()
                .map(::TeamItem)
                .toList()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_list, container, false)
        unbinder = ButterKnife.bind(this, view)
        recyclerView.adapter = adapter

        fab.setOnClickListener { startActivity(Intent(context, NewTeamActivity::class.java)) }

        return view
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDataSet(teamsList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        adapter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }
}
