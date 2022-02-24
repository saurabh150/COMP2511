package unsw.gloriaromanus.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.json.JSONArray;

import unsw.gloriaromanus.ArrayUtil;
import unsw.gloriaromanus.movement.MoveUnit;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import unsw.gloriaromanus.units.UnitFactory;
import unsw.gloriaromanus.victory.Goal;
import unsw.gloriaromanus.victory.GoalGenerator;

public class Game implements Observer, Subject {

    private static final String PROVINCES_JSON = "src/unsw/gloriaromanus/config/provinces.json";
    private static final String FACTIONS_JSON = "src/unsw/gloriaromanus/config/factions.json";

    private List<Faction> unusedFactions;
    private List<Faction> factions;
    private List<Province> unusedProvinces;
    private List<Province> provinces;
    private Random rng = new Random();
    private int turn;
    private MoveUnit moveUnit;
    private Goal gamegoal;
    private Observer observer = null;

    /**
     * Manages the entire game for the backend. All initialisation of provinces and
     * faction allocation happens here. Also manages the game goal.
     */
    public Game() {
        this.factions = new ArrayList<>();
        this.unusedFactions = initFactions();
        this.unusedProvinces = initProvinces();
        this.provinces = new ArrayList<>();
        GoalGenerator goalGenerator = new GoalGenerator();
        gamegoal = goalGenerator.generate();
        moveUnit = new MoveUnit(unusedProvinces);
        turn = 0;
    }

    public void setGameGoal(Goal gamegoal) {
        this.gamegoal = gamegoal;
    }

    public static String checkNPlayers(int players) {
        if (players < 2 || players > 15) {
            return "Invalid number. Please choose between 2 and 15 players";
        }
        return "Success";
    }

    public void seedRandom(int seed) {
        rng = new Random(seed);
    }

    public MoveUnit getMoveUnit() {
        return moveUnit;
    }

    /**
     * Initialises the game based on N players
     *
     * @param players number of players
     */
    public void init(int players) {
        moveUnit = new MoveUnit(unusedProvinces);
        for (int i = 0; i < players; i++) {
            factions.add(getUnusedFaction());
        }
        randomlyAssignProvinces();
    }

    /**
     * Initialises the game assuming factions are already chosen
     *
     * @param players number of players
     */
    public void init() {
        moveUnit = new MoveUnit(unusedProvinces);
        randomlyAssignProvinces();
    }

    /**
     *
     * @return all in-play factions
     */
    public List<Faction> getFactions() {
        return factions;
    }

    /**
     * Random assign provinces to factions
     */
    private void randomlyAssignProvinces() {
        int i = 0;
        Province p = getUnusedProvince();
        while (p != null) {
            Faction f = factions.get(i % factions.size());
            f.addProvince(p);
            p = getUnusedProvince();
            i++;
        }
    }

    /**
     * Gets a province from a list of unassigned provinces
     *
     * @return province
     */
    private Province getUnusedProvince() {
        int size = unusedProvinces.size();
        if (size == 0)
            return null;
        int randomNumber = rng.nextInt(size);
        Province p = unusedProvinces.get(randomNumber);
        provinces.add(p);
        unusedProvinces.remove(p);
        return p;
    }

    /**
     * Gets a faction from the list of unused factions
     *
     * @return faction
     */
    private Faction getUnusedFaction() {
        int size = unusedFactions.size();
        if (size == 0)
            return null;
        int randomNumber = rng.nextInt(size);
        Faction f = unusedFactions.get(randomNumber);
        f.initMoveUnit(moveUnit);
        unusedFactions.remove(f);
        return f;
    }

