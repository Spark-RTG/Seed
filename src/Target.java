public class Target extends Seed{
    private int timesOfEnergyEmission;
    private double energyGiven;

    public Target(int positionX, int positionY, int timesOfEnergyEmission, double energyGiven) {
        super(positionX, positionY, energyGiven);
        this.timesOfEnergyEmission = timesOfEnergyEmission;
        this.energyGiven = energyGiven;
    }

    @Override
    public void updateEnergy() {
        this.setEnergy(energyGiven);
//        if (this.getPositionY() == 8 && this.getPositionX() == 1) {
//            System.out.println("This is [8][1] target: ");
//            for (int i = 0; i < this.getLinks().length; ++i) {
//                System.out.println(this.getLinks()[i]);
//            }
//        }
//        if (this.getPositionY() == 9 && this.getPositionX() == 2) {
//            System.out.println("This is [9][2] target: ");
//            for (int i = 0; i < this.getLinks().length; ++i) {
//                System.out.println(this.getLinks()[i]);
//            }
//        }
    }

    @Override
    public void updateLinks() throws IllegalStateException {
        int counter = 0;
        for (int i = 0; i < this.getLinks().length; ++i) {
            if (this.getLinks()[i] != null && this.getLinks()[i].getTarget() != null) {
                counter++;
            }
        }
        if (counter > 0) {
            if (timesOfEnergyEmission > 0) {
                for (int i = 0; i < this.getLinks().length; ++i) {
                    Link link = this.getLinks()[i];
                    if (link != null) {
                        if (link.getPrevious() != this) {
                            link.swap();
                        }
                        link.setEnergy(0);
                    }
                }

                for (int i = 0; i < this.getLinks().length; ++i) {
                    Link link = this.getLinks()[i];
                    if (link != null) {
                        link.setEnergy(energyGiven);
                        if (--timesOfEnergyEmission <= 0) {
                            throw new IllegalStateException("This target should be removed immediately.");
                        }
                    }
                }
                if (this.getPositionY() == 8 && this.getPositionX() == 1
                        || this.getPositionY() == 9 && this.getPositionX() == 2) {
                    System.out.println("This is target " + this.getPositionY() + ", " + this.getPositionX());
                    for (Link link : this.getLinks()) {
                        System.out.println(link);
                    }
                }

            }
        }
    }

    @Override
    public boolean isSurvive() {
        return timesOfEnergyEmission > 0;
    }

    @Override
    public String toString() {
        return "" +
//                "Target at [" + this.getPositionY() + "][" + this.getPositionX() + "] with energy: "
//                + this.getEnergy() + "." +
                "";
    }

    @Override
    public boolean ableToGrow() {
        return false;
    }

    public double getEnergyGiven() {
        return energyGiven;
    }

    public double getTimesOfEnergyEmission() {
        return timesOfEnergyEmission;
    }

    public void setEnergyGiven(double energyGiven) {
        this.energyGiven = energyGiven;
    }

    public void setTimesOfEnergyEmission(int timesOfEnergyEmission) {
        this.timesOfEnergyEmission = timesOfEnergyEmission;
    }
}
