package application.game.logic;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int dimension;
    private List<Player> players = new ArrayList<>(2);
    private int playerNumber = 0;
    private boolean isFinished = false;
    private String error = null;
    private Long winer = null;

    private static final int JUMP_STEP = 4;
    private static final int NORMAL_STEP = 2;

    private int nextPlayerNumber() {
        return (playerNumber + 1) % 2;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getError() {
        return error;
    }

    public Long getWiner() {
        return winer;
    }

    public Game(int dimension, Long userId1, Long userId2) {
        this.dimension = dimension;
        players.add(new Player(dimension, userId1));
        players.add(new Player(dimension, userId2));
    }

    @SuppressWarnings("ConstantConditions")
    public List<Point> iterationOfGame(List<Point> points) {
        error = null;
        for (Point point: points) {
            if (!checkCoordinate(point)) {
                error = "Incorrect coordinates";
                return null;
            }
        }
        final Player curPlayer = players.get(playerNumber);
        final Player enemyPlayer = players.get(nextPlayerNumber());
        final List<Point> result = new ArrayList<>();
        switch (points.size()) {
            case 1:
                if (!canMove(points.get(0))) {
                    error = "Can't go to this point";
                    return null;
                }
                curPlayer.getField().clearCell(curPlayer.getLocation());
                curPlayer.setLocation(points.get(0));
                if (curPlayer.haveWon()) {
                    isFinished = true;
                    winer = curPlayer.getUserId();
                    return null;
                }
                curPlayer.getField().occupyCellByPlayer(points.get(0), 'M');

                enemyPlayer.getField().clearCell(recountCoordinates(curPlayer.getLocation()));
                enemyPlayer.getField().occupyCellByPlayer(recountCoordinates(points.get(0)), 'E');

                result.add(recountCoordinates(points.get(0)));
                break;
            case 2:
                final Wall myWall = buildWall(points.get(0), points.get(1));
                if (myWall == null) {
                    error = "Can't add wall";
                    return null;
                }
                players.get(playerNumber).getField().addWall(myWall);

                final Wall enemyWall = buildWall(recountCoordinates(points.get(0)), recountCoordinates(points.get(1)));
                players.get(nextPlayerNumber()).getField().addWall(enemyWall);

                result.add(enemyWall.getLocation().get(0));
                result.add(enemyWall.getLocation().get(2));
                break;
            default:
                error = "Wrong number of points";
                return null;
        }
        playerNumber = nextPlayerNumber();
        return result;
    }

    private Point recountCoordinates(Point point) {
        return new Point(2 * (dimension - 1) - point.getFirstCoordinate(),
                2 * (dimension - 1) - point.getSecondCoordinate());
    }

    @SuppressWarnings({"OverlyComplexBooleanExpression", "OverlyComplexMethod"})
    private boolean canMove(Point point) {
        final Field field = players.get(playerNumber).getField();
        if (field.getCellStatus(point) != 'F') {
            return false;
        }
        final Point curLocation = players.get(playerNumber).getLocation();
        //Проверка на то, движется ли игрок только по горизонтали или только по вертикали
        if ((point.getFirstCoordinate() - curLocation.getFirstCoordinate()) == 0
                ^ (point.getSecondCoordinate() - curLocation.getSecondCoordinate()) == 0) {
            final Point middle = new Point((point.getFirstCoordinate() + curLocation.getFirstCoordinate()) / 2,
                    (point.getSecondCoordinate() + curLocation.getSecondCoordinate()) / 2);
            switch (Math.abs(point.getFirstCoordinate() - curLocation.getFirstCoordinate())) {
                case NORMAL_STEP:
                    return field.getCellStatus(middle) == 'F';
                case JUMP_STEP:
                    return (field.getCellStatus(middle) == 'E'
                            && field.getCellStatus(new Point(middle.getFirstCoordinate() - 1, middle.getSecondCoordinate())) == 'F'
                            && field.getCellStatus(new Point(middle.getFirstCoordinate() + 1, middle.getSecondCoordinate())) == 'F');
                default:
            }
            switch (Math.abs(point.getSecondCoordinate() - curLocation.getSecondCoordinate())) {
                case NORMAL_STEP:
                    return field.getCellStatus(middle) == 'F';
                case JUMP_STEP:
                    return (field.getCellStatus(middle) == 'E'
                            && field.getCellStatus(new Point(middle.getFirstCoordinate(), middle.getSecondCoordinate() - 1)) == 'F'
                            && field.getCellStatus(new Point(middle.getFirstCoordinate(), middle.getSecondCoordinate() + 1)) == 'F');
                default:
            }
        } else {
            return false;
        }
        return false;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean checkCoordinate(Point point) {
        return (point.getFirstCoordinate() > 0 && point.getFirstCoordinate() < (dimension * 2 - 1)
                && point.getSecondCoordinate() > 0 && point.getSecondCoordinate() < (dimension * 2 - 1));
    }

    //begin - верхняя или левая
    //end - нижняя или правая
    private Wall buildWall(Point begin, Point end) {
        if (begin.getFirstCoordinate() == end.getFirstCoordinate()
                && Math.abs(begin.getSecondCoordinate() - end.getSecondCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(begin.getFirstCoordinate(), begin.getSecondCoordinate() - 1), end);
            if (canAddWall(wall)) {
                return wall;
            } else {
                return null;
            }
        }
        if (begin.getSecondCoordinate() == end.getSecondCoordinate()
                && Math.abs(end.getFirstCoordinate() - begin.getFirstCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(end.getFirstCoordinate() - 1, end.getSecondCoordinate()), end);
            if (canAddWall(wall)) {
                return wall;
            } else {
                return null;
            }
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
