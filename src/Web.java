//Optimize the randomization problem: Make the web know which way has the great efficiency.
//Add parameter "growTendency" to each node.
//Try to make the decrease of priority increase with time.
//Try to let the die of seeds decrease the priority of relative links.
//Try to make the web itself take more consideration with the highest overall benefit.
//  The overall benefit could be customized by users. "Live Longer" & "Eats more"
//Record the overall benefit that the web gets for each cycle.
//Record the centroid of the web for each cycle.
//Let the web learn how to :
//  1. change the priority function.
//  2. change the growing sequence with different weight.
//  3. change the grow tendency function.
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is a class try to simulate the behavior of slime bacteria.
 * @version 0.0.1 Version: XiaZhang
 * Basic structure of the web. Try to find as much target as possible
 * and live as many generations as possible.
 * @author Yukun Song 2021.3.22
 */
public class Web {

    private ArrayList<Seed> generalWeb;
    private ArrayList<Seed> generatedWeb;
    private Seed[][] matrixOfSeeds;
    private int[][] imageOfMap;
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory");
    private static final double INITIAL_ENERGY = 1500;
    private int generation;

    public Web(double initialEnergy) throws IOException, ClassNotFoundException {
        FileInputStream resource = new FileInputStream("OutputMatrix");
        ObjectInputStream in = new ObjectInputStream(resource);
        imageOfMap = (int[][]) in.readObject();
        matrixOfSeeds = new Seed[imageOfMap.length][imageOfMap[0].length];
        generalWeb = new ArrayList<>();
        Seed initial = new Seed(matrixOfSeeds.length / 2, matrixOfSeeds[0].length / 2, initialEnergy);
        matrixOfSeeds[matrixOfSeeds.length / 2][matrixOfSeeds[0].length / 2] = initial;
        generalWeb.add(initial);
        generatedWeb = new ArrayList<>();
        generation = 0;
        fillTargets();
    }

    public Web() throws IOException, ClassNotFoundException {
        this(INITIAL_ENERGY);
    }

    private void fillTargets() {
        int timesOfEmission = 1;
        double energyGiven = 100;
        for (int i = 0; i < imageOfMap.length; i++) {
            for (int j = 0; j < imageOfMap[i].length; j++) {
                if (imageOfMap[i][j] == 2) {
                    Target temp = new Target(j, i, timesOfEmission, energyGiven);
                    matrixOfSeeds[i][j] = temp;
                    generalWeb.add(temp);
                }
            }
        }
    }

    public double countTargetEnergy() {
        double sum = 0;
        for (int i = 0; i < imageOfMap.length; i++) {
            for (int j = 0; j < imageOfMap[i].length; j++) {
                if (imageOfMap[i][j] == 2) {
                    Target target = (Target) matrixOfSeeds[i][j];
                    if (target != null) {
                        sum += target.getEnergy() * target.getTimesOfEnergyEmission();
                    }
                }
            }
        }
        return sum;
    }
    /**
     * Merge sort
     */
    private void sortSeeds() {
        generalWeb = sort(generalWeb);
    }

    private ArrayList<Seed> merge(ArrayList<Seed> seeds1, ArrayList<Seed> seeds2) {
        int firstPointer = 0;
        int secondPointer = 0;
        ArrayList<Seed> result = new ArrayList<>();
        while (firstPointer < seeds1.size() && secondPointer < seeds2.size()) {
            if (seeds1.get(firstPointer).compareTo(seeds2.get(secondPointer)) >= 0) {
                result.add(seeds1.get(firstPointer));
                ++firstPointer;
            } else {
                result.add(seeds2.get(secondPointer));
                ++secondPointer;
            }
        }
        while (firstPointer < seeds1.size()) {
            result.add(seeds1.get(firstPointer));
            ++firstPointer;
        }
        while (secondPointer < seeds2.size()) {
            result.add(seeds2.get(secondPointer));
            ++secondPointer;
        }
        return result;
    }

    private ArrayList<Seed> sort(ArrayList<Seed> seeds) {
        if (seeds.size() > 1) {
            int m = seeds.size() / 2;
            ArrayList<Seed> leftSeeds = new ArrayList<>();
            ArrayList<Seed> rightSeeds = new ArrayList<>();
            for (int i = 0; i < m; ++i) {
                leftSeeds.add(seeds.get(i));
            }
            for (int i = m; i < seeds.size(); ++i) {
                rightSeeds.add(seeds.get(i));
            }
            leftSeeds = sort(leftSeeds);
            rightSeeds = sort(rightSeeds);
            return merge(leftSeeds, rightSeeds);
        } else {
            return seeds;
        }
    }

    /**
     * Refresh the web by one cycle.
     * @return the amount of energy gained in this cycle.
     */
    public void refresh() {
        addNewBorn();
        sortSeeds();
        connectContiguous();
        updateLinkToTarget();
        updateSeedEnergy();
        updateLinkToSeed();
        ++generation;
    }

