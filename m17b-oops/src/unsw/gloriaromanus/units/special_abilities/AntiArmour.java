package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

public class AntiArmour implements SpecialAbility {
    private static final double ENEMY_ARMOUR_MULTIPLIER = 0.5;

    @Override
    public void applyCreationEffect(Unit unit) {
        //Do nothing because no creation effects
    }

    /**
     * Applies the enemy effect multiplier if the attribute is armour.
     * Returns -1 otherwise
     * @param attribute
     * @param enemyUnit
     * @return
     */
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
        if (attribute == UnitAttribute.ARMOUR) {
            return (int) Math.round(enemyUnit.getArmour() * ENEMY_ARMOUR_MULTIPLIER);
        }
        return -1;
    }

    @Override
    public int applyEffect(UnitAttribute attribute, Unit unit, String battleId, boolean isHeroic) {
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public String toString() {
        return "AntiArmour";
    }
}
