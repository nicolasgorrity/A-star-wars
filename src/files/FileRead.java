package files;

import field.BlockType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Nicolas on 14/10/2016
 *
 * This class purpose is reading a level file (.txt) to read and encode information about the level matrix, the robots, and their goals.
 * A level must be rectangle (n x m) and have walls on sides.
 *
 * A level file is written in two parts:
 *      - First, each line contains each robot's initial position and goal
 *      - Then, the matrix is drawn with W for walls, E for empty spaces, and T for traps.
 *      - Two parts of the file are separated by a semicolon.
 *      For example:
 *
 *      6,18        Initial position of robot = 6, this robot's goal position = 18
 *      16,7        Initial position of robot = 16, this robot's goal position = 7
 *      ;
 *      WWWWW
 *      WEEEW
 *      WEETW
 *      WEEEW
 *      WWWWW
 *
 * The matrix is encoded into an 1D ArrayList (see matrix encoding info in Direction.java).
 * The robots initial positions and goals are stored into an hashMap.
 * These two containers will be retrieved in the constructor of SearchField.
 */

public class FileRead {

    private final String levelName;

    private short rowNumber, columnNumber;
    private ArrayList<BlockType> theMatrix;
    private HashMap<Short, Short> robotsKeyLocations;

    //The method readFile() is called in the constructor, so that the file is already read and decoded just after creating the FileRead object
    public FileRead(String levelName) throws IOException {
        this.levelName = levelName;
        theMatrix = new ArrayList<>();
        robotsKeyLocations = new HashMap<>();
        this.readFile();
    }

    //Reads a file and retrieves the data into the containers
    private void readFile() {
        //Get the file
        InputStream inputStream = getClass().getResourceAsStream("/resources/levels/" + levelName + ".txt");
        //Storing the content of the file into a BufferReader, then reading the BufferReader
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            //We define a line to read
            String line = br.readLine();
            //First lines correspond to the robots initial positions and goals
            while (line != null && line.charAt(0) != ';') {
                String[] locationParts = line.split(",");
                if (locationParts.length != 2) {
                    System.out.println("Error when reading file: bad writing of key robot locations");
                    return;
                }
                //Store freshly read robot and goal into the hashMap
                robotsKeyLocations.put(Short.parseShort(locationParts[0].trim()), Short.parseShort(locationParts[1].trim()));
                line = br.readLine();
            }
            //Read the matrix and encode it into the ArrayList
            line = br.readLine();
            rowNumber = 0;
            while (line != null) {
                //Here we calculate the number of rows by incrementing it each time a line is read
                rowNumber++;
                for (int i = 0; i < line.length(); i++) {
                    //Decoding the character and creating the corresponding BlockType
                    switch (line.charAt(i)) {
                        case 'E':
                            theMatrix.add(BlockType.EMPTY);
                            break;
                        case 'W':
                            theMatrix.add(BlockType.WALL);
                            break;
                        case 'T':
                            theMatrix.add(BlockType.TRAP);
                            break;
                        default:
                            break;
                    }
                }
                line = br.readLine();
            }
            //Calculate the number of columns
            columnNumber = (short) (theMatrix.size() / rowNumber);

            //Add goals to the matrix ArrayList
            Collection<Short> goalList = robotsKeyLocations.values();
            for (Short elem : goalList) {
                theMatrix.set(elem, BlockType.GOAL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public short getRowNumberFromFile() {
        return rowNumber;
    }

    public short getColumnNumberFromFile() {
        return columnNumber;
    }

    public ArrayList<BlockType> getMatrixFromFile() {
        return theMatrix;
    }

    public HashMap<Short, Short> getRobotsKeyLocationsFromFile() {
        return robotsKeyLocations;
    }
}