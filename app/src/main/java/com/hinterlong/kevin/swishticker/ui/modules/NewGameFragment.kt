package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.data.Team
import kotlinx.android.synthetic.main.fragment_new_game.*


class NewGameFragment : Fragment() {
    private var homeTeam: Team? = null
    private var awayTeam: Team? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Save instance state? Teams deselected when switching to different tab

        selectMyTeam.setOnClickListener {
            SelectTeamDialog(view.context, this).onSelectTeam {
                homeTeam = it
                // TODO: update default home team
                selectMyTeam.visibility = View.GONE
                setTeamView(R.id.homeTeamCard, homeTeam!!)
                setReady(view.context)
            }.show()
        }

        selectAwayTeam.setOnClickListener {
            SelectTeamDialog(view.context, this).onSelectTeam {
                awayTeam = it
                selectAwayTeam.visibility = View.GONE
                setTeamView(R.id.awayTeamCard, awayTeam!!)
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
        context?.let {
            setReady(it, homeTeam != null)
        }
    }

    private fun setTeamView(cardId: Int, team: Team) {
        view?.let {
            val card: CardView = it.findViewById(cardId)
            card.visibility = View.VISIBLE
            selectMyTeam.visibility = View.GONE
            val name: TextView = card.findViewById(R.id.teamName)
            val size: TextView = card.findViewById(R.id.teamSize)
            name.text = team.name
        }
    }

    private fun setReady(context: Context, ready: Boolean = true) {
        when (ready) {
            true -> {
                homeCenterAction.text = context.getText(R.string.start)
                homeCenterAction.background = context.getDrawable(R.drawable.home_button_bg_ready)
                homeCenterSeparatorLeft.background = context.getDrawable(R.color.homeReady)
                homeCenterSeparatorRight.background = context.getDrawable(R.color.homeReady)
            }
            false -> {
                homeCenterAction.text = context.getText(R.string.versus)
                homeCenterAction.background = context.getDrawable(R.drawable.home_button_bg_not_ready)
                homeCenterSeparatorLeft.background = context.getDrawable(R.color.homeNotReady)
                homeCenterSeparatorRight.background = context.getDrawable(R.color.homeNotReady)
            }
        }
    }
}
