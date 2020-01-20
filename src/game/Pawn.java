package game;

import graphics.Model;
import graphics.Renderer;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class Pawn {
    private Vector3f pos;
    private Vector4f color;
    private Model model;
    private Type type;
    boolean isKing;
    Vector3f rotation;
    private static Vector3f rotationAxis = new Vector3f(1, 0, 0);
    private Boolean[] alive;
    private Vector3f dest;
    public static float animation_time = 0.3f;

    public enum Color {
        WHITE, BLACK, NONE;

        @Override
        public String toString() {
            if (this==WHITE)
                return "białe";
            else if (this==BLACK)
                return "czarne";
            else return "żadne";
        }
    }

    public enum Type {
        NONE(Color.NONE, false),
        WHITE(Color.WHITE, false),
        BLACK(Color.BLACK, false),
        WHITE_KING(Color.WHITE, true),
        BLACK_KING(Color.BLACK, true);
        public Color color;
        public boolean king;
        Type(Color c, boolean k) {
            color = c;
            king = k;
        }
    }

    public Pawn(Model model, Vector4f color) {
        pos = new Vector3f();
        this.color = new Vector4f(color);
        this.model = model;
        isKing = false;
        rotation = new Vector3f();
        alive = new Boolean[]{true};
    }

    boolean isAlive() {
        return alive[0];
    }

    AnimationThread makeKing() {
        isKing = true;
        AnimationThread a = new AnimationThread(new Vec3Interp(pos, dest, new JumpInterp(1.f)), animation_time);
        a.with(new AnimationThread(new Vec3Lerp(rotation, new Vector3f(-(float)Math.PI, 0, 0)), animation_time));
        return a;
    }
    boolean isKing() {
        return isKing;
    }

    void setType(Type type) {
        this.type = type;
    }

    void set(int i, int j) {
        pos.set(0.5f+i, 0.0f, 0.5f+j);
    }

    AnimationThread move(int i, int j) {
        dest = new Vector3f(0.5f+i, 0.0f, 0.5f+j);
        AnimationThread a = new AnimationThread(new Vec3Interp(pos, dest, new JumpInterp(0.5f)), animation_time);
        return a;
    }
    AnimationThread fade() {
        AnimationThread a = new AnimationThread(new Vec4Lerp(color, new Vector4f(color.x, color.y, color.z, 0.0f)), animation_time);
        a.then(new AnimationThread(new SetVal<>(alive, false), 0.f));
        return a;
    }
    void render(Renderer renderer) {
        Vector3f dr = new Vector3f(pos).add(model.origin);
        renderer.getShader().pass("u_col", color);
        renderer.translate(dr);
        renderer.rotate(rotation.x, rotationAxis);

        renderer.renderModel(model);
        renderer.rotate(-rotation.x, rotationAxis);
        renderer.translate(dr.mul(-1));

    }

}
