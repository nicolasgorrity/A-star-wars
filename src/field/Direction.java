package field;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This class contains all the information relative to possible directions robots can take.
 *
 * Every direction type stores the value of the move, i.e. the difference brought to a robot location if it takes this direction.
 * Locations are encoded in one dimension:
 * In an example of a 3x3 level, locations would be encoded as follows:
 *      0 1 2
 *      3 4 5
 *      6 7 8
 *
 * Consequently:
 *      - moving right is equivalent to location = location + 1
 *      - moving left  is equivalent to location = location - 1
 *      - moving down  is equivalent to location = location + number of columns in the level
 *      - moving up    is equivalent to location = location - number of columns in the level
 *
 * This class was supposed to be an enum, but the values or UP and DOWN change depending on the level.
 * Values in enum are stored during compilation so they cannot be changed dynamically.
 * Consequently this class works the same way as an enum, but is implemented as a class.
 */

public class Direction {

    public static short colNumber = 1;

    public static Direction UP = new Direction();
    public static Direction DOWN = new Direction();
    public static Direction RIGHT = new Direction();
    public static Direction LEFT = new Direction();
    public static Direction NONE = new Direction();

    public short getDirectionValue() {
        if (this.equals(UP)) return (short) (-1 * colNumber);
        if (this.equals(DOWN)) return colNumber;
        if (this.equals(RIGHT)) return (short) (1);
        if (this.equals(LEFT)) return (short) (-1);
        return (short) (0);
    }

    @Override
    public String toString() {
        if (this.equals(UP)) return "UP";
        if (this.equals(DOWN)) return "DOWN";
        if (this.equals(RIGHT)) return "RIGHT";
        if (this.equals(LEFT)) return "LEFT";
        return "NONE";
    }

    public static Direction getDirectionWithValue(short directionValue) {
        if (directionValue == (short) (-1 * colNumber)) return UP;
        if (directionValue == colNumber) return DOWN;
        if (directionValue == 1) return RIGHT;
        if (directionValue == -1) return LEFT;
        return NONE;
    }

    public static short getNumberOfPossibleDirections() {
        return 5;
    }
}
