package unsw.gloriaromanus.victory;

import java.util.ArrayList;
import java.util.List;

public class GoalComposite extends Goal {

    private List<Goal> subgoals;
    
    /**
     * 
     * @param condition
     */
    public GoalComposite(VictoryCondition condition) {
        super(condition);
        subgoals = new ArrayList<>();
    }

    /**
     * 
     * @param s is a goal to add in the subgoals list
     */
    public void addSubgoal(Goal s) {
        subgoals.add(s);
    }

    /**
     * 
     * @return subgoals of the Goal
     */
    public List<Goal> getSubgoals() {
        return subgoals;
    }

}
