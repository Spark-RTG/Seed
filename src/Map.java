import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Map {
    private Room[][] rooms;
    private int typesOfRooms; //including empty, start, exit rooms

    public static void main(String[] args) throws IOException {
        FileOutputStream OutputMatrix = new FileOutputStream("OutputMatrix");
        Map a = new Map(7, 8, 0.25);
        //PrintWriter printWriter = new PrintWriter(OUTPUTMATRIX);
        int[][] roomTypeMatrix = new int[a.getRooms().length][a.getRooms()[0].length];
        for (int i = 0; i < a.getRooms().length; i++) {
            for (int j = 0; j < a.getRooms()[i].length; j++) {
                roomTypeMatrix[i][j] = a.getRooms()[i][j].getRoomType();
                System.out.print(a.getRooms()[i][j].getRoomType() + " ");
                //printWriter.print(a.getRooms()[i][j].getRoomType() + " ");
            }
            System.out.println();
            //printWriter.println();
        }
        //printWriter.close();
        ObjectOutputStream out = new ObjectOutputStream(OutputMatrix);
        out.writeObject(roomTypeMatrix);
        out.close();
    }
    /**
     * The constructor method of Map
     * @param distanceFromStartToExit amount of rooms (including start and exit) - 1
     *           so that the center is at Map[distanceFromStartToExit][distanceFromStartToExit]
     *           the size of the map is a square with side length (distanceFromStartToExit * 2 + 1)
     * @param typesOfRooms the total number of Room types
     * @param portionOfEmptyRooms the portion of rooms intended to be empty
     */
    public Map(int distanceFromStartToExit, int typesOfRooms, double portionOfEmptyRooms){
        Room[][] map = new Room[distanceFromStartToExit * 2 + 1][distanceFromStartToExit * 2 + 1];
        for (int row = 0; row < map.length; row++) {
            for (int allColumn = 0; allColumn < map[row].length; allColumn++) {
                map[row][allColumn] = new Room(0, allColumn, row);
            }
            for (int column = Math.abs(distanceFromStartToExit - row);
                 column <= 2 * distanceFromStartToExit - Math.abs(distanceFromStartToExit - row);
                 column++) {
                Random rn = new Random();
                int randomRoomType = rn.nextInt(typesOfRooms - 2) + 3;
                if (column == Math.abs(distanceFromStartToExit - row)
                        || column == 2 * distanceFromStartToExit
                        - Math.abs(distanceFromStartToExit - row)) {
                    map[row][column] = new Room(2, column, row);
                } else {
                    map[row][column] = new Room(randomRoomType, column, row);
                }
            }
        }
        map[distanceFromStartToExit][distanceFromStartToExit].setRoomType(1);
        makeEmptyRooms(map, portionOfEmptyRooms);
        //makeRoomsLinked(map);
        this.rooms = map;
    }

    /**
     * Helper method of the constructor to make rooms linked by exits.
     * @param rooms the unlinked rooms 2D array
     */
    private static void makeRoomsLinked(Room[][] rooms) {
        for (int y = 0; y < rooms.length; y++) {
            for (int x = 0; x < rooms[y].length; x++) {
                if (rooms[y][x].getRoomType() != 0) {
                    if (y - 1 >= 0 && rooms[y - 1][x].getRoomType() != 0) {
                        rooms[y][x].getExits()[0].setTarget(rooms[y - 1][x]);
                    }
                    if (x + 1 < rooms[y].length && rooms[y][x + 1].getRoomType() != 0) {
                        rooms[y][x].getExits()[1].setTarget(rooms[y][x + 1]);
                    }
                    if (y + 1 < rooms.length && rooms[y + 1][x].getRoomType() != 0) {
                        rooms[y][x].getExits()[2].setTarget(rooms[y + 1][x]);
                    }
                    if (x - 1 >= 0 && rooms[y][x - 1].getRoomType() != 0) {
                        rooms[y][x].getExits()[3].setTarget(rooms[y][x - 1]);
                    }
                }
            }
        }
    }

    /**
     * Helper method of the constructor to make empty rooms
     * @param portionOfEmptyRooms the portion of empty rooms intended from the second circle.
     * @param rooms the origin 2-D array of Rooms
     */
    private static void makeEmptyRooms(Room[][] rooms, double portionOfEmptyRooms) {
        if (portionOfEmptyRooms > 1) {
            throw new IllegalArgumentException("The portion entered should be at most 1.");
        }
        int originX = rooms.length / 2;
        int originY = rooms.length / 2;
        for (int i = 2; i < rooms.length / 2; i++) {
            ArrayList<Room> candidateEmptyRooms = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                if (j == 0) {
                    candidateEmptyRooms.add(rooms[originY + i][originX]);
                    candidateEmptyRooms.add(rooms[originY - i][originX]);
                } else if (j == i) {
                    candidateEmptyRooms.add(rooms[originY][originX + i]);
                    candidateEmptyRooms.add(rooms[originY][originX - i]);
                } else {
                    candidateEmptyRooms.add(rooms[originY + i - j][originX + j]);
                    candidateEmptyRooms.add(rooms[originY - i + j][originX + j]);
                    candidateEmptyRooms.add(rooms[originY + i - j][originX - j]);
                    candidateEmptyRooms.add(rooms[originY - i + j][originX - j]);
                }
            }
            randomChoose(candidateEmptyRooms,
                    (int) (candidateEmptyRooms.size() * portionOfEmptyRooms));
        }
    }

    /**
     * This method is the helper method of makeEmptyRooms that randomly empties the rooms
     * @param rooms the candidate empty rooms
     * @param number of empty rooms intended in the candidate rooms*
     */
    private static void randomChoose(ArrayList<Room> rooms, int number) {
        for (int i = 0; i < number; i++) {
            int  removed = (int) (Math.random() * rooms.size());
            rooms.remove(removed).setRoomType(0);
        }
    }

    public Room[][] getRooms() {
        return rooms;
    }
}
