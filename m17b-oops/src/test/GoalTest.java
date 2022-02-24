package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.victory.Goal;
import unsw.gloriaromanus.victory.GoalAND;
import unsw.gloriaromanus.victory.GoalComposite;
import unsw.gloriaromanus.victory.GoalConquest;
import unsw.gloriaromanus.victory.GoalGenerator;
import unsw.gloriaromanus.victory.GoalOR;
import unsw.gloriaromanus.victory.GoalTreasury;
import unsw.gloriaromanus.victory.GoalWealth;
import unsw.gloriaromanus.victory.VictoryCondition;

public class GoalTest {

    @Test
    public void testSimpleWealth() {
        
        Faction f = new Faction("Roman");

        VictoryCondition w = new GoalWealth();
        Goal g = new Goal(w);
        
        // Should fail since f has 0 townWealth
        assertEquals(g.achieved(f), false);

        // Should pass now
        f.addTownWealth(400000);
        assertEquals(g.achieved(f), true);
        
        assertEquals(g.toReadable().toString(), "{\"goal\":\"WEALTH\"}");
        
    }

    @Test
    public void testSimpleConquest() {
        
        Faction f = new Faction("Roman");
        List<Province> provinces = new ArrayList<>();

        // Creating fake provinces
        for (int i = 0; i < 53; i++)
            provinces.add(new Province(("p"+i)));

        VictoryCondition c = new GoalConquest();
        Goal g = new Goal(c);
        
        // Should fail since f has 0 provinces
        assertEquals(g.achieved(f), false);

        // Should pass now
        for (Province p: provinces)
            f.addProvince(p);
        assertEquals(g.achieved(f), true);
        
        assertEquals(g.toReadable().toString(), "{\"goal\":\"CONQUEST\"}");
    }

    @Test
    public void testSimpleTreasury() {
        Faction f = new Faction("Roman");

        VictoryCondition t = new GoalTreasury();
        Goal g = new Goal(t);
        
        // Should fail since f has 0 treasury
        assertEquals(g.achieved(f), false);

        // Should pass now
        f.addTreasury(100000);
        assertEquals(g.achieved(f), true);
        
        assertEquals(g.toReadable().toString(), "{\"goal\":\"TREASURY\"}");
    }

    @Test
    public void testSimpleAND() {
        Faction f = new Faction("Roman");

        VictoryCondition t = new GoalTreasury();
        VictoryCondition w = new GoalWealth();
        VictoryCondition a = new GoalAND();
        
        GoalComposite g = new GoalComposite(a);
        Goal g1 = new Goal(t);
        Goal g2 = new Goal(w);
        g.addSubgoal(g1);
        g.addSubgoal(g2);

        // Should fail since f has 0 townWealth and treasury
        assertEquals(g.achieved(f), false);

        // Should fail since onlt treasury is fulfilled
        f.addTreasury(100000);
        assertEquals(g.achieved(f), false);

        // Should work now
        f.addTownWealth(400000);
        assertEquals(g.achieved(f), true);

        assertEquals(g.toReadable().toString(), "{\"goal\":\"AND\",\"subgoals\":[{\"goal\":\"TREASURY\"},{\"goal\":\"WEALTH\"}]}");
    }

    @Test
    public void testSimpleOR() {
        Faction f = new Faction("Roman");

        VictoryCondition t = new GoalTreasury();
        VictoryCondition w = new GoalWealth();
        VictoryCondition o = new GoalOR();
        
        GoalComposite g = new GoalComposite(o);
        Goal g1 = new Goal(t);
        Goal g2 = new Goal(w);
        g.addSubgoal(g1);
        g.addSubgoal(g2);

        // Should fail since f has 0 townWealth and treasury
        assertEquals(g.achieved(f), false);

        // Should pass since treasury is fulfilled
        f.addTreasury(100000);
        assertEquals(g.achieved(f), true);

        // Should also pass now
        f.addTownWealth(400000);
        assertEquals(g.achieved(f), true);

        assertEquals(g.toReadable().toString(), "{\"goal\":\"OR\",\"subgoals\":[{\"goal\":\"TREASURY\"},{\"goal\":\"WEALTH\"}]}");
    }

    @Test
    public void testComplexGoal()  {
        Faction f = new Faction("Roman");

        VictoryCondition t = new GoalTreasury();
        VictoryCondition w = new GoalWealth();
        VictoryCondition o = new GoalOR();
        VictoryCondition a = new GoalAND();
        VictoryCondition c = new GoalConquest();
        
        GoalComposite gc2 = new GoalComposite(o);
        Goal g1 = new Goal(t);
        Goal g2 = new Goal(c);
        Goal g3 = new Goal(w);
        gc2.addSubgoal(g1);
        gc2.addSubgoal(g2);

        GoalComposite gc = new GoalComposite(a);
        gc.addSubgoal(g3);
        gc.addSubgoal(gc2);

        // Since no goal is achieved go far;
        assertEquals(gc.achieved(f), false);
        
        // Should still fail since Its AND(false, OR(true, false));
        f.addTreasury(100000);
        assertEquals(gc.achieved(f),false);
        
        // Shold pass now
        f.addTownWealth(400000);
        assertEquals(gc.achieved(f), true);

        String outString = "{\"goal\":\"AND\",\"subgoals\":[{\"goal\":\"WEALTH\"},{\"goal\":\"OR\",\"subgoals\":[{\"goal\":\"TREASURY\"},{\"goal\":\"CONQUEST\"}]}]}";
        assertEquals(gc.toReadable().toString(), outString);
    }

    @Test
    public void testRandomGoal0() {
        GoalGenerator gg = new GoalGenerator();
        gg.seedRandom(0);
        Goal g = gg.generate();
        assertEquals(g.toReadable().toString(), "{\"goal\":\"OR\",\"subgoals\":[{\"goal\":\"TREASURY\"},{\"goal\":\"WEALTH\"}]}");
        Faction f = new Faction("Roman");

        // Should fail since f has 0 townWealth and treasury
        assertEquals(g.achieved(f), false);

        // Should pass since treasury is fulfilled
        f.addTreasury(100000);
        assertEquals(g.achieved(f), true);

        // Should also pass now
        f.addTownWealth(400000);
        assertEquals(g.achieved(f), true);
    }

    @Test
    public void testRandomGoal2() {
        GoalGenerator gg = new GoalGenerator();
        gg.seedRandom(2);
        String outString = "{\"goal\":\"AND\",\"subgoals\":[{\"goal\":\"TREASURY\"},{\"goal\":\"OR\",\"subgoals\":[{\"goal\":\"CONQUEST\"},{\"goal\":\"WEALTH\"}]}]}";
        Goal gc = gg.generate();
        assertEquals(gc.toReadable().toString(), outString);


        Faction f = new Faction("Roman");
        // Since no goal is achieved go far;
        assertEquals(gc.achieved(f), false);
        
        // Should still fail since Its AND(false, OR(true, false));
        f.addTreasury(100000);
        assertEquals(gc.achieved(f),false);
        
        // Shold pass now
        f.addTownWealth(400000);
        assertEquals(gc.achieved(f), true);

    }


}
