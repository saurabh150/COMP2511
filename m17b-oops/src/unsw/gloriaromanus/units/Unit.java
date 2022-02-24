package unsw.gloriaromanus.units;

import org.json.JSONObject;

import unsw.gloriaromanus.units.special_abilities.SpecialAbility;

import java.util.ArrayList;
import java.util.List;
import static unsw.gloriaromanus.units.UnitAttribute.*;

/**
 * Represents a basic unit of soldiers
 * This unit is read in from a json object upon instantiation
 *
 */
public class Unit {
    private SoldierType unitType;
    private UnitCategory unitCategory;
    private int trainingTurns;
    private int remainingTurns;
    private boolean range;  // range of the unit- true for ranged unit, false for melee unit
    private int numSoldiers;  // the number of soldiers in this unit (should reduce based on depletion)
    private int armour;  // armour defense
    private int morale;  // resistance to fleeing
    private int speed;  // ability to disengage from disadvantageous battle
    private int missileAttack; // missile attack damage
    private int meleeAttack; // melee attack damage
    private int defenseSkill;  // skill to defend in battle. Does not protect from arrows!
    private int shieldDefense; // a shield
    private int charge;
    private int cost;
    private int movementPoints;
    private int currentMovementPoints;
    private List<SpecialAbility> specialAbilities;
    private int unitId;
    private int strength;

    public Unit(JSONObject json) {
        this.unitType = SoldierType.valueOf(json.getString(NAME.toString()).toUpperCase());
        this.unitCategory = UnitCategory.valueOf(json.getString(TYPE.toString()));
        this.trainingTurns = json.getInt(TRAINING_TURNS.toString());
        this.remainingTurns = this.trainingTurns;
        this.range = json.getBoolean(RANGE.toString());
        this.numSoldiers = json.getInt(NUM_TROOPS.toString());
        this.armour = json.getInt(ARMOUR.toString());
        this.morale = json.getInt(MORALE.toString());
        this.speed = json.getInt(SPEED.toString());
        this.missileAttack = json.getInt(MISSILE_ATTACK.toString());
        this.meleeAttack = json.getInt(MELEE_ATTACK.toString());
        this.defenseSkill = json.getInt(DEFENSE_SKILL.toString());
        this.shieldDefense = json.getInt(SHIELD_DEFENSE.toString());
        this.charge = json.getInt(CHARGE.toString());
        this.cost = json.getInt(COST.toString());
        this.movementPoints = json.getInt(MOVEMENT_POINTS.toString());
        this.currentMovementPoints = movementPoints;
        this.strength = json.getInt(STRENGTH.toString());
        this.specialAbilities = new ArrayList<>();
    }



    public int getMovementPoints() {
        return movementPoints;
    }

    public int getNumSoldiers(){
        return numSoldiers;
    }

    public boolean hasSoldiers() {return numSoldiers > 0;}

    public int getTrainingTurns() {
        return trainingTurns;
    }

    public void decrementTrainingTurns() {
        trainingTurns--;
    }

    public void reduceSoldiers(int numToReduce) {
        this.numSoldiers -= numToReduce;
        this.numSoldiers = Math.max(numSoldiers, 0);
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public int getMorale() {
        return morale;
    }

    public boolean isRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }

    public int getShieldDefense() {
        return shieldDefense;
    }

    public int getMissileAttack() {
        return missileAttack;
    }

    public int getCharge() {
        return charge;
    }

    public int getMeleeAttack() {
        return meleeAttack + getCharge();
    }

    public int getDefenseSkill() {
        return defenseSkill;
    }

    public int getArmour() {
        return armour;
    }

    public UnitCategory getUnitCategory() {
        return unitCategory;
    }

    public SoldierType getUnitType() {
        return unitType;
    }

    public int getCost() {
        return cost;
    }

    public void setArmour(int armour) {
        this.armour = armour;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setMeleeAttack(int meleeAttack) {
        this.meleeAttack = meleeAttack;
    }

    public void setDefenseSkill(int defenseSkill) {
        this.defenseSkill = defenseSkill;
    }

    public void reduceCurrentMovementPoints(int reduce) {
        this.currentMovementPoints -= reduce;
    }

    public void resetCurrentMovementPoints() {
        this.currentMovementPoints = movementPoints;
    }

    public int getCurrentMovementPoints() {
        return currentMovementPoints;
    }

    public void setShieldDefense(int shieldDefense) {
        this.shieldDefense = shieldDefense;
    }

    public void addSpecialAbility(SpecialAbility specialAbility) {
        this.specialAbilities.add(specialAbility);
    }

    /**
     * Called upon unit creation to apply special abilities creation effects
     */
    public void applyCreationEffects() {
        for (SpecialAbility specialAbility: specialAbilities) {
            specialAbility.applyCreationEffect(this);
        }
    }

    /**
     * Applies the effect on the enemy unit and returns modified attribute.
     * Assumes that effects don't stack- currently none of them do for this function.
     * @param attribute attribute that the special ability modifies
     * @param enemyUnit enemy this special ability applies to
     * @return altered stat for enemy attribute affected
     */
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
        for (SpecialAbility specialAbility: specialAbilities) {
            int effect = specialAbility.applyEnemyEffectMultiplier(attribute, enemyUnit);
            if (effect != -1) return effect;
        }
        return -1;
    }

    /**
     * Applies the effect by calling the special abilities.
     * Assumes that effects can't stack- currently none of them do for this function
     * @param attribute attribute that the spsecial ability modifies
     * @param battleId Id of the battle
     * @param isHeroic whether the battle is heroic or not
     * @return value of the attribute now that the special ability has been applied
     */
    public int applyEffect(UnitAttribute attribute, String battleId, boolean isHeroic) {
        for (SpecialAbility specialAbility: specialAbilities) {
            int effect = specialAbility.applyEffect(attribute, this, battleId, isHeroic);
            if (effect != -1) return effect;
        }
        return -1;
    }

    public List<SpecialAbility> getSpecialAbilities() {
        return specialAbilities;
    }

    /**
     * Decreases remainingTurns by one
     * @return true if remainingTurns is 0
     */
    public boolean train() {
        remainingTurns--;
        return (remainingTurns <= 0);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitType=" + unitType +
                ", unitCategory=" + unitCategory +
                ", trainingTurns=" + trainingTurns +
                ", range=" + range +
                ", numSoldiers=" + numSoldiers +
                ", armour=" + armour +
                ", morale=" + morale +
                ", speed=" + speed +
                ", missileAttack=" + missileAttack +
                ", meleeAttack=" + meleeAttack +
                ", defenseSkill=" + defenseSkill +
                ", shieldDefense=" + shieldDefense +
                ", charge=" + charge +
                ", cost=" + cost +
                ", movementPoints=" + movementPoints +
                ", specialAbilities=" + specialAbilities +
                '}';
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getStrength() {
        return strength;
    }


}
