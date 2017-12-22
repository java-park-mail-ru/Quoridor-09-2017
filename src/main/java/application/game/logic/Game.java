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

    public void goToNextPlayerNumber() {
        playerNumber = nextPlayerNumber();
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

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Game(int dimension, Long userId1, Long userId2) {
        this.dimension = dimension;
        players.add(new Player(dimension, userId1));
        players.add(new Player(dimension, userId2));
    }

    @SuppressWarnings({"ConstantConditions", "OverlyComplexMethod"})
    public List<Point> iterationOfGame(List<Point> points) {
        if (points == null) {
            error = "Null is comming as parametr";
            return null;
        }
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
                if (!canMove(players.get(playerNumber).getLocation(), points.get(0), players.get(playerNumber).getField())) {
                    error = "Can't go to this point";
                    return null;
                }

                final Point oldLocation = curPlayer.getLocation();
                curPlayer.getField().clearCell(oldLocation);
                curPlayer.setLocation(points.get(0));
                curPlayer.getField().occupyCellByPlayer(points.get(0), 'M');

                enemyPlayer.getField().clearCell(recountCoordinates(oldLocation));
                enemyPlayer.getField().occupyCellByPlayer(recountCoordinates(points.get(0)), 'E');

                result.add(recountCoordinates(points.get(0)));
                if (curPlayer.haveWon()) {
                    isFinished = true;
                    winer = curPlayer.getUserId();
                    return result;
                }
                break;
            case 2:
                if (!players.get(playerNumber).canAddWall()) {
                    error = "Can't add wall";
                    return null;
                }
                final Wall myWall = buildWall(points.get(0), points.get(1), playerNumber);
                if (myWall == null) {
                    error = "Can't add wall";
                    return null;
                }
                players.get(playerNumber).getField().addWall(myWall);

                final Wall enemyWall = buildWallWithoutChecks(recountCoordinates(points.get(1)), recountCoordinates(points.get(0)));
                players.get(nextPlayerNumber()).getField().addWall(enemyWall);

                players.get(playerNumber).decWallsCount();
                result.add(enemyWall.getLocation().get(0));
                result.add(enemyWall.getLocation().get(2));
                break;
            default:
                error = "Wrong number of points";
                return null;
        }
        goToNextPlayerNumber();
        return result;
    }

    private Point recountCoordinates(Point point) {
        return new Point(2 * (dimension - 1) - point.getFirstCoordinate(),
                2 * (dimension - 1) - point.getSecondCoordinate());
    }

    @SuppressWarnings({"OverlyComplexBooleanExpression", "OverlyComplexMethod"})
    private boolean canMove(Point curLocation, Point point, Field field) {
        if (field.getCellStatus(point) != 'F') {
            return false;
        }
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
        return (point.getFirstCoordinate() >= 0 && point.getFirstCoordinate() < (dimension * 2 - 1)
                && point.getSecondCoordinate() >= 0 && point.getSecondCoordinate() < (dimension * 2 - 1));
    }

    private Wall buildWallWithoutChecks(Point begin, Point end) {
        if (begin.getFirstCoordinate() == end.getFirstCoordinate()
                && Math.abs(begin.getSecondCoordinate() - end.getSecondCoordinate()) == 2) {
            return new Wall(begin, new Point(begin.getFirstCoordinate(), begin.getSecondCoordinate() - 1), end);
        }
        if (begin.getSecondCoordinate() == end.getSecondCoordinate()
                && Math.abs(end.getFirstCoordinate() - begin.getFirstCoordinate()) == 2) {
            return new Wall(begin, new Point(end.getFirstCoordinate() - 1, end.getSecondCoordinate()), end);
        }
        return null;
    }

    //begin - верхняя или левая
    //end - нижняя или правая
    private Wall buildWall(Point begin, Point end, int player) {
        if (begin.getFirstCoordinate() == end.getFirstCoordinate()
                && Math.abs(begin.getSecondCoordinate() - end.getSecondCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(begin.getFirstCoordinate(), begin.getSecondCoordinate() - 1), end);
            if (canAddWall(wall, player)) {
                return wall;
            } else {
                return null;
            }
        }
        if (begin.getSecondCoordinate() == end.getSecondCoordinate()
                && Math.abs(end.getFirstCoordinate() - begin.getFirstCoordinate()) == 2) {
            final Wall wall = new Wall(begin, new Point(end.getFirstCoordinate() - 1, end.getSecondCoordinate()), end);
            if (canAddWall(wall, player)) {
                return wall;
            } else {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean canAddWall(Wall wall, int player) {
        for (Point point : wall.getLocation()) {
            if (players.get(player).getField().getCellStatus(point) != 'F') {
                return false;
            }
        }
        return (wall.getLocation().get(0).getFirstCoordinate() != wall.getLocation().get(1).getFirstCoordinate()
                || wall.getLocation().get(0).getFirstCoordinate() % 2 != 0)
                && (wall.getLocation().get(0).getSecondCoordinate() != wall.getLocation().get(1).getSecondCoordinate()
                || wall.getLocation().get(0).getSecondCoordinate() % 2 != 0)
                && canArriveAtFinish(wall, player);
    }

    public boolean canArriveAtFinish(Wall wall, int player) {
        Field field = new Field(players.get(player).getField());
        field.addWall(wall);
        final int matrixSize = dimension * dimension;
        final int[][] adjacencyMatrix = new int[matrixSize][matrixSize];
        initAdjacencyMatrix(adjacencyMatrix);
        buildAdjacencyMatrix(field, adjacencyMatrix);
        final int[] transitiveClosure = new int[matrixSize];
        initTransitiveClosure(transitiveClosure);
        buildTransitiveClosure(adjacencyMatrix, transitiveClosure, getNumberFromCurLocation(player));
        final boolean result = contentsFinish(transitiveClosure);

        player = (player + 1) % 2;
        field = new Field(players.get(player).getField());
        field.addWall(buildWallWithoutChecks(recountCoordinates(wall.getLocation().get(2)),
                recountCoordinates(wall.getLocation().get(0))));
        initAdjacencyMatrix(adjacencyMatrix);
        buildAdjacencyMatrix(field, adjacencyMatrix);
        initTransitiveClosure(transitiveClosure);
        buildTransitiveClosure(adjacencyMatrix, transitiveClosure, getNumberFromCurLocation(player));
        return result && contentsFinish(transitiveClosure);
    }

    private void initAdjacencyMatrix(int[][] adjacencyMatrix) {
        for (int i = 0; i < dimension * dimension; i++) {
            for (int j = 0; j < dimension * dimension; j++) {
                if (i == j) {
                    adjacencyMatrix[i][j] = 1;
                    continue;
                }
                adjacencyMatrix[i][j] = 0;
            }
        }
    }

    private void buildAdjacencyMatrix(Field field, int[][] adjacencyMatrix) {
        for (int i = 0; i < dimension * dimension; i++) {
            final Point curLocation = getPointFromNumber(i);
            for (int j = 0; j < dimension * dimension; j++) {
                final Point target = getPointFromNumber(j);
                if (canMove(curLocation, target, field)) {
                    adjacencyMatrix[i][j] = 1;
                }
            }
        }
    }

    private Point getPointFromNumber(int pointNumber) {
        return new Point((pointNumber / dimension) * 2, (pointNumber % dimension) * 2);
    }

    private int getNumberFromCurLocation(int player) {
        final Point point = players.get(player).getLocation();
        return ((point.getFirstCoordinate() / 2) * dimension + (point.getSecondCoordinate() / 2));
    }

    private void initTransitiveClosure(int[] transitiveClosure) {
        for (int i = 0; i < transitiveClosure.length; i++) {
            transitiveClosure[i] = -1;
        }
    }

    private void buildTransitiveClosure(int[][] adjacencyMatrix, int[] transitiveClosure, int rowNumber) {
        for (int j = 0; j < dimension * dimension; j++) {
            if ((adjacencyMatrix[rowNumber][j] == 1) && (transitiveClosure[j] < 0)) {
                transitiveClosure[j] = 1;
                buildTransitiveClosure(adjacencyMatrix, transitiveClosure, j);
            }
        }
    }

    private boolean contentsFinish(int[] transitiveClosure) {
        for (int i = dimension * dimension - dimension; i < dimension * dimension; i++) {
            if (transitiveClosure[i] > 0) {
                return true;
            }
        }
        return false;
    }
}