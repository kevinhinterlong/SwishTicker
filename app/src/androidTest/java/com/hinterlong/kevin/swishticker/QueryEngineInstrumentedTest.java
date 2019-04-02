package com.hinterlong.kevin.swishticker;

import android.content.Context;

import com.hinterlong.kevin.swishticker.data.Action;
import com.hinterlong.kevin.swishticker.data.ActionType;
import com.hinterlong.kevin.swishticker.data.Game;
import com.hinterlong.kevin.swishticker.data.Player;
import com.hinterlong.kevin.swishticker.data.Team;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QueryEngineInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.hinterlong.kevin.swishticker", appContext.getPackageName());
    }

    @Test
    public void addTeam() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        QueryEngine.init(appContext);

        QueryEngine qe = QueryEngine.getInstance();
        int bemarsId = qe.addTeam(new Team("bemars"));
        String bemarsName = qe.getTeam(bemarsId).getName();

        assertEquals("bemars", bemarsName);
    }

    @Test
    public void addPlayers() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        QueryEngine.init(appContext);

        QueryEngine qe = QueryEngine.getInstance();
        int bemarsId = qe.addTeam(new Team("bemars"));

        int nanamaruId = qe.addPlayer(bemarsId, new Player("nanamaru", 1));

        int nanamaruNumber = qe.getPlayer(nanamaruId).getNumber();

        assertEquals(1, nanamaruNumber);
    }

    @Test
    public void addGames() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        QueryEngine.init(appContext);

        QueryEngine qe = QueryEngine.getInstance();
        int bemarsId = qe.addTeam(new Team("bemars"));
        int polarisId = qe.addTeam(new Team("polaris"));

        int nanamaruId = qe.addPlayer(bemarsId, new Player("nanamaru", 1));
        int shellyId = qe.addPlayer(polarisId, new Player("shelly", 1));

        int gameId = qe.addGame(new Game(bemarsId, polarisId));

        assertEquals(bemarsId, qe.getGame(gameId).getHomeTeamId());
        assertEquals(0, qe.getGame(gameId).getAwayScore());

        qe.addAwayAction(gameId, new Action(ActionType.THREE_POINT, shellyId));

        assertEquals(3, qe.getGame(gameId).getAwayScore());
    }
}
