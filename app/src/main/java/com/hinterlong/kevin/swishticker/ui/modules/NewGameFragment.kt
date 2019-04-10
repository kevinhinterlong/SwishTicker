package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Game
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.utilities.Prefs
import kotlinx.android.synthetic.main.fragment_new_game.*
import kotlinx.android.synthetic.main.team_card.view.*
import timber.log.Timber


class NewGameFragment : Fragment() {
    private var homeTeam: LiveData<Team>? = null
    private var awayTeam: LiveData<Team>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeListener: (View) -> Unit = {
            Timber.d("Selecting home team")
            SelectTeamDialog(view.context, this).onSelectTeam(this::setHomeTeamId).show()
        }
        selectMyTeam.setOnClickListener(homeListener)
        homeTeamCard.setOnClickListener(homeListener)

        val awayListener: (View) -> Unit = {
            Timber.d("Selecting away team")
            SelectTeamDialog(view.context, this).onSelectTeam(this::setAwayTeamId).show()
        }
        selectAwayTeam.setOnClickListener(awayListener)
        awayTeamCard.setOnClickListener(awayListener)
        setClickListener(view.context)
    }

    override fun onResume() {
        super.onResume()
        val teamId = Prefs.defaultTeamId
        if (teamId != null) {
            setHomeTeamId(teamId)
        }
    }

    private fun setHomeTeamId(id: Long?) {
        Prefs.defaultTeamId = id
        val context = context
        setReady(id != null)
        if (id == null) {
            homeTeam?.removeObservers(this)
            homeTeam = null
        } else if (context != null) {
            homeTeam = AppDatabase.getInstance(context).teamDao().getTeam(id)
            homeTeam?.observe(this, Observer {
                setTeam(it, selectMyTeam, R.id.homeTeamCard)
            })
            setClickListener(context)
        }
    }

    private fun setAwayTeamId(id: Long?) {
        val context = context
        if (id == null) {
            awayTeam?.removeObservers(this)
            awayTeam = null
        } else if (context != null) {
            awayTeam = AppDatabase.getInstance(context).teamDao().getTeam(id)
            awayTeam?.observe(this, Observer {
                setTeam(it, selectAwayTeam, R.id.awayTeamCard)
            })
            setClickListener(context)
        }
    }

    private fun setClickListener(context: Context) {
        homeCenterAction.setOnClickListener {
            Toast.makeText(context, getString(R.string.must_select_home_team), Toast.LENGTH_SHORT).show()
        }
        homeTeam?.observe(this, Observer { home ->
            val db = AppDatabase.getInstance(context)
            homeCenterAction.setOnClickListener {
                val team = Team("Away")
                val awayId = db.teamDao().insertTeam(team)
                val gameId = db.gameDao().insertGame(Game(home.id, awayId))
                InGameTrackerActivity.resume(context, gameId)
            }

            awayTeam?.observe(this, Observer { away ->
                homeCenterAction.setOnClickListener {
                    val gameId = db.gameDao().insertGame(Game(home.id, away.id))
                    InGameTrackerActivity.resume(context, gameId)
                }
            })
        })
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
