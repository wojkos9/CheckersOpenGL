package utils;

public class MathUtils {
    public static double CosineInterpolate(double y1, double y2, double mu)
    {
        double mu2  = (1-Math.cos(mu*Math.PI))/2;
        return (y1*(1-mu2)+y2*mu2);
    }
}
