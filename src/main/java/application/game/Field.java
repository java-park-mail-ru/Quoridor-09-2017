package application.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {
    private int xSize;
    private int ySize;

    /*
     * Values: F(free) - cell is not occupied,
     *         W(wall) - cell is occupied by wall,
     *         P(player) - cell is occupied by player
    */
    private Map<Point, Character> field = new HashMap<>();

    private List<Player> players = new ArrayList<>();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Wall> walls = new ArrayList<>();

    private void initializeField() {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                field.put(new Point(i, j), 'F');
            }
        }
    }

    //default playerCount = 2
    public Field(int xSize, int ySize, int playerCount) {
        this.xSize = xSize;
        this.ySize = ySize;
        initializeField();

        final Point point1 = new Point(xSize / 2, 0);
        players.add(new Player(point1));
        field.put(point1, 'P');

        final Point point2 = new Point(xSize / 2, ySize);
        players.add(new Player(point2));
        field.put(point2, 'P');

        if (playerCount == 4) {
            final Point point3 = new Point(0, ySize / 2);
            players.add(new Player(point3));
            field.put(point3, 'P');

            final Point point4 = new Point(xSize, ySize / 2);
            players.add(new Player(point4));
            field.put(point4, 'P');
        }
    }

    public char getCellStatus(Point point) {
        return field.get(point);
    }

    public boolean isPlayerOnTheFinish(int playerNumber) {
        switch (playerNumber) {
            case 0:
                return players.get(playerNumber).getLocation().getyCoordinate() == ySize;
            case 1:
                return players.get(playerNumber).getLocation().getyCoordinate() == 0;
            case 2:
                return players.get(playerNumber).getLocation().getxCoordinate() == xSize;
            case 3:
                return players.get(playerNumber).getLocation().getxCoordinate() == 0;
            default:
                return false;
        }
    }

    public void clearCell(Point point) {
        field.replace(point, 'F');
    }

    public void addWall(Wall wall) {
        walls.add(wall);
        for (Point point : wall.getLocation()) {
            field.replace(point, 'W');
        }
    }

    public void occupyCellByPlayer(Point point) {
        field.replace(point, 'P');
    }

    public List<Player> getPlayers() {
        return players;
    }
}
