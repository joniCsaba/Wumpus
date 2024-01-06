package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeroTest {
    private Hero hero;
    private GameBoard gameBoard;

    @BeforeEach
    public void setup() {
        gameBoard = mock(GameBoard.class);
        hero = new Hero(gameBoard, 1, 1, 1);
    }

    @Test
    public void testTurnRight() {
        hero.setDirection(Hero.Direction.NORTH);
        hero.turnRight();
        assertEquals(Hero.Direction.EAST, hero.getDirection());
    }

    @Test
    public void testTurnLeft() {
        hero.setDirection(Hero.Direction.NORTH);
        hero.turnLeft();
        assertEquals(Hero.Direction.WEST, hero.getDirection());
    }

    @Test
    public void testPickUpGold() {
        hero.pickUpGold();
        assertTrue(hero.isHasGold());
    }

    @Test
    public void testSetGameBoard() {
        GameBoard newGameBoard = mock(GameBoard.class);
        hero.setGameBoard(newGameBoard);
        assertEquals(newGameBoard, hero.getGameBoard());
    }

    @Test
    public void testSetDirection() {
        hero.setDirection(Hero.Direction.SOUTH);
        assertEquals(Hero.Direction.SOUTH, hero.getDirection());
    }

    @Test
    public void testIsHasGold() {
        hero.setHasGold(true);
        assertTrue(hero.isHasGold());
    }

    @Test
    public void testSetHasGold() {
        hero.setHasGold(true);
        assertTrue(hero.isHasGold());
    }

    @Test
    public void testShootArrow() {
        when(gameBoard.processArrowShot(anyInt(), anyInt(), any(Hero.Direction.class))).thenReturn(true);
        assertTrue(hero.shootArrow());
        assertEquals(0, hero.getArrows());
    }

    @Test
    public void testShootArrowNoArrowsLeft() {
        hero.setArrows(0);
        assertFalse(hero.shootArrow());
    }
}
