package unsw.gloriaromanus.victory;

import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;

public class GoalWealth implements VictoryCondition {
    
    private static final int GOAL = 400000;

    @Override
    public String getGoal() {
        return "WEALTH";
    }

    @Override
    public boolean achieved(Goal g, Faction f) {
        if (f.getWealth() >= GOAL)
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
