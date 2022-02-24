package unsw.gloriaromanus.victory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoalGenerator {
    private Random rng = new Random();;
    private List<VictoryCondition> conditionsLeft = new ArrayList<>();
    private List<VictoryCondition> logicalLeft = new ArrayList<>();
    
    public GoalGenerator() {
        initialise();
    }

    public void seedRandom(int seed) {
        rng = new Random(seed);
    }

    public Goal generate() {
        if (rng.nextInt(3) == 0) {
            return generateSingleGoal();
        }
        return generateMultiGoal();
    }

    private void initialise() {
        conditionsLeft.add(new GoalConquest());
        conditionsLeft.add(new GoalTreasury());
        conditionsLeft.add(new GoalWealth());

        logicalLeft.add(new GoalAND());
        logicalLeft.add(new GoalOR());
    }

    private Goal generateSingleGoal() {
        VictoryCondition logic = getLogic();
        VictoryCondition condition1 = getCondition();
        VictoryCondition condition2 = getCondition();
        
        GoalComposite gc = new GoalComposite(logic);
        Goal g1 = new Goal(condition1);
        Goal g2 = new Goal(condition2);

        gc.addSubgoal(g1);
        gc.addSubgoal(g2);
        return gc;
    }

    private Goal generateMultiGoal() {
        VictoryCondition logic1 = getLogic();
        VictoryCondition logic2 = getLogic();
        VictoryCondition condition1 = getCondition();
        VictoryCondition condition2 = getCondition();
        VictoryCondition condition3 = getCondition();
        
        GoalComposite gc1 = new GoalComposite(logic1);
        GoalComposite gc2 = new GoalComposite(logic2);
        Goal g1 = new Goal(condition1);
        Goal g2 = new Goal(condition2);
        Goal g3 = new Goal(condition3);

        gc2.addSubgoal(g2);
        gc2.addSubgoal(g3);

        gc1.addSubgoal(g1);
        gc1.addSubgoal(gc2);
        return gc1;
    }

    private VictoryCondition getCondition() {
        VictoryCondition vc = conditionsLeft.get(rng.nextInt(conditionsLeft.size()));
        conditionsLeft.remove(vc);
        return vc;
    }

    private VictoryCondition getLogic() {
        return logicalLeft.get(rng.nextInt(logicalLeft.size()));
    }
}
