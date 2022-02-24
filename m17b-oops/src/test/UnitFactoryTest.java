package test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.Test;
import org.json.JSONObject;

import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitFactory;
import unsw.gloriaromanus.units.special_abilities.*;

import static unsw.gloriaromanus.units.UnitAttribute.*;

import java.util.Arrays;
import java.util.List;


public class UnitFactoryTest {
	private UnitFactory unitFactory = new UnitFactory();

    private String testMissileInfantry = "{\"name\": \"missile_infantry\",\r\n    \"type\": \"INFANTRY\",\r\n    \"training_turns\": 1,\r\n    \"range\": true,\r\n    \"num_troops\": 30,\r\n    \"armour\": 5,\r\n    \"morale\": 15,\r\n    \"speed\": 8,\r\n    \"missile_attack\": 8,\r\n    \"melee_attack\": 4,\r\n    \"defense_skill\": 7,\r\n    \"shield_defense\": 3,\r\n    \"charge\": 0,\r\n    \"cost\": 50,\r\n    \"movement_points\":10\r\n  }";

	@Test
	public void testLoadFromConfig() {
        JSONObject missileInfantry = new JSONObject(testMissileInfantry);
		Unit unit = unitFactory.createUnit("Rome", SoldierType.MISSILE_INFANTRY);

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
	public void testBerserkerAbilityLoaded() {
		Unit berserkers = unitFactory.createUnit("Gallic", SoldierType.BERSERKERS);
		List<SpecialAbility> specialAbilities = Arrays.asList(new BerserkerRage(), new ShieldCharge());
		assertEquals(specialAbilities, berserkers.getSpecialAbilities());

		berserkers = unitFactory.createUnit("Celtic", SoldierType.BERSERKERS);
		assertEquals(specialAbilities, berserkers.getSpecialAbilities());

		berserkers = unitFactory.createUnit("Briton", SoldierType.BERSERKERS);
		assertEquals(specialAbilities, berserkers.getSpecialAbilities());

		berserkers = unitFactory.createUnit("Germanic", SoldierType.BERSERKERS);
		assertEquals(specialAbilities, berserkers.getSpecialAbilities());

		berserkers = unitFactory.createUnit("Hungarian", SoldierType.BERSERKERS);
		specialAbilities = Arrays.asList(new ShieldCharge());
		assertEquals(specialAbilities, berserkers.getSpecialAbilities());
	}

	@Test
	public void testBerserkerRageCreation() {
		Unit berserkers = unitFactory.createUnit("Gallic", SoldierType.BERSERKERS);
		Unit berserkersNoAbility = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		assertEquals(Integer.MAX_VALUE, berserkers.getMorale());
		assertEquals(0, berserkers.getArmour());
		assertEquals(0, berserkers.getShieldDefense());
		assertEquals(berserkersNoAbility.getMeleeAttack() * 2, berserkers.getMeleeAttack());
	}

	@Test
	public void testHeroicChargeAbilityLoaded() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		List<SpecialAbility> specialAbilities = Arrays.asList(new HeroicCharge());
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		assertEquals(specialAbilities, unit.getSpecialAbilities());
		unit = unitFactory.createUnit("Rome", SoldierType.HEAVY_CAVALRY);
		assertEquals(specialAbilities, unit.getSpecialAbilities());
		unit = unitFactory.createUnit("Rome", SoldierType.LANCERS);
		assertEquals(specialAbilities, unit.getSpecialAbilities());
		unit = unitFactory.createUnit("Germanic", SoldierType.PIKEMEN);
		assertNotEquals(specialAbilities, unit.getSpecialAbilities());
	}

	@Test
	public void testHorseArcherAbilityLoaded() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.HORSE_ARCHERS);
		List<SpecialAbility> specialAbilities = Arrays.asList(new CantabrianCircle());
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Germanic", SoldierType.PIKEMEN);
		assertNotEquals(specialAbilities, unit.getSpecialAbilities());
	}

	@Test
	public void testAntiArmourAbilityLoaded() {
		UnitFactory unitFactory = new UnitFactory();
		Unit unit = unitFactory.createUnit("Rome", SoldierType.JAVELIN_SKIRMISHERS);
		List<SpecialAbility> specialAbilities = Arrays.asList(new AntiArmour());
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Germanic", SoldierType.PIKEMEN);
		assertNotEquals(specialAbilities, unit.getSpecialAbilities());
	}

	@Test
	public void testPhalanxAbilityLoaded() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.PIKEMEN);
		List<SpecialAbility> specialAbilities = Arrays.asList(new Phalanx(), new ShieldCharge());
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Rome", SoldierType.HOPLITE);
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Germanic", SoldierType.DRUID);
		assertNotEquals(specialAbilities, unit.getSpecialAbilities());
	}

	@Test
	public void testShieldChargeAbilityLoaded() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.DRUID);
		List<SpecialAbility> specialAbilities = Arrays.asList(new ShieldCharge());
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		assertEquals(specialAbilities, unit.getSpecialAbilities());

		unit = unitFactory.createUnit("Germanic", SoldierType.HEAVY_CAVALRY);
		assertNotEquals(specialAbilities, unit.getSpecialAbilities());
	}

}
