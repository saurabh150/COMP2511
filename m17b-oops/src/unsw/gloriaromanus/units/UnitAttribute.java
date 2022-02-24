package unsw.gloriaromanus.units;

public enum UnitAttribute {
    NAME("name"),
    TYPE("type"),
    TRAINING_TURNS("training_turns"),
    RANGE("range"),
    NUM_TROOPS("num_troops"),
    ARMOUR("armour"),
    MORALE("morale"),
    SPEED("speed"),
    MISSILE_ATTACK("missile_attack"),
    MELEE_ATTACK("melee_attack"),
    DEFENSE_SKILL("defense_skill"),
    SHIELD_DEFENSE("shield_defense"),
    CHARGE("charge"),
    COST("cost"),
    STRENGTH("strength"),
    MOVEMENT_POINTS("movement_points");

    private String attribute;

    private UnitAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return attribute.toLowerCase();
    }
}
