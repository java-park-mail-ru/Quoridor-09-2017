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
public class PlayerMovementTest {
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

    @Test
    public void check_initial_configuration() {
        assertTrue(game.getPlayers().get(0).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(0).getLocation().getSecondCoordinate() == 8);

        assertTrue(game.getPlayers().get(1).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(1).getLocation().getSecondCoordinate() == 8);

        assertTrue(Objects.equals(game.getPlayers().get(0).getUserId(), userId1));
        assertTrue(Objects.equals(game.getPlayers().get(1).getUserId(), userId2));

        final Point self = new Point(0, 8);
        final Point enemy = new Point(16, 8);
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(self) == 'M');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(enemy) == 'E');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(self) == 'M');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(enemy) == 'E');
    }

    @Test
    public void check_wrong_number_of_incoming_points() {
        assertNull(game.iterationOfGame(null));
        final List<Point> points = new ArrayList<>();
        final Point point = new Point(2, 2);
        points.add(point);
        points.add(point);
        points.add(point);
        assertNull(game.iterationOfGame(points));
    }

    @Test
    public void simple_move() {
        //future location of first player
        final Point point1 = new Point(2, 8);
        final List<Point> movement = new ArrayList<>();
        movement.add(point1);

        //result - location of first player in relation to the second player
        final List<Point> result = game.iterationOfGame(movement);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getFirstCoordinate() == 14);
        assertTrue(result.get(0).getSecondCoordinate() == 8);

        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(0, 8)) == 'F');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(new Point(2, 8)) == 'M');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(result.get(0)) == 'E');
        assertTrue(game.getPlayers().get(1).getField().getCellStatus(new Point(16, 8)) == 'F');
    }

    @Test
    public void check_queue_of_players() {
        final Point point1 = new Point(2, 8);
        final List<Point> movement = new ArrayList<>();
        movement.add(point1);

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        game.iterationOfGame(movement);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId2));
        game.iterationOfGame(movement);
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
    }

    @SuppressWarnings("TooBroadScope")
    @Test
    public void check_invalid_coordinates() {
        final Point point1 = new Point(1, 8);
        final Point point2 = new Point(0, 7);
        final Point point3 = new Point(0, 9);
        final Point point4 = new Point(-1, 8);
        final List<Point> points = new ArrayList<>();

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        points.add(point1);
        assertNull(game.iterationOfGame(points));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getSecondCoordinate() == 8);
        points.clear();

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        points.add(point2);
        assertNull(game.iterationOfGame(points));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getSecondCoordinate() == 8);
        points.clear();

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        points.add(point3);
        assertNull(game.iterationOfGame(points));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getSecondCoordinate() == 8);
        points.clear();

        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        points.add(point4);
        assertNull(game.iterationOfGame(points));
        assertTrue(Objects.equals(game.getPlayers().get(game.getPlayerNumber()).getUserId(), userId1));
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getFirstCoordinate() == 0);
        assertTrue(game.getPlayers().get(game.getPlayerNumber()).getLocation().getSecondCoordinate() == 8);
        points.clear();
    }

    @Test
    public void check_jump_and_win() {
        final Long userId3 = 3L;
        final Long userId4 = 4L;
        final Game gameDim3 = new Game(3, userId3, userId4);
        List<Point> movement = new ArrayList<>();

        final Point point1 = new Point(2, 2);
        movement.add(point1);
        gameDim3.iterationOfGame(movement);
        movement.clear();

        //second player can't occupy this cell, because this cell is already occupied
        assertNull(gameDim3.iterationOfGame(movement));
        movement.clear();

        //second player jump over first player
        assertFalse(gameDim3.isFinished());
        assertTrue(Objects.equals(gameDim3.getPlayers().get(gameDim3.getPlayerNumber()).getUserId(), userId4));
        final Point point2 = new Point(4, 2);
        movement.add(point2);
        movement = gameDim3.iterationOfGame(movement);
        assertEquals(movement.get(0).getFirstCoordinate(), 0);
        assertEquals(movement.get(0).getSecondCoordinate(), 2);
        assertTrue(gameDim3.isFinished());
        assertTrue(gameDim3.getPlayers().get(1).haveWon());
        //check final coordinates
        assertTrue(gameDim3.getPlayers().get(1).getLocation().getFirstCoordinate() == 4);
        assertTrue(gameDim3.getPlayers().get(1).getLocation().getSecondCoordinate() == 2);
    }

    private void put_first_player_at_the_center() {
        final Point point = new Point(8, 8);
        game.getPlayers().get(0).setLocation(point);
        game.getPlayers().get(0).getField().occupyCellByPlayer(point, 'M');
    }

    @Test
    public void check_ability_to_move() {
        //put first player in center of field
        final Point point = new Point(8, 8);
        put_first_player_at_the_center();
        final List<Point> movement = new ArrayList<>();
        final Point point1 = new Point(12, 8);
        final Point point2 = new Point(4, 8);
        final Point point3 = new Point(8, 12);
        final Point point4 = new Point(8, 4);

        //first player can't jump, because there are no players here
        movement.add(point1);
        assertNull(game.iterationOfGame(movement));
        movement.clear();
        movement.add(point2);
        assertNull(game.iterationOfGame(movement));
        movement.clear();
        movement.add(point3);
        assertNull(game.iterationOfGame(movement));
        movement.clear();
        movement.add(point4);
        assertNull(game.iterationOfGame(movement));
        movement.clear();

        //check forward jump
        //put second player in front of the first player
        final Point point5 = new Point(10, 8);
        game.getPlayers().get(0).getField().occupyCellByPlayer(point5, 'E');
        movement.add(point1);
        game.iterationOfGame(movement);
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point) == 'F');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point1) == 'M');
        assertTrue(game.getPlayers().get(0).getLocation().getFirstCoordinate() == 12);
        assertTrue(game.getPlayers().get(0).getLocation().getSecondCoordinate() == 8);
        movement.clear();
        //pass the move of second player
        game.goToNextPlayerNumber();
        put_first_player_at_the_center();

        //check back jump back
        final Point point6 = new Point(6, 8);
        game.getPlayers().get(0).getField().occupyCellByPlayer(point6, 'E');
        movement.add(point2);
        game.iterationOfGame(movement);
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point) == 'F');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point2) == 'M');
        assertTrue(game.getPlayers().get(0).getLocation().getFirstCoordinate() == 4);
        assertTrue(game.getPlayers().get(0).getLocation().getSecondCoordinate() == 8);
        movement.clear();
        game.goToNextPlayerNumber();
        put_first_player_at_the_center();

        //check jump up
        final Point point7 = new Point(8, 10);
        game.getPlayers().get(0).getField().occupyCellByPlayer(point7, 'E');
        movement.add(point3);
        game.iterationOfGame(movement);
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point) == 'F');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point3) == 'M');
        assertTrue(game.getPlayers().get(0).getLocation().getFirstCoordinate() == 8);
        assertTrue(game.getPlayers().get(0).getLocation().getSecondCoordinate() == 12);
        movement.clear();
        game.goToNextPlayerNumber();
        put_first_player_at_the_center();

        //check jump down
        final Point point8 = new Point(8, 6);
        game.getPlayers().get(0).getField().occupyCellByPlayer(point8, 'E');
        movement.add(point4);
        game.iterationOfGame(movement);
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point) == 'F');
        assertTrue(game.getPlayers().get(0).getField().getCellStatus(point4) == 'M');
        assertTrue(game.getPlayers().get(0).getLocation().getFirstCoordinate() == 8);
        assertTrue(game.getPlayers().get(0).getLocation().getSecondCoordinate() == 4);
    }
}
