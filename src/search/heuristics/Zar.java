package search.heuristics;

import field.Direction;
import search.Node;
import search.SearchField;

import java.util.*;

/**
 * Created by Fabien on 22/10/2016
 *
 * This class is supposed to be useful to delineate the zone directly accessible by a robot (ZAR),
 * i.e. the zone which is not locked by any wall or robot.
 *
 * We wanted to calculate this in each node, for our second version of A* where robots move simultaneously.
 *
 * What we are currently doing is making every robot move in one of these directions: UP, DOWN, LEFT, RIGHT, NONE when possible.
 * Allowing the robot not to move (NONE) is necessary because, in many solutions,
 * there are configurations where a robot has to wait so the other robot can make its move.
 *
 * The ZAR was supposed to allow us to remove NONE from this list.
 * So a robot wouldn't move only when its goal is not situated in its ZAR (i.e. another robot is blocking the way).
 *
 * But after doing some tests, we noticed that the ZAR was very expensive in time to calculate, because it needs to be done for each node.
 *
 * The ZAR would have been useful to give a solution where robots move more synchronously (i.e. Robot1 does not wait uselessly to begin its path)
 * but we considered that the extra time needed for those calculations weren't worth such a little improvement of the solution found.
 */

public class Zar {
    //ZAR = Zone Accessible by Robot
    private SearchField theSearchField;
    private Set<Short> zar;
    private ArrayList<Direction> directionsList;

    public Zar(SearchField searchField) {
        theSearchField = searchField;
        zar = new HashSet<>();
        directionsList = new ArrayList<>();
        buildDirectionsList();
    }

    public void calculateZar(short robotIndex, Node myNode) {
        //Creation of a queue
        Queue<Short> myQueue = new LinkedList<>();
        //Start location (= robot current location)
        short processedPos = myNode.getRobotsList().get(robotIndex).getCurrentLocation();

        short newPos;
        boolean isNewPosProcessedIsRobot;

        //Add the start location to the ZAR
        zar.add(processedPos);

        //Enqueue the start location
        myQueue.add(processedPos);

        //While the search Queue isn't empty
        while (!myQueue.isEmpty()) {
            //Dequeue the first element
            processedPos = myQueue.poll();

            //Here we are searching for the walkable neighbour locations
            for (Direction dir : directionsList) {
                //Retrieve the neighbour location for the specified direction
                newPos = (short) (processedPos + dir.getDirectionValue());

                //Check if there si a robot on the neighbour location
                isNewPosProcessedIsRobot = myNode.getListOfRobotsLocations().contains(newPos);

                //If there is no robot on the neighbour location and it is walkable
                if (!isNewPosProcessedIsRobot && theSearchField.getBlocktypeFromPos(newPos).isWalkable() && zar.contains(newPos)) {
                    //Add the neighbour location to the ZAR
                    zar.add(newPos);
                    //Enqueue the neighbour location
                    myQueue.add(newPos);
                }
            }
        }
    }

    private void buildDirectionsList() {
        directionsList.add(Direction.UP);
        directionsList.add(Direction.DOWN);
        directionsList.add(Direction.LEFT);
        directionsList.add(Direction.RIGHT);
    }
}