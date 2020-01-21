package font;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import utils.Texture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
    private Map<Integer, IntBuffer> letters;
    private Texture texture;
    public FontRenderer() {
        letters = new HashMap<>();
    }
    public void load(String path, String img_path) {
        texture = new Texture();
        try {
            texture.load(img_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner sc;
        try {
            sc = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < 4; i++) //skip info
            sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("char")) {
                IntBuffer params = BufferUtils.createIntBuffer(8);
                int i1, i2 = 0;
                for (int i = 0; i < 8; i++) {
                    i1 = line.indexOf("=", i2);
                    i2 = line.indexOf(" ", i1);
                    int x = Integer.parseInt(line.substring(i1+1, i2));
                    params.put(x);
                }
                letters.put(params.get(0), params);
                System.out.println();
            }
        }
    }
    public void renderText(String text) {
        int W = 640, H = 480;
        int TW = 512;
        glLoadIdentity();
        glOrtho( -W/2.f, W/2.f, -H/2.f, H/2.f, -1, 1 );

        byte[] b = text.getBytes();
        texture.bind();
        glTranslatef(-W/2+5, H/2, 0);

        glScalef(0.5f, 0.5f, 0.5f);

        for (int i = 0; i < b.length; i++) {
            int c = (char) b[i];
            IntBuffer params = letters.get(c);
            if (params == null)
                continue;
            float x = params.get(1);
            float y = params.get(2);
            float w = params.get(3);
            float h = params.get(4);
            float dx = params.get(5);
            float dy = params.get(6);
            float x_advance = params.get(7);
            glBegin(GL_QUADS);
            glTexCoord2f(x/TW, (y+h)/TW);
            glVertex3f(dx, -h-dy, 0);

            glTexCoord2f((x+w)/TW, (y+h)/TW);
            glVertex3f(dx+w, -h-dy, 0);

            glTexCoord2f((x+w)/TW, y/TW);
            glVertex3f(dx+w, -dy, 0);


            glTexCoord2f(x/TW, y/TW);
            glVertex3f(dx, -dy, 0);
            glEnd();

            glTranslatef(x_advance*0.8f, 0, 0);
        }

    }
}
