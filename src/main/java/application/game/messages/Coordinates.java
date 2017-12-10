package application.game.messages;

import application.game.logic.Point;
import application.websocket.HandleExeption;
import application.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class Coordinates extends Message {
    private List<Integer> coordinates = new ArrayList<>();

    public List<Integer> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Point> fromIntArrayToPointList() throws HandleExeption {
        final List<Point> points = new ArrayList<>();
        try {
            for (int i = 0; i < coordinates.size(); i += 2) {
                if (coordinates.get(i) < 0 || coordinates.get(i + 1) < 0) {
                    throw new RuntimeException();
                }
                points.add(new Point(coordinates.get(i), coordinates.get(i + 1)));
            }
        } catch (RuntimeException e) {
            throw new HandleExeption("Invalid data from user ", e);
        }
        if (points.isEmpty()) {
            throw new HandleExeption("Empty data from user");
        }
        return points;
    }

    public void fromPointListToIntArray(List<Point> points) {
        coordinates.clear();
        for (Point point : points) {
            coordinates.add(point.getFirstCoordinate());
            coordinates.add(point.getSecondCoordinate());
        }
    }
}
