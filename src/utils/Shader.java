package utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program_id;
    public Shader() {

    }

    private int sourceShader(int type, String fname) {
        int shader_id = glCreateShader(type);
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(fname)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        glShaderSource(shader_id, source);
        glCompileShader(shader_id);
        int[] params = new int[] {0};
        glGetShaderiv(shader_id, GL_INFO_LOG_LENGTH, params);

        if (params[0] > 0) {
            String log = glGetShaderInfoLog(shader_id);
            System.out.println("Error compiling "+fname+":");
            System.out.println(log);
        }
        return shader_id;
    }
    public void load(String vertexFile, String fragmentFile) {
        int vertex_id = sourceShader(GL_VERTEX_SHADER, vertexFile);
        int fragment_id = sourceShader(GL_FRAGMENT_SHADER, fragmentFile);

        program_id = glCreateProgram();

        glAttachShader(program_id, vertex_id);
        glAttachShader(program_id, fragment_id);
        glLinkProgram(program_id);

        int[] params = new int[] {0};
        glGetProgramiv(program_id, GL_INFO_LOG_LENGTH, params);
        if (params[0] > 0) {
            String log = glGetProgramInfoLog(program_id);
            System.out.println("Program log:");
            System.out.println(log);
        }
    }
    public void use() {
        glUseProgram(program_id);
    }
    public void pass(String name, float f) {
        int loc = glGetUniformLocation(program_id, name);
        if (loc != -1)
            glUniform1f(loc, f);
    }
    public void pass(String name, Vector3f v) {
        int loc = glGetUniformLocation(program_id, name);
        if (loc != -1)
            glUniform3f(loc, v.x, v.y, v.z);
    }
    public void pass(String name, Vector4f v) {
        int loc = glGetUniformLocation(program_id, name);
        if (loc != -1)
            glUniform4f(loc, v.x, v.y, v.z, v.w);
    }
    public void pass(String name, Matrix4f m) {
        int loc = glGetUniformLocation(program_id, name);
        if (loc != -1)
            glUniformMatrix4fv(loc, false, m.get(new float[16]));
    }
    public int getProgramId() {
        return program_id;
    }
}