    /**
     * Add all new-born seeds to the web.
     */
    private void addNewBorn() {
        generalWeb.addAll(generatedWeb);
        for (int i = 0; i < generatedWeb.size(); ++i) {
            matrixOfSeeds[generatedWeb.get(i).getPositionY()][generatedWeb.get(i).getPositionX()] = generatedWeb.get(i);
        }
        generatedWeb.clear();
    }

    /**
     * Connect all unrelated but contiguous seeds.
     */
    private void connectContiguous() {
        for (int k = 0; k < generalWeb.size(); ++k) {
            Seed seed = generalWeb.get(k);
//            if (seed.getPositionY() == 7 && seed.getPositionX() == 7) {
//                System.out.println("7,7 Link status before getting related: ");
//                for(Link link : seed.getLinks()) {
//                    System.out.println(link);
//                }
//            }
            for (int i = 0; i < seed.getLinks().length; ++i) {
                if (seed.getLinks()[i] == null) {
                    try {
                        if (i == 0 && matrixOfSeeds[seed.getPositionY() - 1][seed.getPositionX()] != null) {
                            Link added = new Link(seed,
                                    matrixOfSeeds[seed.getPositionY() - 1][seed.getPositionX()], 0);
                            seed.setNorthLink(added);
                            matrixOfSeeds[seed.getPositionY() - 1][seed.getPositionX()].setSouthLink(added);
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) { }
                    try {
                        if (i == 1 && matrixOfSeeds[seed.getPositionY()][seed.getPositionX() + 1] != null) {
                            Link added = new Link(seed,
                                    matrixOfSeeds[seed.getPositionY()][seed.getPositionX() + 1], 0);
                            seed.setEastLink(added);
                            matrixOfSeeds[seed.getPositionY()][seed.getPositionX() + 1].setWestLink(added);
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) { }
                    try {
                        if (i == 2 && matrixOfSeeds[seed.getPositionY() + 1][seed.getPositionX()] != null) {
                            Link added = new Link(seed,
                                    matrixOfSeeds[seed.getPositionY() + 1][seed.getPositionX()], 0);
                            seed.setSouthLink(added);
                            matrixOfSeeds[seed.getPositionY() + 1][seed.getPositionX()].setNorthLink(added);
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) { }
                    try {
                        if (i == 3 && matrixOfSeeds[seed.getPositionY()][seed.getPositionX() - 1] != null) {
                            Link added = new Link(seed,
                                    matrixOfSeeds[seed.getPositionY()][seed.getPositionX() - 1], 0);
                            seed.setWestLink(added);
                            matrixOfSeeds[seed.getPositionY()][seed.getPositionX() - 1].setEastLink(added);
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) { }
                }
            }
        }
    }

    /**
     * Update all links connected to targets.
     */
    private void updateLinkToTarget() {
        for (int i = 0; i < generalWeb.size(); ++i) {
            Seed temp = generalWeb.get(i);
            if (temp instanceof Target) {
                try {
                    temp.updateLinks();
                } catch (IllegalStateException e) {
                    for (int j = 0; j < temp.getLinks().length; ++j) {
                        Link link = temp.getLinks()[j];
                        if (link != null) {
                            if (link.getTarget().equals(temp)) {
                                link.setTarget(null);
                            } else {
                                link.setPrevious(null);
                            }
                        }
                    }
                    generalWeb.remove(i);
                    matrixOfSeeds[temp.getPositionY()][temp.getPositionX()] = null;
                }
            }
        }
    }

    /**
     * Update the energy of seeds.
     */
    private void updateSeedEnergy() {
        for (int i = 0; i < generalWeb.size(); ++i) {
            Seed seed = generalWeb.get(i);
//            System.out.println(seed);
//            if (seed.getPositionY() == 7 && seed.getPositionX() == 7) {
//                System.out.println("7,7 Link status before update energy: ");
//                for(Link link : seed.getLinks()) {
//                    System.out.println(link);
//                }
//            }
            seed.updateEnergy();
            if (seed.isSurvive()) {
                growSeed(seed);
            } else {
                //System.out.println("Die: " + seed.getPositionY() + ", " + seed.getPositionX());
                for (int j = 0; j < seed.getLinks().length; ++j) {
                    Link link = seed.getLinks()[j];
                    if (link != null) {
                        if (link.getTarget() != null && link.getTarget().equals(seed)) {
                            link.setTarget(null);
                        } else if (link.getPrevious() != null && link.getPrevious().equals(seed)) {
                            link.setPrevious(null);
                        }
                    }
                }
                generalWeb.remove(seed);
                matrixOfSeeds[seed.getPositionY()][seed.getPositionX()] = null;
            }
        }
    }

    /**
     * Update the links between seeds.
     */
    private void updateLinkToSeed() {
        for (int i = 0; i < generalWeb.size(); ++i) {
            Seed seed = generalWeb.get(i);
//            if (seed.getPositionY() == 7 && seed.getPositionX() == 7) {
//                System.out.println("7,7 Link status before update links: ");
//                for(Link link : seed.getLinks()) {
//                    System.out.println(link);
//                }
//            }
            if (!(seed instanceof Target)) {
                seed.updateLinks();
            }
//            if (seed.getPositionY() == 7 && seed.getPositionX() == 7) {
//                System.out.println("7,7 Link status after update links: ");
//                for(Link link : seed.getLinks()) {
//                    System.out.println(link);
//                }
//            }
        }
    }

    /**
     * Grow the specified seed
     * @param seed seed specified
     */
    private void growSeed(Seed seed) {
        if (seed.ableToGrow()) {
            ArrayList<Integer> xChange = new ArrayList<>();
            ArrayList<Integer> yChange = new ArrayList<>();
            int[][] surroundings = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}}; //East, North, South, West
            ArrayList<Integer> randomSequence = randomDirectionSequence(4);
            for (int i = 0; i < randomSequence.size(); ++i) {
                int k = randomSequence.get(i);
                try {
                    if (matrixOfSeeds[seed.getPositionY() + surroundings[k][0]]
                            [seed.getPositionX() + surroundings[k][1]] == null
                            && imageOfMap[seed.getPositionY() + surroundings[k][0]]
                            [seed.getPositionX() + surroundings[k][1]] != 0
                            && imageOfMap[seed.getPositionY() + surroundings[k][0]]
                            [seed.getPositionX() + surroundings[k][1]] != 2) {
                        xChange.add(surroundings[k][1]);
                        yChange.add(surroundings[k][0]);
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) { }
            }
            int growable = xChange.size();
            double energyGiven = Seed.energyGivenToOffSprings(seed) / growable;
            if (growable > 0) {
                seed.setEnergy(seed.getEnergy() - Seed.energyGivenToOffSprings(seed));
            }
            for (int i = 0; i < xChange.size(); ++i) {
                Seed temp;
                temp = new Seed(seed.getPositionX() + xChange.get(i),
                        seed.getPositionY() + yChange.get(i), energyGiven);
                Link tempLink = new Link(seed, temp, Seed.CONSUME);
                if (xChange.get(i) == 0) {
                    if (yChange.get(i) == -1) {
                        seed.setNorthLink(tempLink);
                        temp.setSouthLink(tempLink);
                    } else {
                        seed.setSouthLink(tempLink);
                        temp.setNorthLink(tempLink);
                    }
                } else {
                    if (xChange.get(i) == -1) {
                        seed.setWestLink(tempLink);
                        temp.setEastLink(tempLink);
                    } else {
                        seed.setEastLink(tempLink);
                        temp.setWestLink(tempLink);
                    }
                }
                generatedWeb.add(temp);
                matrixOfSeeds[temp.getPositionY()][temp.getPositionX()] = temp;
            }
        }
    }

    private static ArrayList<Integer> randomDirectionSequence(int number) {
        ArrayList<Integer> source = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            source.add(i);
        }
        ArrayList<Integer> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int  removed = (int) (random.nextDouble() * source.size());
            result.add(source.remove(removed));
        }
        return result;
    }

    /**
     * Print the map received.
     * @throws FileNotFoundException normally won't throw
     */
    private void printMap() throws FileNotFoundException {
        PrintWriter outputMap = new PrintWriter(OUTPUTMAP);
        int[][] image = this.imageOfMap;
        for (int[] lines : image) {
            for (int unit : lines) {
                if (unit != 0 && unit != 2) {
                    outputMap.print(1 + " ");
                } else {
                    outputMap.print(unit + " ");
                }
            }
            outputMap.println();
        }
        outputMap.close();
    }

    /**
     * Print the seeds matrix.
     * @param outputSeed Selected PrintWriter
     * @throws FileNotFoundException normally won't throw
     */
    public void printSeeds(PrintWriter outputSeed) throws FileNotFoundException {
        Seed[][] seeds = this.matrixOfSeeds;
        for (Seed[] seedLine : seeds) {
            for (Seed seed : seedLine) {
                if (seed == null) {
                    outputSeed.print(0 + " ");
                } else if (seed.getEnergy() < 2) {
                    outputSeed.print("# ");
                } else {
                    int log = (int) (Math.log(seed.getEnergy()) / Math.log(2));
                    if (log < 10) {
                        outputSeed.print(log + " ");
                    } else {
                        outputSeed.print("$ ");
                    }
                }
            }
            outputSeed.println();
        }
    }

    public ArrayList<Seed> getGeneralWeb() {
        return generalWeb;
    }

    public static double getInitialEnergy() {
        return INITIAL_ENERGY;
    }

    public int[][] getImageOfMap() {
        return imageOfMap;
    }

    public Seed[][] getMatrixOfSeeds() {
        return matrixOfSeeds;
    }

    public ArrayList<Seed> getGeneratedWeb() {
        return generatedWeb;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneralWeb(ArrayList<Seed> generalWeb) {
        generalWeb = generalWeb;
    }

    public void setImageOfMap(int[][] imageOfMap) {
        this.imageOfMap = imageOfMap;
    }

    public void setMatrixOfSeeds(Seed[][] matrixOfSeeds) {
        this.matrixOfSeeds = matrixOfSeeds;
    }

    public void setGeneratedWeb(ArrayList<Seed> generatedWeb) {
        this.generatedWeb = generatedWeb;
    }
}
