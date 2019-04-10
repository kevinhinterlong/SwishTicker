package com.hinterlong.kevin.swishticker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.*
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment
import com.hinterlong.kevin.swishticker.ui.modules.MyTeamFragment
import com.hinterlong.kevin.swishticker.ui.modules.NewGameFragment
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.util.Colors
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val historyFragment = HistoryFragment()
    private val teamsFragment = MyTeamFragment()
    private val newGameFragment = NewGameFragment()
    private lateinit var result: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withTranslucentStatusBar(true)
            .withStickyHeader(R.layout.nav_drawer_header)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(1).withName(R.string.start_game).withIcon(GoogleMaterial.Icon.gmd_home),
                PrimaryDrawerItem().withIdentifier(2).withName(R.string.my_games).withIcon(GoogleMaterial.Icon.gmd_assignment),
                PrimaryDrawerItem().withIdentifier(3).withName(R.string.my_teams).withIcon(GoogleMaterial.Icon.gmd_people)
            )
            .withOnDrawerItemClickListener { _, _, drawerItem ->
                val id = drawerItem.identifier

                var fragment: Fragment? = null
                when (id) {
                    1L -> fragment = newGameFragment
                    2L -> fragment = historyFragment
                    3L -> fragment = teamsFragment
                    4L -> LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withActivityColor(Colors(ContextCompat.getColor(this, R.color.primaryColor), ContextCompat.getColor(this, R.color.primaryDarkColor)))
                        .withActivityTitle(getString(R.string.about))
                        .withAboutDescription("SwishTicker allows you to keep track of basketball statistics for all your favorite teams.")
                        .start(this)
                    else -> Timber.e("No matching drawer item for id $id")
                }
                if (fragment != null) {
                    val item = result.getDrawerItem(id) as PrimaryDrawerItem
                    title = item.name.getText(this)
                    supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit()
                }
                false
            }
            .withSavedInstance(savedInstanceState)
            .build()


        AppDatabase.getInstance(this).gameDao().getGames().observe(this, Observer {
            val activeGames = it.count { it.active }
            if (activeGames == 0) {
                result.updateBadge(2, StringHolder(""))
            } else {
                result.updateBadge(2, StringHolder(activeGames.toString()))
            }
        })

        result.addStickyFooterItem(PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.about)).withIcon(FontAwesome.Icon.faw_info_circle).withSelectable(false))

        result.setSelection(1, true)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        result.actionBarDrawerToggle.isDrawerIndicatorEnabled = true


        addTestGame()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        var outState = outState
        outState = result.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (result.isDrawerOpen) {
            result.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Adds a single game to test the view
     */
    private fun addTestGame() {
        val db = AppDatabase.getInstance(this)
        db.gameDao().getGames().observe(this, Observer {
            if (it.isEmpty()) {
                val home = Team("Winners")
                val away = Team("Losers")
                val homeId = db.teamDao().insertTeam(home)
                val awayId = db.teamDao().insertTeam(away)
                val homePlayerId1 = db.playerDao().insertPlayer(Player("Player 1", homeId))
                val homePlayerId2 = db.playerDao().insertPlayer(Player("Player 2", homeId))
                val awayPlayerId = db.playerDao().insertPlayer(Player("awayPlayer", awayId))
                val gameId = db.gameDao().insertGame(Game(homeId, awayId))

                val teams = mapOf(
                    homeId to listOf(homePlayerId1, homePlayerId2),
                    awayId to listOf(awayPlayerId)
                )
                (0..4).map(Int::toLong).forEach { period ->
                    (1..20).map { teams.entries.random() }.forEach { team ->
                        val action = ActionType.values().random()
                        val actionResult = if (action in listOf(ActionType.FREE_THROW, ActionType.TWO_POINT, ActionType.THREE_POINT)) {
                            listOf(ActionResult.SHOT_MISS, ActionResult.SHOT_HIT).random()
                        } else {
                            ActionResult.NONE
                        }
                        db.actionDao().insertAction(Action(action, actionResult, team.key, gameId, team.value.random(), period))
                    }
                }
            }
        })

    }
}
