package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

import unsw.gloriaromanus.ArrayUtil;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.movement.Graph;
import unsw.gloriaromanus.movement.MoveUnit;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.UnitFactory;
import unsw.gloriaromanus.units.Unit;

public class MovementTest {
    private static final String PATH1 = "bin/test/test_province.json";
    private static final String PATH2 = "bin/test/test_province_adjacent.json";

    private List<Province> provinces;
    
    @Test
    public void testSimpleGraph() {
        createProvinces();
        Graph.setPath(PATH2);
        Graph g = new Graph(provinces);
        Faction f = new Faction("Roman");
        addProvinces(f);
        System.out.println("Getting shortest path");
        List<Province> p = g.getShortestPath(f, provinces.get(0), provinces.get(6));
        printProvinces(p);
        assertEquals(p.size(), 2);
        assertEquals(p.get(0).getName(), "F");
        assertEquals(p.get(1).getName(), "G");
    }

    @Test
    public void testGraph2() {
        createProvinces();
        Graph.setPath(PATH2);
        Graph g = new Graph(provinces);
        Faction f = new Faction("Roman");
        
        f.addProvince(provinces.get(0));
        f.addProvince(provinces.get(1));
        f.addProvince(provinces.get(2));
        f.addProvince(provinces.get(2));
        f.addProvince(provinces.get(3));
        f.addProvince(provinces.get(4));
        f.addProvince(provinces.get(6));
        
        System.out.println("Getting shortest path");
        List<Province> p = g.getShortestPath(f, provinces.get(0), provinces.get(6));
        printProvinces(p);
        assertEquals(p.size(), 3);
        assertEquals(p.get(0).getName(), "B");
        assertEquals(p.get(1).getName(), "C");
        assertEquals(p.get(2).getName(), "G");
    }

    @Test
    public void testMoveUnits1() {
        UnitFactory uf = new UnitFactory();
        List<Unit> units = new ArrayList<>();
        createProvinces();
        Faction f = new Faction("Romans");
        addProvinces(f);

        units.add(uf.createUnit("Romans", SoldierType.MISSILE_INFANTRY));
        units.add(uf.createUnit("Romans", SoldierType.MISSILE_INFANTRY));
        Graph.setPath(PATH2);
        MoveUnit mu = new MoveUnit(provinces);

        Province p = mu.move(f, units, provinces.get(0), provinces.get(6));
        assertEquals(p.getName(), provinces.get(6).getName());
    }

    @Test
    public void testMoveUnits2() {
        UnitFactory uf = new UnitFactory();
        List<Unit> units = new ArrayList<>();
        createProvinces();
        Faction f = new Faction("Romans");
        addProvinces(f);

        units.add(uf.createUnit("Romans", SoldierType.MISSILE_INFANTRY));
        units.add(uf.createUnit("Romans", SoldierType.ONAGER));
        Graph.setPath(PATH2);
        MoveUnit mu = new MoveUnit(provinces);

        Province p = mu.move(f, units, provinces.get(0), provinces.get(6));
        assertEquals(p.getName(), provinces.get(5).getName());
    }

    @Test
    public void testMoveUnits3() {
        UnitFactory uf = new UnitFactory();
        List<Unit> units = new ArrayList<>();
        createProvinces();
        Faction f = new Faction("Romans");
        addProvinces(f);

        units.add(uf.createUnit("Romans", SoldierType.MISSILE_INFANTRY));
        units.add(uf.createUnit("Romans", SoldierType.MISSILE_INFANTRY));
        Graph.setPath(PATH2);
        MoveUnit mu = new MoveUnit(provinces);

        Province p = mu.move(f, units, provinces.get(0), provinces.get(1));
        assertEquals(p.getName(), provinces.get(1).getName());
    }
    
    private void printProvinces(List<Province> pro) {
        for (int i = 0; i < pro.size(); i++) {
            System.out.println(pro.get(i).getName());
        }
    }

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
