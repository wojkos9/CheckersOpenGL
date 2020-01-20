package graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    public Vector3f position, lookat;
    private Matrix4f V;
    private float cam_distance;
    private Vector2f cam_angles;
    public Camera() {
        position = new Vector3f();
        lookat = new Vector3f();
        V = new Matrix4f();
        cam_distance = 5;
        cam_angles = new Vector2f((float)-Math.PI/2, (float)-Math.PI/4);
        update();
    }
    public void moveCameraDistance(float dr) {
        float new_distance = cam_distance+dr;
        if (new_distance > 0 && new_distance <= 10) {
            cam_distance = new_distance;
            update();
        }
    }
    public void update() {
        position.set(0, 0, cam_distance)
                .rotateAxis(cam_angles.y, 1, 0, 0)
                .rotateAxis(cam_angles.x, 0, 1, 0).add(lookat);
        V.setLookAt(position, lookat, new Vector3f(0, 1, 0));
    }
    public Matrix4f getView() {
        return V;
    }
    public void moveCameraAngle(float a1, float a2) {
        float angle_y = cam_angles.y + a2;
        float max_angle = (float)Math.PI * 0.45f;
        if (Math.abs(cam_angles.y + a2) > max_angle)
            angle_y = Math.signum(angle_y)*max_angle;
        cam_angles.set(cam_angles.x+a1, angle_y);
        update();
    }
}
