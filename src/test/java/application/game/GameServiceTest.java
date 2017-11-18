package application.game;

import application.dao.User;
import application.dao.UserService;
import application.game.logic.Point;
import application.game.messages.Coordinates;
import application.websocket.GameSocketService;
import application.websocket.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SuppressWarnings({"InstanceMethodNamingConvention", "MagicNumber"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class GameServiceTest {
    private Long userId1;
    private Long userId2;

    @MockBean
    private UserService userService;

    @MockBean
    private GameSocketService gameSocketService;

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private GameService gameService;

    @Before
    public void setup() throws IOException {
        userId1 = 1L;
        userId2 = 2L;
        when(userService.getUserById(anyLong())).thenReturn(new User(1L, "test",
                "12345", "test@mail.ru"));
        when(gameSocketService.isConnected(userId1)).thenReturn(true);
        when(gameSocketService.isConnected(userId2)).thenReturn(true);
        doNothing().when(gameSocketService).sendMessageToUser(anyLong(), any(Message.class));
        doNothing().when(gameSocketService).closeConnection(anyLong(), any(CloseStatus.class));
    }

    @Test
    public void add_users_and_begin_session() {
        assertTrue(gameSessionService.getGameSessions().isEmpty());

        gameService.addUser(userId1);
        assertTrue(gameSessionService.getGameSessions().isEmpty());

        gameService.addUser(userId2);
        assertEquals(gameSessionService.getGameSessions().get(userId1),
                gameSessionService.getGameSessions().get(userId2));
    }

    @Test
    public void user_send_coordinates_to_user_successfully() {
        gameService.addUser(userId1);
        gameService.addUser(userId2);
        gameSessionService.getGameSessions().get(userId1).setWaiter(userId2);
        final List<Point> movement = new ArrayList<>();
        movement.add(new Point(0, 6));
        final Coordinates coordinates = new Coordinates();
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId1, coordinates);
        Map<Long, List<Point>> result = gameService.gameStep();
        assertTrue(result.containsKey(userId2));
        assertFalse(result.containsKey(userId1));
        assertEquals(result.get(userId2).size(), 1);
        assertEquals(result.get(userId2).get(0).getFirstCoordinate(), 16);
        assertEquals(result.get(userId2).get(0).getSecondCoordinate(), 10);

        movement.clear();
        movement.add(new Point(0, 1));
        movement.add(new Point(2, 1));
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId2, coordinates);
        result = gameService.gameStep();
        assertTrue(result.containsKey(userId1));
        assertFalse(result.containsKey(userId2));
        assertEquals(result.get(userId1).size(), 2);
        assertEquals(result.get(userId1).get(0).getFirstCoordinate(), 14);
        assertEquals(result.get(userId1).get(0).getSecondCoordinate(), 15);
        assertEquals(result.get(userId1).get(1).getFirstCoordinate(), 16);
        assertEquals(result.get(userId1).get(1).getSecondCoordinate(), 15);
    }

    @Test
    public void check_queue_of_users() {
        gameService.addUser(userId1);
        gameService.addUser(userId2);
        gameSessionService.getGameSessions().get(userId1).setWaiter(userId2);
        final List<Point> movement = new ArrayList<>();
        movement.add(new Point(0, 6));
        final Coordinates coordinates = new Coordinates();
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId1, coordinates);
        assertFalse(gameService.gameStep().isEmpty());

        movement.clear();
        movement.add(new Point(0, 4));
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId1, coordinates);
        assertTrue(gameService.gameStep().isEmpty());

        movement.clear();
        movement.add(new Point(2, 8));
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId2, coordinates);
        assertFalse(gameService.gameStep().isEmpty());
    }

    @Test
    public void user_sent_invalid_coordinates() {
        gameService.addUser(userId1);
        gameService.addUser(userId2);
        gameSessionService.getGameSessions().get(userId1).setWaiter(userId2);
        final List<Point> movement = new ArrayList<>();
        movement.add(new Point(1, 8));
        final Coordinates coordinates = new Coordinates();
        coordinates.fromListToString(movement);
        gameService.putCoordinates(userId1, coordinates);
        assertTrue(gameService.gameStep().isEmpty());
    }

    @After
    public void tearDown() {
        gameSessionService.getGameSessions().clear();
    }
}
