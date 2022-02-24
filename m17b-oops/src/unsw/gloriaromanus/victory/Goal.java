package unsw.gloriaromanus.victory;

import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;

public class Goal {
    private VictoryCondition condition;

    /**
     * 
     * @param condition
     */
    public Goal (VictoryCondition condition) {
        this.condition = condition;
    }

    /**
     * 
     * @return boolean, achieved the goal
     */
    public boolean achieved(Faction f) {
        return condition.achieved(this, f);
    }

    /**
     * 
     * @return JSONObject which is the string equivalent of the Goal
     */    
    public JSONObject toReadable() {
        return condition.toReadable(this);
    }
}
