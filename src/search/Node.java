package search;

import field.Direction;
import robots.Robot;

import java.util.ArrayList;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This class is the structure for the nodes used in the A* search tree.
 * A node contains:
 *      - the list of robots, (so their locations)
 *      - its cost, what had to be spent from the initial node to this one
 *      - its heuristic, an estimation of the additional cost needed to reach the goal state
 *          --> total evaluation of a node = cost + heuristic
 *      - its predecessor hashcode (smaller in memory than the predecessor Node itself)
 */

public class Node {

    private int cost;
    private float heuristic;
    private ArrayList<Robot> robotsList;
    private final Integer predecessorHashCode;

    public Node(ArrayList<Robot> robotsList) {
        this(0, robotsList);
    }

    public Node(int cost, ArrayList<Robot> robotsList) {
        this(cost, robotsList, null);
    }

    public Node(int cost, ArrayList<Robot> robotsList, Integer predecessorHashCode) {
        this(cost, robotsList, predecessorHashCode, 0);
    }

    public Node(int cost, ArrayList<Robot> robotsList, Integer predHashCode, float newPosHeuristic) {
        this.cost = cost;
        copyList(robotsList); //Here we need to store a copy to avoid retrieving a reference
        this.predecessorHashCode = predHashCode;
        this.heuristic = newPosHeuristic;
    }

    public void moveRobot(short robotIndex, Direction direction) {
        robotsList.get(robotIndex).move(direction);
    }

    private void copyList(ArrayList<Robot> robotsList) {
        this.robotsList = new ArrayList<>();
        for (Robot robot : robotsList) {
            Robot copyRobot = new Robot(robot.getCurrentLocation());
            this.robotsList.add(copyRobot);
        }
    }

    @Override
    public boolean equals(Object o) {
        //If both objects are the same instance, they are equal.
        if (this == o) return true;
        //If both objects classes are different, both objects are different.
        if (o == null || getClass() != o.getClass()) return false;
        //If both objects are Nodes and their robots list are equal, both Nodes are equal.
        Node node = (Node) o;
        return this.robotsList.equals(node.robotsList);
    }

    @Override
    public int hashCode() {
        String hash = "";
        for (Robot robot : robotsList) {
            hash += robot.hashCode() + ",";
        }
        return hash.hashCode();
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setHeuristic(float heuristic) {
        this.heuristic = heuristic;
    }

    public ArrayList<Short> getListOfRobotsLocations() {
        //Here we return a copy of the list, because the default behaviour is to return a reference,
        //and when we modify the returned list we do not want the list of this node to be modified too.
        ArrayList<Short> robotsLocations = new ArrayList<>();
        for (short robotIndex = 0; robotIndex < robotsList.size(); robotIndex++) {
            robotsLocations.add(robotsList.get(robotIndex).getCurrentLocation());
        }
        return robotsLocations;
    }

    public float getTotalEvaluation() {
        return heuristic + cost;
    }

    public ArrayList<Robot> getRobotsList() {
        return robotsList;
    }

    public Integer getPredecessorHashCode() {
        return predecessorHashCode;
    }

    @Override
    public String toString() {
        return "Node{" +
                "cost=" + cost +
                ", \ntotal cost=" + getTotalEvaluation() +
                ", \nrobotsList=" + robotsList +
                '}';
    }
}
