package search;

import field.BlockType;
import field.Direction;
import files.FileRead;
import graphics.Window;
import robots.Robot;
import robots.RobotData;
import search.heuristics.Heuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This class contains all the element needed for the A* search.
 * It also includes the structures necessary to store all the relevant data (RobotData, ArrayList for the level matrix)
 */

public class SearchField {

    private short rowNumber;
    private short columnNumber;
    private String levelName;

    //Structures containing useful data
    private static ArrayList<BlockType> matrix; //Contains the map (i.e. the type of each block)
    private RobotData robotData;                //Contains robots information

    //Search tools
    private ArrayList<Direction> directionsList;
    private boolean isSearching;

    public SearchField(String levelName) {
        isSearching = false;
        this.levelName = levelName;
        directionsList = new ArrayList<>();
        //Read the level file and store it into the dedicated structures
        loadDataFromLevel();
    }

    /**
     * This method is our second version of the A* (the first version is not used anymore, but the code is still present in this file)
     *
     * What differs between both A* is the way we are creating the neighbour nodes after having dequeued a node from the priority queue
     * Here, the difference between the parent and the child nodes is:
     *      - Every robot has to move once, by taking a direction among the list: LEFT, UP, RIGHT, DOWN, NONE
     * We have to let the possibility for a robot not to move (with the NONE) direction, because in certain cases it has to wait that
     * another robot moves before he can keep going through its path.
     * If we didn't let the NONE direction available, instead of staying immobile, the robot would go around in circles
     * like making RIGHT-LEFT-RIGHT-LEFT moves, and this would add useless cost to its path.
     *
     * A technique to "remove" the NONE direction from this list was studied with the class Zar (in search.heuristics package).
     * See the comments in this class for more information.
     *
     * We use a custom PriorityQueue, see more in the comments of the file PriorityQueue.java (in search package)
     *
     * @param heuristic is an object whose class depends on the heuristic we want to use: Dijkstra, Manhattan, or Euclide.
     *                  Those three classes inherit from the class Heuristic
     */
    public void AStar(Heuristic heuristic) {
        System.out.println("\n" + heuristic.type() + "\n");
        clearSearch();
        //We build the directions list with "true" parameter when we want the NONE direction to be in the list, false otherwise
        buildDirectionsList(true);
        //Counters of nodes
        long nodesAddedToPriorityQueue = 0;
        long nodesDequeued = 0;
        //Tools: PriorityQueue (OpenList), HashMap (ClosedList), List of possibilities of moves
        RobotPriorityQueue robotPriorityQueue = new RobotPriorityQueue();
        HashMap<Integer, Node> closedList = new HashMap<>();
        ArrayList<ArrayList<Direction>> movesPossibilities;
        //Creation of the initial node
        Node node = new Node(robotData.getRobotsList());
        //Storing initial node
        robotPriorityQueue.add(node);
        //Creating chronometer
        long startTime = System.currentTimeMillis();
        //Construct list of all combinations of simultaneous moves for the robots
        movesPossibilities = moveCombinations();
        //The algorithm keeps running while there is something in the priority queue
        while (!robotPriorityQueue.isEmpty()) {
            //Retrieve the most optimistic node we found yet
            node = robotPriorityQueue.poll();
            nodesDequeued++;
            //Mark this node to avoid revisiting it
            closedList.putIfAbsent(node.hashCode(), node);
            //Test if this node is solution
            if (areRobotsOnGoals(node)) break;
            //Search for neighbour states
            for (int listIndex = 0; listIndex < movesPossibilities.size(); listIndex++) {
                //Get one of the possibilities of moves for all robots, encoded in a list of n_robots directions
                ArrayList<Direction> movePossibility = movesPossibilities.get(listIndex);
                boolean somethingChanged = false;
                //Construct a child node with the parent node as a model
                Node nodeChild = new Node(node.getCost(), node.getRobotsList(), node.hashCode());
                //Move every robot in the node child according to the movePossibility list of directions
                for (short robotIndex = 0; robotIndex < robotData.getRobotsNumber(); robotIndex++) {
                    //For each robot, if it can take its attributed direction, update the child node
                    if (canRobotMove(nodeChild, robotIndex, movePossibility.get(robotIndex))) {
                        somethingChanged = true;
                        //If the robot can move, add cost of move and update the nodeChild list
                        short newPos = getNewPosWithDir(node.getRobotsList().get(robotIndex).getCurrentLocation(), movePossibility.get(robotIndex));
                        nodeChild.setCost(nodeChild.getCost() + getBlockCost(newPos));
                        nodeChild.moveRobot(robotIndex, movePossibility.get(robotIndex));
                    }
                }
                //If no robot has been able to move (walls or other robots blocking), do nothing
                if (somethingChanged) {
                    //The heuristic is the sum of all the current robots heuristics
                    nodeChild.setHeuristic(heuristic.getTotalHeuristic(nodeChild.getListOfRobotsLocations()));
                    //If the node is already in closedList (already marked) but the new node is more optimistic, we just replace it
                    if (closedList.containsKey(nodeChild.hashCode())) {
                        if (closedList.get(node.hashCode()).getTotalEvaluation() > nodeChild.getTotalEvaluation()) {
                            closedList.remove(nodeChild.hashCode());
                            closedList.put(nodeChild.hashCode(), nodeChild);
                        }
                    } else {
                        //If not already marked, add the new node to the priority queue
                        if (robotPriorityQueue.add(nodeChild)) nodesAddedToPriorityQueue++;
                    }
                }
            }
        }
        //Display diagnosis of search onto the console
        if (!robotPriorityQueue.isEmpty())
            System.out.println("Solution found in " + (float) ((System.currentTimeMillis() - startTime) / 1000) + "s!\nNodes dequeued: " + nodesDequeued + "\nNodes added to priority queue: " + nodesAddedToPriorityQueue);
        else
            System.out.println("Solution not found after " + (float) ((System.currentTimeMillis() - startTime) / 1000) + "s:\nNodes dequeued: " + nodesDequeued + "\nNodes added to priority queue: " + nodesAddedToPriorityQueue);
        System.out.println("Total cost of solution: " + node.getCost());
        //Decode the solution found to build the robots path (their stack of directions)
        constructPaths(node, closedList);
    }

