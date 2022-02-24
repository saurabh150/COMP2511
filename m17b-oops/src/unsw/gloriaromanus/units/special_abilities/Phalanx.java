package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

public class Phalanx implements SpecialAbility {
    private static final int DEFENSE_SKILL_MULTIPLIER = 2;
    private static final double SPEED_MULTIPLIER = 0.5;

    /**
     * Applies an effect to the unit on creation.
     * @param unit current unit
     */
    @Override
    public void applyCreationEffect(Unit unit) {
        unit.setDefenseSkill(unit.getDefenseSkill() * DEFENSE_SKILL_MULTIPLIER);
        unit.setSpeed((int) Math.round(unit.getSpeed() * SPEED_MULTIPLIER));
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
        return "Phalanx";
    }
}
