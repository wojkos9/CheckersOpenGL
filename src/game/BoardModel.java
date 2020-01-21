package game;

import graphics.Model;
import graphics.Renderer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import utils.Utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.ARBVertexArrayBGRA.GL_BGRA;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BoardModel extends Model {
    int tex_id;
    int vao;
    int nfaces;
    public BoardModel() {

    }

    public void create(int m) {
        float n = (float)m/2;
        tex_id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex_id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        float[] pixels = new float[]{
                0, 0, 0, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                0, 0, 0, 1};
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 2, 2, 0, GL_BGRA, GL_FLOAT, pixels);
        glBindTexture(GL_TEXTURE_2D, 0);
        /*float[] vertices = {
                0, 0, 0,
                0, 0, m,
                m, 0, 0,
                m, 0, 0,
                0, 0, m,
                m, 0, m};
        n_verts = 6;
        float[] uvs = {
                0, 0,
                0, n,
                n, 0,
                n, 0,
                0, n,
                n, n
        };
        float[] normals = {
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0
        };*/

        nfaces = 6;

        FloatBuffer vertices = BufferUtils.createFloatBuffer(nfaces * 18);
        FloatBuffer normals = BufferUtils.createFloatBuffer(nfaces * 18);
        FloatBuffer uvs = BufferUtils.createFloatBuffer(nfaces * 12);

        float thickness = 1f;
        float delta = 0.1f;

        Vector2f t11 = new Vector2f(0.4f, 0.4f);

        Utils.addFace(vertices, normals, uvs, new Vector3f(0, 1, 0), new Vector3f(n, 0, n),
                new Vector3f(m, 0, m), new Vector2f(0, 0), new Vector2f(n, n), 1);
        Utils.addFace(vertices, normals, uvs, new Vector3f(-1, 0, 0), new Vector3f(n, -thickness/2, 0),
                new Vector3f(m, thickness, 0), new Vector2f(0, 0), t11, 1);
        Utils.addFace(vertices, normals, uvs, new Vector3f(1, 0, 0), new Vector3f(n, -thickness/2, m),
                new Vector3f(m, thickness, 0), new Vector2f(0, 0), t11, 1);
        Utils.addFace(vertices, normals, uvs, new Vector3f(0, 0, -1), new Vector3f(0, -thickness/2, n),
                new Vector3f(0, thickness, m), new Vector2f(0, 0), t11, -1);
        Utils.addFace(vertices, normals, uvs, new Vector3f(0, 0, 1), new Vector3f(m, -thickness/2, n),
                new Vector3f(0, thickness, m), new Vector2f(0, 0), t11, -1);
        Utils.addFace(vertices, normals, uvs, new Vector3f(0, -1, 0), new Vector3f(n, -thickness, n),
                new Vector3f(m, 0, m), new Vector2f(0, 0), new Vector2f(n, n), 1);

        vertices.flip();
        normals.flip();
        uvs.flip();

        ArrayList<FloatBuffer> bufs = new ArrayList<>();
        bufs.add(vertices);
        bufs.add(normals);
        bufs.add(uvs);

//        for (FloatBuffer b : bufs) {
//            for (float f : b.) {
//
//            }
//        }



        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        int vbo2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo2);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        int vbo3 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo3);
        glBufferData(GL_ARRAY_BUFFER, uvs, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(2);


        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void render(Renderer rend) {
        glBindVertexArray(vao);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex_id);
        glDrawArrays(GL_TRIANGLES, 0, nfaces * 6);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);

    }
}
