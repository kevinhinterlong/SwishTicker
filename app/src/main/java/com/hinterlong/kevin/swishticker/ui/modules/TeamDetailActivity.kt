package com.hinterlong.kevin.swishticker.ui.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber

class TeamDetailActivity : AppCompatActivity() {
    private var teamId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail_view)

        if (!intent.hasExtra(TEAM_ID)) {
            Timber.e("No team id set")
            finish()
            return
        }
        teamId = intent.getLongExtra(TEAM_ID, 0)

        val teamFragment = MyTeamFragment()
        teamFragment.arguments = Bundle().apply { putLong(MyTeamFragment.OVERRIDE_DEFAULT_TEAM, teamId) }
        supportFragmentManager.beginTransaction().add(R.id.contentFrame, teamFragment).commit()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AppDatabase.getInstance(this).teamDao().getTeam(teamId).observe(this, Observer {
            title = it.name
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.team_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.editName -> {
                var teamLiveData = AppDatabase.getInstance(this).teamDao().getTeam(teamId)
                var updateName: Observer<Team>? = null
                updateName = Observer { team ->
                    EditTeamNameDialog(this, team.name) {
                        updateName?.let { teamLiveData.removeObserver(it) }
                        AppDatabase.getInstance(this).teamDao()
                            .updateTeam(team.copy(name = it).also { it.id = team.id })
                    }.show()
                }
                teamLiveData.observe(this, updateName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TEAM_ID = "TEAM_ID"

        fun withTeam(context: Context, teamId: Long) {
            val intent = Intent(context, TeamDetailActivity::class.java)
            intent.putExtra(TEAM_ID, teamId)
            context.startActivity(intent)
        }
    }
}
