package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

import java.util.Objects;

public class ShieldCharge implements SpecialAbility {
    private String battleId;
    private int numEngagementsInBattle = 1;

    @Override
    public void applyCreationEffect(Unit unit) {
        // No creation effects
    }

    // no effect
    @Override
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
        return -1;
    }

    /**
     * Applies an effect to the melee attack of the unit. Every fourth engagement, the shield defense is
     * added to the melee attack
     * @param attribute unit attribute type
     * @param unit current unit
     * @param battleId unique battle id
     * @param isHeroic whether the battle is heroic
     * @return new attribute value
     */
    public int applyEffect(UnitAttribute attribute, Unit unit, String battleId, boolean isHeroic) {
        if (attribute == UnitAttribute.MELEE_ATTACK) {
            incrementNumEngagementsThisBattle(battleId);
            if (numEngagementsInBattle % 4 == 0) return unit.getMeleeAttack() + unit.getShieldDefense();
        }
        return -1;
    }

    /**
     * Increments the number of engagements this battle
     * @param battleId unique battle id
     */
    private void incrementNumEngagementsThisBattle(String battleId) {
        if (battleId.equals(this.battleId)) {
            numEngagementsInBattle++;
        } else {
            numEngagementsInBattle = 1;
            this.battleId = battleId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShieldCharge that = (ShieldCharge) o;
        return numEngagementsInBattle == that.numEngagementsInBattle &&
                Objects.equals(battleId, that.battleId);
    }

    @Override
    public String toString() {
        return "ShieldCharge";
    }
}
