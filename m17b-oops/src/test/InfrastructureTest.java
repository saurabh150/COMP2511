package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import unsw.gloriaromanus.infratructures.Farm;
import unsw.gloriaromanus.infratructures.Market;

public class InfrastructureTest {
    
    private int farmCost = 300;
    private int farmTurnsToBuild = 2;
    private int farmCostToUpgrade = 300;
    private int farmTownWealthGeneration = 10;

    private int marketcost = 200;
    private int marketTurnsToBuild = 1;
    private int marketCostToUpgrade = 200;
    private int marketTownWealthGeneration = 10;
    
    @Test
    public void testFarm() {
        Farm f = new Farm();
        assertEquals(f.getClass(), Farm.class);
        
        // Takes 2 turns to build
        // Assuming one turn is over, building Farm
        assertEquals(f.build(), false);
        // Another turn, should return true now
        assertEquals(f.build(), true);
         
        // Getting other values
         assertEquals(f.getCost(), farmCost);
         assertEquals(f.getTurnsToBuild(), farmTurnsToBuild);
         assertEquals(f.getCostToUpgrade(), farmCostToUpgrade);
 
         // Getting turn by turn wealth generation
         assertEquals(f.update(), farmTownWealthGeneration);

    }
    @Test
    public void testMarket() {
        Market m = new Market();
        assertEquals(m.getClass(), Market.class);

        // Takes onw turn to build market
        assertEquals(m.build(), true);

        // Getting other values
        assertEquals(m.getCost(), marketcost);
        assertEquals(m.getTurnsToBuild(), marketTurnsToBuild);
        assertEquals(m.getCostToUpgrade(), marketCostToUpgrade);

        // Getting turn by turn wealth generation
        assertEquals(m.update(), marketTownWealthGeneration);

        assertEquals(m.upgrade(), marketCostToUpgrade);
        assertEquals(m.upgrade(), marketCostToUpgrade);
        assertEquals(m.upgrade(), marketCostToUpgrade);
        assertEquals(m.upgrade(), 0);
    }
}
