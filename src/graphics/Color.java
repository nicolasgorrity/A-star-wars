package graphics;

import field.BlockType;

/**
 * Created by Nicolas on 27/10/2016
 *
 * This enumeration purpose is to store all the RGB values for the colors used by the LWJGL graphic interface
 */

public enum Color {

    WHITE(1.0f, 1.0f, 1.0f),
    DARK(0.1f, 0.1f, 0.1f),
    GREY_MEDIUM(0.4f, 0.4f, 0.4f),
    BLUE(0.172f, 0.457f, 1.0f),
    GREEN(0f, 1f, 0.5f),
    BROWN(0.539f, 0.18f, 0.105f),

    BLUE_JEDI(0.1914f, 0.3672f, 0.957f),
    GREEN_JEDI(0.4805f, 0.9219f, 0.3281f),
    RED_SITH(1.0f, 0.2344f, 0.2461f);

    private float redValue = 0f;
    private float greenValue = 0f;
    private float blueValue = 0f;

    Color(float redValue, float greenValue, float blueValue) {
        this.redValue = redValue;
        this.greenValue = greenValue;
        this.blueValue = blueValue;
    }

    public float getRedValue() {
        return redValue;
    }

    public float getGreenValue() {
        return greenValue;
    }

    public float getBlueValue() {
        return blueValue;
    }

    public static Color getSquareColorForBlock(BlockType blockType) {
        if (blockType.equals(BlockType.EMPTY)) return BLUE_JEDI;
        else if (blockType.equals(BlockType.WALL)) return DARK;
        else if (blockType.equals(BlockType.TRAP)) return RED_SITH;
        else return BLUE_JEDI; //Goal (will be green only if robot is on goal)
    }
}
