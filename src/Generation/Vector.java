package Generation;

public class Vector {
    private double xChange;
    private double yChange;

    public Vector(double xChange, double yChange) {
        this.xChange = xChange;
        this.yChange = yChange;
    }

    public static double dot(Vector a, Vector b) {
        return a.xChange * b.xChange + a.yChange * b.yChange;
    }

    public double getXChange() {
        return xChange;
    }

    public double getYChange() {
        return yChange;
    }
}
