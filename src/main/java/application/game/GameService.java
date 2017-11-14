package application.game;

import application.User;
import application.UserService;
import application.game.models.Coordinates;
import application.websocket.GameSocketService;
import application.websocket.HandleExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger("application");

    @NotNull
    private ConcurrentLinkedQueue<Long> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private ConcurrentHashMap<Long, List<Point>> tasks = new ConcurrentHashMap<>();

    @NotNull
    private final UserService userService;

    @NotNull
    private final GameSocketService gameSocketService;

    public GameService(UserService userService,
                       GameSocketService gameSocketService) {
        this.userService = userService;
        this.gameSocketService = gameSocketService;
    }

    public void addUser(@NotNull Long userId) {

        //если играет - то выйти
        //организовать gameSessionService

        waiters.add(userId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User with id " + userId + " added to the waiting list");
        }
        tryStartGame();
    }

    public void addPoints(@NotNull Long userId, @NotNull Coordinates coordinates) {
        List<Point> points = null;
        try {
            points = coordinates.getPointsOfCoordinates();
        } catch (HandleExeption e) {

            //послать сообщение о повторе хода

            return;
        }
        tasks.put(userId, points);
    }

    public void tryStartGame() {
        final Set<Long> matchedPlayers = new LinkedHashSet<>();
        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedPlayers.size() >= 1) {
            final Long candidate = waiters.poll();
            if (!insureCandidate(candidate)) {
                continue;
            }
            matchedPlayers.add(candidate);
            if (matchedPlayers.size() == 2) {

                //начинаем игру!!!
                //GameSessionService

                matchedPlayers.clear();
            }
        }
        waiters.addAll(matchedPlayers);
    }

    private boolean insureCandidate(@NotNull Long candidate) {
        return gameSocketService.isConnected(candidate) &&
                userService.getUserById(candidate) != null;
    }
}
