package application.game;

import java.util.Objects;

public class GameLogic {
    private Field field;
    private int playerCount;
    private int playerNumber;
    private Point newLocation = null;

    public GameLogic(int xSize, int ySize, int playerCount) {
        this.playerCount = playerCount;
        playerNumber = 0;
        field = new Field(xSize, ySize, playerCount);
    }

    /*
     * if wall == null, then player are mooving
     * direction:
     *      0 - forward;
     *      1 - back;
     *      2 - right;
     *      3 - left
     * return:
     *      "Ok", if all operations are valid;
     *      "Victory", if a player wins
     */
    public String iterationOfGame(int direction, Wall wall) {
        if (wall != null) {
            if (canAddWall(wall)) {
                field.addWall(wall);
                return "Ok";
            } else {
                return "Can't add wall: some of cells are occupied";
            }
        } else {
            final Point oldLocation = field.getPlayers().get(playerNumber % playerCount).getLocation();
            String errorMessage = null;
            switch (direction) {
                case 0:
                    errorMessage = moveForward(oldLocation);
                    break;
                case 1:
                    errorMessage = moveBack(oldLocation);
                    break;
                case 2:
                    errorMessage = moveRight(oldLocation);
                    break;
                case 3:
                    errorMessage = moveLeft(oldLocation);
                    break;
                default:
            }
            if (!Objects.equals(errorMessage, "Ok")) {
                field.getPlayers().get(playerNumber % playerCount).setLocation(oldLocation);
                return errorMessage;
            }
            field.clearCell(oldLocation);
            field.occupyCellByPlayer(newLocation);
            playerNumber++;
            return (field.isPlayerOnTheFinish(playerNumber % playerCount) ? "Victory" : "Ok");
        }
    }

    private boolean canAddWall(Wall wall) {
        for (Point point : wall.getLocation()) {
            if (field.getCellStatus(point) != 'F') {
                return false;
            }
        }
        return true;
    }

    private String moveOnSituation(int curPlayer, Point oldLocation,
                                   int xNeighbor, int yNeighbor,
                                   int xDifference, int yDifference) {
        final char nearCellStatus = field.getCellStatus(new Point(oldLocation.getxCoordinate() + xNeighbor,
                oldLocation.getyCoordinate() + yNeighbor));
        if (nearCellStatus == 'F') {
            field.getPlayers().get(curPlayer).changeLocation(xDifference, yDifference);
            newLocation = field.getPlayers().get(curPlayer).getLocation();
            return "Ok";
        } else if (nearCellStatus == 'W') {
            return "Can't move a player: there is a wall";
        }
        if (field.getCellStatus(new Point(oldLocation.getxCoordinate() + xNeighbor * 2,
                oldLocation.getyCoordinate() + yNeighbor * 2)) == 'P') {
            field.getPlayers().get(curPlayer).changeLocation(xDifference * 2, yDifference * 2);
            newLocation = field.getPlayers().get(curPlayer).getLocation();
            return "Ok";
        }
        return "Unknown error";
    }

    private String moveForward(Point oldLocation) {
        final int curPlayer = playerNumber % playerCount;
        switch (curPlayer) {
            case 0:
                return moveOnSituation(curPlayer, oldLocation, 0, 1, 0, 2);
            case 1:
                return moveOnSituation(curPlayer, oldLocation, 0, -1, 0, -2);
            case 2:
                return moveOnSituation(curPlayer, oldLocation, 1, 0, 2, 0);
            case 3:
                return moveOnSituation(curPlayer, oldLocation, -1, 0, -2, 0);
            default:
                return "Unknown error";
        }
    }

    private String moveBack(Point oldLocation) {
        final int curPlayer = playerNumber % playerCount;
        switch (curPlayer) {
            case 0:
                return moveOnSituation(curPlayer, oldLocation, 0, -1, 0, -2);
            case 1:
                return moveOnSituation(curPlayer, oldLocation, 0, 1, 0, 2);
            case 2:
                return moveOnSituation(curPlayer, oldLocation, -1, 0, -2, 0);
            case 3:
                return moveOnSituation(curPlayer, oldLocation, 1, 0, 2, 0);
            default:
                return "Unknown error";
        }
    }

    private String moveRight(Point oldLocation) {
        final int curPlayer = playerNumber % playerCount;
        switch (curPlayer) {
            case 0:
                moveOnSituation(curPlayer, oldLocation, 1, 0, 2, 0);
            case 1:
                moveOnSituation(curPlayer, oldLocation, -1, 0, -2, 0);
            case 2:
                moveOnSituation(curPlayer, oldLocation, 0, -1, 0, -2);
            case 3:
                moveOnSituation(curPlayer, oldLocation, 0, 1, 0, 2);
            default:
                return "Unknown error";
        }
    }

    private String moveLeft(Point oldLocation) {
        final int curPlayer = playerNumber % playerCount;
        switch (curPlayer) {
            case 0:
                moveOnSituation(curPlayer, oldLocation, -1, 0, -2, 0);
            case 1:
                moveOnSituation(curPlayer, oldLocation, 1, 0, 2, 0);
            case 2:
                moveOnSituation(curPlayer, oldLocation, 0, 1, 0, 2);
            case 3:
                moveOnSituation(curPlayer, oldLocation, 0, -1, 0, -2);
            default:
                return "Unknown error";
        }
    }
}
