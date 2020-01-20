package graphics;

import org.joml.*;
import utils.Shader;

import java.lang.Math;

import static org.lwjgl.opengl.GL20.*;

public class Renderer {
    private Matrix4f M, P;
    private boolean m_dirty;
    private Shader activeShader;
    static Shader shaderDiffuse, shaderTextureDiffuse;

    public Camera camera;

    public Renderer() {
        camera = new Camera();
        M = new Matrix4f();
        P = new Matrix4f().perspective((float)Math.toRadians(90), 1.f, 0.1f, 20.f);
        m_dirty = true;
        shaderDiffuse = new Shader();
        shaderDiffuse.load("assets/shaders/diffuse.vsh", "assets/shaders/diffuse.fsh");
        shaderTextureDiffuse = new Shader();
        shaderTextureDiffuse.load("assets/shaders/tex_diff.vsh", "assets/shaders/tex_diff.fsh");
    }
    public void update() {
        Matrix4f MVP = new Matrix4f(P).mul(camera.getView()).mul(M);
        activeShader.pass("u_m", M);
        activeShader.pass("mvp", MVP);
        m_dirty = false;
    }


    public void begin(Shader shader) {
        shader.use();
        activeShader = shader;
        update();
    }

    public void translate(Vector3f dr) {
        M.translate(dr);
        m_dirty = true;
    }

    public void rotate(float angle, Vector3f axis) {
        M.rotate(angle, axis);
        m_dirty = true;
    }


    public Matrix4f getMVP() {
        return new Matrix4f(P).mul(camera.getView()).mul(M);
    }

    public Vector2f unproject(Vector2f win_pos) {
        Vector4f win_vec = new Vector4f(2*win_pos.x-1, 1-2*win_pos.y, 0, 1);
        Matrix4f i_pv = new Matrix4f(P).mul(camera.getView()).invert();
        Vector4f p = win_vec.mul(i_pv);
        Vector3f cam = camera.position;
        Vector3f pc = new Vector3f(p.x, p.y, p.z).div(p.w).sub(cam);

        float l = -cam.y/pc.y;

        float x = cam.x + l * pc.x;
        float z = cam.z + l * pc.z;
        return new Vector2f(x, z);
    }

    public void renderModel(Model model) {
        if (m_dirty) {
            update();
            //activeShader.pass("u_m", M);
            //activeShader.pass("mvp", new Matrix4f(P).mul(V).mul(M));
        }
        model.render(this);
    }


    public Shader getShader() {
        return activeShader;
    }
    public void beginShaderDiffuse() {
        begin(shaderDiffuse);
        shaderDiffuse.pass("u_lightPos", new Vector3f(1, 2, 1));
        shaderDiffuse.pass("u_ambient", 0.5f);
    }
    public void beginShaderTextureDiffuse() {
        begin(shaderTextureDiffuse);
        shaderTextureDiffuse.pass("u_lightPos", new Vector3f(0, 1, 3));
        shaderTextureDiffuse.pass("u_ambient", 0.5f);
    }


}
