package application.game;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private int xSize;
    private int ySize;

    /*
     * Values: F(free) - cell is not occupied,
     *         W(wall) - cell is occupied by wall,
     *         M(me) - cell is occupied by our player
     *         E(enemy) - cell is occupied by enemy
     * First array - x axis, second - y axis
    */
    private List<List<Character>> field = new ArrayList<>();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Wall> walls = new ArrayList<>();

    private void initializeField() {
        for (int i = 0; i < xSize; i++) {
            final List<Character> line = new ArrayList<>();
            for (int j = 0; j < ySize; j++) {
                line.add('F');
            }
            field.add(line);
        }
    }

    public Field(int dimension) {
        this.xSize = dimension * 2 - 1;
        this.ySize = dimension * 2 - 1;
        initializeField();

        field.get(0).set(ySize / 2, 'M');
        field.get(xSize - 1).set(ySize / 2, 'E');
    }

    public char getCellStatus(Point point) {
        return field.get(point.getxCoordinate()).get(point.getyCoordinate());
    }

    public void clearCell(Point point) {
        field.get(point.getxCoordinate()).set(point.getyCoordinate(), 'F');
    }

    public void addWall(Wall wall) {
        walls.add(wall);
        for (Point point : wall.getLocation()) {
            field.get(point.getxCoordinate()).set(point.getyCoordinate(), 'W');
        }
    }

    public void occupyCellByPlayer(Point point, char playerIdentifier) {
        field.get(point.getxCoordinate()).set(point.getyCoordinate(), playerIdentifier);
    }
}
