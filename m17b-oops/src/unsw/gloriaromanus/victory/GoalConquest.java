package unsw.gloriaromanus.victory;

import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;

public class GoalConquest implements VictoryCondition {
    
    private static final int GOAL = 53;
    
    @Override
    public String getGoal() {
        return "CONQUEST";
    }

    @Override
    public boolean achieved(Goal g, Faction f) {
        if (f.getConquestSize() == GOAL)
            return true;
        return false;
    }

    @Override
    public JSONObject toReadable(Goal g) {
        JSONObject json = new JSONObject();
        json.put("goal", getGoal());
        return json;
    }
    
}
