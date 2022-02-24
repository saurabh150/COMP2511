package unsw.gloriaromanus.units.special_abilities;

import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitAttribute;

/**
 * Special abilities that some of the units have
 */
public interface SpecialAbility {
    /**
     * Applies an effect to the unit upon unit creation
     * @param unit unit to apply effect to
     */
    void applyCreationEffect(Unit unit);

    /**
     * Apply the effect on to the enemy attribute. Returns the modified value
     * of the enemy attribute
     * @param attribute unit attribute
     * @param enemyUnit enemy unit
     * @return modified value attribute
     */
    int applyEnemyEffectMultiplier(UnitAttribute attribute, Unit enemyUnit);

    /**
     * Applies an effect on to the current unit.
     * @param attribute unit attribute
     * @param unit current unit
     * @param battleId unique battle id
     * @param isHeroic whether the battle is heroic
     * @return
     */
    int applyEffect(UnitAttribute attribute, Unit unit, String battleId, boolean isHeroic);

}