    //This method generates a table containing all possibilities of moves for the robots
    //For 2 robots, this would be
    //UP:UP ; UP:DOWN ; UP:LEFT ; UP:RIGHT ; UP:NONE ; DOWN:UP ; DOWN:DOWN ; DOWN:LEFT ; DOWN:RIGHT ; DOWN:NONE ; LEFT:UP ; LEFT:DOWN ; LEFT:LEFT ; etc...
    private ArrayList<ArrayList<Direction>> moveCombinations() {
        ArrayList<ArrayList<Direction>> moveCombinations = new ArrayList<>();
        for (short row = 0; row < Math.pow(directionsList.size(), robotData.getRobotsNumber()); row++) {
            ArrayList<Direction> rowList = new ArrayList<>();
            for (short robotIndex = robotData.getRobotsNumber(); robotIndex > 0; robotIndex--) {
                rowList.add(directionsList.get((int) ((row / Math.pow(directionsList.size(), robotIndex - 1)) % directionsList.size())));
            }
            moveCombinations.add(rowList);
        }
        return moveCombinations;
    }

    /**
     * This method is our second version of the A* (the first version is not used anymore, but the code is still present in this file)
     *
     * What differs between both A* is the way we are creating the neighbour nodes after having dequeued a node from the priority queue.
     * Here, the difference between the parent and the child nodes is that we only move one robot at a time,
     * while the others stay immobile. So on the screen the robots would make each move one after the other.
     *
     * We use a custom PriorityQueue, see more in the comments of the file PriorityQueue.java (in search package)
     *
     * @param heuristic is an object whose class depends on the heuristic we want to use: Dijkstra, Manhattan, or Euclide.
     *                  Those three classes inherit from the class Heuristic
     */
    public void AstarOld(Heuristic heuristic) {
        System.out.println("\n" + heuristic.type() + "\n");
        clearSearch();
        //We build the directions list with "true" parameter when we want the NONE direction to be in the list, false otherwise
        buildDirectionsList(false);
        //Counters of nodes
        long nodesAddedToPriorityQueue = 0;
        long nodesDequeued = 0;
        //Tools: PriorityQueue (OpenList), Hashmap (ClosedList)
        RobotPriorityQueue robotPriorityQueue = new RobotPriorityQueue();
        HashMap<Integer, Node> closedList = new HashMap<>();
        //Creation of the initial node
        Node node = new Node(robotData.getRobotsList());
        //Storing initial node
        robotPriorityQueue.add(node);
        //Creating chronometer
        long startTime = System.currentTimeMillis();
        //The algorithm keeps running while there is something in the priority queue
        while (!robotPriorityQueue.isEmpty()) {
            //Retrieving the most optimistic node we found yet
            node = robotPriorityQueue.poll();
            nodesDequeued++;
            //Mark this node to avoid revisiting it
            closedList.putIfAbsent(node.hashCode(), node);
            //Test if this node is solution
            if (areRobotsOnGoals(node)) break;
            //Search for neighbour states
            for (short robotIndex = 0; robotIndex < robotData.getRobotsNumber(); robotIndex++) {
                for (Direction direction : directionsList) {
                    //If the specified robot can move in the specified direction
                    if (canRobotMove(node, robotIndex, direction)) {
                        //Calculate its future location
                        short newPos = getNewPosWithDir(node.getRobotsList().get(robotIndex).getCurrentLocation(), direction);
                        //Create of the child node with the parent child model
                        Node nodeChild = new Node(node.getCost() + getBlockCost(newPos), node.getRobotsList(), node.hashCode());
                        //Update the robots list with the move of the robot
                        nodeChild.moveRobot(robotIndex, direction);
                        nodeChild.setHeuristic(heuristic.getTotalHeuristic(nodeChild.getListOfRobotsLocations()));
                        //If already in closedList but the new node is more optimistic, we just replace it
                        if (closedList.containsKey(nodeChild.hashCode())) {
                            if (closedList.get(nodeChild.hashCode()).getCost() > nodeChild.getCost()) {
                                closedList.remove(nodeChild.hashCode());
                                closedList.put(nodeChild.hashCode(), nodeChild);
                            }
                        } else {
                            //If relevant, add the new node to the priority queue (tests are made internally)
                            if (robotPriorityQueue.add(nodeChild)) nodesAddedToPriorityQueue++;
                        }
                    }
                }
            }
        }
        //Display the diagnosis of the search onto the console
        if (!robotPriorityQueue.isEmpty())
            System.out.println("Solution found in " + (float) ((System.currentTimeMillis() - startTime) / 1000) + "s!\nNodes dequeued: " + nodesDequeued + "\nNodes added to priority queue: " + nodesAddedToPriorityQueue);
        else
            System.out.println("Solution not found after " + (float) ((System.currentTimeMillis() - startTime) / 1000) + "s:\nNodes dequeued: " + nodesDequeued + "\nNodes added to priority queue: " + nodesAddedToPriorityQueue);
        System.out.println("Total cost of solution: " + node.getCost());
        //Decode the solution found to build the robots path (their stack of directions)
        constructPaths(node, closedList);
    }

