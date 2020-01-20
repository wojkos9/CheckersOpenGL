package utils;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.Vector;

import static org.lwjgl.opengl.ARBVertexArrayBGRA.GL_BGRA;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Texture {
    private int tex_id;
    public Texture() {

    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, tex_id);
    }

    public int load(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        tex_id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex_id);

// set the texture wrapping/filtering options (on the currently bound texture object)
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, pixels);

        glBindTexture(GL_TEXTURE_2D, 0);
        return tex_id;
    }

    public static class TexturedModel {

        private int vao_id, vbo_id, n_verts;

        private void bufferPutN(Vector<Float> vec, String split[], int n) {
            for (int i = 0; i < n; i++) {
                vec.add(Float.parseFloat(split[i+1]));
            }
        }

        public boolean load(String fname) {
            Scanner sc = null;
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

            IntegerArray i_normals = new IntegerArray();


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

                            i_uvs.add(Integer.parseInt(s1[1]));
                            i_uvs.add(Integer.parseInt(s2[1]));
                            i_uvs.add(Integer.parseInt(s3[1]));

                            i_normals.add(Integer.parseInt(s1[2]));
                            i_normals.add(Integer.parseInt(s2[2]));
                            i_normals.add(Integer.parseInt(s3[2]));
                            break;
                        default:
                            break;
                    }
                }
            }


            //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vao_id);
            //glBufferData(GL_ELEMENT_ARRAY_BUFFER, i_verts.toIntArray(), GL_STATIC_DRAW);
            n_verts = 0;
            FloatBuffer buf_verts = BufferUtils.createFloatBuffer(i_verts.size()*3);
            for (int i : i_verts) {
                float v1 = verts.elementAt(3*i-3);
                float v2 = verts.elementAt(3*i+1-3);
                float v3 = verts.elementAt(3*i+2-3);
                buf_verts.put(v1);
                buf_verts.put(v2);
                buf_verts.put(v3);
                n_verts++;
            }
            buf_verts.flip();

            FloatBuffer buf_uvs = BufferUtils.createFloatBuffer(i_uvs.size()*2);
            for (int i : i_uvs) {
                float v1 = uvs.elementAt(2*i-2);
                float v2 = uvs.elementAt(2*i+1-2);
                buf_uvs.put(v1);
                buf_uvs.put(v2);
            }
            buf_uvs.flip();

            vao_id = glGenVertexArrays();
            glBindVertexArray(vao_id);

            vbo_id = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
            glBufferData(GL_ARRAY_BUFFER, buf_verts, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            int vbo2 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo2);
            glBufferData(GL_ARRAY_BUFFER, buf_uvs, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);


            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            return true;
        }

        public void render() {
            //System.out.println("DRAW");
            glBindVertexArray(vao_id);
            int err;
            while((err = glGetError()) != GL_NO_ERROR)
            {
                System.out.println("Error: "+Integer.toHexString(err));
            }
            glDrawArrays(GL_TRIANGLES, 0, n_verts);
            glBindVertexArray(0);
        }
    }
}
