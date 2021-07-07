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
import Generation.SigmoidPara;
import Generation.Vector;
import Generation.Web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class General {
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory.txt");
    private static ArrayList<Result> resultList;
    private static int highestCycle = 0;
    private static double highestEnergyGain = 0;
    private static SigmoidPara sigmoidPara;
    private static ArrayList<SigmoidPara> sigmoidParas;
    private static final int CENTROID = 0;
    private static final int CENTER = 1;
    private static final int DONTOPTIMIZE = 2;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        resultList = new ArrayList<>();
        sigmoidParas = new ArrayList<>();
        sigmoidPara = new SigmoidPara();
        ArrayList<Vector> initialMovement = new ArrayList<>();

        PrintWriter outputSeed = null;
        outputSeed = new PrintWriter(OUTPUTSEED);
        if (outputSeed != null) {
            outputSeed.println("Generation 0 :");
        }
        Result initGeneration = newGeneration(initialMovement, sigmoidPara, new ArrayList<>(),null);
        resultList.add(initGeneration);
        sigmoidParas.add(sigmoidPara);
        highestCycle = initGeneration.getCycleAmount();
        highestEnergyGain = initGeneration.getTotalEnergyGained();
        int generation = 1;
        int total = 10;
        //System.out.println(System.currentTimeMillis());
        while (generation < total) {
            if (outputSeed != null) {
                outputSeed.println("Generation " + generation + " :");
            }
            ArrayList<Vector> optimizedMovements = optimization(resultList, DONTOPTIMIZE);
            //sigmoidPara = optimizePara(sigmoidParas);
            ArrayList<Vector> optimizedCenters = optimization(resultList, CENTER);
            Result curr = newGeneration(optimizedMovements, sigmoidPara, optimizedCenters,outputSeed);
            if (curr == null) {
                continue;
            }
            resultList.add(curr);
            sigmoidParas.add(sigmoidPara);
            if (curr.getCycleAmount() > highestCycle) {
                highestCycle = curr.getCycleAmount();
            }
            if (curr.getTotalEnergyGained() > highestEnergyGain) {
                highestEnergyGain = curr.getTotalEnergyGained();
            }
            generation++;
        }
        //System.out.println(highestCycle);
        double a = 0;
        for (Result result : resultList) {
            a += calculateScore(result.getCycleAmount(), result.getTotalEnergyGained());
        }
        a /= resultList.size();
        System.out.println(a);
        //System.out.println(System.currentTimeMillis());
        if (outputSeed != null) {
            outputSeed.close();
        }
    }

    private static double calculateScore(int cycle, double energyGains) {
        //TODO(Think about a valid function for scoring the generation).
        //return ((double) cycle / highestCycle) * ((double) energyGains / highestEnergyGain);
        return ((double) cycle / highestCycle);
        //return (cycle) * (energyGains);
    }

    private static SigmoidPara optimizePara(ArrayList<SigmoidPara> sigmoidParas) {
        //TODO(Use statistical method to optimize the movements).
        ArrayList<Double> scores = new ArrayList<>();
        if (sigmoidParas.size() == 1) {
            return new SigmoidPara();
        }
        for (int i = 0; i < resultList.size(); i++) {
            scores.add(calculateScore(resultList.get(i).getCycleAmount(), resultList.get(i).getTotalEnergyGained()));
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
        Map<SigmoidPara, Double> sigmaMap = new LinkedHashMap<>();
        for (int i = 0; i < sigmoidParas.size(); i++) {
            double result = (scores.get(i) - mean) / sigma;
            sigmaMap.put(sigmoidParas.get(i), result);
        }
        return optimizeParaHelper(sigmaMap);
    }

    private static SigmoidPara optimizeParaHelper(Map<SigmoidPara, Double> sigmoidParaDoubleMap) {
        double c1 = 0.5;
        double c2 = 0;
        double zoom = 4;
        int size = 0;
        for (Map.Entry<SigmoidPara, Double> sigmoidParaDoubleEntry : sigmoidParaDoubleMap.entrySet()) {
            if (sigmoidParaDoubleEntry.getValue() >= 0) {
                c2 += sigmoidParaDoubleEntry.getKey().getC2() * sigmoidParaDoubleEntry.getValue();
                size++;
            }
        }
        //System.out.println(sigmaSum);
        c2 /= size;
        return new SigmoidPara(c1, c2, zoom);
    }

    private static ArrayList<Vector> optimization(ArrayList<Result> rList, int type) {
        //ArrayList<Double> scores = new ArrayList<>();
        if (rList.size() == 1) {
            return new ArrayList<Vector>();
        }
        Map<Double, Integer> indicesMap = new HashMap<>();
        PriorityQueue<Double> heap = new PriorityQueue<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                if (o2 - o1 > 0) {
                    return 1;
                } else if (o2 - o1 < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < rList.size(); i++) {
            double temp = calculateScore(rList.get(i).getCycleAmount(), rList.get(i).getTotalEnergyGained());
            indicesMap.put(temp, i);
            heap.add(temp);
            //System.out.println(temp);
            //scores.add(temp);
        }
        ArrayList<Result> chosenResults = new ArrayList<>();
        int maxCycle = 0;
        for (int i = 0; i < 5 && i < heap.size(); i++) {
            Result temp = rList.get(indicesMap.get(heap.poll()));
            chosenResults.add(temp);
            if (temp.getCycleAmount() > maxCycle) {
                maxCycle = temp.getCycleAmount();
            }
        }
        ArrayList<Vector> resultCentroids = new ArrayList<>();
        ArrayList<Vector> resultCenters = new ArrayList<>();
        for (int i = 0; i < maxCycle - 1; i++) {
            Vector currentCentroidMove = new Vector(0, 0);
            Vector currentCenterMove = new Vector(0, 0);
            int effectiveCount = 0;
            for (int j = 0; j < chosenResults.size(); j++) {
                if (chosenResults.get(j).getCycleAmount() < i + 2) {
                    continue;
                }
                ArrayList<Double> xList = chosenResults.get(j).getCentroidsX();
                ArrayList<Double> yList = chosenResults.get(j).getCentroidsY();
                ArrayList<Vector> cList = chosenResults.get(j).getCenterList();
                currentCentroidMove = Vector.add(currentCentroidMove,
                        new Vector(xList.get(i + 1) - xList.get(i),
                        yList.get(i + 1) - yList.get(i)));
                currentCenterMove = Vector.add(currentCenterMove, Vector.minus(cList.get(i + 1), cList.get(i)));
                effectiveCount++;
            }
            currentCentroidMove.divideBy(effectiveCount);
            currentCenterMove.divideBy(effectiveCount);
            resultCentroids.add(currentCentroidMove);
            resultCenters.add(currentCenterMove);
        }
        if (type == CENTROID) {
            return resultCentroids;
        } else if (type == CENTER){
            return resultCenters;
        } else {
            return new ArrayList<>();
        }
//        double mean = 0;
//        for (Double score: scores) {
//            mean += score;
//        }
//        mean /= scores.size();
//        double sigma = 0;
//        for (Double score: scores) {
//            sigma += (score - mean) * (score - mean);
//        }
//        sigma /= scores.size();
//        sigma = Math.sqrt(sigma);
//        //System.out.println(sigma);
//        Map<Result, Double> sigmaMap = new LinkedHashMap<>();
//        for (int i = 0; i < resultList.size(); i++) {
//            double result = (scores.get(i) - mean) / sigma;
//            sigmaMap.put(resultList.get(i), result);
//        }
//        //System.out.println(sigmaMap);
//        return optimizationHelper(sigmaMap);
    }

    private static ArrayList<Vector> optimizationHelper(Map<Result, Double> sigmaMap) {
        ArrayList<Vector> result = new ArrayList<>();
        //System.out.println(sigmaMap.entrySet());
        int size = 0;
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
                    size++;
                    //movement = Vector.norm(movement);
                    movement.multiplyBy(entry.getValue());
                    sigmaSum += entry.getValue();
                    curr = Vector.add(curr, movement);
                }
            }
            curr.divideBy(size);
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
    private static Result newGeneration(ArrayList<Vector> optimizedMovements, SigmoidPara sigmoidPara,
                                        ArrayList<Vector> optimizedCenterMovements,
                                        PrintWriter printWriter)
            throws IOException, ClassNotFoundException {
        Web web = new Web(optimizedMovements, optimizedCenterMovements);
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
                return null;
                //throw new IllegalStateException("The eternity problem exists.");
                //break;
            }
        }
        if (printWriter != null) {
            printWriter.println("========================");
        }
        Result result = new Result(web.getCycle(), initEnergy - web.countTargetEnergy(),
                web.getCentroidsX(), web.getCentroidsY(), web.getCenterList());
        return result;
    }
}
