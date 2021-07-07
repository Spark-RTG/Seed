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
        if (a.xChange == 0 && a.yChange == 0) {
            return new Vector(0, 0);
        }
        double dx = a.xChange / (Math.sqrt(a.xChange * a.xChange + a.yChange * a.yChange));
        double dy = a.yChange / (Math.sqrt(a.xChange * a.xChange + a.yChange * a.yChange));
        return new Vector(dx, dy);
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.xChange + b.xChange, a.yChange + b.yChange);
    }

    /**
     * Minus a by b
     * @param a The minuend vector
     * @param b The subtrahend vector
     * @return a - b
     */
    public static Vector minus(Vector a, Vector b) {
        return new Vector(a.xChange - b.xChange, a.yChange - b.yChange);
    }

    public void multiplyBy(double multiplier) {
        xChange *= multiplier;
        yChange *= multiplier;
    }

    public void divideBy(double divider) {
        xChange /= divider;
        yChange /= divider;
    }

    public double getXChange() {
        return xChange;
    }

    public double getYChange() {
        return yChange;
    }

    public void setxChange(double xChange) {
        this.xChange = xChange;
    }

    public void setyChange(double yChange) {
        this.yChange = yChange;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "xChange=" + xChange +
                ", yChange=" + yChange +
                '}';
    }
}
