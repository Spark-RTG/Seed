package Generation;
import java.io.*;
import java.util.*;


public class Web {

    private ArrayList<Seed> generalWeb;
    private ArrayList<Seed> generatedWeb;
    private Seed[][] matrixOfSeeds;
    private int[][] imageOfMap;
    private int cycle;
    private boolean alive;
    private final ArrayList<Double> centroidsX;
    private final ArrayList<Double> centroidsY;
    private final ArrayList<Vector> centerList; //This is oriented by 0,0 instead of 7,7
    private final ArrayList<Vector> optimizedMovements;
    private final ArrayList<Vector> optimizedCenterMovements;
    private SigmoidPara sigmoidPara;
    private static final File OUTPUTMAP = new File("Map");
    private static final File OUTPUTSEED = new File("GrowHistory");
    private static final double INITIAL_ENERGY = 1500;

    public Web(double initialEnergy, ArrayList<Vector> optimizedMovements, SigmoidPara sigmoidPara,
               ArrayList<Vector> optimizedCenterMovements)
            throws IOException, ClassNotFoundException {
        FileInputStream resource = new FileInputStream("OutputMatrix");
        ObjectInputStream in = new ObjectInputStream(resource);
        imageOfMap = (int[][]) in.readObject();
        matrixOfSeeds = new Seed[imageOfMap.length][imageOfMap[0].length];
        generalWeb = new ArrayList<>();
        this.sigmoidPara = sigmoidPara;
        this.optimizedMovements = optimizedMovements;
        this.optimizedCenterMovements = optimizedCenterMovements;
        centroidsX = new ArrayList<>();
        centroidsY = new ArrayList<>();
        centerList = new ArrayList<>();
        Seed initial = new Seed(matrixOfSeeds.length / 2, matrixOfSeeds[0].length / 2, initialEnergy);
        matrixOfSeeds[matrixOfSeeds.length / 2][matrixOfSeeds[0].length / 2] = initial;
        centroidsX.add(7.0);
        centroidsY.add(7.0);
        generalWeb.add(initial);
        generatedWeb = new ArrayList<>();
        cycle = 0;
        alive = true;
        fillTargets();
    }

    public Web(ArrayList<Vector> optimizedMovements, ArrayList<Vector> optimizedCenterMovements)
            throws IOException, ClassNotFoundException {
        this(INITIAL_ENERGY, optimizedMovements, new SigmoidPara(), optimizedCenterMovements);
    }

    public Web(SigmoidPara sigmoidPara) throws IOException, ClassNotFoundException {
        this(INITIAL_ENERGY, new ArrayList<>(), sigmoidPara, new ArrayList<>());
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
        Comparator<Seed> comparator = new Comparator<Seed>() {
            @Override
            public int compare(Seed o1, Seed o2) {
                return Double.compare(o2.getEnergy(), o1.getEnergy());
            }
        };
        generalWeb.sort(comparator);
        //generalWeb = sort(generalWeb);
        //System.out.println(generalWeb.get(0).getEnergy());
//        if (generalWeb.get(0).getEnergy() == 184) {
//            System.out.println(cycle);
//            for (Seed seed: generalWeb) {
//                System.out.println(seed);
//            }
//        }
    }

//    private ArrayList<Seed> merge(ArrayList<Seed> seeds1, ArrayList<Seed> seeds2) {
//        int firstPointer = 0;
//        int secondPointer = 0;
//        ArrayList<Seed> result = new ArrayList<>();
//        while (firstPointer < seeds1.size() && secondPointer < seeds2.size()) {
//            if (seeds1.get(firstPointer).compareTo(seeds2.get(secondPointer)) >= 0) {
//                result.add(seeds1.get(firstPointer));
//                ++firstPointer;
//            } else {
//                result.add(seeds2.get(secondPointer));
//                ++secondPointer;
//            }
//        }
//        while (firstPointer < seeds1.size()) {
//            result.add(seeds1.get(firstPointer));
//            ++firstPointer;
//        }
//        while (secondPointer < seeds2.size()) {
//            result.add(seeds2.get(secondPointer));
//            ++secondPointer;
//        }
//        return result;
//    }
//
//    private ArrayList<Seed> sort(ArrayList<Seed> seeds) {
//        if (seeds.size() > 1) {
//            int m = seeds.size() / 2;
//            ArrayList<Seed> leftSeeds = new ArrayList<>();
//            ArrayList<Seed> rightSeeds = new ArrayList<>();
//            for (int i = 0; i < m; ++i) {
//                leftSeeds.add(seeds.get(i));
//            }
//            for (int i = m; i < seeds.size(); ++i) {
//                rightSeeds.add(seeds.get(i));
//            }
//            leftSeeds = sort(leftSeeds);
//            rightSeeds = sort(rightSeeds);
//            return merge(leftSeeds, rightSeeds);
//        } else {
//            return seeds;
//        }
//    }

