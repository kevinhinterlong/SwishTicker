package com.hinterlong.kevin.swishticker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.data.Team
import com.hinterlong.kevin.swishticker.ui.modules.EditTeamNameDialog
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment
import com.hinterlong.kevin.swishticker.ui.modules.MyTeamFragment
import com.hinterlong.kevin.swishticker.ui.modules.NewGameFragment
import com.hinterlong.kevin.swishticker.utilities.Prefs
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.util.Colors
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val historyFragment = HistoryFragment()
    private val teamsFragment = MyTeamFragment()
    private val newGameFragment = NewGameFragment()
    private lateinit var result: Drawer
    private var menu: Menu? = null

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
                    1L -> {
                        menu?.clear()
                        fragment = newGameFragment
                    }
                    2L -> {
                        menu?.clear()
                        fragment = historyFragment
                    }
                    3L -> {
                        fragment = teamsFragment
                        menuInflater.inflate(R.menu.team_menu, menu)
                    }
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.editName -> {
                val teamLiveData = AppDatabase.getInstance(this).teamDao().getTeam(Prefs.defaultTeamId!!)
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
}
