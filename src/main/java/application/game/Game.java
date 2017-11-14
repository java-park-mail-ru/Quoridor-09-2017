package application.game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int dimension;
    private List<Player> players = new ArrayList<>(2);
    private int playerNumber = 0;

    private int nextPlayerNumber() {
        return (playerNumber + 1) % 2;
    }

    public Game(int dimension) {
        this.dimension = dimension;
        players.add(new Player(dimension));
        players.add(new Player(dimension));
    }

    public String iterationOfGame(Point ... points) {
        for (Point point: points) {
            if (!checkCoordinate(point)) {
                return "Incorrect coordinates";
            }
        }
        final Player curPlayer = players.get(playerNumber);
        final Player enemyPlayer = players.get(nextPlayerNumber());
        String resultMessage = "Ok";
        switch (points.length) {
            case 1:
                if (!canMove(points[0])) {
                    return "Can't go to this point";
                }
                curPlayer.getField().clearCell(curPlayer.getLocation());
                curPlayer.setLocation(points[0]);
                if (curPlayer.haveWon()) {
                    return "Player number " + playerNumber + " have won!";
                }
                curPlayer.getField().occupyCellByPlayer(points[0], 'M');
                enemyPlayer.getField().clearCell(recountCoordinates(curPlayer.getLocation()));
                enemyPlayer.getField().occupyCellByPlayer(recountCoordinates(points[0]), 'E');
                break;
            case 2:
                final Wall myWall = buildWall(points[0], points[1]);
                if (myWall == null) {
                    return "Can't add wall";
                }
                players.get(playerNumber).getField().addWall(myWall);
                final Wall enemyWall = buildWall(recountCoordinates(points[0]), recountCoordinates(points[1]));
                players.get(nextPlayerNumber()).getField().addWall(enemyWall);
                break;
            default:
                resultMessage = "Wrong number of points";
        }
        playerNumber = nextPlayerNumber();
        return resultMessage;
    }

    private Point recountCoordinates(Point point) {
        return new Point(2 * (dimension - 1) - point.getxCoordinate(),
                2 * (dimension - 1) - point.getyCoordinate());
    }

    @SuppressWarnings({"OverlyComplexBooleanExpression", "OverlyComplexMethod"})
    private boolean canMove(Point point) {
        final Field field = players.get(playerNumber).getField();
        if (field.getCellStatus(point) != 'F') {
            return false;
        }
        final Point curLocation = players.get(playerNumber).getLocation();
        //Проверка на то, движется ли игрок только по горизонтали или только по вертикали
        if ((point.getxCoordinate() - curLocation.getxCoordinate()) == 0 ^
                (point.getyCoordinate() - curLocation.getyCoordinate()) == 0) {
            final Point middle = new Point((point.getxCoordinate() + curLocation.getxCoordinate()) / 2,
                    (point.getyCoordinate() + curLocation.getyCoordinate()) / 2);
            switch (Math.abs(point.getxCoordinate() - curLocation.getxCoordinate())) {
                case 2:
                    return field.getCellStatus(middle) == 'F';
                case 4:
                    return (field.getCellStatus(middle) == 'E' &&
                            field.getCellStatus(new Point(middle.getxCoordinate() - 1, middle.getyCoordinate())) == 'F' &&
                            field.getCellStatus(new Point(middle.getxCoordinate() + 1, middle.getyCoordinate())) == 'F');
                default:
            }
            switch (Math.abs(point.getyCoordinate() - curLocation.getyCoordinate())) {
                case 2:
                    return field.getCellStatus(middle) == 'F';
                case 4:
                    return (field.getCellStatus(middle) == 'E' &&
                            field.getCellStatus(new Point(middle.getxCoordinate(), middle.getyCoordinate() - 1)) == 'F' &&
                            field.getCellStatus(new Point(middle.getxCoordinate(), middle.getyCoordinate() + 1)) == 'F');
                default:
            }
        } else {
            return false;
        }
        return false;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean checkCoordinate(Point point) {
        return (point.getxCoordinate() > 0 && point.getxCoordinate() < (dimension * 2 - 1) &&
        point.getyCoordinate() > 0 && point.getyCoordinate() < (dimension * 2 - 1));
    }

    //точки в списке могут храниться не по порядку, но это не важно
    //begin - верхняя или левая
    //end - нижняя или правая
    private Wall buildWall(Point begin, Point end) {
        if (begin.getxCoordinate() == end.getxCoordinate() &&
                Math.abs(begin.getyCoordinate() - end.getyCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(begin.getxCoordinate(), begin.getyCoordinate() - 1), end);
            return (canAddWall(wall) ? wall : null);
        }
        if (begin.getyCoordinate() == end.getyCoordinate() &&
                Math.abs(end.getxCoordinate() - begin.getxCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(end.getxCoordinate() - 1, end.getyCoordinate()), end);
            return (canAddWall(wall) ? wall : null);
        }
        return null;
    }

    private boolean canAddWall(Wall wall) {
        for (Point point : wall.getLocation()) {
            if (players.get(playerNumber).getField().getCellStatus(point) != 'F') {
                return false;
            }
        }
        return true;
    }
}
