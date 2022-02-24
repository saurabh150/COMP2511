package unsw.gloriaromanus.backend;

import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BattleResolver implements Subject {
    private static final int PROBABILITY_BOUND = 100;
    private static double ATTACK_ALLY = 0.1;
    private final String ATTACK_UNIT_STR = "Attacking unit: ";
    private final String DEFEND_UNIT_STR = "Defending unit: ";
    private final List<Unit> attackers;
    private final List<Unit> attackerFleeUnits;
    private final List<Unit> defenders;
    private final List<Unit> defenderFleeUnits;
    private final String battleId;
    private final Random rand;
    private int numEngagements;
    private Observer observer = null;

    /**
     * Manages the entire battle between one army and another.
     * @param attackers list of attacker units
     * @param defenders list of defending units
     * @param rand Random number
     */
    public BattleResolver(List<Unit> attackers, List<Unit> defenders, Random rand) {
        this.attackers = attackers;
        this.defenders = defenders;
        this.attackerFleeUnits = new ArrayList<>();
        this.defenderFleeUnits = new ArrayList<>();
        this.battleId = UUID.randomUUID().toString();
        this.rand = rand;
        this.numEngagements = 0;
    }

    /**
     * Invading unit attacks defending unit. Returns true if attack wins.
     * Returns false if attack draws or loses
     *
     * @return whether attackers win
     */
    public boolean attack() {
        // Engagements occur until attackers or defenders has 0 units left AND less than 200 engagements.
        while (!attackers.isEmpty() && !defenders.isEmpty() && numEngagements <= 200) {
            Unit attackingUnit = attackers.get(rand.nextInt(attackers.size()));
            Unit defendingUnit = defenders.get(rand.nextInt(defenders.size()));
            fightEngagement(attackingUnit, defendingUnit);
        }
        // Whether attacker wins or loses, they have to combine to main attacker army
        attackers.addAll(attackerFleeUnits);
        // If attacker wins, they move on to occupy the new province (can't do anything rest of turn)

        // logic- attack wins if defenders have 0 units left
        //      - attack loses or draws if defenders have units left
        if (defenders.isEmpty()) {
            return true;
        }
        defenders.addAll(defenderFleeUnits);
        return false;
    }

    /**
     * This starts an engagement between 2 units.
     *
     * @param attackingUnit - unit attacking province
     * @param defendingUnit - unit defending province
     */
    private void fightEngagement(Unit attackingUnit, Unit defendingUnit) {
        observer.update("Starting an engagement between: " + ATTACK_UNIT_STR + attackingUnit.getUnitType()+ " " + DEFEND_UNIT_STR + defendingUnit.getUnitType());
        this.numEngagements++;

        boolean isRanged = checkIfRangedEngagement(attackingUnit, defendingUnit);

        int attackingCasualties = getEngagementInflictedCasualties(defendingUnit, attackingUnit, isRanged);
        int defendingCasualties = getEngagementInflictedCasualties(attackingUnit, defendingUnit, isRanged);

        boolean attackAlliesKilled = handleCasualties(attackingUnit, defendingUnit, defendingCasualties);
        boolean defenceAlliesKilled = handleCasualties(defendingUnit, attackingUnit, attackingCasualties);

        // Engagement ends when one party is dead
        if (checkIfUnitsAreDead(attackingUnit, defendingUnit)) return;

        // Checks whether allies were killed, then sets the enemy casualties as 0
        if (attackAlliesKilled) defendingCasualties = 0;
        if (defenceAlliesKilled) attackingCasualties = 0;

        // Checks whether the units break depending on whether allies are killed
        handleBreaks(attackingUnit, attackingCasualties, defendingUnit, defendingCasualties);

    }

    /**
     * Handles the possibility that a unit breaks. If both break, they restart another engagement.
     * If one breaks, it tries to route and pursuing unit attacks them.
     * @param attackingUnit unit in attack
     * @param attackingCasualties casualties attack had in last engagement
     * @param defendingUnit  unit in defence
     * @param defendingCasualties casualties defence had in last engagement
     */
    private void handleBreaks(Unit attackingUnit, int attackingCasualties, Unit defendingUnit, int defendingCasualties) {
        boolean attackingUnitBreaks = doesUnitBreak(attackingUnit, attackingCasualties, defendingUnit, defendingCasualties);
        boolean defendingUnitBreaks = doesUnitBreak(defendingUnit, defendingCasualties, attackingUnit, attackingCasualties);

        if (attackingUnitBreaks && defendingUnitBreaks) {
            // Both break successfully
            bothUnitsFlee(attackingUnit, defendingUnit);
        } else if (!attackingUnitBreaks && !defendingUnitBreaks) {
            // Game goes through another engagement
            fightEngagement(attackingUnit, defendingUnit);
        } else if (attackingUnitBreaks) {
            // Case where one unit breaks and other doesn't.
            attemptToRoute(attackingUnit, defendingUnit);
            observer.update(ATTACK_UNIT_STR + attackingUnit.getUnitType().toString() + " has broken! " + defendingUnit.getUnitType().toString() + " is pursuing!");
        } else {
            attemptToRoute(defendingUnit, attackingUnit);
            observer.update(DEFEND_UNIT_STR + defendingUnit.getUnitType().toString() + " has broken! " + attackingUnit.getUnitType().toString() + " is pursuing!");
        }
    }


    /**
     * Handles the casualties depending on whether there are any elephants in the attack or defence.
     * If there is an elephant unit, there is a 0.1 chance it will attack an ally
     * Returns true if killed ally
     * @param attacker the unit that is currently inflicting casualties
     * @param victim unit that casualties are inflicted on
     * @param victimCasualties casualties of victim
     * @return boolean whether allies are killed
     */
    private boolean handleCasualties(Unit attacker, Unit victim, int victimCasualties) {
        Unit randomAlly;
        if (attacker.getUnitType() == SoldierType.ELEPHANTS && resolveProbability(ATTACK_ALLY)) {
            randomAlly = randomAlliedUnit(attacker);
            // THere are no allies
            if (randomAlly == null) {
                victim.reduceSoldiers(victimCasualties);
                return false;
            }
            randomAlly.reduceSoldiers(victimCasualties);
            checkIfUnitIsDead(randomAlly);

            return true;
        } else {
            victim.reduceSoldiers(victimCasualties);
        }
        return false;
    }

    /**
     * Units will continue to route until they flee successfully or are killed.
     *
     * @param routing  routing unit
     * @param pursuing pursuing unit
     */
    private void attemptToRoute(Unit routing, Unit pursuing) {
        String isAttackStr = isUnitAttack(routing) ? ATTACK_UNIT_STR : DEFEND_UNIT_STR;
        if (routeEngagementKill(routing, pursuing)) return;
        while (!routesSuccessfully(routing.getSpeed(), pursuing.getSpeed())) {
            // Pursuing unit inflicts damages on fleeing unit
            observer.update(isAttackStr + routing.getUnitType() + " failed to route!");
            if (routeEngagementKill(routing, pursuing)) return;
        }

        // If the unit has survived above, it has routed successfully
        boolean isAttack = isUnitAttack(routing);
        if (isAttack && routing.hasSoldiers()) attackingUnitFlees(routing);
        if (!isAttack && routing.hasSoldiers()) defendingUnitFlees(routing);
    }

    /**
     * Enacts a routing engagement. Returns true if all routing are killed.
     * Returns false if routing is still alive.
     * @param routing routing unit
     * @param pursuing pursuing unit
     * @return boolean
     */
    private boolean routeEngagementKill(Unit routing, Unit pursuing) {
        this.numEngagements++;
        boolean isRanged = checkIfRangedEngagement(routing, pursuing);
        int routingCasualties = getEngagementInflictedCasualties(pursuing, routing, isRanged);

        handleCasualties(pursuing, routing, routingCasualties);
        return checkIfUnitIsDead(routing);
    }

    /**
     * This checks if the units have troops left. If not, it removes the dead troop from the battle list.
     *
     * @param attackingUnit unit which is attacking
     * @param defendingUnit unit which is defending
     * @return
     */
    private boolean checkIfUnitsAreDead(Unit attackingUnit, Unit defendingUnit) {
        return checkIfUnitIsDead(attackingUnit) || checkIfUnitIsDead(defendingUnit);
    }

    /**
     * This checks if the unit has troops left. If not, it removes the dead troop from the battle list.
     *
     * @param unit given unit
     * @return
     */
    private boolean checkIfUnitIsDead(Unit unit) {
        boolean isDead = false;
        String isAttack = isUnitAttack(unit) ? ATTACK_UNIT_STR : DEFEND_UNIT_STR;
        if (!unit.hasSoldiers()) {
            observer.update(isAttack + unit.getUnitType().toString() + " has died.");
            attackers.remove(unit);
            defenders.remove(unit);
            isDead = true;
        }
        return isDead;
    }

    /**
     * Calculated whether the routing unit routes successfully given their speed and the pursuing speed.
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

    /**
     * Removes attacking unit from currently battling attackers. Adds to fleeing units.
     *
     * @param attackingUnit unit that is in the attacking army
     */
    private void attackingUnitFlees(Unit attackingUnit) {
        observer.update(ATTACK_UNIT_STR + attackingUnit.getUnitType() + " has fleed!");
        attackers.remove(attackingUnit);
        attackerFleeUnits.add(attackingUnit);
    }

    /**
     * Removes defending unit from currently battling defender. Adds to fleeing units.
     *
     * @param defendingUnit unit that is in the defending army
     */
    private void defendingUnitFlees(Unit defendingUnit) {
        observer.update(DEFEND_UNIT_STR + defendingUnit.getUnitType() + " has fleed!");
        defenders.remove(defendingUnit);
        defenderFleeUnits.add(defendingUnit);
    }

    /**
     * Removes both attacking and defending units from battling armies. Adds to fleeing units.
     *
     * @param attackingUnit unit in attacking army
     * @param defendingUnit unit in defending army
     */
    private void bothUnitsFlee(Unit attackingUnit, Unit defendingUnit) {
        attackingUnitFlees(attackingUnit);
        defendingUnitFlees(defendingUnit);
    }

    /**
     * This checks if the battle will be a ranged or melee engagement. It resolves this probability by checking whether
     * units are melee or ranged.
     * If both are melee, engagement is melee.
     * If both are ranged, engagement is ranged.
     * Else, there is base level chance of either depending on the speed of both units.
     *
     * @param unit1 arbitrary unit 1
     * @param unit2 arbitrary unit 2
     * @return
     */
    private boolean checkIfRangedEngagement(Unit unit1, Unit unit2) {
        if (unit1.isRange() && unit2.isRange()) return true;
        if (!unit1.isRange() && !unit2.isRange()) return false;

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

    /**
     * Gets the number of casualties inflicted by the current unit to enemy unit depending on whether the engagement
     * is ranged or melee.
     *
     * @param currentUnit current unit inflicting casualties on enemy
     * @param enemyUnit   unit receiving casualties
     * @param isRanged    whether engagement is ranged
     * @return
     */
    private int getEngagementInflictedCasualties(Unit currentUnit, Unit enemyUnit, boolean isRanged) {
        if (isRanged) return getRangedEngagementInflictedCasualties(currentUnit, enemyUnit);
        return getMeleeEngagementInflictedCasualties(currentUnit, enemyUnit);
    }

    /**
     * Gets the damage the current unit inflicts on an enemy unit in a ranged engagement.
     *
     * @param currentUnit - unit inflicting damage
     * @param enemyUnit   - unit receiving damage
     * @return number of casualties
     */
    private int getRangedEngagementInflictedCasualties(Unit currentUnit, Unit enemyUnit) {
        int casualties = 0;
        if (!currentUnit.isRange()) {
            // Increments the num engagements in unit if they are melee infantry
            currentUnit.applyEffect(UnitAttribute.MELEE_ATTACK, this.battleId, isUnitHeroic(currentUnit));
            return casualties;
        }
        // Checks for special abilities
        // Armour
        int enemyArmour = currentUnit.applyEnemyEffectMultiplier(UnitAttribute.ARMOUR, enemyUnit);
        if (enemyArmour == -1) enemyArmour = enemyUnit.getArmour();

        // Missile Attack
        int currentMissileAttack = enemyUnit.applyEnemyEffectMultiplier(UnitAttribute.MISSILE_ATTACK, currentUnit);
        if (currentMissileAttack == -1) currentMissileAttack = currentUnit.getMissileAttack();
        double random = rand.nextGaussian();
        // Calculation
        casualties = (int) Math.round(((double) enemyUnit.getNumSoldiers() * 0.1) *
        (double) currentMissileAttack / (enemyArmour + enemyUnit.getShieldDefense()) *
        (random + 1));


        casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
        casualties = Math.max(0, casualties);
        return casualties;
    }

    /**
     * Gets the damage the current unit inflicts on an enemy unit in a melee engagement.
     *
     * @param currentUnit - unit inflicting damage
     * @param enemyUnit   - unit receiving damage
     * @return number of casualties
     */
    private int getMeleeEngagementInflictedCasualties(Unit currentUnit, Unit enemyUnit) {
        int casualties = 0;

        // Applies special abilities
        int currentMeleeAttack = currentUnit.applyEffect(UnitAttribute.MELEE_ATTACK, this.battleId, isUnitHeroic(currentUnit));
        if (currentMeleeAttack == -1) currentMeleeAttack = currentUnit.getMeleeAttack();
        double random = rand.nextGaussian();
        // Calculation
        casualties = (int) Math.round(((double) enemyUnit.getNumSoldiers() * 0.1) *
        (double) currentMeleeAttack / (enemyUnit.getArmour() + enemyUnit.getShieldDefense() + enemyUnit.getDefenseSkill()) *
        (random + 1));
        casualties = Math.min(enemyUnit.getNumSoldiers(), casualties);
        casualties = Math.max(0, casualties);
        return casualties;
    }

    /**
     * Checks whether the current unit breaks.
     *
     * @param currentUnit     current unit
     * @param casualties      casualties current unit had
     * @param enemyUnit       enemy unit
     * @param enemyCasualties casualties enemy unit had
     * @return
     */
    private boolean doesUnitBreak(Unit currentUnit, int casualties, Unit enemyUnit, int enemyCasualties) {
        // I've made an assumption that the base level breaking chance can never be below 0%
        // Apply effect
        int currentMorale = currentUnit.applyEffect(UnitAttribute.MORALE, battleId, isUnitHeroic(currentUnit));
        if (currentMorale == -1) currentMorale = currentUnit.getMorale();


        double breakingChance = Math.max(0.0, 1.0 - (currentMorale * 0.1));
        breakingChance += ((double) casualties / (currentUnit.getNumSoldiers() + casualties)) /
                ((double) enemyCasualties / (enemyUnit.getNumSoldiers() + enemyCasualties));
        breakingChance = Math.max(0.05, breakingChance);
        breakingChance = Math.min(1.00, breakingChance);

        return resolveProbability(breakingChance);
    }

    /**
     * Given a probability between 0 and 1. It returns true or false when randomly resolving that probability.
     *
     * @param probability number between 0 and 1
     * @return boolean- the resolved probability
     */
    private boolean resolveProbability(double probability) {
        probability *= PROBABILITY_BOUND;
        int randomNumber = rand.nextInt(PROBABILITY_BOUND);

        return randomNumber < probability;
    }

    /**
     * Checks whether the current unit is heroic.
     * If current unit is in an army with less than half the size of the enemy army
     *
     * @param unit - current unit
     * @return boolean whether unit is heroic
     */
    private boolean isUnitHeroic(Unit unit) {
        if (attackers.contains(unit)) {
            return (2 * attackers.size()) < defenders.size();
        }
        return 2 * defenders.size() < attackers.size();
    }

    /**
     * Checks whether the given unit is in attacking army
     *
     * @param unit current unit
     * @return boolean whether unit is attacking
     */
    private boolean isUnitAttack(Unit unit) {
        return attackers.contains(unit);
    }

    /**
     * Returns a random allied unit.
     * @param unit current unit
     * @return random ally
     */
    private Unit randomAlliedUnit(Unit unit) {
        Unit randomAlly;
        if (isUnitAttack(unit) && attackers.size() > 1) {
            randomAlly = attackers.get(rand.nextInt(attackers.size()));
        } else if (!isUnitAttack(unit) && defenders.size() > 1) {
            randomAlly = defenders.get(rand.nextInt(defenders.size()));
        } else {
            return null;
        }
        if (!randomAlly.equals(unit)) {
            return randomAlly;
        }
        return randomAlliedUnit(unit);
    }

    public static void setAttackAlly(double attackAlly) {
        ATTACK_ALLY = attackAlly;
    }

	@Override
	public void attach(Observer o) {
		this.observer = o;

	}

	@Override
	public void detach(Observer o) {
		this.observer = null;

	}

	@Override
	public void notifyObservers(String message) {
        if (observer != null)
            observer.update(message);
	}
}
