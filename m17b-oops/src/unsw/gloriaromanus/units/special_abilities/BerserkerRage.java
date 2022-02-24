package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

public class BerserkerRage implements SpecialAbility {
    private static final int MELEE_ATTACK_MULTIPLIER = 2;

    /**
     * Applies creation effect.
     * @param unit current unit
     */
    @Override
    public void applyCreationEffect(Unit unit) {
        unit.setMorale(Integer.MAX_VALUE);
        unit.setShieldDefense(0);
        unit.setArmour(0);
        unit.setMeleeAttack(unit.getMeleeAttack() * MELEE_ATTACK_MULTIPLIER);
    }

    @Override
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
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
        return "BerserkerRage";
    }
}
