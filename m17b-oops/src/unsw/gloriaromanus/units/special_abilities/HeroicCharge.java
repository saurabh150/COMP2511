package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

public class HeroicCharge implements SpecialAbility {
    private static final double MORALE_MULTIPLIER = 1.5;


    @Override
    public void applyCreationEffect(Unit unit) {
        // No creation effects
    }

    /**
     * No effects so return -1
     */
    @Override
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
        return -1;
    }

    /**
     * Unit will double its charge attack damage,
     * @param attribute unit attribute type
     * @param unit current unit
     * @param battleId unique battle id
     * @return new attribute value
     */
    @Override
    public int applyEffect(UnitAttribute attribute, Unit unit, String battleId, boolean isHeroic) {
        if (attribute == UnitAttribute.MELEE_ATTACK && isHeroic) {
            return unit.getMeleeAttack() + unit.getCharge();
        }
        if (attribute == UnitAttribute.MORALE && isHeroic) {
            return (int) Math.round(MORALE_MULTIPLIER * unit.getMorale());
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public String toString() {
        return "HeroicCharge";
    }
}
