package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.units.SoldierType;

public class ProvinceTest {
    private int trainingSlots = 2;
    private int farmCost = 300;
    private int marketCost = 200;
    private int townWealthGen = 10;
    @Test
    public void testInfrastructureProvince() {
        Province p = new Province("BestProvince");
        assertEquals(p.getTrainingSlots(), trainingSlots);
        
        // trying to build farm-> should succeed  
        assertEquals(p.buildFarm(), true);
        // trying to build market-> should fail as farm already constructing
        assertEquals(p.buildMarket(), false);
        
        // Takes two turns to build farm
        p.update();
        p.update();
        // Checking if buildingWealth increases
        assertEquals(p.getBuildingWealth(), farmCost);

        // Should be able to build market now, takes one turn to build it
        assertEquals(p.buildMarket(), true);
        p.update();
        // Checking the townWealth generated from farm
        assertEquals(p.getTownWealth(), townWealthGen);
        // Checking the building wealth 
        assertEquals(p.getBuildingWealth(), (farmCost + marketCost));
        // Trying to build farm, should fail since farm already exists
        assertEquals(p.buildFarm(), false);
        // Trying to build market, should fail since market already exists
        assertEquals(p.buildMarket(), false);

        p.update();
        assertEquals(p.getTownWealth(), (townWealthGen*3));
        
        // Upgrading Farm and market, should increase the buildingWealth
        p.upgradeFarm();
        assertEquals(p.getBuildingWealth(), (farmCost*2 + marketCost));

        p.upgradeaMarket();
        assertEquals(p.getBuildingWealth(), (farmCost*2 + marketCost*2));
    }

    @Test
    public void testTax() {
        Province p = new Province("BestProvince");
        
        // Since initially tax is normal, should have +0 townWealth
        p.update();
        assertEquals(p.getTownWealth(), 0);

        p.setLowTaxRate();
        
        // Having 10 turns to generate taxMoney
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();
        p.update();

        // Checking the taxRevenue and townwealth
        assertEquals(p.getTaxRevenue(), 10);
        assertEquals(p.getTownWealth(), (10*10));
        
        // Increasing tax
        p.setHighTaxRate();
        p.update();

        // Should reduce the townWealth by 10
        assertEquals(p.getTownWealth(), (10*9));
        assertEquals(p.getTaxRevenue(), 18);

        p.setVeryHighTaxRate();
        p.update();
        p.update();
        // Should reduce the townWealth by -60
        assertEquals(p.getTownWealth(), (10*3));
        assertEquals(p.getTaxRevenue(), 8);
        p.setNormalTaxRate();
        // Since new rate is 0.15
        assertEquals(p.getTaxRevenue(), 5);

    }

    @Test
    public void testUnits() {
        Province p = new Province("BestProvince");
        // Creating a unit
        p.createUnit("Romans", SoldierType.ARCHERS);
        // Checking if the unit is still under training
        assertEquals(0 ,p.getUnits().size());
        // updateing Training
        p.updateTrainees();
        assertEquals(1 ,p.getUnits().size());
        
    }


}
