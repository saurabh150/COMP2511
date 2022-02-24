package unsw.gloriaromanus.units;

import org.json.JSONObject;

import unsw.gloriaromanus.units.special_abilities.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class UnitFactory {
    private static final String CONFIG_PATH = "src/unsw/gloriaromanus/config/unit_category_stats.json";
    private static UnitFactory instance = new UnitFactory();
    private JSONObject unitConfig;
    private int unitIdCount;

    private UnitFactory() {
        this.unitIdCount = 0;
        try {
            String content = Files.readString(Paths.get(CONFIG_PATH));
            unitConfig = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UnitFactory getInstance() {
		return instance;
	}

    public Unit createUnit(String faction, SoldierType type) {
        String unitType = type.toString();
        JSONObject jsonObject = unitConfig.getJSONObject(unitType);
        jsonObject.put("name", unitType.toUpperCase());
        Unit unit = new Unit(jsonObject);
        addSpecialAbilities(unit, faction);
        unit.applyCreationEffects();
        unit.setUnitId(unitIdCount);
        unitIdCount++;
        return unit;
    }


    private void addSpecialAbilities(Unit unit, String faction) {
        switch (unit.getUnitType()) {
            case BERSERKERS:
                checkBerserkerRage(unit, faction);
                unit.addSpecialAbility(new ShieldCharge());
                break;
            case CHARIOTS:
            case ELEPHANTS:
            case HEAVY_CAVALRY:
            case LANCERS:
                unit.addSpecialAbility(new HeroicCharge());
                break;
            case HORSE_ARCHERS:
                unit.addSpecialAbility(new CantabrianCircle());
                break;
            case JAVELIN_SKIRMISHERS:
                unit.addSpecialAbility(new AntiArmour());
                break;
            case PIKEMEN:
            case HOPLITE:
                unit.addSpecialAbility(new Phalanx());
                unit.addSpecialAbility(new ShieldCharge());
                break;
            case DRUID:
            case LEGIONARY:
                unit.addSpecialAbility(new ShieldCharge());
                break;
            default:
                break;
        }
    }

    private void checkBerserkerRage(Unit unit, String faction) {
        List<String> factions = Arrays.asList("Gallic", "Celtic", "Briton", "Germanic");
        if (factions.contains(faction)) {
            unit.addSpecialAbility(new BerserkerRage());
        }
    }
}
