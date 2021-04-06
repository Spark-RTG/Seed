import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class General {
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory");

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<Double> generationAmount = new ArrayList<>();
        ArrayList<Double> energyAmount = new ArrayList<>();
        PrintWriter outputSeed = new PrintWriter(OUTPUTSEED);
        double[] initGeneration = newGeneration(-1, -1, outputSeed);
        double averageCycle = initGeneration[0];
        double averageEnergyGains = initGeneration[1];
        generationAmount.add(averageCycle);
        energyAmount.add(averageEnergyGains);
        int times = 4;
        while (times > 0) {
            outputSeed.println("Generation " + (5 - times) + " :");
            double[] curr = newGeneration(averageCycle, averageEnergyGains, outputSeed);
            generationAmount.add(curr[0]);
            energyAmount.add(curr[1]);
            double tempGeneration = 0;
            double tempEnergy = 0;
            for (Double generation : generationAmount) {
                tempGeneration += generation;
            }
            tempGeneration /= generationAmount.size();
            averageCycle = tempGeneration;
            for (Double energy : energyAmount) {
                tempEnergy += energy;
            }
            tempEnergy /= energyAmount.size();
            averageEnergyGains = tempEnergy;
            --times;
        }
        outputSeed.close();
    }

    /**
     *
     * @param averageCycle the average number of cycles that previous generation lived
     * @param averageEnergyGains the average energy gains in the previous generations
     * @param printWriter the printWriter used to print this generation
     * @return a double array : index 0 stands for the generation number
     *                        : index 1 stands for the total energy gain in this generation
     * @throws IOException normally won't throw
     * @throws ClassNotFoundException normally won't throw
     */
    private static double[] newGeneration(double averageCycle, double averageEnergyGains, PrintWriter printWriter)
            throws IOException, ClassNotFoundException {
        double[] result = new double[2];
        Web web = new Web();
        //web.printMap();
        double initEnergy = web.countTargetEnergy();
        boolean alive = true;
        while (alive) {
            alive = false;
            web.refresh();
            for (int i = 0; i < web.getGeneralWeb().size(); ++i) {
                if (!(web.getGeneralWeb().get(i) instanceof Target)) {
                    alive = true;
                    break;
                }
            }
            printWriter.println();
            printWriter.println("Cycle " + web.getGeneration() + " : ");
            printWriter.println();
            if (web.getGeneration() % 1 == 0) {
                web.printSeeds(printWriter);
            }
        }
        printWriter.println("========================");
        result[0] = web.getGeneration();
        result[1] = initEnergy - web.countTargetEnergy();
        return result;
    }
}
