package game;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import font.FontRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import utils.Pair;

import java.util.*;
import java.util.concurrent.Semaphore;


public class Test {
    Vector4f tv4(Vector3f v) {
        return new Vector4f(v.x, v.y, v.z, 1.0f);
    }
    void test1() {
        Vector4f point = new Vector4f(0, 0.3f, 0.0f, 1);
        Vector3f cam = new Vector3f(0, 1.0f, 2f);
        Matrix4f V = new Matrix4f().setLookAt(cam, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        Matrix4f P = new Matrix4f().perspective((float)Math.toRadians(90), 1.f, 0.1f, 1.f);

        Matrix4f VP = new Matrix4f(P).mul(V);
        //Matrix4f PV = new Matrix4f(P).mul(V);
        Vector4f c = new Vector4f(point).mul(VP);
        System.out.println(VP);
        Vector4f d = new Vector4f(c).div(c.w);
        System.out.println(c);
        System.out.println(d.x + " " + d.y + " " + d.z);

        Matrix4f iVP = new Matrix4f(VP).invert();

        Vector4f p = new Vector4f(d.x, d.y, 1.0f, 1.f).mul(iVP);
        p.div(p.w);
        System.out.println(p+"");

        Vector4f pc = new Vector4f(p).sub(cam.x, cam.y, cam.z, 0.0f);

        float l = -cam.z/pc.z;

        float x = cam.x + l * pc.x;
        float y = cam.y + l * pc.y;
        System.out.println(x+" "+ y);
    }
    void test2() {
        Vector4f point = new Vector4f(1.f, 0.0f, 0.f, 1);
        //Vector3f cam = new Vector3f(0, 0, 1);
        //Matrix4f V = new Matrix4f().setLookAt(cam, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        Matrix4f P = new Matrix4f().perspective((float)Math.toRadians(90), 1.f, 0.1f, 1.f);
        Vector4f p = point.mul(P);
        p.div(p.w);
        System.out.println(p);
    }
    class Ab {
        public String par;
        public Ab(String p){
            par = p;
        }
    }
    void test3() {
        Map<Pair, Pair> map = new HashMap<>();
        Pair a = new Pair(1, 2);
        Pair b = new Pair(3, 4);
        map.put(new Pair(1, 1), a);
        map.put(new Pair(2, 2), b);
        for (Pair val : map.values()) {
            System.out.println(val.i + " " + val.j);
        }
        Pair p = map.get(new Pair(1, 1));
        System.out.println(p.i + " " + p.j);
        for (Pair val : map.values()) {
            System.out.println(val.i + " " + val.j);
        }
    }
    Stack<Thread> threads;
    class XThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(""+new Random().nextInt(100));
        }
    };
    Thread waiter = new Thread(){
        @Override
        public void run() {
            super.run();
            while (!threads.isEmpty()) {
                Thread a = threads.pop();
                a.start();
                try {
                    a.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    void push(Thread t) {
        threads.push(t);
        if (!waiter.isAlive())
            waiter.start();
    }
    void test4() {
        Semaphore sem = new Semaphore(1);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("B");
            }
        }.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("A");
        sem.release();
    }
    public enum Color {
        WHITE, BLACK, NONE
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

    void test5() {
        Type a = Type.WHITE;
        a.king = true;
        System.out.println((a == Type.WHITE_KING) + "");

    }

    public Test() {

    }
    void run() {
        test5();
    }
    public static void main(String args[]) {
        new Test().run();
    }
}
