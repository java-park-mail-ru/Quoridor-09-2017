package application.game.logic;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private int firstSize;
    private int secondSize;

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
        for (int i = 0; i < firstSize; i++) {
            final List<Character> line = new ArrayList<>();
            for (int j = 0; j < secondSize; j++) {
                line.add('F');
            }
            field.add(line);
        }
    }

    public Field(int dimension) {
        this.firstSize = dimension * 2 - 1;
        this.secondSize = dimension * 2 - 1;
        initializeField();

        field.get(0).set(secondSize / 2, 'M');
        field.get(firstSize - 1).set(secondSize / 2, 'E');
    }

    public char getCellStatus(Point point) {
        return field.get(point.getFirstCoordinate()).get(point.getSecondCoordinate());
    }

    public void clearCell(Point point) {
        field.get(point.getFirstCoordinate()).set(point.getSecondCoordinate(), 'F');
    }

    public void addWall(Wall wall) {
        walls.add(wall);
        for (Point point : wall.getLocation()) {
            field.get(point.getFirstCoordinate()).set(point.getSecondCoordinate(), 'W');
        }
    }

    public void occupyCellByPlayer(Point point, char playerIdentifier) {
        field.get(point.getFirstCoordinate()).set(point.getSecondCoordinate(), playerIdentifier);
    }
}
