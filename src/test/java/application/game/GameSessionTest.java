package application.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.CloseStatus;

import application.game.logic.Point;
import application.websocket.GameSocketService;
import application.websocket.Message;

@SuppressWarnings({"InstanceMethodNamingConvention", "MagicNumber"})
public class GameSessionTest {
    private Long userId1;
    private Long userId2;
    private Long waiter;
    private GameSession testGameSession;

    private GameSessionService gameSessionService;
    private GameSocketService gameSocketService;

    @Before
    public void setup() throws IOException {
        gameSocketService = Mockito.mock(GameSocketService.class);
        gameSessionService = new GameSessionService(gameSocketService);
        userId1 = 1L;
        userId2 = 2L;
        waiter = userId2;
        testGameSession = new GameSession(userId1, userId2, waiter, gameSessionService);
        when(gameSocketService.isConnected(userId1)).thenReturn(true);
        when(gameSocketService.isConnected(userId2)).thenReturn(true);
        doNothing().when(gameSocketService).sendMessageToUser(anyLong(), any(Message.class));
        doNothing().when(gameSocketService).closeConnection(anyLong(), any(CloseStatus.class));
    }

    @Test
    public void check_gamesession_config() {
        assertFalse(testGameSession.isFinished());
        assertEquals(testGameSession.getFirstUserId(), userId1);
        assertEquals(testGameSession.getSecondUserId(), userId2);
        assertEquals(testGameSession.getWaiter(), waiter);
        assertEquals(testGameSession.getAnotherPlayer(userId1), userId2);
        assertEquals(testGameSession.getAnotherPlayer(userId2), userId1);
        assertNotNull(testGameSession.getSessionId());
        assertFalse(testGameSession.getFirstResult());
        assertFalse(testGameSession.getSecondResult());
        assertNotNull(testGameSession.getGame());
        assertEquals(testGameSession.getGame().getPlayers().get(0).getUserId(), userId1);
        assertEquals(testGameSession.getGame().getPlayers().get(1).getUserId(), userId2);
    }

    @Test
    public void check_start_game() {
        gameSessionService.startGame(userId1, userId2);
        assertNotNull(gameSessionService.getSessions());
        assertEquals(gameSessionService.getGameSessions().get(userId1),
                gameSessionService.getGameSessions().get(userId2));
        assertTrue(gameSessionService.isPlaying(userId1));
        assertTrue(gameSessionService.isPlaying(userId2));
        assertFalse(gameSessionService.isPlaying(3L));
    }

    @Test
    public void check_queue_of_players() {
        gameSessionService.startGame(userId1, userId2);
        final GameSession gameSession = gameSessionService.getGameSessions().get(userId1);
        gameSession.setWaiter(userId2);
        assertEquals(gameSession.getWaiter(), userId2);
        final List<Point> movement = new ArrayList<>();
        movement.add(new Point(2, 8));
        assertNotNull(gameSessionService.handleTask(userId1, movement));
        assertEquals(gameSession.getWaiter(), userId1);
        assertNotNull(gameSessionService.handleTask(userId2, movement));
        assertEquals(gameSession.getWaiter(), userId2);

        movement.clear();
        movement.add(new Point(4, 8));
        assertNull(gameSessionService.handleTask(userId2, movement));
        assertEquals(gameSession.getWaiter(), userId2);
    }

    @Test
    public void check_correct_handle() {
        gameSessionService.startGame(userId1, userId2);
        final GameSession gameSession = gameSessionService.getGameSessions().get(userId1);
        gameSession.setWaiter(userId2);
        final List<Point> movement = new ArrayList<>();
        movement.add(new Point(2, 8));
        AbstractMap.SimpleEntry<Long, List<Point>> result = gameSessionService.handleTask(userId1, movement);
        assertEquals(result.getKey(), userId2);
        assertEquals(result.getValue().size(), 1);
        assertEquals(result.getValue().get(0).getFirstCoordinate(), 14);
        assertEquals(result.getValue().get(0).getSecondCoordinate(), 8);
        movement.clear();

        movement.add(new Point(1, 2));
        movement.add(new Point(1, 0));
        result = gameSessionService.handleTask(userId2, movement);
        assertEquals(result.getKey(), userId1);
        assertEquals(result.getValue().size(), 2);
        assertEquals(result.getValue().get(0).getFirstCoordinate(), 15);
        assertEquals(result.getValue().get(0).getSecondCoordinate(), 16);
        assertEquals(result.getValue().get(1).getFirstCoordinate(), 15);
        assertEquals(result.getValue().get(1).getSecondCoordinate(), 14);
    }

    @Test
    public void check_session_is_finished() {
        gameSessionService.startGame(userId1, userId2);
        final GameSession gameSession = gameSessionService.getGameSessions().get(userId1);
        gameSession.setWaiter(userId2);

        final List<Point> movement = new ArrayList<>();
        //1 iteration
        movement.add(new Point(2, 8));
        gameSessionService.handleTask(userId1, movement);
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //2 iteration
        movement.add(new Point(4, 8));
        gameSessionService.handleTask(userId1, movement);
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //3 iteration
        movement.add(new Point(6, 8));
        gameSessionService.handleTask(userId1, movement);
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //4 iteration
        movement.add(new Point(8, 8));
        gameSessionService.handleTask(userId1, movement);
        movement.clear();
        movement.add(new Point(10, 8));
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //5 iteration
        movement.add(new Point(10, 8));
        gameSessionService.handleTask(userId1, movement);
        movement.clear();
        movement.add(new Point(12, 8));
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //6 iteration
        movement.add(new Point(12, 8));
        gameSessionService.handleTask(userId1, movement);
        movement.clear();
        movement.add(new Point(14, 8));
        gameSessionService.handleTask(userId2, movement);
        movement.clear();
        //7 iteration
        movement.add(new Point(14, 8));
        gameSessionService.handleTask(userId1, movement);
        movement.clear();
        movement.add(new Point(16, 8));
        gameSessionService.handleTask(userId2, movement);
        movement.clear();

        assertTrue(gameSession.tryFinishGame());
        assertFalse(gameSession.getFirstResult());
        assertTrue(gameSession.getSecondResult());
    }

    @Test
    public void check_incorrect_handle() {
        gameSessionService.startGame(userId1, userId2);
        final GameSession gameSession = gameSessionService.getGameSessions().get(userId1);
        gameSession.setWaiter(userId2);

        final List<Point> movement = new ArrayList<>();
        assertNull(gameSessionService.handleTask(userId1, movement));
        assertEquals(gameSession.getWaiter(), userId2);

        movement.add(new Point(1, 8));
        assertNull(gameSessionService.handleTask(userId1, movement));
        assertEquals(gameSession.getWaiter(), userId2);
        movement.clear();

        movement.add(new Point(16, 9));
        movement.add(new Point(16, 7));
        assertNull(gameSessionService.handleTask(userId1, movement));
        assertEquals(gameSession.getWaiter(), userId2);
        movement.clear();

        movement.add(new Point(2, 8));
        movement.add(new Point(4, 8));
        movement.add(new Point(6, 8));
        assertNull(gameSessionService.handleTask(userId1, movement));
        assertEquals(gameSession.getWaiter(), userId2);
    }
}
