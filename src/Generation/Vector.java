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

    public static Vector norm(Vector a){
        double dx = a.xChange / (Math.sqrt(a.xChange * a.xChange + a.yChange * a.yChange));
        double dy = a.yChange / (Math.sqrt(a.xChange * a.xChange + a.yChange * a.yChange));
        return new Vector(dx, dy);
    }

    public double getXChange() {
        return xChange;
    }

    public double getYChange() {
        return yChange;
    }
}