    /**
     * Refresh the web by one cycle.
     * @return the amount of energy gained in this cycle.
     */
    public void refresh() {
        addNewBorn();
        sortSeeds();
        connectContiguous();
        updateLinkToTarget();
        updateCentroidsAndCenters();
        updateLinkToSeed();
        updateSeedEnergy();
        updateAlive();
        ++cycle;
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
                    ((Target) temp).updateLinks();
                } catch (IllegalStateException e) {
                    for (int j = 0; j < temp.getLinks().length; ++j) {
                        Link link = temp.getLinks()[j];
                        if (link != null) {
                            if (link.getTarget() != null && link.getTarget().equals(temp)) {
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
//                System.out.println("7,7 Generation.Link status before update energy: ");
//                for(Generation.Link link : seed.getLinks()) {
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
//                System.out.println("7,7 Generation.Link status before update links: ");
//                for(Generation.Link link : seed.getLinks()) {
//                    System.out.println(link);
//                }
//            }
            if (!(seed instanceof Target)) {
                seed.updateLinks(sigmoidPara, cycle, optimizedCenterMovements);
            }
//            if (seed.getPositionY() == 7 && seed.getPositionX() == 7) {
//                System.out.println("7,7 Generation.Link status after update links: ");
//                for(Generation.Link link : seed.getLinks()) {
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
            ArrayList<Vector> growDecision = new ArrayList<>();
            ArrayList<Vector> growHelper = new ArrayList<>();
            //Map<Vector, Double> optimization = new LinkedHashMap<>();
            // East, North, West, South
            Vector[] surroundings = {new Vector(1, 0), new Vector(0, -1),
                    new Vector(-1, 0), new Vector(0, 1)};
            ArrayList<Integer> randomSequence = randomDirectionSequence(4);
            for (int i = 0; i < randomSequence.size(); ++i) {
                int k = randomSequence.get(i);
                try {
                    if (matrixOfSeeds[seed.getPositionY() + (int) surroundings[k].getYChange()]
                            [seed.getPositionX() + (int) surroundings[k].getXChange()] == null
                            && imageOfMap[seed.getPositionY() + (int) surroundings[k].getYChange()]
                            [seed.getPositionX() + (int) surroundings[k].getXChange()] != 0
                            && imageOfMap[seed.getPositionY() + (int) surroundings[k].getYChange()]
                            [seed.getPositionX() + (int) surroundings[k].getXChange()] != 2) {
                        growHelper.add(
                                new Vector(seed.getPositionX() - centroidsX.get(cycle)
                                        + surroundings[k].getXChange(),
                                        seed.getPositionY() - centroidsY.get(cycle)
                                                + surroundings[k].getYChange()));
                        growDecision.add(surroundings[k]);
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) { }
            }
            //TODO("There may exist some error.")
            //System.out.println(cycle +" "+ optimizedMovements.size());
            if (cycle < optimizedMovements.size()) {
                ArrayList<Vector> tempDecision = new ArrayList<>();
                for (int i = 0; i < growDecision.size(); i++) {
                    if (decideToGrow(growHelper.get(i))) {
                        tempDecision.add(growDecision.get(i));
                    }
                }
                growDecision = tempDecision;
            }
            int growable = growDecision.size();
            double energyGiven = Seed.energyGivenToOffSprings(seed) / growable;
            if (growable > 0) {
                seed.setEnergy(seed.getEnergy() - Seed.energyGivenToOffSprings(seed));
            }
            for (int i = 0; i < growDecision.size(); ++i) {
                Seed temp;
                temp = new Seed(seed.getPositionX() + (int) growDecision.get(i).getXChange(),
                        seed.getPositionY() + (int) growDecision.get(i).getYChange(), energyGiven);
                Link tempLink = new Link(seed, temp, Seed.CONSUME);
                if (growDecision.get(i).getXChange() == 0) {
                    if ((int) growDecision.get(i).getYChange() == -1) {
                        seed.setNorthLink(tempLink);
                        temp.setSouthLink(tempLink);
                    } else {
                        seed.setSouthLink(tempLink);
                        temp.setNorthLink(tempLink);
                    }
                } else {
                    if ((int) growDecision.get(i).getXChange() == -1) {
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

    private boolean decideToGrow(Vector vector) {
        //TODO(Think about a valid method to decide whether to grow).
        Vector optimized = optimizedMovements.get(cycle);
        if (optimized.getXChange() == 0 && optimized.getYChange() == 0) {
            return true;
        }
        Vector current = Vector.norm(vector);
        double theta = Math.acos(Vector.dot(optimized, current)) / Math.PI * 180;
        if (theta >= 90) {
            return true;
        } else {
            double possibility = (theta / 90 * 0.8) + 0.2;
            Random rand = new Random();
            return rand.nextDouble() <= possibility;
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

    private void updateAlive() {
        if (generatedWeb.isEmpty()) {
            alive = false;
            for (int i = 0; i < generalWeb.size(); ++i) {
                if (!(generalWeb.get(i) instanceof Target)) {
                    alive = true;
                    break;
                }
            }
        }
    }

    private void updateCentroidsAndCenters() {
        double currentCentroidX = 0;
        double currentCentroidY = 0;
        double currentCenterX = 0;
        double currentCenterY = 0;
        double totalEnergy = 0;
        for (Seed seed: generalWeb) {
            if (!(seed instanceof Target)) {
                currentCentroidX += seed.getPositionX();
                currentCenterX += seed.getPositionX() * seed.getEnergy();
                currentCentroidY += seed.getPositionY();
                currentCenterY += seed.getPositionY() * seed.getEnergy();
                totalEnergy += seed.getEnergy();
            }
        }
        centroidsX.add(currentCentroidX / generalWeb.size());
        centroidsY.add(currentCentroidY / generalWeb.size());
        currentCenterX /= totalEnergy;
        currentCenterY /= totalEnergy;
        centerList.add(new Vector(currentCenterX, currentCenterY));
        //        centroidsY.add(currentCentroidY);
//        if (cycle % 10 == 0) {
//            System.out.println(currentCentroidX + ", " + currentCentroidY);
//        }
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

    public ArrayList<Double> getCentroidsX() {
        return centroidsX;
    }

    public ArrayList<Double> getCentroidsY() {
        return centroidsY;
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

    public int getCycle() {
        return cycle;
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

    public boolean isAlive() {
        return alive;
    }

    public ArrayList<Vector> getCenterList() {
        return centerList;
    }
}
