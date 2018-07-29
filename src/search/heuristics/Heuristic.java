package search.heuristics;

import search.SearchField;

import java.util.ArrayList;

/**
 * Created by Fabien on 25/10/2016
 *
 * This class is the super class for every different heuristic that we created.
 * To implement a new heuristic, we just have to create a class extending Heuristic,
 * and implement the method calculateValues() where every value of heuristic is calculated, relatively to each robot.
 *
 * The blocks have heuristic values which are different depending on the goal considered, so we need to calculate and
 * store as many matrices of grades as we have robots.
 */

public class Heuristic {

    protected SearchField theSearchField;
    protected ArrayList<ArrayList<Float>> heuristicField;

    public Heuristic(SearchField searchField) {
        //Initialise every list and get the searchField data so it can be read while calculating heuristic
        theSearchField = searchField;
        heuristicField = new ArrayList<>();
        for (int i = 0; i < searchField.getRobotsList().size(); i++) {
            heuristicField.add(new ArrayList<>());
        }
    }

    public float getHeuristicAtPos(short pos, short robotIndex) {
        return heuristicField.get(robotIndex).get(pos);
    }

    //This methods returns the sum of the heuristics of the list of robots
    public float getTotalHeuristic(ArrayList<Short> robotsLocations) {
        float totalHeuristic = 0;
        for (short robotIndex = 0; robotIndex < robotsLocations.size(); robotIndex++) {
            totalHeuristic += getHeuristicAtPos(robotsLocations.get(robotIndex), robotIndex);
        }
        return totalHeuristic;
    }

    @Override
    public String toString() {
        String output = "";
        short nRobots = (short) theSearchField.getRobotsList().size();
        for (short index = 0; index < nRobots; index++) {
            output += "Robot " + index + " : \n";
            for (short i = 0; i < theSearchField.getRowNumber(); i++) {
                for (short j = 0; j < theSearchField.getColumnNumber(); j++) {
                    output += " " + heuristicField.get(index).get(i * theSearchField.getColumnNumber() + j);
                }
                output += "\n";
            }
            output += "\n";
        }
        return output;
    }

    public String type() {
        return "Heuristic";
    }
}