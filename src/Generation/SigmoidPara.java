package Generation;

public class SigmoidPara {
    private final double c1;
    private final double c2;
    private final double zoom;

    public SigmoidPara(double c1, double c2, double zoom) {
        this.c1 = c1;
        this.c2 = c2;
        this.zoom = zoom;
    }

    public SigmoidPara() {
        this(0.5, 28, 4);
    }

    public double getC1() {
        return c1;
    }

    public double getC2() {
        return c2;
    }

    public double getZoom() {
        return zoom;
    }
}
