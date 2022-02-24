package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

public class CantabrianCircle implements SpecialAbility {
    private static final double ENEMY_MISSILE_ATTACK_MULTIPLIER = 0.5;

    @Override
    public void applyCreationEffect(Unit unit) {
        //No creation effects
    }

    @Override
    public int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit) {
        if (attribute == UnitAttribute.MISSILE_ATTACK && enemyUnit.isRange()) {
            return (int) Math.round(enemyUnit.getMissileAttack() * ENEMY_MISSILE_ATTACK_MULTIPLIER);
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
        return "CantabrianCircle";
    }
}
