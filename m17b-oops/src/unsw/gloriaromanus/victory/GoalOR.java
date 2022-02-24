package unsw.gloriaromanus.victory;
import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;

public class GoalOR implements VictoryCondition {
    @Override
    public String getGoal() {
        return "OR";
    }

    @Override
    public boolean achieved(Goal g, Faction f) {
        for (Goal subgoal: ((GoalComposite) g).getSubgoals()) {
            if (subgoal.achieved(f))
                return true;
        }
        return false;
    }

    @Override
    public JSONObject toReadable(Goal g) {
        JSONObject json = new JSONObject();
        json.put("goal", getGoal());

        JSONArray jsonA = new JSONArray();
        for (Goal subgoal: ((GoalComposite) g).getSubgoals()) {
            jsonA.put(subgoal.toReadable());
        }
        json.put("subgoals",jsonA);
        
        return json;
    }
}