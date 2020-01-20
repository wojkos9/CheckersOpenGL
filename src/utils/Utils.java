package utils;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;

public class Utils {
    public static void drawOutlineAt(Vector2i board_pos) {
        float height = 0.02f;
        glLineWidth(5.f);
        glBegin(GL_LINES);
        glVertex3f(board_pos.x, height, board_pos.y);
        glVertex3f(board_pos.x, height, board_pos.y+1);
        glVertex3f(board_pos.x, height, board_pos.y+1);
        glVertex3f(board_pos.x+1, height, board_pos.y+1);
        glVertex3f(board_pos.x+1, height, board_pos.y+1);
        glVertex3f(board_pos.x+1, height, board_pos.y);
        glVertex3f(board_pos.x+1, height, board_pos.y);
        glVertex3f(board_pos.x, height, board_pos.y);
        glEnd();

    }

    public static String millisToTimeString(long millis) {
        millis /= 1000;
        long s = millis%60;
        millis /= 60;
        long m = millis%60;
        long h = millis / 60;
        return (h > 0 ? h+":" : "") + m+":"+(s<10?"0":"")+s;
    }
}
