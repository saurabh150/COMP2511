package unsw.gloriaromanus.backend;

import unsw.gloriaromanus.ArrayUtil;
import unsw.gloriaromanus.movement.MoveUnit;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Faction {
    private List<Province> provinces;
    private String name;
    private static final String FACTION_UNITS_JSON = "src/unsw/gloriaromanus/config/faction_units.json";
    private static final String UNIT_STATS_JSON = "src/unsw/gloriaromanus/config/unit_category_stats.json";
    private MoveUnit moveUnit;

    private int townWealth;
    private int buildingWealth;
    private int treasury;

    /**
     * Faction for the game. This is essentially modelling the player. Player can construct buildings,
     * train troops and move units.
     * @param name name of the faction
     */
    public Faction(String name) {
        this.name = name;
        provinces = new ArrayList<>();

        townWealth = 0;
        buildingWealth = 0;
        treasury = 0;
    }

    /**
     * initalises moveunit object which is responsible for moving units from one friendly province to another
     * @param moveUnit moveunit object
     */
    public void initMoveUnit(MoveUnit moveUnit) {
        this.moveUnit = moveUnit;
    }

    /**
     * Moves the units from start to dest
     * @param units
     * @param start
     * @param dest
     */
    public Province move(List<Unit> units, Province start, Province dest) {
        Province p = moveUnit.move(this, units, start, dest);
        if (p == null)
            return p;
        if (!p.getName().equals(start.getName())) {
            p.addUnits(units);
        }
        return p;
    }

    /**
     *
     * @param province
     */
    public void addProvince(Province province) {
        provinces.add(province);
    }

    /**
     *
     * @param province
     */
    public void removeProvince(Province province) {
        provinces.remove(province);
    }

    /**
     *
     * @return List of provinces in faction
     */
    public List<Province> getProvinces() {
        List<Province> ps = new ArrayList<>();
        for (Province p: provinces) {
            ps.add(p);
        }

        return ps;
    }

    /**
     *
     * @return number of provinces the faction owns
     */
	public int getConquestSize() {
		return provinces.size();
	}

    /**
     *
     * @return treasury
     */
	public int getTreasuryAmount() {
		return treasury;
	}

    /**
     *
     * @return total wealth
     */
	public int getWealth() {
		return townWealth+buildingWealth;
    }

    /**
     *
     * @param province
     * @return true if the faction owns the province
     */
	public boolean containsProvince(Province province) {
		return provinces.contains(province);
	}

    /**
     * Creates a unit in the given province
     * @param p province
     * @param type soldier type
     */
    public boolean createUnitInProvince(Province p, String type) {
        int cost = p.createUnit(name, type);
        if (cost == 0) return false;
        treasury -= cost;
        return true;
    }

    /**
     * Add town wealth
     * @param amount
     */
    public void addTownWealth(int amount) {
        townWealth += amount;
    }

    /**
     * Add treasury
     * @param amount
     */
	public void addTreasury(int amount) {
        treasury += amount;
	}

    /**
     * Updates the turn for the current province. Increments training, updates treasury and buildings
     */
    public void updateTurn() {
        provinces.forEach(Province::updateTrainees);
        provinces.forEach(Province::update);
        townWealth = 0;
        buildingWealth = 0;
        for (Province p: provinces) {
            addTreasury(p.getTaxRevenue());
            townWealth += p.getTownWealth();
            buildingWealth += p.getBuildingWealth();
        }
    }

    public String getName() {
        return name;
    }


    public void mapProvinceToFaction(Map<String, String> map) {
        for (Province province: provinces) {
            map.put(province.getName(), name);
        }
    }

    public List<String> getAffordableUnits() {

        List<String> units = new ArrayList<>();

        for (String u : getTrainableUnits()) {
            try {
                String content = Files.readString(Paths.get(UNIT_STATS_JSON));
                JSONObject unitsStats = new JSONObject(content);
                JSONObject unitStats = unitsStats.getJSONObject(u);
                if (unitStats.getInt("cost") <= treasury) {
                    units.add(u);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return units;

    }

	public List<String> getTrainableUnits() {
        List<String> units = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(FACTION_UNITS_JSON));
            JSONObject ownership = new JSONObject(content);
            JSONArray unitsJSON = ownership.getJSONArray(name);
            units = ArrayUtil.convert(unitsJSON);
            return units;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return units;
    }

    public boolean gameLost() {
        return provinces.isEmpty();
    }

}
