package utils;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

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
    public static void addFace(FloatBuffer vertices, FloatBuffer normals, FloatBuffer uvs, Vector3f n,
                               Vector3f midpoint, Vector3f sizes, Vector2f t00, Vector2f t11, int sign) {
        Vector3f m = new Vector3f(midpoint);
        Vector3f s = new Vector3f(sizes);
        //Vector3f n = new Vector3f(midpoint).normalize();
        vertices.put(new float[] {m.x+s.x/2, m.y-s.y/2, m.z-sign*s.z/2});
        vertices.put(new float[] {m.x+s.x/2, m.y+s.y/2, m.z+s.z/2});
        vertices.put(new float[] {m.x-s.x/2, m.y-s.y/2, m.z-s.z/2});
        vertices.put(new float[] {m.x-s.x/2, m.y-s.y/2, m.z-s.z/2});
        vertices.put(new float[] {m.x+s.x/2, m.y+s.y/2, m.z+s.z/2});
        vertices.put(new float[] {m.x-s.x/2, m.y+s.y/2, m.z+sign*s.z/2});
        for (int i = 0; i < 6; i++) {
            normals.put(new float[]{n.x, n.y, n.z});
        }

        uvs.put(new float[] {
                t11.x, t00.y,
                t11.x, t11.y,
                t00.x, t00.y,
                t00.x, t00.y,
                t11.x, t11.y,
                t00.x, t11.y
        });

    }
}
