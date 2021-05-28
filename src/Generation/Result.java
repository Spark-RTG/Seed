package Generation;

import java.util.ArrayList;

public class Result {
    private final int cycleAmount;
    private final double totalEnergyGained;
    private final ArrayList<Double> centroidsX;
    private final ArrayList<Double> centroidsY;

    public Result(int cycleAmount, double totalEnergyGained,
                  ArrayList<Double> centroidsX, ArrayList<Double> centroidsY) {
        this.cycleAmount = cycleAmount;
        this.totalEnergyGained = totalEnergyGained;
        this.centroidsX = centroidsX;
        this.centroidsY = centroidsY;
    }

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
