package org.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
@XmlAccessorType(XmlAccessType.FIELD)

public class Hero {

    enum Direction { NORTH, EAST, SOUTH, WEST }
    private transient GameBoard gameBoard;
    @XmlElement
    private int row;
    @XmlElement
    private int column;
    @XmlElement
    private Direction direction;
    @XmlElement
    private int arrows;
    @XmlElement
    private boolean hasGold;
    public Hero() {
    }
    public Hero(GameBoard gameBoard, int row, int column, int wumpusCount) {
        this.gameBoard = gameBoard;
        this.row = row;
        this.column = column;
        this.direction = Direction.NORTH;
        this.arrows = wumpusCount;
        this.hasGold = false;
    }

    public void turnRight() {
        this.direction = Direction.values()[(this.direction.ordinal() + 1) % 4];
    }

    public void turnLeft() {
        this.direction = Direction.values()[(this.direction.ordinal() + 3) % 4];
    }


    public void pickUpGold() {
        this.hasGold = true;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isHasGold() {
        return hasGold;
    }

    public void setHasGold(boolean hasGold) {
        this.hasGold = hasGold;
    }

    public boolean shootArrow() {
        if (this.arrows > 0) {
            this.arrows--;
            System.out.println("A hős lőtt egy nyíllal " + this.direction + " irányba.");
            return gameBoard.processArrowShot(this.row, this.column, this.direction);
        } else {
            System.out.println("Nincsenek több nyilak.");
            return false;
        }
    }




    public void setArrows(int arrows) {
        this.arrows = arrows;
    }




    public Direction getDirection() {
        return direction;
    }

    public int getArrows() {
        return arrows;
    }

    public boolean hasGold() {
        return hasGold;
    }




    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
