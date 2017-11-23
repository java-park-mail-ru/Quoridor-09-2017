package application.game.logic;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    private List<Point> location = new ArrayList<>();

    public Wall(Point point1, Point point2, Point point3) {
        location.add(point1);
        location.add(point2);
        location.add(point3);
    }

    public List<Point> getLocation() {
        return location;
    }
}
