import graphics.Drawer;
import graphics.Window;

/**
 * Created by Nicolas on 11/10/2016
 */

public class MainClass {

    public static void main(String[] args) {
        editGraphicSettings();

        new Drawer("level1");
        System.exit(0);
    }

    private static void editGraphicSettings() {
        Window.WIDTH = 980;
        Window.HEIGHT = 980;
        Window.LINE_WIDTH = 3;
        Window.DELAY_MS = 250;
        Window.MUTE = false;
        Window.USE_BITMAPS = true;
        Window.FULL_SCREEN = false;
        Window.ENABLE_GRID = true;
        Window.DEFAULT_WINDOW_NAME = "Parcel Wars";
    }
}
