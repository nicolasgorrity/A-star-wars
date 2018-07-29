package search.heuristics;

import search.SearchField;

/**
 * Created by Fabien on 25/10/2016
 *
 * This heuristic doesn't consider the presence of any wall or traps.
 * The only thing considered is the difference of locations between locations and goals.
 * Manhattan distance is the sum of the difference of distance in abscissa and the difference of distance in ordinate:
 *      m = abs(x1 - x2) + abs(y1 - y2)
 */

public class Manhattan extends Heuristic {

    public Manhattan(SearchField searchField) {
        super(searchField);
        //Heuristic values are calculated as soon as the object is declared.
        calculateValues();
    }

    private void calculateValues() {
        for (short index = 0; index < theSearchField.getRobotsList().size(); index++) {
            distanceManhattan(index);
        }
    }

    private void distanceManhattan(short robotIndex) {
        int sizeOfField = theSearchField.getMatrix().size();
        short goalLocation = theSearchField.getRobotData().getGoalLocationForRobot(robotIndex);
        for (short i = 0; i < sizeOfField; i++) {
            heuristicField.get(robotIndex).add((float) Math.abs(theSearchField.getRowFromPos(i) - theSearchField.getRowFromPos(goalLocation))
                    + Math.abs(theSearchField.getColumnFromPos(i) - theSearchField.getColumnFromPos(goalLocation)));
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
        return "Manhattan Heuristic";
    }
}
