package graphics;

import field.BlockType;
import field.Direction;
import robots.Robot;
import search.SearchField;
import search.heuristics.Dijkstra;
import search.heuristics.Euclidean;
import search.heuristics.Manhattan;

import java.util.ArrayList;

import static graphics.Color.*;

/**
 * Created by Nicolas on 18/10/2016
 *
 * This class purpose is to handle the use of the Window class and link it to the SearchField data and requesting:
 *      - the correct drawings on screen
 *      - the correct musics to play
 *      - the correct actions requested by user interface
 */

public class Drawer {

    private Window window;
    private static float heightMultiplier;
    private static float widthMultiplier;

    private SearchField searchField;

    private ArrayList<BlockType> blockTypesOrder;
    private short selectedRobotIndex = -1;

    public final short inactivitySoundIndex = 0;
    public final short searchingSoundIndex = 1;
    public final short goalReachedSoundIndex = 2;
    private Sound inactivitySound;
    private Sound searchingSound;
    private Sound goalReachedSound;

    public Drawer(String levelName) {
        //Loading music files
        initSounds();
        //Loading level data
        setSearchField(levelName);
        //Calculate ratio (useful for drawing to screen)
        updateRatios();
        //Create a list of block types (in a precise order) which will be gone through while displaying the matrix
        initBlockTypesList();
        //Create the window
        this.window = new Window(this);
        //Launch the game loop
        window.run();
    }

    //This method is used every time a level is (re-)loaded
    public boolean setSearchField(String levelName) {
        //UnSelect robots
        selectedRobotIndex = -1;
        //Restart the inactivity sound
        playSound(inactivitySoundIndex);
        //Load the level data
        this.searchField = new SearchField(levelName);
        return true;
    }

    public void drawMatrix() {
        //Draw squares
        for (short row = 0; row < searchField.getRowNumber(); row++) {
            for (short column = 0; column < searchField.getColumnNumber(); column++) {
                //If the use of textures is activated, we display the level with them. Otherwise we just use squares and lines (Press G to toggle this boolean)
                if (Window.USE_BITMAPS) {
                    //Get the type of the block to be displayed, and draw it at the correct place on the screen.
                    BlockType blockType = searchField.getMatrix().get(searchField.getPosFromCoords(row, column));
                    window.drawTexturedQuad(column * widthMultiplier, row * heightMultiplier, widthMultiplier, heightMultiplier, ImageData.getImagedataForBlocktype(blockType));
                    //If the block is a goal, we also display its number on it.
                    if (blockType.equals(BlockType.GOAL)) {
                        window.drawTexturedQuad(column * widthMultiplier, row * heightMultiplier, widthMultiplier, heightMultiplier, ImageData.getImageDataForNumber(searchField.getIndexOfGoal(searchField.getPosFromCoords(row, column))));
                    }
                } else {
                    //Get the type of the block to be displayed, and draw a square with the color related to the type of block
                    setBlockColor(searchField.getMatrix().get(searchField.getPosFromCoords(row, column)));
                    window.drawRectangle(column * widthMultiplier, row * heightMultiplier, (column + 1) * widthMultiplier, (row + 1) * heightMultiplier);
                }
            }
        }
        //Draw robots
        for (short robotIndex = 0; robotIndex < searchField.getRobotData().getRobotsNumber(); robotIndex++) {
            drawRobot(robotIndex);
        }
        //Draw lines between blocks
        if (Window.ENABLE_GRID) {
            if (Window.USE_BITMAPS) {
                for (BlockType blockType : blockTypesOrder) {
                    for (short pos = 0; pos < searchField.getMatrix().size(); pos++) {
                        if (searchField.getBlocktypeFromPos(pos).equals(blockType))
                            drawEmptySquareAroundLocation(Window.LINE_WIDTH, pos);
                    }
                }
                //If a goal is reached, we draw green lines around it
                for (short posGoal : searchField.getRobotData().getGoalLocations()) {
                    if (searchField.isPositionFoundGoal(posGoal))
                        drawEmptyColoredSquareAroundLocation(Window.LINE_WIDTH, posGoal, Color.GREEN_JEDI);
                }
            } else drawGridLines(Window.LINE_WIDTH, DARK);
        }
        //When a robot is selected by the user, draw a white square around it
        if (selectedRobotIndex != -1) {
            drawEmptyColoredSquareAroundLocation(Window.LINE_WIDTH, searchField.getRobotData().getRobotAtIndex(selectedRobotIndex).getCurrentLocation(), WHITE);
        }
    }

