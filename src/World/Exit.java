package World;

import World.Room;

public class Exit {
    //Generation.Target could be null, in which case stands for it's connecting to an empty room or the border.
    private Room target;

    public Exit(Room target) {
        this.target = target;
    }

    public Exit() {
        this(null);
    }

    public Room getTarget() {
        return target;
    }

    public void setTarget(Room target) {
        this.target = target;
    }
}
