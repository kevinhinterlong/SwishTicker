package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.utilities.Prefs
import kotlinx.android.synthetic.main.fragment_new_game.*


class NewGameFragment : Fragment() {
    private var homeTeam: Team? = null
    private var awayTeam: Team? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectMyTeam.setOnClickListener {
            SelectTeamDialog(view.context, this).onSelectTeam {
                setHomeTeam(it)
            }.show()
        }

        selectAwayTeam.setOnClickListener {
            SelectTeamDialog(view.context, this).onSelectTeam {
                setAwayTeam(it)
            }.show()
        }

        homeCenterAction.setOnClickListener {
            if (homeTeam == null) {
                Toast.makeText(view.context, getString(R.string.must_select_home_team), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(view.context, "TODO: Start game", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val teamId = Prefs.defaultTeamId
        if (teamId != null && teamId != homeTeam?.id) {
            context?.let { homeTeam = AppDatabase.getInstance(it).teamDao().getTeam(teamId) }
        }
        setHomeTeam(homeTeam)
        setAwayTeam(awayTeam)
    }

    private fun setHomeTeam(it: Team?) {
        homeTeam = it
        Prefs.defaultTeamId = it?.id
        setReady(homeTeam != null)
        setTeam(homeTeam, selectMyTeam, R.id.homeTeamCard)
    }

    private fun setAwayTeam(it: Team?) {
        awayTeam = it
        setTeam(awayTeam, selectAwayTeam, R.id.awayTeamCard)
    }

    private fun setTeam(it: Team?, selectButton: View, teamCardId: Int) = when (it) {
        null -> {
            selectButton.visibility = View.VISIBLE
        }
        else -> {
            setTeamView(selectButton, teamCardId, it)
            selectButton.visibility = View.GONE
        }
    }

    private fun setTeamView(selectButton: View, cardId: Int, team: Team) {
        view?.let {
            val card: CardView = it.findViewById(cardId)
            card.visibility = View.VISIBLE
            selectButton.visibility = View.GONE
            val name: TextView = card.findViewById(R.id.teamName)
            val size: TextView = card.findViewById(R.id.teamSize)
            name.text = team.name
        }
    }

    private fun setReady(ready: Boolean = true) = context?.let {
        when (ready) {
            true -> {
                homeCenterAction.text = it.getText(R.string.start)
                homeCenterAction.background = it.getDrawable(R.drawable.home_button_bg_ready)
                homeCenterSeparatorLeft.background = it.getDrawable(R.color.homeReady)
                homeCenterSeparatorRight.background = it.getDrawable(R.color.homeReady)
            }
            false -> {
                homeCenterAction.text = it.getText(R.string.versus)
                homeCenterAction.background = it.getDrawable(R.drawable.home_button_bg_not_ready)
                homeCenterSeparatorLeft.background = it.getDrawable(R.color.homeNotReady)
                homeCenterSeparatorRight.background = it.getDrawable(R.color.homeNotReady)
            }
        }
    }
}