    //This allows the user to select a robot by clicking on it, in order to move it manually
    public void selectRobotAtScreenPos(double xPos, double yPos) {
        //Get the position of the robot according to the position of the mouse click
        short position = searchField.getPosFromCoords((short) (yPos / heightMultiplier), (short) (xPos / widthMultiplier));
        //First check if there is a robot where the user clicked. If not, deselect the robot selected
        if (searchField.isAnyRobotOnPosition(position)) {
            //Select the robot, except if the user clicked on robot which was already selected, deselect it.
            if (selectedRobotIndex == searchField.getIndexOfRobot(position)) {
                selectedRobotIndex = -1;
            } else {
                selectedRobotIndex = searchField.getIndexOfRobot(position);
            }
        } else {
            selectedRobotIndex = -1;
        }
    }

    public void moveSelectedRobotManually(Direction direction) {
        if (selectedRobotIndex != -1) {
            searchField.getRobotData().moveRobotManually(selectedRobotIndex, direction);
        }
    }

    private void drawRobot(short robotIndex) {
        Robot robot = searchField.getRobotsList().get(robotIndex);
        short robotColumn = searchField.getColumnFromPos(robot.getCurrentLocation());
        short robotRow = searchField.getRowFromPos(robot.getCurrentLocation());
        //If textures are activated, we draw the robot texture and the number of the robot above. Else we only draw a little square, symbolizing the robot.
        if (Window.USE_BITMAPS) {
            window.drawTexturedQuad(robotColumn * widthMultiplier, robotRow * heightMultiplier, widthMultiplier, heightMultiplier, ImageData.getImageDataForRobot(searchField.getRobotData().getLastDirectionOfRobot(robotIndex)));
            window.drawTexturedQuad(robotColumn * widthMultiplier, robotRow * heightMultiplier, widthMultiplier, heightMultiplier, ImageData.getImageDataForNumber(robotIndex));
        } else {
            Window.setColor(GREEN);
            window.drawRectangle((robotColumn + 0.2f) * widthMultiplier, (robotRow + 0.2f) * heightMultiplier, (robotColumn + 0.8f) * widthMultiplier, ((robotRow + 0.8f) * heightMultiplier));
        }
    }

    private void drawGridLines(int lineWidth, Color color) {
        Window.setColor(color);
        //Horizontal lines
        for (short row = 0; row <= searchField.getRowNumber(); row++) {
            window.drawLine(lineWidth, 0, row * heightMultiplier, Window.WIDTH, row * heightMultiplier);
        }
        //Vertical lines
        for (short column = 0; column <= searchField.getColumnNumber(); column++) {
            window.drawLine(lineWidth, column * widthMultiplier, 0, column * widthMultiplier, Window.HEIGHT);
        }
    }

    private void drawEmptyColoredSquareAroundLocation(int lineWidth, short position, Color color) {
        //An empty square is drawn like four lines around the square
        BlockType blockType = searchField.getBlocktypeFromPos(position);
        short row = searchField.getRowFromPos(position);
        short column = searchField.getColumnFromPos(position);
        if (color == null) color = Color.getSquareColorForBlock(blockType);
        //Define the color
        Window.setColor(color);
        //Top line
        window.drawLine(lineWidth, column * widthMultiplier, row * heightMultiplier, (column + 1) * widthMultiplier, row * heightMultiplier);
        //Bottom line
        window.drawLine(lineWidth, column * widthMultiplier, (row + 1) * heightMultiplier, (column + 1) * widthMultiplier, (row + 1) * heightMultiplier);
        //Left line
        window.drawLine(lineWidth, column * widthMultiplier, row * heightMultiplier, column * widthMultiplier, (row + 1) * heightMultiplier);
        //Right line
        window.drawLine(lineWidth, (column + 1) * widthMultiplier, row * heightMultiplier, (column + 1) * widthMultiplier, (row + 1) * heightMultiplier);
    }

