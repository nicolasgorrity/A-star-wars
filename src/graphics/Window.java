package graphics;

import field.Direction;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Nicolas on 17/10/2016
 *
 * This class handles the behaviour of the window with the graphic interface LWJGL.
 */

public class Window {

    //List of graphic parameters
    public static String DEFAULT_WINDOW_NAME = "Parcel Wars";
    public static int WIDTH = 1080;
    public static int HEIGHT = 720;
    public static boolean FULL_SCREEN = false;
    public static boolean ENABLE_GRID = true;
    public static int LINE_WIDTH = 3;
    public static boolean USE_BITMAPS = false;
    public static int DELAY_MS = 90;
    public static boolean MUTE = false;

    private long window;
    private Drawer drawer;
    private int currentWidth;
    private int currentHeight;
    private int screenWidth;
    private int screenHeight;

    private Texture texture;

    private boolean previousIsSearching = false, isSearching = false;

    public Window(Drawer drawer) {
        currentHeight = Window.HEIGHT;
        currentWidth = Window.WIDTH;
        this.drawer = drawer;
        this.init();
    }

    public void run() {
        try {
            loop();

            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName(), (FULL_SCREEN ? glfwGetPrimaryMonitor() : NULL), MemoryUtil.NULL);
        if (window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
            } else if (key == GLFW_KEY_D && action == GLFW_RELEASE) {                                   //Launch search algorithms
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName() + " - A* Searching: Dijkstra heuristic");
                drawer.launchAStarWithDijkstra();
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
            } else if (key == GLFW_KEY_SEMICOLON && action == GLFW_RELEASE) {
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName() + " - A* Searching: Manhattan heuristic");
                drawer.launchAStarWithManhattan();
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
            } else if (key == GLFW_KEY_E && action == GLFW_RELEASE) {
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName() + " - A* Searching: Euclide heuristic");
                drawer.launchAStarWithEuclide();
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
            } else if (key == GLFW_KEY_S && action == GLFW_RELEASE) {                                   //Mute / UnMute
                if (Window.MUTE) {
                    Window.MUTE = false;
                    drawer.unMuteSounds();
                } else {
                    Window.MUTE = true;
                    drawer.muteSounds();
                }
            } else if (key == GLFW_KEY_UP && action == GLFW_PRESS) {                                    //Move robots manually
                drawer.moveSelectedRobotManually(Direction.UP);
            } else if (key == GLFW_KEY_DOWN && action == GLFW_PRESS) {
                drawer.moveSelectedRobotManually(Direction.DOWN);
            } else if (key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
                drawer.moveSelectedRobotManually(Direction.LEFT);
            } else if (key == GLFW_KEY_RIGHT && action == GLFW_PRESS) {
                drawer.moveSelectedRobotManually(Direction.RIGHT);
            } else if (key == GLFW_KEY_0 && action == GLFW_RELEASE) {                                   //Load levels
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level0");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_1 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level1");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_2 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level2");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_3 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level3");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_4 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level4");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_5 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level5");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_6 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level6");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_7 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level7");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_8 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level8");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_9 && action == GLFW_RELEASE) {
                isSearching = previousIsSearching = false;
                drawer.setSearchField("level9");
                glfwSetWindowTitle(window, DEFAULT_WINDOW_NAME + " - " + drawer.getLevelName());
                setDimensions();
            } else if (key == GLFW_KEY_G && action == GLFW_RELEASE) {                                           //Toggle the use of bitmaps
                USE_BITMAPS = !USE_BITMAPS;
            }
        });
        //Mouse callback event: select robots
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                double xPos[] = new double[2];
                double yPos[] = new double[2];
                glfwGetCursorPos(window, xPos, yPos);
                drawer.selectRobotAtScreenPos(Window.WIDTH * xPos[0] / currentWidth, Window.HEIGHT * yPos[0] / currentHeight);
            }
        });

        glfwSetCursorPosCallback(window, (l, v, v1) -> {
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        screenHeight = vidMode.height();
        screenWidth = vidMode.width();
        // Center our window
        glfwSetWindowPos(window, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2);
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();
        //Allows resizing the window and the drawings
        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                glViewport(0, 0, width, height);
                currentHeight = height;
                currentWidth = width;
                drawer.updateRatios();
            }
        });
        //Enable transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //Load bank of images
        loadTexture();
        //Define the clear color
        glClearColor(Color.WHITE.getRedValue(), Color.WHITE.getGreenValue(), Color.WHITE.getBlueValue(), 1.0f);
        //Initiate the dimensions of the window
        setDimensions();
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //Drawing matrix to the screen
            drawer.drawMatrix();

            //Updating robots position if they are currently going through their path after A* search
            isSearching = drawer.updateMatrix();
            //Playing goalReachedSound if robots just ended their path
            if (!isSearching && previousIsSearching) {
                drawer.playSound(drawer.goalReachedSoundIndex);
            }
            previousIsSearching = isSearching;

            //Some necessary graphic commands
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);
            glfwSwapBuffers(window); // swap the color buffers

            //Poll for window events. The key callback above will only be invoked during this call.
            glfwPollEvents();
        }
    }

    private void loadTexture() {
        try {
            texture = new Texture("texture");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawTexturedQuad(float startPosX, float startPosY, float width, float height, ImageData imageData) {
        setColor(Color.WHITE);
        glPushMatrix();
        glTranslatef(startPosX, startPosY, 0);
        glBegin(GL_QUADS);
        glBindTexture(GL_TEXTURE_2D, texture.id);
        //Top left corner
        glTexCoord2f(imageData.getTextureRangeX1(), imageData.getTextureRangeY1());
        glVertex2f(0, 0);
        //Bottom left corner
        glTexCoord2f(imageData.getTextureRangeX1(), imageData.getTextureRangeY2());
        glVertex2f(0, height);
        //Bottom right corner
        glTexCoord2f(imageData.getTextureRangeX2(), imageData.getTextureRangeY2());
        glVertex2f(width, height);
        //Top right corner
        glTexCoord2f(imageData.getTextureRangeX2(), imageData.getTextureRangeY1());
        glVertex2f(width, 0);
        glEnd();
        glPopMatrix();
    }

    public void drawRectangle(float xTopLeft, float yTopLeft, float xBottomRight, float yBottomRight) {
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(xTopLeft, yTopLeft);
        glVertex2f(xTopLeft, yBottomRight);
        glVertex2f(xBottomRight, yBottomRight);
        glVertex2f(xBottomRight, yTopLeft);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public void drawLine(float lineWidth, float x1, float y1, float x2, float y2) {
        glDisable(GL_BLEND);
        glLineWidth(lineWidth);
        glBegin(GL_LINES);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glEnd();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void setColor(Color color) {
        glColor3f(color.getRedValue(), color.getGreenValue(), color.getBlueValue());
    }

    private void setDimensions() {
        //Calculate dimensions so blocks are always squares and are not over-sized or under-sized.
        int dimW = (int) Math.ceil(0.95f * screenWidth / drawer.getLevelColumnNumber());
        int dimH = (int) Math.ceil(0.9f * screenHeight / drawer.getLevelRowNumber());
        if (dimW < dimH) {
            WIDTH = dimW * drawer.getLevelColumnNumber();
            HEIGHT = dimW * drawer.getLevelRowNumber();
        } else {
            WIDTH = dimH * drawer.getLevelColumnNumber();
            HEIGHT = dimH * drawer.getLevelRowNumber();
        }
        //Update drawing multipliers
        drawer.updateRatios();
        //Loading value for drawing sizes multiplier
        glViewport(0, 0, WIDTH, HEIGHT);
        //Resize window
        glfwSetWindowSize(window, WIDTH, HEIGHT);
        //Center the window
        glfwSetWindowPos(window, (screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2);
    }
}