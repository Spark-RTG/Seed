import Generation.Result;
import Generation.Vector;
import Generation.Web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class General {
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory.txt");
    private static double averageScore;
    private static final double MAX_SCORE = 100;
    private static ArrayList<Result> resultList;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        resultList = new ArrayList<>();
        ArrayList<Integer> generationAmount = new ArrayList<>();
        ArrayList<Double> energyAmount = new ArrayList<>();
        PrintWriter outputSeed = new PrintWriter(OUTPUTSEED);
        outputSeed.println("Generation 0 :");
        Result initGeneration = newGeneration(-1, -1, outputSeed);
        resultList.add(initGeneration);
//        generationAmount.add(initGeneration.getGenerationAmount());
//        energyAmount.add(initGeneration.getTotalEnergyGained());
        int times = 4;
        while (times > 0) {
            outputSeed.println("Generation " + (5 - times) + " :");
            ArrayList<Vector> optimizedMovements = new ArrayList<>();
//            generationAmount.add(curr.getGenerationAmount());
//            energyAmount.add(curr.getTotalEnergyGained());
            optimization(optimizedMovements, resultList);
            Result curr = newGeneration(optimizedMovements, outputSeed);
            resultList.add(curr);
//            double tempGeneration = 0;
//            double tempEnergy = 0;
//            for (Integer generation : generationAmount) {
//                tempGeneration += generation;
//            }
//            tempGeneration /= generationAmount.size();
//            averageCycle = tempGeneration;
//            for (Double energy : energyAmount) {
//                tempEnergy += energy;
//            }
//            tempEnergy /= energyAmount.size();
//            averageEnergyGains = tempEnergy;
            --times;
        }
        outputSeed.close();
    }

    private static double calculateScore(int cycle, double energyGains) {
        //TODO(Think about a valid function for scoring the generation).
    }

    private static void optimization(ArrayList<Vector> optimizedMovements,
                                     ArrayList<Result> resultList) {
        //TODO(Use statistical method to optimize the movements).
    }

    /**
     *
     * @param
     * @param
     * @param printWriter the printWriter used to print this generation
     * @return a double array : index 0 stands for the generation number
     *                        : index 1 stands for the total energy gain in this generation
     * @throws IOException normally won't throw
     * @throws ClassNotFoundException normally won't throw
     */
    private static Result newGeneration(ArrayList<Vector> optimizedMovements, PrintWriter printWriter)
            throws IOException, ClassNotFoundException {
        Web web = new Web(optimizedMovements);
        double initEnergy = web.countTargetEnergy();
        while (web.isAlive()) {
            web.refresh();
            printWriter.println();
            printWriter.println("Cycle " + web.getCycle() + " : ");
            printWriter.println();
            if (web.getCycle() % 1 == 0) {
                web.printSeeds(printWriter);
            }
        }
        printWriter.println("========================");
        Result result = new Result(web.getCycle(), initEnergy - web.countTargetEnergy(),
                web.getCentroidsX(), web.getCentroidsY());
        return result;
    }
}
