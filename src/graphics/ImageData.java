package graphics;

import field.BlockType;
import field.Direction;

/**
 * Created by Nicolas on 27/10/2016
 *
 * This enumeration contains all the information necessary for displaying textures onto the screen with LWJGL.
 * The pictures are loaded from a single picture containing all of them: texture.png
 * Each texture has 4 attributes: x1, y1, x2, y2.
 * On a scale of 0 to 1, this attributes indicates the location of the texture on the picture texture.png.
 * The picture contains 4 textures in width and 3 in height.
 * So, for example, if we want the second in width and third in height texture, we will have:
 *      x1 = 0.5f  /  x2 = 0.75f  /  y1 = 0.3333f  /  y2 = 0.6666f
 *
 * There are textures for block types, for robots (one texture for each direction), and for numbers attributed to a robot and its goal.
 */

public enum ImageData {

    NUMBER_1(0f, 0.25f, 0f, 0.3333f),
    NUMBER_2(0.25f, 0.50f, 0f, 0.3333f),
    NUMBER_3(0.50f, 0.75f, 0f, 0.3333f),
    NUMBER_4(0.75f, 1.0f, 0f, 0.3333f),

    TRAP_IM(0f, 0.25f, 0.3333f, 0.6666f),
    GOAL_IM(0.25f, 0.50f, 0.3333f, 0.6666f),
    EMPTY_IM(0.50f, 0.75f, 0.3333f, 0.6666f),
    WALL_IM(0.75f, 1.0f, 0.3333f, 0.6666f),

    ROBOT_UP(0f, 0.25f, 0.6666f, 1.0f),
    ROBOT_DOWN(0.25f, 0.50f, 0.6666f, 1.0f),
    ROBOT_LEFT(0.50f, 0.75f, 0.6666f, 1.0f),
    ROBOT_RIGHT(0.75f, 1.0f, 0.6666f, 1.0f);

    private float textureRangeX1 = 0f;
    private float textureRangeX2 = 0f;
    private float textureRangeY1 = 0f;
    private float textureRangeY2 = 0f;

    ImageData(float textureRangeX1, float textureRangeX2, float textureRangeY1, float textureRangeY2) {
        this.textureRangeX1 = textureRangeX1;
        this.textureRangeX2 = textureRangeX2;
        this.textureRangeY1 = textureRangeY1;
        this.textureRangeY2 = textureRangeY2;
    }

    public float getTextureRangeX1() {
        return textureRangeX1;
    }

    public float getTextureRangeX2() {
        return textureRangeX2;
    }

    public float getTextureRangeY1() {
        return textureRangeY1;
    }

    public float getTextureRangeY2() {
        return textureRangeY2;
    }

    public static ImageData getImagedataForBlocktype(BlockType blockType) {
        switch (blockType) {
            case EMPTY:
                return EMPTY_IM;
            case WALL:
                return WALL_IM;
            case TRAP:
                return TRAP_IM;
            case GOAL:
            default:
                return GOAL_IM;
        }
    }

    public static ImageData getImageDataForRobot(Direction direction) {
        if (direction.equals(Direction.DOWN)) return ROBOT_DOWN;
        else if (direction.equals(Direction.UP)) return ROBOT_UP;
        else if (direction.equals(Direction.LEFT)) return ROBOT_LEFT;
        return ROBOT_RIGHT;
    }

    public static ImageData getImageDataForNumber(short robotIndex) {
        switch (robotIndex) {
            case 0:
                return NUMBER_1;
            case 1:
                return NUMBER_2;
            case 2:
                return NUMBER_3;
            case 3:
            default:
                return NUMBER_4;
        }
    }
}
