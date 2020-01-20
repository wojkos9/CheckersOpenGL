package game;

import graphics.Model;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PawnFactory {
    private static class ModelParameters {
        String filename;
        int i0;
        Vector3f origin;
        public ModelParameters(String f, int i, Vector3f o) {
            filename = f;
            i0 = i;
            origin = o;
        }
        public Model makeModel() {
            Model m = new Model();
            m.load(filename, i0);
            m.origin.set(origin);
            return m;
        }
    }
    private static Model model_white, model_black;
    private static ModelParameters[] modelsParameters = new ModelParameters[]{
            new ModelParameters("assets/models/pawn3.obj", 1, new Vector3f(0.f, 0.15f, 0.f)),
            new ModelParameters("assets/models/pawn_chess.obj", 1, new Vector3f(0.f, 0.71f, 0.f)),
            new ModelParameters("assets/models/pawn_square.obj", 1, new Vector3f(0.f, 0.51f, 0.f))

    };
    private static Vector4f black = new Vector4f(0.2f, 0.2f, 0.2f, 1.f),
                            white = new Vector4f(0.8f, 0.8f, 0.8f, 1.f);
    public static Pawn create(Pawn.Type type) {
        Pawn p = type == Pawn.Type.BLACK ? new Pawn(model_black, black) : new Pawn(model_white, white);
        p.setType(type);
        return p;
    }
    public static void loadModels(int m1, int m2) {
        model_white = modelsParameters[m1].makeModel();
        if (m2 == m1)
            model_black = model_white;
        else
            model_black = modelsParameters[m2].makeModel();
    }
}
