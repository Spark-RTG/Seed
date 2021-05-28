package Generation;

public class Seed implements Comparable<Seed> {

    private int positionX;
    private int positionY;
    private Link[] links;
    private double energy;
    public static final double GROW_THRESHOLD = 16;
    public static final double CONSUME = 4;

    @SuppressWarnings("checkstyle:JavadocMethod")
    public Seed(int positionX, int positionY, double energy) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.energy = energy;
        this.links = new Link[4];
    }


    public boolean isSurvive() {
        return energy > 0;
    }

    public void updateEnergy() {
        this.energy -= CONSUME;
//        if (this.getPositionY() == 8 && this.getPositionX() == 2) {
//            for (int i = 0; i < this.getLinks().length; ++i) {
//                System.out.println(this.getLinks()[i]);
//            }
//        }
        for (int i = 0; i < links.length; ++i) {
            Link link = links[i];
            if (link != null) {
                if (link.getTarget() != null) {
                    if (link.getTarget().equals(this)) {
                        this.energy += link.getEnergy();
                    }
                }
                if (link.getPrevious() != null) {
                    if (link.getPrevious().equals(this)) {
                        if (this.energy <= link.getEnergy()) {
                            link.setEnergy(this.energy);
                            this.setEnergy(0);
                            break;
                        } else {
                            this.energy -= link.getEnergy();
                        }
                    }
                }
            }
        }
    }

    public void updateLinks(SigmoidPara sigmoidPara) {
        for (int i = 0; i < links.length; ++i) {
            if (links[i] != null) {
                if (links[i].getTarget() == null || links[i].getPrevious() == null) {
                    links[i] = null;
                } else if (links[i].getTarget().equals(this)) {
                    if (this.energy > links[i].getPrevious().energy && !(links[i].getPrevious() instanceof Target)) {
                        links[i].swap();
                    }
                }
            }
        }

        for (int i = 0; i < links.length; ++i) {
            Link link = links[i];
            if (link != null && link.getPrevious().equals(this) && !(link.getTarget() instanceof Target)) {
                link.refresh(sigmoidPara.getC1(), sigmoidPara.getC2(), sigmoidPara.getZoom());
            }
        }

        for (int i = 0; i < links.length; ++i) {
            Link link = links[i];
            if (link != null && link.getPrevious().equals(this) && !(link.getTarget() instanceof Target)) {
                link.setEnergy(energyGivenToNormalLink(this, link));
            }
        }
    }

    //TODO(Require customization)
    public static double energyGivenToNormalLink(Seed seed, Link link) {
        int counter = 0;
        for (int i = 0; i < seed.getLinks().length; ++i) {
            Link singleLink = seed.getLinks()[i];
            if (singleLink != null && singleLink.getPrevious().equals(seed)) {
                counter += singleLink.getPriority();
            }
        }
        return Math.max((seed.energy - CONSUME * 2) / counter * link.getPriority(), 0);
    }

    public static double energyGivenToOffSprings(Seed seed) {
        return seed.energy / 2;
    }

    @Override
    public int compareTo(Seed seed) {
        return (int) (this.energy - seed.energy);
    }

    @Override
    public String toString() {
        return "Generation.Seed at [" + positionY + "][" + positionX + "] with energy: " + energy + ".";
    }

    public double getEnergy() {
        return energy;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public Link[] getLinks() {
        return links;
    }

    public double getConsume() {
        return CONSUME;
    }

    public double getGrowThreshold() {
        return GROW_THRESHOLD;
    }

    public boolean ableToGrow() {
        return this.energy >= GROW_THRESHOLD;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setNorthLink(Link link) {
        links[0] = link;
    }

    public void setEastLink(Link link) {
        links[1] = link;
    }

    public void setSouthLink(Link link) {
        links[2] = link;
    }

    public void setWestLink(Link link) {
        links[3] = link;
    }
}
