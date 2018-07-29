package search.heuristics;

import search.SearchField;

/**
 * Created by Fabien on 28/10/2016
 *
 * With this heuristic, we only care about the distance between locations and goals. We don't care about walls and traps.
 * Euclidean distance is the distance as the crow flies, i.e.:
 *      e = sqrt [ (y2 - y1)² + (x2 - x1)² ]
 */

public class Euclidean extends Heuristic {
    public Euclidean(SearchField searchField) {
        super(searchField);
        //Heuristic values are calculated as soon as the object is declared.
        calculateValues();
    }

    private void calculateValues() {
        for (short index = 0; index < theSearchField.getRobotsList().size(); index++) {
            distanceEuclide(index);
        }
    }

    private void distanceEuclide(short robotIndex) {
        int sizeOfField = theSearchField.getMatrix().size();
        short goalLocation = theSearchField.getRobotData().getGoalLocationForRobot(robotIndex);
        for (short i = 0; i < sizeOfField; i++) {
            heuristicField.get(robotIndex).add((float) Math.sqrt(Math.pow(theSearchField.getRowFromPos(i) - theSearchField.getRowFromPos(goalLocation), 2)
                    + Math.pow(theSearchField.getColumnFromPos(i) - theSearchField.getColumnFromPos(goalLocation), 2)));
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
                    output += " " + heuristicField.get(index).get((i * theSearchField.getColumnNumber()) + j);
                }
                output += "\n";
            }
            output += "\n";
        }
        return output;
    }

    @Override
    public String type() {
        return "Euclide Heuristic";
    }
}