    private void drawEmptySquareAroundLocation(int lineWidth, short position) {
        drawEmptyColoredSquareAroundLocation(lineWidth, position, null);
    }

    private void setBlockColor(BlockType blockType) {
        //This is used for non-texture display, to select the color of blocks depending on their type
        switch (blockType) {
            case EMPTY:
                Window.setColor(WHITE);
                break;
            case WALL:
                Window.setColor(GREY_MEDIUM);
                break;
            case GOAL:
                Window.setColor(BLUE);
                break;
            case TRAP:
                Window.setColor(BROWN);
                break;
        }
    }

    public short getLevelRowNumber() {
        return searchField.getRowNumber();
    }

    public short getLevelColumnNumber() {
        return searchField.getColumnNumber();
    }

    private void initSounds() {
        //Load music sounds
        inactivitySound = new Sound("inactivity");
        goalReachedSound = new Sound("goalReached");
        searchingSound = new Sound("searching");
        //Only unMute sounds after 200ms, in order to wait for the window to be launched
        muteSounds();
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                unMuteSounds();
            }
        }, 200);
    }

    public void playSound(short soundIndex) {
        //To play a sound, we stop every other else before launching it
        switch (soundIndex) {
            //This sound is looped
            case searchingSoundIndex:
                inactivitySound.stop();
                goalReachedSound.stop();
                searchingSound.loop();
                break;
            //This sound is only played once after the goals are reached after the A* search.
            case goalReachedSoundIndex:
                inactivitySound.stop();
                searchingSound.stop();
                goalReachedSound.play();
                playSoundWithDelay(inactivitySoundIndex, goalReachedSound.getMicroSecondsDuration() / 1080);
                break;
            //This sound is looped
            case inactivitySoundIndex:
            default:
                inactivitySound.stop();
                searchingSound.stop();
                goalReachedSound.stop();
                inactivitySound.loop();
        }
    }

    private void playSoundWithDelay(short soundIndex, long msDelay) {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                playSound(soundIndex);
            }
        }, msDelay);
    }

    public void muteSounds() {
        inactivitySound.mute();
        searchingSound.mute();
        goalReachedSound.mute();
        Window.MUTE = true;
    }

    public void unMuteSounds() {
        inactivitySound.unmute();
        searchingSound.unmute();
        goalReachedSound.unmute();
        Window.MUTE = false;
    }

    public void updateRatios() {
        heightMultiplier = Window.HEIGHT / searchField.getRowNumber();
        widthMultiplier = Window.WIDTH / searchField.getColumnNumber();
    }

    public String getLevelName() {
        return searchField.getLevelName();
    }

    public boolean updateMatrix() {
        //Calls updateRobots() and returns a boolean allowing goalReachedSound to be played or not
        return searchField.updateRobots() || searchField.getRobotData().isSomethingBeingDisplayed();
    }

    private void initBlockTypesList() {
        blockTypesOrder = new ArrayList<>();
        blockTypesOrder.add(BlockType.WALL);
        blockTypesOrder.add(BlockType.EMPTY);   //DO NOT MODIFY THE ORDER OF THIS LIST
        blockTypesOrder.add(BlockType.GOAL);    //THE ORDER IS IMPORTANT WHEN WE DRAW EVERYTHING ON SCREEN
        blockTypesOrder.add(BlockType.TRAP);
    }

    public void launchAStarWithManhattan() {
        selectedRobotIndex = -1;
        if (!searchField.isSearchingOrDisplaying()) {
            playSound(searchingSoundIndex);
            searchField.AStar(new Manhattan(searchField));
        }
    }

    public void launchAStarWithDijkstra() {
        selectedRobotIndex = -1;
        if (!searchField.isSearchingOrDisplaying()) {
            playSound(searchingSoundIndex);
            searchField.AStar(new Dijkstra(searchField));
        }
    }

    public void launchAStarWithEuclide() {
        selectedRobotIndex = -1;
        if (!searchField.isSearchingOrDisplaying()) {
            playSound(searchingSoundIndex);
            searchField.AStar(new Euclidean(searchField));
        }
    }
}