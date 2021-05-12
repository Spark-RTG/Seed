package World;

public class Room {
    private final int exitNumber = 4;
    private int roomType; //0 stands for empty, 1 stands for start, 2 stands for exit
    private Exit[] exits; //Sequence: North, East, South, West
    private int positionX;
    private int positionY;
    public static final int TYPE_AMOUNT = 8;

    public Room(int roomType, int positionX, int positionY, Exit[] exits) {
        if (exits == null || exits.length != 4) {
            throw new IllegalArgumentException("There could only be four exits");
        }
        this.roomType = roomType;
        this.exits = exits;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Room(int roomType, Exit[] exits) {
        this(roomType, 0, 0, exits);
    }

    public Room(int roomType, int positionX, int positionY) {
        Exit[] exits = new Exit[exitNumber];
        for (int i = 0; i < exits.length; i++) {
            exits[i] = new Exit();
        }
        this.roomType = roomType;
        this.exits = exits;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Room() {
        Exit[] exits = new Exit[exitNumber];
        for (int i = 0; i < exits.length; i++) {
            exits[i] = new Exit();
        }
        this.roomType = 0;
        this.exits = exits;
        this.positionX = 0;
        this.positionY = 0;
    }

    public int getRoomType() {
        return roomType;
    }

    public Exit[] getExits() {
        return exits;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setExits(Exit[] exits) {
        this.exits = exits;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

}
