package application.game;

public class Player {
    private Point location;
    private int dimension;
    private Field field;

    public Player(int dimension) {
        location = new Point(0, dimension - 1);
        this.dimension = dimension;
        field = new Field(dimension);
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
        return (location.getxCoordinate() == (dimension * 2 - 2));
    }
}
