package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitFactory;
import unsw.gloriaromanus.units.special_abilities.AntiArmour;
import unsw.gloriaromanus.units.special_abilities.BerserkerRage;
import unsw.gloriaromanus.units.special_abilities.CantabrianCircle;
import unsw.gloriaromanus.units.special_abilities.HeroicCharge;
import unsw.gloriaromanus.units.special_abilities.Phalanx;
import unsw.gloriaromanus.units.special_abilities.ShieldCharge;
import unsw.gloriaromanus.units.special_abilities.SpecialAbility;

import static unsw.gloriaromanus.units.UnitAttribute.*;


public class SpecialAbilityTest {
	private UnitFactory unitFactory = new UnitFactory();
	private final String battleId = "battleId";


	@Test
	public void antiArmourTest() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.JAVELIN_SKIRMISHERS);
		Unit enemyUnit = unitFactory.createUnit("Rome", SoldierType.HOPLITE);
		int enemyArmour = enemyUnit.getArmour();
		int newEnemyArmour = unit.applyEnemyEffectMultiplier(ARMOUR, enemyUnit);
		assertEquals(enemyArmour/2, newEnemyArmour);
		assertEquals(enemyArmour, enemyUnit.getArmour());

		int invalidAttribute = unit.applyEnemyEffectMultiplier(MORALE, enemyUnit);
		assertEquals(-1, invalidAttribute);
		int noEffect = unit.applyEffect(MORALE, battleId, true);
		assertEquals(-1, noEffect);

		assertEquals(new AntiArmour(), new AntiArmour());
		SpecialAbility antiArmour = new AntiArmour();
		assertEquals(antiArmour, antiArmour);
		assertNotNull(antiArmour);
		assertFalse(antiArmour.equals(null));
	}

	@Test
	public void cantrabrianCircleTest() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.HORSE_ARCHERS);
		Unit enemyUnit = unitFactory.createUnit("Rome", SoldierType.HOPLITE);
		int enemyAttack = enemyUnit.getMissileAttack();
		int newEnemyAttack = unit.applyEnemyEffectMultiplier(MISSILE_ATTACK, enemyUnit);
		assertEquals(-1, newEnemyAttack);
		assertEquals(0, enemyAttack);

		enemyUnit = unitFactory.createUnit("Rome", SoldierType.BALLISTA);
		enemyAttack = enemyUnit.getMissileAttack();
		newEnemyAttack = unit.applyEnemyEffectMultiplier(MISSILE_ATTACK, enemyUnit);
		assertEquals(enemyAttack/2, newEnemyAttack);


		int invalidAttribute = unit.applyEnemyEffectMultiplier(MORALE, enemyUnit);
		assertEquals(-1, invalidAttribute);
		int noEffect = unit.applyEffect(MORALE, battleId, true);
		assertEquals(-1, noEffect);

		assertEquals(new CantabrianCircle(), new CantabrianCircle());
		SpecialAbility cantabrian = new CantabrianCircle();
		assertEquals(cantabrian, cantabrian);
		assertNotNull(cantabrian);
		assertFalse(cantabrian.equals(null));
	}

	@Test
	public void heroicChargeTest() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		Unit enemyUnit = unitFactory.createUnit("faction", SoldierType.BALLISTA);

		int enemyEffect = unit.applyEnemyEffectMultiplier(MELEE_ATTACK, enemyUnit);
		assertEquals(-1, enemyEffect);

		int meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, true);
		assertEquals(unit.getMeleeAttack() + unit.getCharge(), meleeAttack);

		int morale = unit.applyEffect(MORALE, battleId, false);
		assertEquals(-1, morale);
		morale = unit.applyEffect(MORALE, battleId, true);
		assertEquals(unit.getMorale() * 1.5, morale);

		int noEffect = unit.applyEffect(MISSILE_ATTACK, battleId, true);
		assertEquals(-1, noEffect);

		assertEquals(new HeroicCharge(), new HeroicCharge());
		SpecialAbility heroicCharge = new HeroicCharge();
		assertEquals(heroicCharge, heroicCharge);
		assertNotNull(heroicCharge);
		assertFalse(heroicCharge.equals(null));
	}

	@Test
	public void shieldChargeTest() {
		Unit unit = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		int meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(unit.getMeleeAttack() + unit.getShieldDefense(), meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, battleId, false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, "newId", false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, "newId", false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, "newId", false);
		assertEquals(-1, meleeAttack);
		meleeAttack = unit.applyEffect(MELEE_ATTACK, "newId", false);
		assertEquals(unit.getMeleeAttack() + unit.getShieldDefense(), meleeAttack);

		int invalidMoraleEffect = unit.applyEffect(MORALE, battleId, false);
		assertEquals(-1, invalidMoraleEffect);

		Unit enemyUnit = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		int enemyEffect = unit.applyEnemyEffectMultiplier(MORALE, enemyUnit);
		assertEquals(-1, enemyEffect);

		SpecialAbility shieldCharge = new ShieldCharge();
		assertEquals(shieldCharge, shieldCharge);
		assertEquals(new ShieldCharge(), new ShieldCharge());
		assertNotNull(shieldCharge);
		assertFalse(shieldCharge.equals(null));
		shieldCharge.applyEffect(MELEE_ATTACK, unit, "battleId", false);
		assertNotEquals(new ShieldCharge(), shieldCharge);
		SpecialAbility testShieldCharge = new ShieldCharge();
		testShieldCharge.applyEffect(MELEE_ATTACK, unit, "newId", false);
		assertNotEquals(testShieldCharge, shieldCharge);
		testShieldCharge.applyEffect(MELEE_ATTACK, unit, "newId", false);
		assertNotEquals(testShieldCharge, shieldCharge);
	}


	@Test
	public void bersekerAbilityTest() {
		Unit unit = unitFactory.createUnit("Germanic", SoldierType.BERSERKERS);
		int noEffect = unit.applyEffect(MISSILE_ATTACK, battleId, true);
		assertEquals(-1, noEffect);

		Unit enemyUnit = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		int enemyEffect = unit.applyEnemyEffectMultiplier(MORALE, enemyUnit);
		assertEquals(-1, enemyEffect);

		assertEquals(new BerserkerRage(), new BerserkerRage());
		SpecialAbility berserkerRage = new BerserkerRage();
		assertEquals(berserkerRage, berserkerRage);
		assertNotNull(berserkerRage);
		assertFalse(berserkerRage.equals(null));
		assertNotEquals(berserkerRage, new ShieldCharge());
	}

	@Test
	public void phalanxAbilityTest() {
		Unit unit = unitFactory.createUnit("Germanic", SoldierType.HOPLITE);
		int noEffect = unit.applyEffect(MISSILE_ATTACK, battleId, true);
		assertEquals(-1, noEffect);

		Unit enemyUnit = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		int enemyEffect = unit.applyEnemyEffectMultiplier(MORALE, enemyUnit);
		assertEquals(-1, enemyEffect);

		assertEquals(new Phalanx(), new Phalanx());
		SpecialAbility phalanx = new Phalanx();
		assertEquals(phalanx, phalanx);
		assertNotNull(phalanx);
		assertFalse(phalanx.equals(null));

	}


}
