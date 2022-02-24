package test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Game;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.units.SoldierType;

public class GameTest {

    @Test
    public void testGame1() {
        // Initializing Game
        Game game = new Game();
        game.seedRandom(1);
        game.init(2);
        List<Faction> factions = game.getFactions();
        // Checking if two factions were creating
        assertEquals(2, factions.size());

        // Checking if provinces are fairly divided
        Faction f1 = factions.get(0);
        Faction f2 = factions.get(1);
        int p1 = f1.getConquestSize();
        int p2 = f2.getConquestSize();
        if (p1 != p2 && p1 > p2 && ((p1 - p2) > 2 )) {
            assertEquals(false, true);
        }

        if ((p1 != p2 && p1 < p2 && ((p2 - p1) > 2 )))
            assertEquals(false, true);

        // Checking turns
        assertEquals(f1, game.getFactionTurn());
        // game.incrementTurn(); // commented this out because it could be broken- i added increment turn to get faction turn
        assertEquals(f2, game.getFactionTurn());

        // Checking Victory condition without anything done
        assertEquals("", game.checkVictory());
        // Completing all possible goals for f1
        f1.addTownWealth(400000);
        f1.addTreasury(100000);
        List<Province> ps = f2.getProvinces();
        for(Province p: ps) {
            f2.removeProvince(p);
            f1.addProvince(p);
        }
        assertEquals(0, f2.getConquestSize());

        assertEquals(f1.getName(), game.checkVictory());
    }

    @Test
    public void testBattle1() {
        // Initializing Game
        Game game = new Game();
        game.seedRandom(1);
        game.init(2);
        List<Faction> factions = game.getFactions();
        // Checking if two factions were creating
        assertEquals(2, factions.size());

        // Checking if provinces are fairly divided
        Faction f1 = factions.get(0);
        Faction f2 = factions.get(1);

        Province a = f1.getProvinces().get(0);
        Province b = f2.getProvinces().get(0);

        // Creating more units in f1 than f2 to make f1 win
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
        f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);

        f2.createUnitInProvince(b, SoldierType.MISSILE_INFANTRY);
        f1.updateTurn();
        f2.updateTurn();
        f1.updateTurn();
        f2.updateTurn();
        assertEquals(6, a.getUnits().size());
        assertEquals(1, b.getUnits().size());

        // Doing battle in game
        game.startBattle(a, b);

        // Checking if province b now belongs to f1
        assertEquals(true, f1.containsProvince(a));
        assertEquals(true, f1.containsProvince(b));
        assertEquals(false, f2.containsProvince(b));
    }


}
