package game;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Vector;

public class AnimationThread extends Thread {
    float framerate = 120.f;
    private Vector<AnimationThread> simul = new Vector<>();
    private AnimationThread next = null;
    private Interpolation interp;
    private float time;
    public AnimationThread(Interpolation interp, float time) {
        this.interp = interp;
        this.time = time;
    }

    public AnimationThread then(AnimationThread next) {
        this.next = next;
        return this;
    }
    public AnimationThread with(AnimationThread a) {
        simul.add(a);
        return this;
    }

    @Override
    public void run() {
        super.run();
        for (AnimationThread a : simul) {
            a.start();
        }
        float t = 0;
        float step = 1.f/(time*framerate);
        interp.init();
        while (interp.update(t)) {
            t += step;
            try {
                sleep((long) (1000/framerate));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (AnimationThread a : simul) {
            try {
                a.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (next != null) {
            next.start();
        }
    }
}

abstract class Interpolation<T extends Object> {
    protected T interpolee, target;
    protected Interpolation() {}
    public Interpolation(T interpolee, T target) {
        this.interpolee = interpolee;
        this.target = target;
    }
    public void init(){}
    public abstract boolean update(float t);
}

abstract class Vec3InterpFun {
    abstract void interpolate(Vector3f v, Vector3f w, float t, Vector3f res);
}

class JumpInterp extends Vec3InterpFun {
    private float height;
    public JumpInterp(float height) {
        this.height = height;
    }
    @Override
    void interpolate(Vector3f v, Vector3f w, float t, Vector3f res) {
        Vector3f tmp = v.lerp(w, t, new Vector3f());
        res.set(tmp.x,tmp.y - 4*height*t*(t-1), tmp.z);
    }
}

class Vec3Interp extends Interpolation<Vector3f> {
    private Vector3f v0;
    private Vec3InterpFun fun;
    public Vec3Interp(Vector3f interpolee, Vector3f target, Vec3InterpFun fun) {
        super(interpolee, target);
        this.fun = fun;
    }
    public void init() {
        v0 = new Vector3f(interpolee);
    }
    @Override
    public boolean update(float t) {
        if (t < 1.0f) {
            fun.interpolate(v0, target, t, interpolee);
            return true;
        }
        interpolee.set(target);
        return false;
    }
}

class Vec3Lerp extends Interpolation<Vector3f> {
    private Vector3f v0;
    public Vec3Lerp(Vector3f interpolee, Vector3f target) {
        super(interpolee, target);
        v0 = new Vector3f(interpolee);
    }
    @Override
    public boolean update(float t) {
        if (t < 1.0f) {
            v0.lerp(target, t, interpolee);
            return true;
        }
        interpolee.set(target);
        return false;
    }
}
class Vec4Lerp extends Interpolation<Vector4f> {
    private Vector4f v0;
    public Vec4Lerp(Vector4f interpolee, Vector4f target) {
        super(interpolee, target);
        v0 = new Vector4f(interpolee);
    }
    @Override
    public boolean update(float t) {
        if (t < 1.0f) {
            v0.lerp(target, t, interpolee);
            return true;
        }
        interpolee.set(target);
        return false;
    }
}

class SetVal<T> extends Interpolation<T[]> {
    private T target_val;
    public SetVal(T[] interpolee, T target) {
        this.interpolee = interpolee;
        this.target_val = target;
    }
    @Override
    public boolean update(float t) {
        interpolee[0] = target_val;
        return false;
    }
}
