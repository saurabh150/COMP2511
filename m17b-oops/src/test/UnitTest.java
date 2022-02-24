package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import unsw.gloriaromanus.units.Unit;
import static unsw.gloriaromanus.units.UnitAttribute.*;

public class UnitTest {

    private String testMissileInfantry = "{\"name\": \"missile_infantry\",\r\n    \"type\": \"INFANTRY\",\r\n    \"training_turns\": 1,\r\n    \"range\": true,\r\n    \"num_troops\": 30,\r\n    \"armour\": 5,\r\n    \"morale\": 15,\r\n    \"speed\": 8,\r\n    \"missile_attack\": 8,\r\n    \"melee_attack\": 4,\r\n    \"defense_skill\": 7,\r\n    \"shield_defense\": 3,\r\n    \"charge\": 0,\r\n    \"cost\": 50,\r\n    \"movement_points\":10\r\n  }";

    @Test
    public void testUnitLoadedJSONCorrectly() {
        JSONObject missileInfantry = new JSONObject(testMissileInfantry);

        Unit unit = new Unit(missileInfantry);
        assertEquals(missileInfantry.getString(NAME.toString()), unit.getUnitType().toString());
        assertEquals(missileInfantry.getString(TYPE.toString()), unit.getUnitCategory().toString());
        assertEquals(missileInfantry.getInt(TRAINING_TURNS.toString()), unit.getTrainingTurns());
        assertEquals(missileInfantry.getBoolean(RANGE.toString()), unit.isRange());
        assertEquals(missileInfantry.getInt(NUM_TROOPS.toString()), unit.getNumSoldiers());
        assertEquals(missileInfantry.getInt(ARMOUR.toString()), unit.getArmour());
        assertEquals(missileInfantry.getInt(MORALE.toString()), unit.getMorale());
        assertEquals(missileInfantry.getInt(SPEED.toString()), unit.getSpeed());
        assertEquals(missileInfantry.getInt(MISSILE_ATTACK.toString()), unit.getMissileAttack());
        assertEquals(missileInfantry.getInt(MELEE_ATTACK.toString()), unit.getMeleeAttack());
        assertEquals(missileInfantry.getInt(DEFENSE_SKILL.toString()), unit.getDefenseSkill());
        assertEquals(missileInfantry.getInt(SHIELD_DEFENSE.toString()), unit.getShieldDefense());
        assertEquals(missileInfantry.getInt(CHARGE.toString()), unit.getCharge());
        assertEquals(missileInfantry.getInt(COST.toString()), unit.getCost());
        assertEquals(missileInfantry.getInt(MOVEMENT_POINTS.toString()), unit.getMovementPoints());
    }

    @Test
    public void reduceSoldiersTest() {
        JSONObject missileInfantry = new JSONObject(testMissileInfantry);
        Unit unit = new Unit(missileInfantry);
        int numSoldiers = unit.getNumSoldiers();
        assertTrue(unit.hasSoldiers());
        unit.reduceSoldiers(1);
        assertEquals(numSoldiers - 1, unit.getNumSoldiers());

        unit.reduceSoldiers(100);
        assertEquals(0, unit.getNumSoldiers());
        assertFalse(unit.hasSoldiers());
    }

    @Test
    public void reduceNumTurns() {
        JSONObject missileInfantry = new JSONObject(testMissileInfantry);
        Unit unit = new Unit(missileInfantry);
        unit.decrementTrainingTurns();
        assertEquals(0, unit.getTrainingTurns());
    }


    @Test
    public void test() throws IOException {
        try {
            String content = Files.readString(Paths.get("src/unsw/gloriaromanus/landlocked_provinces.json"));
            JSONArray landlocked = new JSONArray(content);
            System.out.println(landlocked.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
