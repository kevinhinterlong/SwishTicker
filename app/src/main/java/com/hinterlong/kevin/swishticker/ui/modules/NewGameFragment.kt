package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Game
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.utilities.Prefs
import kotlinx.android.synthetic.main.fragment_new_game.*
import kotlinx.android.synthetic.main.team_card.view.*
import timber.log.Timber


class NewGameFragment : Fragment() {
    private var homeTeam: Team? = null
    private var awayTeam: Team? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeListener: (View) -> Unit = {
            Timber.d("Selecting home team")
            SelectTeamDialog(view.context, this).onSelectTeam(this::setHomeTeam).show()
        }
        selectMyTeam.setOnClickListener(homeListener)
        homeTeamCard.setOnClickListener(homeListener)

        val awayListener: (View) -> Unit = {
            Timber.d("Selecting away team")
            SelectTeamDialog(view.context, this).onSelectTeam(this::setAwayTeam).show()
        }
        selectAwayTeam.setOnClickListener(awayListener)
        awayTeamCard.setOnClickListener(awayListener)

        homeCenterAction.setOnClickListener {
            if (homeTeam == null) {
                Toast.makeText(view.context, getString(R.string.must_select_home_team), Toast.LENGTH_SHORT).show()
            } else {
                val db = AppDatabase.getInstance(view.context)
                val awayId = if (awayTeam == null) {
                    val team = Team("Away")
                    val teamId = db.teamDao().insertTeam(team)
                    teamId
                } else {
                    awayTeam!!.id
                }
                val gameId = db.gameDao().insertGame(Game(homeTeam!!.id, awayId))
                InGameTrackerActivity.resume(view.context, gameId)
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
            card.teamName.text = team.name
        }
    }

    private fun setReady(ready: Boolean = true) = context?.let {
        when (ready) {
            true -> {
                homeCenterAction.text = it.getText(R.string.start)
                homeCenterAction.background = it.getDrawable(R.drawable.home_button_bg_ready)
                homeCenterSeparatorLeft.background = it.getDrawable(R.color.home_ready)
                homeCenterSeparatorRight.background = it.getDrawable(R.color.home_ready)
            }
            false -> {
                homeCenterAction.text = it.getText(R.string.versus)
                homeCenterAction.background = it.getDrawable(R.drawable.home_button_bg_not_ready)
                homeCenterSeparatorLeft.background = it.getDrawable(R.color.home_not_ready)
                homeCenterSeparatorRight.background = it.getDrawable(R.color.home_not_ready)
            }
        }
    }
}
