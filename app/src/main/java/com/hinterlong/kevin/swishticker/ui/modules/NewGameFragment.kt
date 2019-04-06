package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team


class NewGameFragment : Fragment() {
    private lateinit var unbinder: Unbinder
    @BindView(R.id.autoCompleteTeamName) lateinit var tv: AutoCompleteTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_game, container, false)
        unbinder = ButterKnife.bind(this, view)

        context?.let {
            val teams = AppDatabase.getInstance(it).teamDao().getTeams().map(::TeamWrapper).toList()
            ArrayAdapter(it, android.R.layout.simple_list_item_1, teams).also { adapter ->
                tv.setAdapter(adapter)
            }
        }

        tv.onItemClickListener = AdapterView.OnItemClickListener { arg0, _, arg2, _ ->
            val selected = arg0.adapter.getItem(arg2) as TeamWrapper
            Toast.makeText(context, "Clicked $arg2 name: ${selected.team.name}", Toast.LENGTH_SHORT).show()
        }


        return view
    }

    private data class TeamWrapper(val team: Team) {
        override fun toString() = team.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }
}
