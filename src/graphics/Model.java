package graphics;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.Vector;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Model {
    public Vector3f origin;
    protected int vao_id, n_verts;
    public Model() {
        origin = new Vector3f();
    }
    public Model(String fname, int i0) {
        this();
        load(fname, i0);
    }

    private void bufferPutN(Vector<Float> vec, String split[], int n) {
        for (int i = 0; i < n; i++) {
            vec.add(Float.parseFloat(split[i+1]));
        }
    }
    public boolean load(String fname){
        return load(fname, 0);
    }
    public boolean load(String fname, int i0) {
        Scanner sc;
        try {
            sc = new Scanner(new File(fname));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }


        Vector<Float> verts = new Vector<>();
        Vector<Float> normals = new Vector<>();
        Vector<Float> uvs = new Vector<>();
        Vector<Integer> i_verts = new Vector<>();
        Vector<Integer> i_uvs = new Vector<>();

        Vector<Integer> i_normals = new Vector<>();


        while(sc.hasNextLine()) {
            String ln = sc.nextLine();
            if (ln != null && !ln.isEmpty() && !ln.startsWith("#")) {
                String[] split = ln.split(" ");
                switch (split[0]) {
                    case "v":
                        bufferPutN(verts, split, 3);
                        break;
                    case "vn":
                        bufferPutN(normals, split, 3);
                        break;
                    case "vt":
                        bufferPutN(uvs, split, 2);
                        break;
                    case "f":
                        String[] s1 = split[1].split("/");
                        String[] s2 = split[2].split("/");
                        String[] s3 = split[3].split("/");

                        i_verts.add(Integer.parseInt(s1[0]));
                        i_verts.add(Integer.parseInt(s2[0]));
                        i_verts.add(Integer.parseInt(s3[0]));

                        //i_uvs.add(Integer.parseInt(s1[1]));
                        //i_uvs.add(Integer.parseInt(s2[1]));
                        //i_uvs.add(Integer.parseInt(s3[1]));

                        i_normals.add(Integer.parseInt(s1[2]));
                        i_normals.add(Integer.parseInt(s2[2]));
                        i_normals.add(Integer.parseInt(s3[2]));
                        break;
                    default:
                        break;
                }
            }
        }

        n_verts = i_verts.size();
        FloatBuffer buf_verts = BufferUtils.createFloatBuffer(n_verts*3);
        for (int i : i_verts) {
            float v1 = verts.elementAt(3*(i-i0));
            float v2 = verts.elementAt(3*(i-i0)+1);
            float v3 = verts.elementAt(3*(i-i0)+2);
            buf_verts.put(v1);
            buf_verts.put(v2);
            buf_verts.put(v3);
        }
        buf_verts.flip();

        FloatBuffer buf_normals = BufferUtils.createFloatBuffer(i_normals.size()*3);
        for (int i : i_normals) {
            float v1 = normals.elementAt(3*(i-i0));
            float v2 = normals.elementAt(3*(i-i0)+1);
            float v3 = normals.elementAt(3*(i-i0)+2);
            buf_normals.put(v1);
            buf_normals.put(v2);
            buf_normals.put(v3);

        }
        buf_normals.flip();

        vao_id = glGenVertexArrays();
        glBindVertexArray(vao_id);

        int vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, buf_verts, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        int vbo_id2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id2);
        glBufferData(GL_ARRAY_BUFFER, buf_normals, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);


        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        return true;
    }
    public void render(Renderer rend) {
        glBindVertexArray(vao_id);
        glDrawArrays(GL_TRIANGLES, 0, n_verts);
        glBindVertexArray(0);
    }
    public int getVAO() {
        return vao_id;
    }
    public int getNumVerts() {
        return n_verts;
    }
}
