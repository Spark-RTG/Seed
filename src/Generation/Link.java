package Generation;

public class Link {
    private Seed previous;
    private Seed target;
    private double energy;
    private double priority;

    public Link(Seed previous, Seed target, double energy, double priority) {
        this.previous = previous;
        this.target = target;
        this.energy = energy;
        this.priority = priority;
    }

    public Link(Seed previous, Seed target, double energy) {
        this(previous,target,energy,1);
    }
    public Link(Seed previous, Seed target) {
        this(previous, target, 0);
    }

    /**
     * Sigmoid function
     */
    //TODO(Optimize this function)
    public void refresh(double c1, double c2, double zoom) {
        this.priority -= 1;
        this.priority = Math.max(10 / (1 + Math.exp(-c1 * (energy - c2) / zoom)) + 1, priority);
//        System.out.println("Generation.Seed " + this.previous.getPositionY() + ", " + this.previous.getPositionX() +
//                " passed " + this.energy + " to seed " + this.target.getPositionY() + ", "
//                + this.target.getPositionX());
    }

    public void swap(){
        if (previous != null && target != null) {
            Seed temp = target;
            target = previous;
            previous = temp;
        }
    }

    @Override
    public String toString() {
        return "This link has " + energy + " energy, links from " + previous + " to " + target + ".";
    }
    public double getEnergy() {
        return energy;
    }

    public double getPriority() {
        return priority;
    }

    public Seed getPrevious() {
        return previous;
    }

    public Seed getTarget() {
        return target;
    }

    public void setTarget(Seed target) {
        this.target = target;
    }

    public void setPrevious(Seed previous) {
        this.previous = previous;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }
}
