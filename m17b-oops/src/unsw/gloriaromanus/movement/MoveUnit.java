package unsw.gloriaromanus.movement;

import java.util.List;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.units.Unit;

public class MoveUnit {
    private Graph graph;

    /**
     * Initializes the map and gives access to move units
     * @param provinces
     */
    public MoveUnit(List<Province> provinces) {
        this.graph = new Graph(provinces);
    }

    /**
     *
     * @param f
     * @param units
     * @param start
     * @param dest
     * @return Generates the province the units need to move to
     */
    public Province move(Faction f, List<Unit> units, Province start, Province dest) {
        List<Province> path = graph.getShortestPath(f, start, dest);
        if (path.isEmpty())
            return null;

        int provincesUnitsCanMove = provincesUnitsCanMove(units);
        if (provincesUnitsCanMove == 0) {
            return null;
        }

        if (provincesUnitsCanMove > path.size()) {
            updateMovementPoints(units, path.size());
            return path.get(path.size() - 1);
        }
        updateMovementPoints(units, provincesUnitsCanMove);
        return path.get(provincesUnitsCanMove - 1);
    }

    /**
     *
     * @param units
     * @return the number of provinces the units can travel
     */
    private int provincesUnitsCanMove(List<Unit> units) {
        int maxMovementPoints = 15;
        for (Unit u: units) {
            int mp = u.getCurrentMovementPoints();
            if (mp < maxMovementPoints)
                maxMovementPoints = mp;
        }
        int provinces = 0;
        while (true) {
            maxMovementPoints -= 4;
            if (maxMovementPoints >= 0)
                provinces++;
            else break;
        }
        return provinces;
    }

    private void updateMovementPoints(List<Unit> units, int provincesUnitsCanMove) {
        for (Unit u: units) {
            u.reduceCurrentMovementPoints(provincesUnitsCanMove * 4);
        }
    }

    public boolean checkPathExists(Faction f, Province start, Province dest) {
        if (graph.getShortestPath(f, start, dest).isEmpty())
            return false;
        return true;
    }
}
