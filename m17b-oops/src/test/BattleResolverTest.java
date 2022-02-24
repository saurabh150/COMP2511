package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import unsw.gloriaromanus.backend.BattleResolver;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;
import unsw.gloriaromanus.units.UnitFactory;

public class BattleResolverTest {
	private final String battleId = "battleId";
	private final String faction1 = "Germanic";
	private static final int PROBABILITY_BOUND = 100;
	private static final double ATTACK_ALLY = 0.1;

	private Random random;
	private UnitFactory unitFactory = new UnitFactory();

	@Test
	public void emptyDefence() {
		int randomSeed = 200;
		Unit attack = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
	}

	@Test
	public void singleUnitArmiesRanged() {
		int randomSeed = 100;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.ARCHERS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.MISSILE_INFANTRY);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.ARCHERS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.MISSILE_INFANTRY);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);
		// Start engagement
		int attackCasualties = simpleRangeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

		// In this scenario, attack breaks, defence doesn't
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		// 1) Defence breaks, casualties are:
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 1) Defencee routes and fails
		boolean routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 2) Defence casualties are:
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 2) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 3) Defence casualties are:
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 3) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 4) Defence casualties are:
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 4) Defencee routes and succeeds
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	// Both units are melee. Neither have special abitlies (assuming druid doesn't
	// have special abilities atm)
	@Test
	public void singleUnitArmiesMelee() {
		int randomSeed = 100;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.DRUID);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.BERSERKERS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.DRUID);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);

		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());
		// In this scenario, defence breaks, attack doesn't
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));
		// 1) Defence breaks, casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 1) Defencee routes and fails
		boolean routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 2) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 2) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 3) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 3) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 4) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 4) Defencee routes and succeeds
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void singleUnitArmiesShieldCharge() {
		int randomSeed = 100;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.DRUID);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.DRUID);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);
		// Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

		// In this scenario, defence breaks, attack doesn't
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		// 1) Defence breaks, casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 1) Defencee routes and fails
		boolean routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 2) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 2) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 3) Defence casualties are:
		defenceCasualties = shieldChargeMeleeCasualties(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 3) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 4) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 4) Defencee routes and succeeds
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void elephantTestReversed() {
		int randomSeed = 598800000;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defenceAlly = unitFactory.createUnit(faction1, SoldierType.ELEPHANTS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.ELEPHANTS);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defenceAllyTest = unitFactory.createUnit(faction1, SoldierType.ELEPHANTS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.ELEPHANTS);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defenceAlly);
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(2);
		checkIfRangedEngagement(attackTest, defenceTest);
		// 1st Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		// Simulate random num seq. handle cas
		resolveProbability(ATTACK_ALLY);
		random.nextInt(defenders.size());
		// Enact casualties
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		defenceAllyTest.reduceSoldiers(attackCasualties);
		System.out.println("Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: "
				+ defenceTest.getNumSoldiers() + " ally: " + defenceAllyTest.getNumSoldiers());

		// In this scenario, defence break
		System.out.println("Attack breaks " + doesUnitBreak(attackTest, 0, defenceTest, defenceCasualties));
		System.out.println("Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, 0));

		// 1) Defence breaks, casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 1) Defencee routes and fails
		boolean routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 2) Defence casualties are:
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 2) Defencee routes and fails
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		BattleResolver.setAttackAlly(1);
		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		System.out.println(
				" =========== test attack: " + attackTest.getNumSoldiers() + " def: " + defenceTest.getNumSoldiers());

		assertEquals(1, attackers.size());
		assertEquals(0, defenders.size());
	}

	@Test
	public void elephantTest() {
		int randomSeed = 800;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit attackAlly = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.DRUID);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit attackAllyTest = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.DRUID);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		attackers.add(attackAlly);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(2);
		random.nextInt(1);
		checkIfRangedEngagement(attackTest, defenceTest);
		// 1st Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		// Simulate random num seq. handle cas
		resolveProbability(ATTACK_ALLY);
		random.nextInt(attackers.size());
		// Enact casualties
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		attackAllyTest.reduceSoldiers(defenceCasualties);
		System.out.println("Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: "
				+ defenceTest.getNumSoldiers() + " ally: " + attackAllyTest.getNumSoldiers());

		// In this scenario, both break
		System.out.println("Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, 0));
		System.out.println("Defence breaks " + doesUnitBreak(defenceTest, 0, attackTest, attackCasualties));

		BattleResolver.setAttackAlly(1);
		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		System.out.println(
				" =========== test attack: " + attackTest.getNumSoldiers() + " def: " + defenceTest.getNumSoldiers());
		assertEquals(attackTest.getNumSoldiers(), attackAlly.getNumSoldiers());
		assertEquals(attackAllyTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(2, attackers.size());
		assertEquals(0, defenders.size());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void elephantNoAllyTest() {
		int randomSeed = 800;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.DRUID);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.DRUID);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(2);
		random.nextInt(1);
		checkIfRangedEngagement(attackTest, defenceTest);
		// 1st Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		// Simulate random num seq. handle cas
		resolveProbability(ATTACK_ALLY);
		random.nextInt(attackers.size());
		// Enact casualties
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

		// In this scenario, both break
		System.out.println("Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, 0));
		System.out.println("Defence breaks " + doesUnitBreak(defenceTest, 0, attackTest, attackCasualties));

		BattleResolver.setAttackAlly(1);
		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(1, attackers.size());
		assertEquals(0, defenders.size());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void testDeath() {
		int randomSeed = 8090;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Gallic", SoldierType.BERSERKERS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.BERSERKERS);
		// Testing starting with less soldiers. Since they are berserkers, they never
		// break
		attack.reduceSoldiers(29);
		defence.reduceSoldiers(29);
		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(1, attack.getNumSoldiers());
		assertEquals(0, defence.getNumSoldiers());
	}

	@Test
	public void meleeRangeUnits() {
		int randomSeed = 70;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.BALLISTA);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.LEGIONARY);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.BALLISTA);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);
		// Start engagement
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// In this scenario, neither break
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		// 2nd engagement
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// In this scenario, both break
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void meleeRangeUnitsReversed() {
		int randomSeed = 70;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.HORSE_ARCHERS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.BERSERKERS);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.HORSE_ARCHERS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.BERSERKERS);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);
		// Start engagement- ranged so no attack casualties
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		int attackCasualties = 0;
		int defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// In this scenario, neither break
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		// 2nd engagement
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// In this scenario, defence breaks
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));

		// 1) Defence casualties are:
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 1) Defencee routes and fails
		boolean routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		// 2) Defence casualties are:
		System.out.println(checkIfRangedEngagement(attackTest, defenceTest));
		defenceCasualties = simpleRangeDamageCalculator(attackTest, defenceTest);
		System.out.println("Damage Break:: " + defenceCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);

		// 2) Defencee routes and succeeds
		routesSuccessfully = routesSuccessfully(defenceTest.getSpeed(), attackTest.getSpeed());
		System.out.println("Defence routes " + routesSuccessfully);

		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
		assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
		assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	}

	@Test
	public void greaterThan200Engagements() {
		int randomSeed = 70;
		random = new Random(randomSeed);
		List<Unit> attackers = createListUnits("Rome");
		List<Unit> defenders = createListUnits(faction1);
		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertFalse(battleResolver.attack());
	}

	private List<Unit> createListUnits(String faction) {
		List<Unit> units = new ArrayList<>();
		int i = 0;
		while (i < 100) {
			units.add(unitFactory.createUnit(faction, SoldierType.CHARIOTS));
			i++;
		}
		return units;
	}

	@Test
	public void testHeroicReverse() {
		int randomSeed = 10032;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.CHARIOTS);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.CHARIOTS);

		List<Unit> attackers = createListUnits("Rome");
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		// Imitates random number sequence in attack function
		// Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = heroicDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

		// In this scenario, both break
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));


		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
	}

	@Test
	public void testElephantsUnlikely() {
		int randomSeed = 100;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);
		Unit defence = unitFactory.createUnit("Rome", SoldierType.ELEPHANTS);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = new ArrayList<>();
		defenders.add(defence);
		BattleResolver.setAttackAlly(0);
		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertTrue(battleResolver.attack());
	}

	@Test
	public void testHeroic() {
		int randomSeed = 10032;
		random = new Random(randomSeed);
		Unit attack = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		Unit defence = unitFactory.createUnit(faction1, SoldierType.CHARIOTS);
		Unit attackTest = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
		Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.CHARIOTS);

		List<Unit> attackers = new ArrayList<>();
		attackers.add(attack);
		List<Unit> defenders = createListUnits(faction1);
		defenders.add(defence);
		// Imitates random number sequence in attack function
		random.nextInt(1);
		random.nextInt(1);
		// Start engagement
		int attackCasualties = simpleMeleeDamageCalculator(defenceTest, attackTest);
		int defenceCasualties = heroicDamageCalculator(attackTest, defenceTest);
		System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
		attackTest.reduceSoldiers(attackCasualties);
		defenceTest.reduceSoldiers(defenceCasualties);
		System.out.println(
				"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

		// In this scenario, both break
		System.out.println(
				"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
		System.out.println(
				"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));


		BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
		assertFalse(battleResolver.attack());
	}

	// @Test
	// public void testHeroicReversed() {
	// 	int randomSeed = 10032;
	// 	random = new Random(randomSeed);
	// 	Unit attack = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
	// 	Unit defence = unitFactory.createUnit(faction1, SoldierType.HEAVY_CAVALRY);
	// 	Unit attackTest = unitFactory.createUnit("Rome", SoldierType.CHARIOTS);
	// 	Unit defenceTest = unitFactory.createUnit(faction1, SoldierType.HEAVY_CAVALRY);
	// 	//Simulate heroic charge with few soldiers
	// 	defence.reduceSoldiers(20);
	// 	defenceTest.reduceSoldiers(20);

	// 	List<Unit> attackers = new ArrayList<>();
	// 	attackers.add(attack);
	// 	List<Unit> defenders = new ArrayList<>();
	// 	defenders.add(defence);
	// 	// Imitates random number sequence in attack function
	// 	random.nextInt(1);
	// 	random.nextInt(1);
	// 	// Start engagement
	// 	int attackCasualties = heroicDamageCalculator(defenceTest, attackTest);
	// 	int defenceCasualties = simpleMeleeDamageCalculator(attackTest, defenceTest);
	// 	System.out.println("ATTACK CAS: " + attackCasualties + " DEFENCE CAS: " + defenceCasualties);
	// 	attackTest.reduceSoldiers(attackCasualties);
	// 	defenceTest.reduceSoldiers(defenceCasualties);
	// 	System.out.println(
	// 			"Attack ppl: " + attackTest.getNumSoldiers() + " Defence ppl: " + defenceTest.getNumSoldiers());

	// 	// In this scenario, both break
	// 	System.out.println(
	// 			"Attack breaks " + doesUnitBreak(attackTest, attackCasualties, defenceTest, defenceCasualties));
	// 	System.out.println(
	// 			"Defence breaks " + doesUnitBreak(defenceTest, defenceCasualties, attackTest, attackCasualties));


	// 	BattleResolver battleResolver = new BattleResolver(attackers, defenders, new Random(randomSeed));
	// 	assertTrue(battleResolver.attack());
	// 	assertEquals(attackTest.getNumSoldiers(), attack.getNumSoldiers());
	// 	assertEquals(defenceTest.getNumSoldiers(), defence.getNumSoldiers());
	// }

	private int simpleRangeDamageCalculator(Unit currentUnit, Unit enemyUnit) {
		double rand = this.random.nextGaussian();
		int casualties = (int) Math
				.round(((double) enemyUnit.getNumSoldiers() * 0.1) * (double) currentUnit.getMissileAttack()
						/ (enemyUnit.getArmour() + enemyUnit.getShieldDefense()) * (rand + 1));
		// System.out.println(currentUnit.toString());
		// System.out.println(enemyUnit.toString());
		System.out.println(currentUnit);
		System.out.println(enemyUnit);
		System.out.println("-----------------TEST GAUS: " + rand + " " + casualties);
		casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
		casualties = Math.max(0, casualties);
		return casualties;
	}

    private int heroicDamageCalculator(Unit currentUnit, Unit enemyUnit) {
		double rand = random.nextGaussian();
		int currentMeleeAttack = currentUnit.applyEffect(UnitAttribute.MELEE_ATTACK, "battleId", true);
        // Calculation
        int casualties = (int) Math.round(((double) enemyUnit.getNumSoldiers() * 0.1) *
                (double) currentMeleeAttack/(enemyUnit.getArmour() + enemyUnit.getShieldDefense() + enemyUnit.getDefenseSkill()) *
				(rand + 1));
		System.out.println("=========Current meele att: " + currentMeleeAttack);

		System.out.println("-----------------TEST GAUS: " + rand + " " + casualties);

        casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
        casualties = Math.max(0, casualties);
        return casualties;
    }

	private int simpleMeleeDamageCalculator(Unit currentUnit, Unit enemyUnit) {
		// Calculation
		double rand = this.random.nextGaussian();
		int casualties = (int) Math.round(((double) enemyUnit.getNumSoldiers() * 0.1)
				* (double) currentUnit.getMeleeAttack()
				/ (enemyUnit.getArmour() + enemyUnit.getShieldDefense() + enemyUnit.getDefenseSkill()) * (rand + 1));
		// System.out.println(currentUnit.toString());
		// System.out.println(enemyUnit.toString());
		System.out.println("-----------------TEST GAUS: " + rand + " " + casualties);

		casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
		casualties = Math.max(0, casualties);
		return casualties;
	}

	/**
	 * Checks whether the current unit breaks. non heroic
	 *
	 * @param currentUnit     current unit
	 * @param casualties      casualties current unit had
	 * @param enemyUnit       enemy unit
	 * @param enemyCasualties casualties enemy unit had
	 * @return
	 */
	private boolean doesUnitBreak(Unit currentUnit, int casualties, Unit enemyUnit, int enemyCasualties) {
		// I've made an assumption that the base level breaking chance can never be
		// below 0%
		// Apply effect
		int currentMorale = currentUnit.applyEffect(UnitAttribute.MORALE, battleId, false);
		if (currentMorale == -1)
			currentMorale = currentUnit.getMorale();

		double breakingChance = Math.max(0.0, 1.0 - (currentMorale * 0.1));
		breakingChance += ((double) casualties / (currentUnit.getNumSoldiers() + casualties))
				/ ((double) enemyCasualties / (enemyUnit.getNumSoldiers() + enemyCasualties));
		breakingChance = Math.max(0.05, breakingChance);
		breakingChance = Math.min(1.00, breakingChance);
		// System.out.println("------------------Test breaking chance: " +
		// breakingChance);
		return resolveProbability(breakingChance);
	}

	/**
	 * Given a probability between 0 and 1. It returns true or false when randomly
	 * resolving that probability.
	 *
	 * @param probability number between 0 and 1
	 * @return boolean- the resolved probability
	 */
	private boolean resolveProbability(double probability) {
		probability *= PROBABILITY_BOUND;
		int randomNumber = random.nextInt(PROBABILITY_BOUND);
		// System.out.println(" RANDOM NO. " + randomNumber);
		return randomNumber < probability;
	}

	// /**
	// * Checks whether the current unit is heroic.
	// * If current unit is in an army with less than half the size of the enemy
	// army
	// *
	// * @param unit - current unit
	// * @return boolean whether unit is heroic
	// */
	// private boolean isUnitHeroic(Unit unit) {
	// if (attackers.contains(unit)) {
	// return 2 * attackers.size() < defenders.size();
	// }
	// return 2 * defenders.size() < attackers.size();
	// }
	/**
	 * Calculated whether the routing unit routes successfully given their speed and
	 * the pursuing speed.
	 *
	 * @param routeSpeed  - speed of the routing unit
	 * @param pursueSpeed - speed of the pursuing unit
	 * @return
	 */
	private boolean routesSuccessfully(int routeSpeed, int pursueSpeed) {
		double routingChance = 0.5 + 0.1 * (routeSpeed - pursueSpeed);
		routingChance = Math.max(0.1, routingChance);
		routingChance = Math.min(1.0, routingChance);
		return resolveProbability(routingChance);
	}


	private int shieldChargeMeleeCasualties(Unit currentUnit, Unit enemyUnit) {
		int casualties = 0;

		// Applies special abilities
		int currentMeleeAttack = currentUnit.getMeleeAttack() + currentUnit.getShieldDefense();
		double rand = random.nextGaussian();
		// Calculation
		casualties = (int) Math.round(((double) enemyUnit.getNumSoldiers() * 0.1) * (double) currentMeleeAttack
				/ (enemyUnit.getArmour() + enemyUnit.getShieldDefense() + enemyUnit.getDefenseSkill()) * (rand + 1));
		System.out.println("-----------------TEST GAUS: " + rand + " " + casualties);
		casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
		casualties = Math.max(0, casualties);
		return casualties;
	}

	// /**
	// * Returns a random allied unit.
	// * @param unit current unit
	// * @return random ally
	// */
	// private Unit randomAlliedUnit(Unit unit) {
	// Unit randomAlly;
	// if (isUnitAttack(unit)) {
	// randomAlly = attackers.get(random.nextInt(attackers.size()));
	// } else {
	// randomAlly = defenders.get(random.nextInt(defenders.size()));
	// }
	// if (!randomAlly.equals(unit)) {
	// return randomAlly;
	// }
	// return randomAlliedUnit(unit);
	// }

	/**
	 * This checks if the battle will be a ranged or melee engagement. It resolves
	 * this probability by checking whether units are melee or ranged. If both are
	 * melee, engagement is melee. If both are ranged, engagement is ranged. Else,
	 * there is base level chance of either depending on the speed of both units.
	 *
	 * @param unit1 arbitrary unit 1
	 * @param unit2 arbitrary unit 2
	 * @return
	 */
	private boolean checkIfRangedEngagement(Unit unit1, Unit unit2) {
		if (unit1.isRange() && unit2.isRange())
			return true;
		if (!unit1.isRange() && !unit2.isRange())
			return false;

		Unit ranged;
		Unit melee;
		if (unit1.isRange()) {
			ranged = unit1;
			melee = unit2;
		} else {
			ranged = unit2;
			melee = unit1;
		}

		double meleeEngagementChance = 0.5;
		meleeEngagementChance += 0.1 * (melee.getSpeed() - ranged.getSpeed());

		// There's a maximum 95% chance of either a melee or ranged engagement
		meleeEngagementChance = Math.min(0.95, meleeEngagementChance);
		meleeEngagementChance = Math.max(0.05, meleeEngagementChance);

		return resolveProbability(1 - meleeEngagementChance);
	}

}
