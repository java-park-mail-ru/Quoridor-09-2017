package application.websocket;

import application.game.logic.Point;
import application.game.messages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String JOIN = "{\"class\":\"JoinGame\"}";

    @Test
    public void joinGameWriteTest() throws IOException {
        final JoinGame request = new JoinGame();
        assertEquals(JOIN, objectMapper.writeValueAsString(request));
    }

    @Test
    public void joinGameReadTest() throws IOException {
        final Message fromJson = objectMapper.readValue(JOIN, Message.class);
                assertTrue(fromJson instanceof JoinGame);
    }

    private static final String COORDINATES = "{\"class\":\"Coordinates\",\"coordinates\":[1,2,3,4]}";

    @Test
    public void coordinatesWriteTest() throws IOException {
        final Coordinates request = new Coordinates();
        request.setCoordinates(Arrays.asList(1, 2, 3, 4));
        assertEquals(COORDINATES, objectMapper.writeValueAsString(request));
    }

    @Test
    public void coordinatesReadTest() throws IOException {
        final Message fromJson = objectMapper.readValue(COORDINATES, Message.class);
        assertTrue(fromJson instanceof Coordinates);
        assertEquals(((Coordinates) fromJson).getCoordinates(), Arrays.asList(1, 2, 3, 4));
    }

    @Test
    public void coordinatesFromPointToInt() {
        final Coordinates request = new Coordinates();
        final List<Point> points = new ArrayList<>();
        points.add(new Point(5, 6));
        points.add(new Point(7, 8));
        request.fromPointListToIntArray(points);
        assertEquals(request.getCoordinates(), Arrays.asList(5, 6, 7, 8));
    }

    @Test
    public void coordinatesFromIntToPoint() throws HandleExeption {
        final List<Point> points = new ArrayList<>();
        points.add(new Point(2, 3));
        points.add(new Point(6, 7));
        final Coordinates request = new Coordinates();
        request.setCoordinates(Arrays.asList(2, 3, 6, 7));
        assertEquals(request.fromIntArrayToPointList().size(), points.size());
        assertEquals(request.fromIntArrayToPointList().get(0).getFirstCoordinate(), points.get(0).getFirstCoordinate());
        assertEquals(request.fromIntArrayToPointList().get(0).getSecondCoordinate(), points.get(0).getSecondCoordinate());
        assertEquals(request.fromIntArrayToPointList().get(1).getFirstCoordinate(), points.get(1).getFirstCoordinate());
        assertEquals(request.fromIntArrayToPointList().get(1).getSecondCoordinate(), points.get(1).getSecondCoordinate());
    }

    @Test(expected = HandleExeption.class)
    public void badCoordinatesTest() throws HandleExeption {
        final Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates(Arrays.asList(-2, 0));
        coordinates.fromIntArrayToPointList();
    }

    @Test(expected = HandleExeption.class)
    public void wrongNumberOfCoordinates() throws HandleExeption {
        final Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates(Arrays.asList(2, 8, 2));
        coordinates.fromIntArrayToPointList();
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Test(expected = HandleExeption.class)
    public void emptyCoordinatesTest() throws HandleExeption {
        final Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates(Arrays.asList());
        coordinates.fromIntArrayToPointList();
    }

    private static final String FINISH = "{\"class\":\"FinishGame\",\"won\":true}";

    @Test
    public void finishGameWriteTest() throws IOException {
        final FinishGame request = new FinishGame();
        request.setWon(true);
        assertEquals(FINISH, objectMapper.writeValueAsString(request));
    }

    @Test
    public void finishGameReadTest() throws IOException {
        final Message fromJson = objectMapper.readValue(FINISH, Message.class);
        assertTrue(fromJson instanceof FinishGame);
        assertTrue(((FinishGame)fromJson).getWon());
    }

    private static final String INFO = "{\"class\":\"InfoMessage\",\"message\":\"repeat\"}";

    @Test
    public void infoWrite() throws IOException {
        final InfoMessage request = new InfoMessage();
        request.setMessage("repeat");
        assertEquals(INFO, objectMapper.writeValueAsString(request));
    }

    @Test
    public void infoRead() throws IOException {
        final Message fromJson = objectMapper.readValue(INFO, Message.class);
        assertTrue(fromJson instanceof InfoMessage);
        assertEquals(((InfoMessage) fromJson).getMessage(), "repeat");
    }

    private static final String INIT = "{\"class\":\"InitGame\",\"enemy\":\"Ivan\",\"isFirst\":true}";

    @Test
    public void initGameWriteTest() throws IOException {
        final InitGame request = new InitGame();
        request.setEnemy("Ivan");
        request.setIsFirst(true);
        assertEquals(INIT, objectMapper.writeValueAsString(request));
    }

    @Test
    public void initGameReadTest() throws IOException {
        final Message fromJson = objectMapper.readValue(INIT, Message.class);
        assertTrue(fromJson instanceof InitGame);
        assertEquals(((InitGame) fromJson).getEnemy(), "Ivan");
        assertTrue(((InitGame) fromJson).getIsFirst());
    }
}
