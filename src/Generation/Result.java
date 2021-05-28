package Generation;

import java.util.ArrayList;

public class Result {
    private final int cycleAmount;
    private final double totalEnergyGained;
//    private final double c1;
//    private final double c2;
//    private final double zoom;
    private final ArrayList<Double> centroidsX;
    private final ArrayList<Double> centroidsY;

    public Result(int cycleAmount, double totalEnergyGained,
                  ArrayList<Double> centroidsX, ArrayList<Double> centroidsY) {
        this.cycleAmount = cycleAmount;
        this.totalEnergyGained = totalEnergyGained;
        this.centroidsX = centroidsX;
        this.centroidsY = centroidsY;
//        this.c1 = 0.5;
//        this.c2 = 28;
//        this.zoom = 4;
    }

//    public Result(int cycleAmount, double totalEnergyGained, double c1, double c2, double zoom) {
//        this.cycleAmount = cycleAmount;
//        this.totalEnergyGained = totalEnergyGained;
//        this.c1 = c1;
//        this.c2 = c2;
//        this.zoom = zoom;
//        this.centroidsX = new ArrayList<>();
//        this.centroidsY = new ArrayList<>();
//    }

    public int getCycleAmount() {
        return cycleAmount;
    }

    public double getTotalEnergyGained() {
        return totalEnergyGained;
    }

    @Override
    public String toString() {
        return "Result{" +
                "cycleAmount=" + cycleAmount +
                ", totalEnergyGained=" + totalEnergyGained +
//                ", centroidsX=" + centroidsX +
//                ", centroidsY=" + centroidsY +
                '}';
    }

    public ArrayList<Double> getCentroidsX() {
        return centroidsX;
    }

    public ArrayList<Double> getCentroidsY() {
        return centroidsY;
    }
}
