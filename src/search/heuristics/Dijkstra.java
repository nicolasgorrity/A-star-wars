package search.heuristics;

import field.BlockType;
import field.Direction;
import search.SearchField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by Fabien on 22/10/2016
 *
 * This heuristic is definitely the more efficient and accurate heuristic we can suggest.
 * We use Dijkstra's Uniform-Cost Search algorithm to evaluate the locations.
 * This allows to calculate the real cost needed to move from each location to each goal, by taking in consideration
 * not only the distance between the location and the goal but also the cost of traps, and the presence of walls.
 *
 * We can consider that this is an exact heuristic, or at least an extremely accurate one.
 */

public class Dijkstra extends Heuristic {

    private ArrayList<Direction> directionsList;

    public Dijkstra(SearchField searchField) {
        super(searchField);
        directionsList = new ArrayList<>();
        buildDirectionsList();
        //Heuristic values are calculated as soon as the object is declared.
        calculateValues();
    }

    private void calculateValues() {
        for (short index = 0; index < theSearchField.getRobotsList().size(); index++) {
            distanceDijkstra(index);
        }
    }

    private void buildDirectionsList() {
        directionsList.add(Direction.UP);
        directionsList.add(Direction.DOWN);
        directionsList.add(Direction.LEFT);
        directionsList.add(Direction.RIGHT);
    }

    private void distanceDijkstra(short robotIndex) {
        short goalLocation = theSearchField.getRobotData().getGoalLocationForRobot(robotIndex);
        short actualPos, nextPos;
        float addition;
        int sizeOfField = theSearchField.getMatrix().size();

        //Create a priority queue
        PriorityQueue<Short> myPriority = new PriorityQueue<>((nb1, nb2) -> (int) (heuristicField.get(robotIndex).get(nb1) - heuristicField.get(robotIndex).get(nb2)));

        //Define a marking table
        Set<Short> isMarked = new HashSet<>();

        //Initialise every location heuristic at a maximal value
        for (int i = 0; i < sizeOfField; i++) {
            heuristicField.get(robotIndex).add(Float.MAX_VALUE);
        }

        //The goal location heuristic is set to 0
        heuristicField.get(robotIndex).set(goalLocation, (float) 0);

        //Insert first location (goal) in the priority queue
        myPriority.add(goalLocation);

        //While the priority queue is not empty
        while (!myPriority.isEmpty()) {
            //Dequeue the top location
            actualPos = myPriority.poll();

            //Mark the location dequeued
            isMarked.add(actualPos);
            //For every adjacent location that has not been marked yet
            for (Direction dir : directionsList) {
                //Calculate the value of the neighbour location
                nextPos = (short) (actualPos + dir.getDirectionValue());
                if (nextPos < theSearchField.getMatrix().size() && nextPos >= 0) {
                    //If the neighbour location has not already been marked
                    if (!isMarked.contains(nextPos) && (theSearchField.getBlocktypeFromPos(nextPos) != BlockType.WALL)) {
                        //The heuristic of the neighbour location is equal to its parent location + the cost of the block at this neighbour location
                        addition = (heuristicField.get(robotIndex).get(actualPos) + theSearchField.getBlockCost(actualPos));

                        //If the calculated cost for the neighbour location is inferior to any cost calculated before for this location
                        if (addition < heuristicField.get(robotIndex).get(nextPos) && addition > -1) {
                            //Enqueue this neighbour location with its cost updated
                            heuristicField.get(robotIndex).set(nextPos, addition);
                            myPriority.add(nextPos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String output = "";
        short nRobots = (short) theSearchField.getRobotsList().size();
        for (short index = 0; index < nRobots; index++) {
            output += "Robot " + index + " : \n";
            for (short i = 0; i < theSearchField.getRowNumber(); i++) {
                for (short j = 0; j < theSearchField.getColumnNumber(); j++) {
                    output += " " + Math.round(heuristicField.get(index).get((i * theSearchField.getColumnNumber()) + j));
                }
                output += "\n";
            }
            output += "\n";
        }
        return output;
    }

    @Override
    public String type() {
        return "Dijkstra Heuristic";
    }
}
