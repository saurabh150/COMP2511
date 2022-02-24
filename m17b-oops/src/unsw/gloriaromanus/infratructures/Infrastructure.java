package unsw.gloriaromanus.infratructures;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
public abstract class Infrastructure {

    protected int level;
    private static final String PATH = "src/unsw/gloriaromanus/config/infrastructure_stats.json";

    protected int cost;
    protected int turnsToBuild;
    protected int costToUpgrade;
    protected int townWealthGeneration;
    protected int maxLevel;
    protected int underConstruction;

    /**
     * Initializes all building stats
     * @param type
     */
    public void setBuildingStats(String type) {
        this.level = 1;
        try {
            String content = Files.readString(Paths.get(PATH));
            JSONObject infrastructure = new JSONObject(content);
            JSONObject json = infrastructure.getJSONObject(type);
            this.cost = json.getInt("cost");
            this.costToUpgrade = json.getInt("costToUpgrade");
            this.turnsToBuild = json.getInt("turnsToBuild");
            this.townWealthGeneration = json.getInt("townWealthGeneration");
            this.maxLevel = json.getInt("maxLevel");
            this.underConstruction = turnsToBuild;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * builds the infrastructure
     * @return true if 0 turns lef to build
     */
    public boolean build() {
        underConstruction--;
        return (underConstruction <= 0);
    }

    /**
     *
     * @return cost
     */
    public int getCost() {
        return cost;
    }

    /**
     *
     * @return turnsToBuild
     */
    public int getTurnsToBuild() {
        return turnsToBuild;
    }

    /**
     *
     * @return costToUpgrade
     */
    public int getCostToUpgrade() {
        return costToUpgrade;
    }

    /**
     *
     * @return cost to upgrade
     */
    public int upgrade() {
        if (level < maxLevel) {
            level++;
            townWealthGeneration += 10;
            return costToUpgrade;
        }
        return 0;
    }

    /**
     *
     * @return townWealth generated each turn
     */
    public int update() {
        return townWealthGeneration;
    }

    public int getLevel() {
        return level;
    }
    
    public int getTownWealthGeneration() {
        return townWealthGeneration;
    }

    public boolean isUpgradable() {
        return (level < maxLevel);
    }
}
