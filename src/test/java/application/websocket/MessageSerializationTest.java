package application.websocket;

import application.game.logic.Point;
import application.game.messages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("OverlyBroadThrowsClause")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class MessageSerializationTest {
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void joinGameTest() throws IOException {
        final JoinGame request = new JoinGame();
        final String requestJson = objectMapper.writeValueAsString(request);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertTrue(fromJson instanceof JoinGame);
    }

    @Test
    public void coordinatesTest() throws IOException, HandleExeption {
        final Coordinates request = new Coordinates();
        request.setCoordinates("1 2 3 4");
        final String requestJson = objectMapper.writeValueAsString(request);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertTrue(fromJson instanceof Coordinates);
        assertEquals(((Coordinates) fromJson).getCoordinates(), "1 2 3 4");

        final List<Point> points = new ArrayList<>();
        points.add(new Point(5, 6));
        points.add(new Point(7, 8));
        request.fromListToString(points);
        assertEquals(request.getCoordinates(), "5 6 7 8");
        points.clear();

        points.add(new Point(2, 3));
        points.add(new Point(6, 7));
        request.setCoordinates("2 3 6 7");
        assertEquals(request.fromStringToList().size(), points.size());
        assertEquals(request.fromStringToList().get(0).getFirstCoordinate(), points.get(0).getFirstCoordinate());
        assertEquals(request.fromStringToList().get(0).getSecondCoordinate(), points.get(0).getSecondCoordinate());
        assertEquals(request.fromStringToList().get(1).getFirstCoordinate(), points.get(1).getFirstCoordinate());
        assertEquals(request.fromStringToList().get(1).getSecondCoordinate(), points.get(1).getSecondCoordinate());
    }

    @Test(expected = HandleExeption.class)
    public void badCoordinatesTest() throws HandleExeption {
        final Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates("asdf");
        coordinates.fromStringToList().size();
    }

    @Test(expected = HandleExeption.class)
    public void emptyCoordinatesTest() throws HandleExeption {
        final Coordinates coordinates = new Coordinates();
        coordinates.setCoordinates("");
        coordinates.fromStringToList().size();
    }

    @Test
    public void finishGameTest() throws IOException {
        final FinishGame request = new FinishGame();
        request.setWon(true);
        final String requestJson = objectMapper.writeValueAsString(request);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertTrue(fromJson instanceof FinishGame);
        assertTrue(((FinishGame)fromJson).getWon());
    }

    @Test
    public void infoTest() throws IOException {
        final InfoMessage request = new InfoMessage();
        request.setMessage("repeat");
        final String requestJson = objectMapper.writeValueAsString(request);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertTrue(fromJson instanceof InfoMessage);
        assertEquals(((InfoMessage) fromJson).getMessage(), "repeat");
    }

    @Test
    public void initGameTest() throws IOException {
        final InitGame request = new InitGame();
        request.setSelf(1L);
        request.setEnemy(2L);
        request.setIsFirst(true);
        final String requestJson = objectMapper.writeValueAsString(request);
        final Message fromJson = objectMapper.readValue(requestJson, Message.class);
        assertTrue(fromJson instanceof InitGame);
        assertTrue(((InitGame) fromJson).getSelf() == 1L);
        assertTrue(((InitGame) fromJson).getEnemy() == 2L);
        assertTrue(((InitGame) fromJson).getIsFirst());
    }
}
