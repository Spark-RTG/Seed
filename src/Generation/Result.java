package Generation;

import java.util.ArrayList;

public class Result {
    private final int generationAmount;
    private final double totalEnergyGained;

    public Result(int generationAmount, double totalEnergyGained,
                  ArrayList<Double> centroidsX, ArrayList<Double> centroidsY) {
        this.generationAmount = generationAmount;
        this.totalEnergyGained = totalEnergyGained;
    }

    public int getGenerationAmount() {
        return generationAmount;
    }

    public double getTotalEnergyGained() {
        return totalEnergyGained;
    }
}
