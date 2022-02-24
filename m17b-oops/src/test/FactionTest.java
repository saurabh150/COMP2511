package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

import unsw.gloriaromanus.ArrayUtil;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.movement.Graph;
import unsw.gloriaromanus.movement.MoveUnit;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitFactory;

public class FactionTest {

    private List<Province> provinces;
    private static final String PATH1 = "bin/test/test_province.json";
    private static final String PATH2 = "bin/test/test_province_adjacent.json";
    @Test
    public void testFactionMoney() {

        Faction f = new Faction("Romans");
        Province p1 = new Province("BestProvince");
        Province p2 = new Province("BadProvince");

        assertEquals("Romans", f.getName());
        // Checking if initial wealth of faction is 0
        assertEquals(0, f.getTreasuryAmount());
        assertEquals(0, f.getWealth());

        f.addProvince(p1);
        f.addProvince(p2);
        assertEquals(p1.buildFarm(), true);
        p2.buildMarket();
        f.updateTurn();

        // Since market is made, its buildingCost * 15% rate
        assertEquals(30, f.getTreasuryAmount());
        assertEquals(200, f.getWealth());

        f.updateTurn();
        // Infrastructire should be built 30 + ((200+10) + (300))*15% = 107
        assertEquals(107, f.getTreasuryAmount());
        assertEquals(510, f.getWealth());


        f.updateTurn();
        // 107 + ((200+10*2) + (300))*15% = 186.5 = 187
        assertEquals(530, f.getWealth());
        assertEquals(187, f.getTreasuryAmount());

        assertEquals(true ,f.containsProvince(p1));
        f.removeProvince(p1);
        assertEquals(false, f.containsProvince(p1));
    }

    @Test
    public void testFactionUnit() {
        Faction f = new Faction("Romans");
        createProvinces();
        addProvinces(f);
        Province start = provinces.get(0);
        Province dest = provinces.get(6);

        List<Unit> units = new ArrayList<>();
        f.createUnitInProvince(start, SoldierType.MISSILE_INFANTRY);
        f.createUnitInProvince(start, SoldierType.MISSILE_INFANTRY);
        f.updateTurn();
        f.updateTurn();

        Graph.setPath(PATH2);
        MoveUnit mu = new MoveUnit(provinces);
        f.initMoveUnit(mu);

        units = start.getUnits();
        assertEquals(2, start.getUnits().size());
        assertEquals(0, dest.getUnits().size());

        f.move(units, start, dest);
        assertEquals(0, start.getUnits().size());
        assertEquals(2, dest.getUnits().size());

    }

    // @Test
    // public void testStartBattle1() {
    //     Faction f1 = new Faction("Romans");
    //     Faction f2 = new Faction("Gauls");
    //     createProvinces();
    //     Province a = provinces.get(0);
    //     Province b = provinces.get(1);

    //     // Adding more units to f1 to make f1 win
    //     f1.addProvince(a);
    //     f1.addProvince(b);
    //     f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
    //     f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);
    //     f1.createUnitInProvince(a, SoldierType.MISSILE_INFANTRY);

    //     f2.createUnitInProvince(b, SoldierType.MISSILE_INFANTRY);
    //     f1.updateTurn();
    //     f2.updateTurn();
    //     f1.updateTurn();
    //     f2.updateTurn();
    //     assertEquals(3, a.getUnits().size());
    //     assertEquals(1, b.getUnits().size());

    //     List<Unit> unitsOfA = a.getUnits();

    //     // Checking if any of the units from province a moved to b
    //     if (f1.startBattle(a, b)) {
    //         System.out.println("Won!");
    //         assertEquals(0, a.getUnits().size());
    //         boolean flag = false;
    //         if (b.getUnits().contains(unitsOfA.get(0)))
    //             flag = true;
    //         if (b.getUnits().contains(unitsOfA.get(1)))
    //             flag = true;
    //         if (b.getUnits().contains(unitsOfA.get(2)))
    //             flag = true;
    //         assertEquals(true, flag);
    //     }
    // }

    // @Test
    // public void testStartBattle2() {
    //     Faction romans = new Faction("Romans");
    //     Faction greeks = new Faction("Greek");
    //     Province romanProvince = new Province("romanProvince");
    //     Province greekProvince = new Province("greekProvince");

    //     romans.addProvince(greekProvince);
    //     greeks.addProvince(romanProvince);

    //     UnitFactory unitFactory = new UnitFactory();
    //     Unit attack = unitFactory.createUnit(romans.getName(), SoldierType.ARCHERS);
    //     Unit defence = unitFactory.createUnit(greeks.getName(), SoldierType.BERSERKERS);
    //     List<Unit> attackers = new ArrayList<>();
	// 	attackers.add(attack);
	// 	List<Unit> defenders = new ArrayList<>();
    //     defenders.add(defence);
    //     romanProvince.setUnits(attackers);
    //     greekProvince.setUnits(defenders);
    //     boolean hasWon = romans.startBattle(romanProvince, greekProvince);
    //     System.out.println(hasWon);
    //     System.out.println(attackers);
    //     System.out.println(attack);
    //     System.out.println(defenders);
    //     System.out.println(defence);

    //     if (hasWon) {
    //         assertEquals(Arrays.asList(attack), greekProvince.getUnits());
    //         assertEquals(0, romanProvince.getUnits().size());
    //     } else {
    //         assertEquals(attackers, romanProvince.getUnits());
    //         assertEquals(defenders, greekProvince.getUnits());
    //     }
    //     // System.out.println(battleWon);

    // }




    public void createProvinces() {
        provinces = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(PATH1));
            JSONArray namesJSON = new JSONArray(content);
            List<String> names = ArrayUtil.convert(namesJSON);
            for (int i = 0; i < names.size(); i++) {
                provinces.add(new Province(names.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProvinces(Faction f) {
        for (Province p: provinces) {
            f.addProvince(p);
        }
    }
}
