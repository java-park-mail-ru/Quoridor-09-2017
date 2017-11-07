package application.game;

public class Player {
    private Point location;

    public Player(Point point) {
        this.location = point;
    }

    public void changeLocation(int xDifference, int yDifference) {
        location.setxCoordinate(location.getxCoordinate() + xDifference);
        location.setyCoordinate(location.getyCoordinate() + yDifference);
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
