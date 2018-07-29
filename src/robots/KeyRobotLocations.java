package robots;

import javafx.util.Pair;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This class is based on Pair architecture
 * This is re-implemented only in order to have more understandable methods names.
 * We use a Pair based data structure to store each robot initial location and goal location.
 *
 * We should not need to modify the values after creating the object. So there is no need for setters.
 */

class KeyRobotLocations extends Pair<Integer,Integer> {

    KeyRobotLocations(int startLocation, int goalLocation) {
        super(startLocation,goalLocation);
    }

    short getStartLocation() {
        return super.getKey().shortValue();
    }

    short getGoalLocation() {
        return super.getValue().shortValue();
    }

}
