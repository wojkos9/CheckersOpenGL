package game;

import font.FontRenderer;
import graphics.Renderer;
import org.joml.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static utils.Utils.drawOutlineAt;
import static utils.Utils.millisToTimeString;

import player.AI;
import player.HumanPlayer;
import player.Player;

public class Game {
    private long window;
    private int n_cells, n_rows;
    private GameManager manager;
    private Vector2i cursor_board;
    private int WIDTH = 640, HEIGHT = 480;
    private FontRenderer font_renderer;
    private int white_model_type, black_model_type;
    private boolean accept_input = true;
    private Renderer rend;
    private Vector2f mouse;
    private boolean dragging_view = false;
    private boolean timer_active;
    private int max_time;
    private BoardModel board_model;

    public Game(int n_cells, int n_rows, boolean p1_human, boolean p2_human, int wtype, int btype, int time_s, boolean no_animations) {
        this.n_cells = n_cells;
        this.n_rows = n_rows;
        white_model_type = wtype;
        black_model_type = btype;
        max_time = time_s;
        if (no_animations)
            Pawn.animation_time = 0.f;
        else
            Pawn.animation_time = 0.3f;

        manager = new GameManager(this, n_cells);


        Player player1 = p1_human ? new HumanPlayer(this, Pawn.Color.WHITE) : new AI(this, Pawn.Color.WHITE);
        Player player2 = p2_human ? new HumanPlayer(this, Pawn.Color.BLACK) : new AI(this, Pawn.Color.BLACK);
        manager.setPlayers(player1, player2);
        mouse = new Vector2f();
    }
    void stopTimer() {
        timer_active = false;
    }

    private void loadAssets() {
        font_renderer = new FontRenderer();
        font_renderer.load("assets/fonts/verdana/verdana.fnt", "assets/fonts/verdana/verdana.png");
        PawnFactory.loadModels(white_model_type, black_model_type);
    }

    class ScrollCallback extends GLFWScrollCallback {
        @Override
        public void invoke(long l, double v, double v1) {
            System.out.println(v + " " + v1);
            rend.camera.moveCameraDistance(-(float) v1);
        }
    }

    class CursorCallback extends GLFWCursorPosCallback {
        @Override
        public void invoke(long l, double v, double v1) {
            Vector2f win_pos = new Vector2f((float)v/WIDTH, (float) v1/HEIGHT);
            cursor_board = worldToBoardCoords(rend.unproject(win_pos));
        }
    }
    private void init() {
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Warcaby", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        glfwSetScrollCallback(window, new ScrollCallback());
        glfwSetCursorPosCallback(window, new CursorCallback());

        GL.createCapabilities();

        glClearColor(0.165f, 0.266f, 0.570f, 1.0f);
        cursor_board = new Vector2i();

        rend = new Renderer();

        board_model = new BoardModel();
        board_model.create(n_cells);

        rend.translate(new Vector3f((float)-n_cells/2, 0, (float)-n_cells/2));

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private Vector2i worldToBoardCoords(Vector2f world_coords) {
        return new Vector2i((int)(world_coords.x+(float)n_cells/2), (int)(world_coords.y+(float)n_cells/2));
    }

    private void handleMouse() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            double[] x = new double[1], y = new double[1];
            glfwGetCursorPos(window, x, y);
            Vector2f mouse1 = new Vector2f((float)x[0], (float)y[0]);
            if (dragging_view) {
                Vector2f diff = new Vector2f(mouse1).sub(mouse).mul(0.01f);
                rend.camera.moveCameraAngle(-diff.x, -diff.y);
            }
            mouse.set(mouse1);
            dragging_view = true;
        } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            if (accept_input && !dragging_view) {
                double[] x = new double[1], y = new double[1];
                glfwGetCursorPos(window, x, y);
                Vector2f mouse1 = new Vector2f((float)x[0]/WIDTH, (float)y[0]/HEIGHT);
                Vector2f pos_world = rend.unproject(mouse1);
                Vector2i pos_board = worldToBoardCoords(pos_world);
                manager.select(pos_board);
            }
            dragging_view = true;
        } else {
            dragging_view = false;
        }
    }

    public GameManager getManager() {
        return manager;
    }

    void setAcceptInput(boolean accept) {
        accept_input = accept;
    }

    private void loop() {
        manager.placePawns(n_rows);
        manager.startGame();
        timer_active = true;
        long time0 = System.currentTimeMillis();
        long time_playing = 0;
        while (!glfwWindowShouldClose(window)) {
            handleMouse();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            rend.beginShaderTextureDiffuse();
            rend.renderModel(board_model);

            rend.beginShaderDiffuse();
            for (Pawn p : manager.getPawns().values()) {
                if (p.isAlive()) {
                    p.render(rend);
                }
            }

            glUseProgram(0);
            glLoadMatrixf(rend.getMVP().get(new float[16]));
            Vector2i picked = manager.getCurrent();
            if (accept_input && picked != null) {
                glColor3f(0.0f, 0.0f, 1.0f);
                drawOutlineAt(picked);
            }

            if (manager.getBoard().inBounds(cursor_board)) {
                if (accept_input && manager.canMoveFrom(cursor_board))
                    glColor3f(0.0f, 1.0f, 0.0f);
                else
                    glColor3f(1.0f, 0.0f, 0.0f);
                drawOutlineAt(cursor_board);
            }
            if (timer_active) {
                time_playing = System.currentTimeMillis() - time0;
                if (time_playing >= max_time) {
                    time_playing = max_time;
                    stopTimer();
                }
            }


            glColor3f(0.f, 1.f, 0.f);
            font_renderer.renderText(millisToTimeString(time_playing));

            glfwPollEvents();
            glfwSwapBuffers(window);
            try {
                Thread.sleep(1000/120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.quit();
        glfwDestroyWindow(window);
    }
    void run() {
        init();
        loadAssets();
        loop();
    }


    public static void main(String[] args) {
        new Game(8, 2, false, false, 0, 0, 60, false).run();
    }
}
