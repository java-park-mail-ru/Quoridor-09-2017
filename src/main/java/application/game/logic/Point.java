package application.game.logic;

@SuppressWarnings("unused")
public class Point {
    private int firstCoordinate;
    private int secondCoordinate;

    public Point(int firstCoordinate, int secondCoordinate) {
        this.firstCoordinate = firstCoordinate;
        this.secondCoordinate = secondCoordinate;
    }

    public int getFirstCoordinate() {
        return firstCoordinate;
    }

    public void setFirstCoordinate(int firstCoordinate) {
        this.firstCoordinate = firstCoordinate;
    }

    public int getSecondCoordinate() {
        return secondCoordinate;
    }

    public void setSecondCoordinate(int secondCoordinate) {
        this.secondCoordinate = secondCoordinate;
    }
}
