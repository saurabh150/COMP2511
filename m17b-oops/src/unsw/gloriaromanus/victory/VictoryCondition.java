package unsw.gloriaromanus.victory;

import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;

public interface VictoryCondition {
    /**
     * 
     * @return the string of Type of Condition
     */
    public String getGoal();

    /**
     * 
     * @param g
     * @param f
     * @return if condition met
     */
    public boolean achieved(Goal g, Faction f);

    /**
     * 
     * @param g
     * @return A readable victory condition
     */
    public JSONObject toReadable(Goal g);
}
