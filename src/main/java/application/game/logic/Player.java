package application.game.logic;

public class Player {
    private static final int DEFAULT_WALLS_COUNT = 8;

    private Long userId;
    private Point location;
    private int dimension;
    private Field field;
    private int wallsCount;

    public Player(int dimension, Long userId) {
        this.userId = userId;
        location = new Point(0, dimension - 1);
        this.dimension = dimension;
        field = new Field(dimension);
        wallsCount = DEFAULT_WALLS_COUNT;
    }

    public Long getUserId() {
        return userId;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Field getField() {
        return field;
    }

    public boolean haveWon() {
        return (location.getFirstCoordinate() == (dimension * 2 - 2));
    }

    public boolean canAddWall() {
        return (this.wallsCount != 0);
    }

    public void decWallsCount() {
        this.wallsCount--;
    }
}