    public boolean areRobotsOnGoals(Node node) {
        ArrayList<Robot> nodeRobotsList = node.getRobotsList();
        for (short robotIndex = 0; robotIndex < nodeRobotsList.size(); robotIndex++) {
            if (nodeRobotsList.get(robotIndex).getCurrentLocation() != robotData.getGoalLocationForRobot(robotIndex))
                return false;
        }
        return true;
    }

    private void constructPaths(Node winNode, HashMap<Integer, Node> closedList) {
        //We start from the goal node and backtrack until the start node.
        //Between every parent and child node we retrieve the index of the robot which has to move, and the direction it has to take
        while (winNode.getPredecessorHashCode() != null) {
            if (!closedList.containsKey(winNode.getPredecessorHashCode()))
                System.out.println("Error in SearchField.constructPaths(): Could not find predecessor in hashMap.");
            for (short robotIndex = 0; robotIndex < robotData.getRobotsNumber(); robotIndex++) {
                Direction direction = Direction.getDirectionWithValue((short) (winNode.getRobotsList().get(robotIndex).getCurrentLocation() - closedList.get(winNode.getPredecessorHashCode()).getRobotsList().get(robotIndex).getCurrentLocation()));
                robotData.stackDirectionForRobot(robotIndex, direction);
            }
            winNode = closedList.get(winNode.getPredecessorHashCode());
        }
        robotData.setAllRobotsToBeDisplayed(true);
        isSearching = false;
    }

