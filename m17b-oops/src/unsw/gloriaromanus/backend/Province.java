package unsw.gloriaromanus.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitFactory;
import unsw.gloriaromanus.infratructures.Farm;
import unsw.gloriaromanus.infratructures.Infrastructure;
import unsw.gloriaromanus.infratructures.Market;

public class Province {
    // ******************* Province Stats *******************
    private String name;
    private int trainingSlots;

    // *******************     Money      *******************
    private int townWealth;
    private int buildingWealth;
    private int taxRate;
    private int taxRateBonus;

    private List<Unit> units;
    private List<Unit> underTraining;
    private final UnitFactory unitFactory;
    private Infrastructure underConstruction;
    private Farm farm;
    private Market market;

    /**
     * Each region which can be owned by a faction
     * Place that owns all the wealth and buildings
     * Start training troops here
     * @param name
     */
    public Province(String name) {
        this.name = name;
        trainingSlots = 2;

        townWealth = 0;
        buildingWealth = 0;
        taxRate = 10;
        taxRateBonus = 10;

        units = new ArrayList<>();
        underTraining = new ArrayList<>();
        unitFactory = UnitFactory.getInstance();
    }

    public int getTrainingSlots() {
        return trainingSlots;
    }

    public String getName() {
        return name;
    }

    /**
     * Updates the wealth and construction building turns
     */
    public void update() {
        townWealth += taxRateBonus;

        if (farm != null)
            addTownWealth(farm.update());

        if (market != null)
            addTownWealth(market.update());

        if (underConstruction != null) {
            build();
        }

    }

    /**
     * Clears all the current production in the province. Including construction and units in training.
     */
    public void clearProduction() {
        this.underConstruction = null;
        this.underTraining = new ArrayList<>();
    }
// ******************************* Infrastructure ***********************************
    public boolean farmExist() {
        if (farm != null) return true;
        if (underConstruction instanceof Farm) return true;
        return false;
    }

    public boolean marketExist() {
        if (market != null) return true;
        if (underConstruction instanceof Market) return true;
        return false;
    }

    public Farm getFarm() {
        return farm;
    }

    public Market getMarket() {
        return market;
    }

    /**
     *
     * Builds the underConstruction infrastructure
     */
    private void build() {
        if (underConstruction.build()) {
            assignBuilding();
        }
    }

    /**
     *
     * @return true if a market can be built
     */
    public boolean buildMarket() {
        if (market == null && underConstruction == null) {
            underConstruction = new Market();
            return true;
        }
        return false;
    }

    /**
     *
     * @return true if a farm can be built
     */
    public boolean buildFarm() {
        if (farm == null && underConstruction == null) {
            underConstruction = new Farm();
            return true;
        }
        return false;
    }

    /**
     * checks whether there are any under construction buildings and assigns
     * to the actual building in the province
     */
    private void assignBuilding() {
        if (underConstruction instanceof Farm) {
            addFarm((Farm)underConstruction);
            addBuildingWealth(farm.getCost());
        }

        if (underConstruction instanceof Market) {
            addMarket((Market)underConstruction);
            addBuildingWealth(market.getCost());
        }

        underConstruction = null;
    }

	private void addFarm(Farm farm) {
        this.farm = farm;
    }

    private void addMarket(Market marker) {
        this.market = marker;
    }

    public void upgradeFarm() {
        addBuildingWealth(farm.upgrade());
    }

    public void upgradeaMarket() {
        addBuildingWealth(market.upgrade());
    }

    public int getUnderConsTime() {
        return underConstruction.getTurnsToBuild();
    }

    public int getUnderConsCost() {
        return underConstruction.getCost();
    }

    // ************************** Unit ********************************

    public List<Unit> getUnits() {
        List<Unit> temp = new ArrayList<>();
        for (Unit u: units) {
            temp.add(u);
        }
        return temp;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public void removeAllUnits() {
        this.units.clear();
    }

    public void removeUnits(List<Unit> units) {
        this.units.removeAll(units);
    }

    public void addUnits(List<Unit> units) {
        this.units.addAll(units);
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    public void mapProvinceToNumberTroops(Map<String, Integer> map) {
        Integer numTroops = Integer.valueOf(0);
        for (Unit unit: units) {
            numTroops += unit.getNumSoldiers();
        }
        map.put(name, numTroops);
    }

    /**
     * Creates a unit given the faction name and soldier type
     * @param faction faction name
     * @param type soldier type
     */
    public void createUnit(String faction, SoldierType type) {
        Unit u = unitFactory.createUnit(faction, type);
        underTraining.add(u);
	}

    /**
     * Creates a unit given the faction name and soldier type
     * @param faction faction name
     * @param type soldier type
     */
    public int createUnit(String faction, String type) {
        if (underTraining.size() > 1) {
            return 0;
        }
        
        SoldierType soldierType = SoldierType.valueOf(type.toUpperCase());
        Unit u = unitFactory.createUnit(faction, soldierType);
        underTraining.add(0, u);
        return u.getCost();
	}

    public int getLastTraineeTurns() {
        return underTraining.get(0).getTrainingTurns();
    }
    
    /**
     * Updates the trainee turns. If the trainee has 0 turns left
     * to train, this moves them into the list of units this province owns
     */
    public void updateTrainees() {
        // System.out.println("\tUpdating Trainees at "+ name);
        List<Unit> toDel = new ArrayList<>();
        for (Unit u : underTraining) {
            if (u.train()) {
                toDel.add(u);
                units.add(u);
            }
        }
        for (Unit u: toDel) {
            underTraining.remove(u);
        }
    }

    public int getUnitsStrength() {
        int strength = 0;
        for (Unit unit: units) {
            strength += unit.getStrength();
        }
        return strength;
    }

    // ************************ Money ******************************

    /**
     * Gets the tax revenue in the current turn
     * @return revenue
     */
    public int getTaxRevenue() {
        float multi = (float)taxRate / (float)100.00;
        float wealth = (float)(townWealth + buildingWealth);
        float revenue = wealth * multi;
        // System.out.println("\t\t"+revenue + " = " + Math.round(revenue) + " = " + multi + " * " + wealth);
        return Math.round(revenue);
    }

    public int getTaxRate() {
        return taxRate;
    }

    public int getTownWealth() {
        return townWealth;
    }

    public int getBuildingWealth() {
        return buildingWealth;
    }

    public void setLowTaxRate() {
        taxRate = 10;
        taxRateBonus = 10;
    }

    public void setNormalTaxRate() {
        taxRate = 15;
        taxRateBonus = 0;
    }

    public void setHighTaxRate() {
        taxRate = 20;
        taxRateBonus = -10;
    }

    public void setVeryHighTaxRate() {
        taxRate =  25;
        taxRateBonus = -30;
    }

	public void addTownWealth(int amount) {
        townWealth += amount;
    }

	public void addBuildingWealth(int amount) {
        buildingWealth += amount;
	}
	// ************************************************************

	public Unit getUnitByType(String unitType) {
        Unit u = null;
        for (Unit unit: units) {
            if (unit.getUnitType().toString().equals(unitType)) {
                u = unit;
                break;
            }
        }
        if (u != null)
            units.remove(u);
		return u;
	}

	public Unit getUnitByID(int parseInt) {
        Unit unit = null;
        for (Unit u: units) {
            if (u.getUnitId() == parseInt) {
                unit = u;
                break;
            }
        }
        if (unit != null)
            units.remove(unit);
		return unit;
	}
}
