package application.gamelogic;

import application.game.logic.Game;
import application.game.logic.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@SuppressWarnings({"InstanceMethodNamingConvention", "MagicNumber"})
public class MovementWithWallsTest {
    private Long userId1;
    private Long userId2;
    private Game game;
    private static final int FIELD_DIMENSION = 9;

    @Before
    public void setup() {
        userId1 = 1L;
        userId2 = 2L;
        game = new Game(FIELD_DIMENSION, userId1, userId2);
    }

    @SuppressWarnings("TooBroadScope")
    @Test
    public void add_wall() {
        List<Point> result;
        final List<Point> movement = new ArrayList<>();
        //add vertical wall
        final Point point1 = new Point(1, 2);
        final Point point2 = new Point(1, 0);
        movement.add(point1);
        movement.add(point2);

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        result = game.iterationOfGame(movement);
        assertTrue(result.size() == 2);
        assertTrue(result.get(0).getFirstCoordinate() == 15);
        assertTrue(result.get(0).getSecondCoordinate() == 16);
        assertTrue(result.get(1).getFirstCoordinate() == 15);
        assertTrue(result.get(1).getSecondCoordinate() == 14);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId2));

        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point1) == 'W');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(1, 1)) == 'W');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point2) == 'W');

        assertTrue(game.getPlayers().get(1).getField().getCellStatus(new Point(15, 14)) == 'W');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(new Point(15, 15)) == 'W');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(new Point(15, 16)) == 'W');
        movement.clear();

        //add horizontal wall
        final Point point3 = new Point(0, 1);
        final Point point4 = new Point(2, 1);
        movement.add(point3);
        movement.add(point4);

        result = game.iterationOfGame(movement);
        assertTrue(result.get(0).getFirstCoordinate() == 14);
        assertTrue(result.get(0).getSecondCoordinate() == 15);
        assertTrue(result.get(1).getFirstCoordinate() == 16);
        assertTrue(result.get(1).getSecondCoordinate() == 15);

        assertTrue(game.getPlayers().get(1).getField().getCellStatus(point3) == 'W');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(new Point(1, 1)) == 'W');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(point4) == 'W');

        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(14, 15)) == 'W');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(15, 15)) == 'W');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(16, 15)) == 'W');
    }

    @Test
    public void wall_is_in_front_of_the_player() {
        final List<Point> movement = new ArrayList<>();
        //add vertical wall if front of the second player
        final Point point1 = new Point(15, 9);
        final Point point2 = new Point(15, 7);
        movement.add(point1);
        movement.add(point2);
        game.iterationOfGame(movement);
        movement.clear();

        final Point point3 = new Point(2, 8);
        movement.add(point3);
        assertNull(game.iterationOfGame(movement));
        movement.clear();

        final Point point4 = new Point(4, 8);
        movement.add(point4);
        assertNull(game.iterationOfGame(movement));
        movement.clear();

        final Point point5 = new Point(0, 6);
        movement.add(point5);
        assertNotNull(game.iterationOfGame(movement));
    }

    @Test
    public void wall_on_wall() {
        add_wall();
        final List<Point> movement = new ArrayList<>();
        final Point point1 = new Point(0, 1);
        final Point point2 = new Point(2, 1);
        movement.add(point1);
        movement.add(point2);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertNull(game.iterationOfGame(movement));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
    }

    @Test
    public void wall_on_player() {
        final List<Point> movement = new ArrayList<>();
        final Point point1 = new Point(16, 9);
        final Point point2 = new Point(16, 7);
        movement.add(point1);
        movement.add(point2);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertNull(game.iterationOfGame(movement));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
    }

    @Test
    public void walls_on_the_cells_for_players() {
        final List<Point> movement = new ArrayList<>();
        final Point point1 = new Point(2, 9);
        final Point point2 = new Point(2, 7);
        movement.add(point1);
        movement.add(point2);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertNull(game.iterationOfGame(movement));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        movement.clear();

        final Point point3 = new Point(0, 0);
        final Point point4 = new Point(2, 0);
        movement.add(point3);
        movement.add(point4);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertNull(game.iterationOfGame(movement));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
    }

    @Test
    public void jump_over_player_and_wall() {
        final List<Point> movement = new ArrayList<>();
        final Point point1 = new Point(3, 9);
        final Point point2 = new Point(3, 7);
        movement.add(point1);
        movement.add(point2);
        game.iterationOfGame(movement);
        movement.clear();
        game.getPlayers().get(0).getField().occupyCellByPlayer(new Point(2, 8), 'E');
        game.goToNextPlayerNumber();
        final Point point3 = new Point(4, 8);
        movement.add(point3);
        assertNull(game.iterationOfGame(movement));
    }
}