    public boolean updateRobots() {
        //Move robots in the RobotData, and let a pause in order to let us see the path on screen
        if (robotData.moveRobots()) {
            try {
                Thread.sleep(Window.DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void loadDataFromLevel() {
        //Reading the file
        try {
            //See how a FileRead reads a level file in the comments of the class FileRead.java (in files package)
            FileRead levelFile = new FileRead(levelName);
            //Retrieving the values read
            rowNumber = levelFile.getRowNumberFromFile();
            columnNumber = levelFile.getColumnNumberFromFile();
            Direction.colNumber = columnNumber;
            matrix = levelFile.getMatrixFromFile();
            robotData = new RobotData(levelFile.getRobotsKeyLocationsFromFile());
            //Display the essential level data onto the console
            displayMatrix();
            System.out.println("rows: " + rowNumber);
            System.out.println("columns: " + columnNumber);
            System.out.println(robotData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMatrix() {
        System.out.println("\n\n");
        for (short i = 0; i < rowNumber; i++) {
            for (short j = 0; j < columnNumber; j++) {
                System.out.print(matrix.get(getPosFromCoords(i, j)));
            }
            System.out.println("");
        }
        System.out.println("\n");
    }

    private void buildDirectionsList(boolean addNone) {
        directionsList.add(Direction.LEFT);
        directionsList.add(Direction.UP);
        directionsList.add(Direction.RIGHT);
        directionsList.add(Direction.DOWN);
        if (addNone) directionsList.add(Direction.NONE);
    }

    public static boolean canRobotMove(Node node, short robotIndex, Direction direction) {
        return canRobotMove(robotIndex, node.getRobotsList(), direction);
    }

    public static boolean canRobotMove(short robotIndex, ArrayList<Robot> robotList, Direction direction) {
        //A robot can move only if the aimed location is walkable and if there is no robot already there
        short currentPos = robotList.get(robotIndex).getCurrentLocation();
        short newPos = getNewPosWithDir(currentPos, direction);
        boolean noRobotOnPos = true;
        for (short robIndex = 0; robIndex < robotList.size(); robIndex++) {
            if (robotList.get(robIndex).getCurrentLocation() == newPos) {
                noRobotOnPos = false;
                break;
            }
        }
        return (noRobotOnPos && matrix.get(newPos).isWalkable());
    }

    public boolean isSearchingOrDisplaying() {
        return (isSearching || robotData.isSomethingBeingDisplayed());
    }

    private void clearSearch() {
        isSearching = true;
        robotData.reInit();
        directionsList.clear();
    }

    public static short getNewPosWithDir(short currentPosition, Direction direction) {
        short newPos = (short) (currentPosition + direction.getDirectionValue());
        if (newPos < matrix.size() && newPos >= 0) {
            return newPos;
        }
        System.out.println("Error in SearchField.getNewPosWithDir(): Index out of range -> pos=" + newPos);
        return -1;
    }

    public short getIndexOfGoal(short position) {
        if (position >= 0 && position < matrix.size()) {
            for (short index = 0; index < robotData.getRobotsNumber(); index++) {
                if (robotData.getGoalLocationForRobot(index) == position) return index;
            }
            System.out.println("Error in SearchField.getIndexOfGoal(): No goal was found at this place -> pos=" + position);
            return -1;
        }
        System.out.println("Error in SearchField.getIndexOfGoal(): Index out of range -> pos=" + position);
        return -1;
    }

    public short getIndexOfRobot(short position) {
        if (position >= 0 && position < matrix.size()) {
            for (short index = 0; index < robotData.getRobotsNumber(); index++) {
                if (robotData.getRobotAtIndex(index).getCurrentLocation() == position) return index;
            }
            return -1;
        }
        System.out.println("Error in SearchField.getIndexOfRobot(): Index out of range -> pos=" + position);
        return -1;
    }

    public boolean isPositionFoundGoal(short position) {
        if (position >= 0 && position < matrix.size()) {
            for (short index = 0; index < robotData.getRobotsNumber(); index++) {
                if (robotData.getGoalLocationForRobot(index) == position && robotData.isRobotOnItsGoal(index))
                    return true;
            }
            return false;
        }
        System.out.println("Error in SearchField.getIndexOfGoal(): Index out of range -> pos=" + position);
        return false;
    }

    public boolean isAnyRobotOnPosition(short position) {
        if (position >= 0 && position < matrix.size()) {
            for (short index = 0; index < robotData.getRobotsNumber(); index++) {
                if (robotData.getRobotAtIndex(index).getCurrentLocation() == position) return true;
            }
            return false;
        }
        System.out.println("Error in SearchField.isAnyRobotOnPosition(): Index out of range -> pos=" + position);
        return false;
    }

    public RobotData getRobotData() {
        return robotData;
    }

    public byte getBlockCost(short position) {
        return matrix.get(position).getCost();
    }

    public short getPosFromCoords(short row, short column) {
        return (short) (row * columnNumber + column);
    }

    public short getColumnFromPos(short position) {
        return (short) (position % columnNumber);
    }

    public short getRowFromPos(short position) {
        return (short) (position / columnNumber);
    }

    public BlockType getBlocktypeFromPos(short position) {
        return matrix.get(position);
    }

    public ArrayList<BlockType> getMatrix() {
        return matrix;
    }

    public short getRowNumber() {
        return rowNumber;
    }

    public short getColumnNumber() {
        return columnNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public ArrayList<Robot> getRobotsList() {
        return robotData.getRobotsList();
    }
}
