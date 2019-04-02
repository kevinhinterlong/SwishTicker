package com.hinterlong.kevin.swishticker.ui;

import android.os.Bundle;

import com.hinterlong.kevin.swishticker.QueryEngine;
import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Team;
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment;
import com.hinterlong.kevin.swishticker.ui.modules.NewGameFragment;
import com.hinterlong.kevin.swishticker.ui.modules.TeamsFragment;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.util.Colors;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    private HistoryFragment historyFragment = new HistoryFragment();
    private TeamsFragment teamsFragment = new TeamsFragment();
    private NewGameFragment newGameFragment = new NewGameFragment();
    private Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withStickyHeader(R.layout.nav_drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName(R.string.start_game).withIcon(GoogleMaterial.Icon.gmd_home),
                        new PrimaryDrawerItem().withIdentifier(2).withName(R.string.your_games).withIcon(GoogleMaterial.Icon.gmd_assignment),
                        new PrimaryDrawerItem().withIdentifier(3).withName(R.string.your_teams).withIcon(GoogleMaterial.Icon.gmd_people)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    long id = drawerItem.getIdentifier();

                    Fragment fragment;
                    if (id == 1) {
                        fragment = newGameFragment;
                    } else if (id == 2) {
                        fragment = historyFragment;
                    } else if (id == 3) {
                        fragment = teamsFragment;
                    } else if (id == 4) {
                        new LibsBuilder()
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                .withAboutIconShown(true)
                                .withAboutVersionShown(true)
                                .withActivityColor(new Colors(ContextCompat.getColor(this, R.color.primaryColor), ContextCompat.getColor(this, R.color.primaryDarkColor)))
                                .withActivityTitle(getString(R.string.about))
                                .withAboutDescription("SwishTicker allows you to keep track of basketball statistics for all your favorite teams.")
                                .start(this);
                        // This must return here since it starts a new activity instead of switching fragments
                        return false;
                    } else {
                        fragment = newGameFragment;
                        Timber.e("No matching drawer item for id %d", id);
                    }
                    PrimaryDrawerItem item = (PrimaryDrawerItem) result.getDrawerItem(id);
                    setTitle(item.getName().getText(this));
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentFrame, fragment).commit();
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result.addStickyFooterItem(new PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.about)).withIcon(FontAwesome.Icon.faw_info_circle).withSelectable(false));

        result.setSelection(1, true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);


        addTestGame();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Adds a single game to test the view
     */
    private void addTestGame() {
        if (QueryEngine.getInstance().getGames().size() == 0) {
            Team home = new Team("Winners");
            Team away = new Team("Losers");
            int homeId = QueryEngine.getInstance().addTeam(home);
            int awayId = QueryEngine.getInstance().addTeam(away);
            Game test = new Game(homeId, awayId);
            QueryEngine.getInstance().addGame(test);
        }
    }
}
