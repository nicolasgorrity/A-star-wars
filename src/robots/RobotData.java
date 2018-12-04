package robots;

import field.Direction;
import search.SearchField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This class purpose is to store every information relative to the robots and not used DURING the A* search:
 *      - The robots list (not always of size 2: we can add as many robots as we want).
 *      - Each robot key locations (initial location and goal location).
 *      - Each robot directions stack (used to store the stack of moves that represents the robot's path found by the A* search).
 *      - A boolean for each robot (toBeDisplayed) indicating whether it has gone through its whole path yet or not.
 *      - A direction for each robot (lastDirection), useful to display it in the correct direction when it's moving on screen.
 */

public class RobotData {

    private ArrayList<KeyRobotLocations> keyLocations;
    private ArrayList<Stack<Direction>> directionStacks;
    private ArrayList<Robot> robotsList;
    private ArrayList<Boolean> toBeDisplayed;
    private ArrayList<Direction> lastDirections;

    public RobotData(HashMap<Short, Short> robotsKeyLocations) {
        keyLocations = new ArrayList<>();
        robotsList = new ArrayList<>();
        //Read of the locations list, which contains start and goal locations for robots. Key is Start, Value is Goal
        for (Map.Entry<Short, Short> locationPair : robotsKeyLocations.entrySet()) {
            keyLocations.add(new KeyRobotLocations(locationPair.getKey(), locationPair.getValue()));
            robotsList.add(new Robot(locationPair.getKey()));
        }
        //Instantiate directions stacks for robots
        directionStacks = new ArrayList<>();
        for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
            directionStacks.add(new Stack<>());
        }
        //List of booleans describing whether or not they have to update their position by popping their direction stack
        toBeDisplayed = new ArrayList<>();
        for (short i = 0; i < keyLocations.size(); i++) toBeDisplayed.add(false);
        //List of directions used to display the robots in the correct direction on screen.
        lastDirections = new ArrayList<>();
        for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
            //At first, all robots are displayed in DOWN direction
            lastDirections.add(Direction.DOWN);
        }
    }

    private boolean moveRobot(short robotIndex) {
        //We can only move a robot if its toBeDisplayed boolean is true
        if (toBeDisplayed.get(robotIndex)) {
            //If there is no more move in the directions stack, it means the robot has finished its path. Set its toBeDisplayed to false.
            if (directionStacks.get(robotIndex).isEmpty()) {
                toBeDisplayed.set(robotIndex, false);
                return false;
            }
            //If there is still moves in the directions stack, we make sure the next location is walkable before doing anything, to avoid any misbehaviour
            if (SearchField.canRobotMove(robotIndex, robotsList, directionStacks.get(robotIndex).peek())) {
                //If it is walkable and there was no other robot on the location, the move can be done
                lastDirections.set(robotIndex, directionStacks.get(robotIndex).peek());
                robotsList.get(robotIndex).move(popDirectionFromRobot(robotIndex));
                return true;
            }
            //If the location wasn't walkable, we still pop this direction out of the stack, or it will try to move this way infinitely.
            popDirectionFromRobot(robotIndex);
        }
        //It returns true if the move has been done, false if the move couldn't be done.
        return false;
    }

    public boolean moveRobots() {
        //This is a method calling the method moveRobot() for each robot of the list.
        if (isSomethingBeingDisplayed()) {
            for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
                moveRobot(robotIndex);
            }
            return true;
        }
        return false;
    }

    //This method allows to move robots with the keyboard arrows prior to launching the A* search.
    public void moveRobotManually(short robotIndex, Direction direction) {
        //We first make sure that the robots aren't currently going through their path just after a search.
        //The user has to wait that robots reach their goals before he can move them himself.
        if (!isSomethingBeingDisplayed()) {
            lastDirections.set(robotIndex, direction);
            if (SearchField.canRobotMove(robotIndex, robotsList, direction)) {
                robotsList.get(robotIndex).move(direction);
            }
        }
    }

    private void updateKeyLocations() {
        ArrayList<KeyRobotLocations> newKeyLocations = new ArrayList<>();
        for (short i = 0; i < getRobotsNumber(); i++) {
            newKeyLocations.add(new KeyRobotLocations(robotsList.get(i).getCurrentLocation(), keyLocations.get(i).getGoalLocation()));
        }
        keyLocations = newKeyLocations;
    }

    public boolean isRobotOnItsGoal(short robotIndex) {
        if (robotIndex < getRobotsNumber()) {
            return (keyLocations.get(robotIndex).getGoalLocation() == robotsList.get(robotIndex).getCurrentLocation());
        }
        System.out.println("Error in RobotData.isRobotOnItsGoal(): index out of range -> robotIndex=" + robotIndex);
        return false;
    }

    //This method allows to re-initialise the values just before an A* search is launched.
    public void reInit() {
        //Those two methods are just here for safety, in theory they shouldn't be needed.
        setAllRobotsToBeDisplayed(false);
        clearDirectionStacks();
        //Updating key locations is particularly important, because the user could have changed the robots initial
        //locations by moving them manually.
        updateKeyLocations();
    }

    public boolean isSomethingBeingDisplayed() {
        boolean nothingDisplaying = false;
        for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
            if (isToBeDisplayed(robotIndex)) nothingDisplaying = true;
        }
        return nothingDisplaying;
    }

    public short getGoalLocationForRobot(short robotIndex) {
        return keyLocations.get(robotIndex).getGoalLocation();
    }

    public ArrayList<Short> getGoalLocations() {
        ArrayList<Short> goalLocations = new ArrayList<>();
        for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
            goalLocations.add(keyLocations.get(robotIndex).getGoalLocation());
        }
        return goalLocations;
    }

    public short getStartLocationForRobot(short robotIndex) {
        return keyLocations.get(robotIndex).getStartLocation();
    }

    public ArrayList<Robot> getRobotsList() {
        return robotsList;
    }

    public Robot getRobotAtIndex(short robotIndex) {
        return robotsList.get(robotIndex);
    }

    public Direction getLastDirectionOfRobot(short robotIndex) {
        return lastDirections.get(robotIndex);
    }

    public short getRobotsNumber() {
        return (short) keyLocations.size();
    }

    public void stackDirectionForRobot(short robotIndex, Direction direction) {
        directionStacks.get(robotIndex).push(direction);
    }

    private Direction popDirectionFromRobot(short robotIndex) {
        return directionStacks.get(robotIndex).pop();
    }

    private void clearDirectionStacks() {
        for (short i = 0; i < getRobotsNumber(); i++) {
            directionStacks.get(i).clear();
        }
    }

    public void setAllRobotsToBeDisplayed(boolean toBeDisplayed) {
        for (short robotIndex = 0; robotIndex < getRobotsNumber(); robotIndex++) {
            setToBeDisplayed(robotIndex, toBeDisplayed);
        }
    }

    private void setToBeDisplayed(short robotIndex, boolean toBeDisplayed) {
        this.toBeDisplayed.set(robotIndex, toBeDisplayed);
    }

    private boolean isToBeDisplayed(short robotIndex) {
        return toBeDisplayed.get(robotIndex);
    }

    @Override
    public String toString() {
        String output = "RobotData:\n\tNumber of robots: " + getRobotsNumber() + "\n";
        for (short i = 0; i < getRobotsNumber(); i++) {
            output += "\tRobot n°" + i + ":  Start=" + getStartLocationForRobot(i) + "  |  Goal=" + getGoalLocationForRobot(i) + "\n";
        }
        return output;
    }
}