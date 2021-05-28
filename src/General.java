//Optimize the randomization problem: Make the web know which way has the great efficiency.
//Add parameter "growTendency" to each node.
//Try to make the decrease of priority increase with time.
//Try to let the die of seeds decrease the priority of relative links.
//Try to make the web itself take more consideration with the highest overall benefit.
//The overall benefit could be customized by users. "Live Longer" & "Eats more"
//Record the overall benefit that the web gets for each cycle.
//Record the centroid of the web for each cycle.
//Let the web learn how to :
//  1. change the priority function.
//  2. change the growing sequence with different weight.
//  3. change the grow tendency function.
//Two bugs: 1.Why the seed could reach eternity
//          2.Why the seed won't grow for such long time.
/**
 * This is a class trying to simulate the behavior of slime bacteria.
 * @version 0.0.2 Version: Low-Intelligence-Seed
 * Basic structure of the web. Try to find as much target as possible
 * and live as many cycles as possible. Optimized behavior with the experience
 * gained by previous generations.
 * @author Yukun Song 2021.5.13
 */
import Generation.Result;
import Generation.Vector;
import Generation.Web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class General {
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory.txt");
    private static ArrayList<Result> resultList;
    private static int highestCycle = 0;
    private static double highestEnergyGain = 0;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        resultList = new ArrayList<>();
        ArrayList<Vector> initialMovement = new ArrayList<>();
        //PrintWriter outputSeed = new PrintWriter(OUTPUTSEED);
        //outputSeed.println("Generation 0 :");
        Result initGeneration = newGeneration(initialMovement, null);
        resultList.add(initGeneration);
        highestCycle = initGeneration.getCycleAmount();
        highestEnergyGain = initGeneration.getTotalEnergyGained();
        int generation = 1;
        int total = 500;
        //System.out.println(System.currentTimeMillis());
        while (generation < total) {
            //outputSeed.println("Generation " + generation + " :");
            ArrayList<Vector> optimizedMovements = optimization(resultList);
            Result curr = newGeneration(optimizedMovements, null);
            resultList.add(curr);
            if (curr.getCycleAmount() > highestCycle) {
                highestCycle = curr.getCycleAmount();
            }
            if (curr.getTotalEnergyGained() > highestEnergyGain) {
                highestEnergyGain = curr.getTotalEnergyGained();
            }
            generation++;
        }
        double a = 0;
        for (Result result : resultList) {
            a += result.getCycleAmount();
        }
        a /= resultList.size();
        System.out.println(a);
        //System.out.println(System.currentTimeMillis());
        //outputSeed.close();
    }

    private static double calculateScore(int cycle, double energyGains) {
        //TODO(Think about a valid function for scoring the generation).
        return (cycle / highestCycle) * (energyGains / highestEnergyGain);
    }

    private static ArrayList<Vector> optimization(ArrayList<Result> resultList) {
        //TODO(Use statistical method to optimize the movements).
        ArrayList<Double> scores = new ArrayList<>();
        if (resultList.size() == 1) {
            return new ArrayList<Vector>();
        }
        for (Result result : resultList) {
            scores.add(calculateScore(result.getCycleAmount(), result.getTotalEnergyGained()));
        }
        double mean = 0;
        for (Double score: scores) {
            mean += score;
        }
        mean /= scores.size();
        double sigma = 0;
        for (Double score: scores) {
            sigma += (score - mean) * (score - mean);
        }
        sigma /= scores.size();
        sigma = Math.sqrt(sigma);
        //System.out.println(sigma);
        Map<Result, Double> sigmaMap = new LinkedHashMap<>();
        for (int i = 0; i < resultList.size(); i++) {
            double result = (scores.get(i) - mean) / sigma;
            sigmaMap.put(resultList.get(i), result);
        }
        //System.out.println(sigmaMap);
        return optimizationHelper(sigmaMap);
    }

    private static ArrayList<Vector> optimizationHelper(Map<Result, Double> sigmaMap) {
        ArrayList<Vector> result = new ArrayList<>();
        //System.out.println(sigmaMap.entrySet());
        for (int i = 0; i < highestCycle; i++) {
            Vector curr = new Vector(0, 0);
            double sigmaSum = 0;
            for (Map.Entry<Result, Double> entry: sigmaMap.entrySet()) {
                //TODO(The efficiency of learning maybe depends on this threshold value.)
                if (entry.getKey().getCycleAmount() > i && entry.getValue() >= 0.3 ) {
                    Vector movement = new Vector(
                            entry.getKey().getCentroidsX().get(i + 1) -
                                    entry.getKey().getCentroidsX().get(i),
                            entry.getKey().getCentroidsY().get(i + 1) -
                                    entry.getKey().getCentroidsY().get(i));
                    //movement = Vector.norm(movement);
                    movement.multiplyBy(entry.getValue());
                    sigmaSum += entry.getValue();
                    curr = Vector.add(curr, movement);
                }
            }
            curr.divideBy(sigmaSum);
            curr = Vector.norm(curr);
            //System.out.println(curr);
            result.add(curr);
        }
        //System.out.println();
        return result;
    }

    /**
     *
     * @param optimizedMovements the sequence of movements optimized by previous methods.
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
            if (printWriter != null) {
                printWriter.println();
                printWriter.println("Cycle " + web.getCycle() + " : ");
                printWriter.println();
                if (web.getCycle() % 1 == 0) {
                    web.printSeeds(printWriter);
                }
            }
            if (web.getCycle() >= 150) {
                throw new IllegalStateException("The eternity problem exists.");
                //break;
            }
        }
        if (printWriter != null) {
            printWriter.println("========================");
        }
        Result result = new Result(web.getCycle(), initEnergy - web.countTargetEnergy(),
                web.getCentroidsX(), web.getCentroidsY());
        return result;
    }
}
