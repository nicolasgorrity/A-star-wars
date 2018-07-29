package field;

/**
 * Created by Nicolas on 11/10/2016
 *
 * This enumeration stores all the information relative to the types of blocks:
 *     - costs (1 for empty and goals, 5 for traps)
 *     - whether it is walkable or not, i.e. whether a robot can move through the block or not
 */

public enum BlockType {

    EMPTY((byte) 1, true),
    TRAP((byte) 5, true),
    GOAL((byte) 1, true),
    WALL(Byte.MAX_VALUE, false);

    private byte cost = -1;
    private boolean isWalkable = false;

    BlockType(byte cost, boolean isWalkable) {
        this.cost = cost;
        this.isWalkable = isWalkable;
    }

    public byte getCost() {
        return cost;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    @Override
    public String toString() {
        if (this.equals(BlockType.EMPTY)) return " "; // Empty
        if (this.equals(BlockType.TRAP)) return "T"; // Trap
        if (this.equals(BlockType.GOAL)) return "G"; // Goal
        return "W"; // Wall
    }
}
