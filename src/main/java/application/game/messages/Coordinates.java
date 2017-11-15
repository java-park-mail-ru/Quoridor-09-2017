package application.game.messages;

import application.game.logic.Point;
import application.websocket.HandleExeption;
import application.websocket.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Coordinates extends Message {
    private String coordinates;

    public void fromListToString(List<Point> points) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Point point : points) {
            stringBuilder.append(point.getFirstCoordinate()).append(' ')
                    .append(point.getSecondCoordinate()).append(' ');
        }
        this.coordinates = stringBuilder.toString().trim();
    }

    public List<Point> fromStringToList() throws HandleExeption {
        final List<Point> result = new ArrayList<>();
        try {
            final List<Integer> numbers = Arrays.stream(coordinates.trim().split(" "))
                    .mapToInt(Integer::parseInt)
                    .boxed().collect(Collectors.toList());
            for (int i = 0; i < numbers.size(); i += 2) {
                result.add(new Point(numbers.get(i), numbers.get(i + 1)));
            }
        } catch (RuntimeException e) {
            throw new HandleExeption("Invalid data from user ", e);
        }
        if (result.isEmpty()) {
            throw new HandleExeption("Empty data from user");
        }
        return result;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