    /**
     * Initialisies a list of factions by reading in from JSON
     *
     * @return
     */
    private List<Faction> initFactions() {
        List<Faction> f = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(FACTIONS_JSON));
            JSONArray namesJSON = new JSONArray(content);
            List<String> names = ArrayUtil.convert(namesJSON);
            for (int i = 0; i < names.size(); i++) {
                f.add(new Faction(names.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // System.out.println(e);
        }
        return f;
    }

    /**
     * Initialises a list of provinces in the game by reading from JSON
     *
     * @return
     */
    private List<Province> initProvinces() {
        List<Province> p = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(PROVINCES_JSON));
            JSONArray namesJSON = new JSONArray(content);
            List<String> names = ArrayUtil.convert(namesJSON);
            for (int i = 0; i < names.size(); i++) {
                p.add(new Province(names.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // System.out.println(e);
        }
        return p;
    }

    /**
     * Increments turn
     */
    private void incrementTurn() {
        this.turn++;
    }

    /**
     * Gets which players turn it is
     *
     * @return faction who's turn it currently is
     */
    public Faction getFactionTurn() {
        Faction f = factions.get(turn % factions.size());
        if (f.gameLost()) {
            factions.remove(f);
            return f;
        }
        f.updateTurn();
        incrementTurn();
        return f;
    }

    /**
     *
     * @return boolean: Checks whether faction has achieved the game goals
     */
    public boolean checkVictory(String factionName) {
        Faction faction = getFactionFromString(factionName);
        return gamegoal.achieved(faction);
    }
    /**
     *
     * @return Faction: Checks whether any faction has achieved the game goals
     */
    public String checkVictory() {
        for (Faction f : factions) {
            if (gamegoal.achieved(f)) {
                System.out.println(f.getName() + " has won the game!");
                return f.getName();
            }
        }
        return "";
    }

    /**
     * Starts a battle between province and enemy province. If the attack is
     * successful, move the units in the given province to the new province.
     *
     * @param province      province owned by the faction
     * @param enemyProvince enemy province this province is attacking
     * @return whether the battle won
     */
    public boolean startBattle(String prov, String enemyProv) {
        boolean attackWon = false;
        Province province = getProvinceFromString(prov);
        Province enemyProvince = getProvinceFromString(enemyProv);

        List<Unit> attackers = province.getUnits();
        BattleResolver battleResolver = new BattleResolver(attackers, enemyProvince.getUnits(), new Random());
        battleResolver.attach(this);
        Faction currFaction = getFaction(province);
        Faction enemyFaction = getFaction(enemyProvince);

        if (battleResolver.attack()) {
            attackWon = true;
            province.removeAllUnits();
            enemyProvince.setUnits(attackers);
            enemyProvince.clearProduction();
            enemyFaction.removeProvince(enemyProvince);
            currFaction.addProvince(enemyProvince);
        }
        return attackWon;
    }

    /**
     * Returns the faction that the province belongs to
     *
     * @param province province search
     * @return faction which province belongs to. Null if not found
     */
    private Faction getFaction(Province province) {
        for (Faction faction : this.factions) {
            if (faction.containsProvince(province))
                return faction;
        }
        return null;
    }

    // ***********************Front End Helper********************************

    public List<String> getUnusedFactionStrings() {
        List<String> names = new ArrayList<>();
        for (Faction uf : unusedFactions) {
            names.add(uf.getName());
        }
        return names;
    }

    public void selectedFaction(String factionName) {

        Faction faction = null;

        for (Faction uf : unusedFactions) {
            if (uf.getName().equals(factionName)) {
                faction = uf;
                break;
            }
        }

        if (faction != null)
            faction.initMoveUnit(moveUnit);
        factions.add(faction);
        unusedFactions.remove(faction);
    }

    public void mapProvinceToFaction(Map<String, String> map) {
        for (Faction faction : factions) {
            faction.mapProvinceToFaction(map);
        }
    }

    public void mapProvinceToNumberTroops(Map<String, Integer> map) {
        for (Province province : provinces) {
            province.mapProvinceToNumberTroops(map);
        }
    }

    public List<String> getProvinces() {
        List<String> provinceStrings = new ArrayList<>();
        for (Province p : provinces) {
            provinceStrings.add(p.getName());
        }
        return provinceStrings;
    }

    public List<String> getFactionStrings() {
        List<String> factionStrings = new ArrayList<>();
        for (Faction f : factions) {
            factionStrings.add(f.getName());
        }
        return factionStrings;
    }

    public int getYear() {
        return this.turn / factions.size();
    }

    public void hardCodeUnitValues() {
        List<SoldierType> soldierTypes = Arrays.asList(SoldierType.values());
        // System.out.println(soldierTypes);
        UnitFactory unitFactory = UnitFactory.getInstance();

        for (Province prov : provinces) {
            Unit unit = unitFactory.createUnit(getFaction(prov).getName(), soldierTypes.get(rng.nextInt(soldierTypes.size())));
            List<Unit> testUnits = new ArrayList<>();
            testUnits.add(unit);
            prov.setUnits(testUnits);
        }
    }

    public Province getProvinceFromString(String province) {
        for (Province prov : provinces) {
            if (prov.getName().equals(province)) return prov;
        }
        return null;
    }

    private Faction getFactionFromString(String faction) {
        for (Faction fac : factions) {
            if (fac.getName().equals(faction)) return fac;
        }
        return null;
    }

    /**
     * Gets province strength given province string
     * @param province
     * @return
     */
    public int getProvinceStrength(String province) {
        for (Province prov: provinces) {
            if (prov.getName().equals(province)) return prov.getUnitsStrength();
        }
        return 0;
    }

    public Goal getGamegoal() {
        return gamegoal;
    }

	@Override
	public void attach(Observer o) {
		this.observer = o;

	}

	@Override
	public void detach(Observer o) {
		this.observer = null;

	}

	@Override
	public void notifyObservers(String message) {
        if (observer != null)
            observer.update(message);

	}

	@Override
	public void update(String message) {
		notifyObservers(message);
	}

}
