package unsw.gloriaromanus.units;

public enum SoldierType {
    MISSILE_INFANTRY,
    SPEARMEN,
    PIKEMEN,
    HOPLITE,
    DRUID,
    LEGIONARY,
    BERSERKERS,
    ARCHERS,
    HEAVY_CAVALRY,
    LANCERS,
    CHARIOTS,
    ELEPHANTS,
    HORSE_ARCHERS,
    JAVELIN_SKIRMISHERS,
    BALLISTA,
    ONAGER;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
