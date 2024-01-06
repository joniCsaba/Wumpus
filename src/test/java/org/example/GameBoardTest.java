package org.example;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard gameBoard;

    @Test
    void testConstructor() {
        int size = 5;
        GameBoard gameBoard = new GameBoard(size);
        assertNotNull(gameBoard.getBoard());
        assertEquals(size, gameBoard.getSize());
        // Further assertions to check the initial state of the board
    }

    @Test
    void testInitializeBoard() {
        int size = 5;
        GameBoard gameBoard = new GameBoard(size);
        gameBoard.initializeBoard();
        assertNotNull(gameBoard.getBoard());
        // Assertions to check the walls and empty spaces are correctly placed
    }

    @Test
    void testAddElement() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        assertEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[2][2]);
        // Add more assertions for other tiles and edge cases
    }

    @Test
    void testRemoveElement() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.removeElement(2, 2);
        assertEquals(GameBoard.Tile.EMPTY, gameBoard.getBoard()[2][2]);
        // Additional checks for removing other elements
    }

    @Test
    void testIsValidPosition() {
        GameBoard gameBoard = new GameBoard(5);
        assertTrue(gameBoard.isValidPosition(1, 1));
        assertFalse(gameBoard.isValidPosition(-1, 1));
        // More assertions for other cases
    }

    @Test
    void testMoveHero() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.moveHeroBasedOnDirection();
        // Assertions to check if hero moved correctly
    }

    @Test
    void testGameOver() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.setGameOver(true);
        assertTrue(gameBoard.isGameOver());
    }

    @Test
    void testPickUpGoldCommand() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO); // Place HERO
        gameBoard.addElement(2, 2, GameBoard.Tile.GOLD); // Place GOLD at HERO's position
        gameBoard.pickUpGoldCommand(); // Attempt to pick up GOLD
        assertTrue(gameBoard.isGoldPickedUp()); // Check if gold is picked up
    }


    @Test
    void testProcessArrowShot() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.WUMPUS);
        boolean result = gameBoard.processArrowShot(2, 1, Hero.Direction.EAST);
        assertTrue(result);
        // Additional test cases for missing the shot or hitting a wall
    }

    // Additional tests written here...

    @Test
    void testGameOverByWumpus() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO); // Place HERO
        gameBoard.addElement(2, 3, GameBoard.Tile.WUMPUS); // Place WUMPUS adjacent to HERO
        gameBoard.getHero().setDirection(Hero.Direction.EAST); // Set HERO direction towards WUMPUS
        gameBoard.moveHeroBasedOnDirection(); // Move HERO towards WUMPUS
        assertTrue(gameBoard.isGameOver()); // Check if game over is true
    }


    @Test
    void testVictoryCondition() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO); // Place HERO at the starting position
        gameBoard.addElement(1, 2, GameBoard.Tile.GOLD); // Place GOLD next to HERO
        gameBoard.getHero().setDirection(Hero.Direction.EAST); // Set HERO direction towards GOLD
        gameBoard.moveHeroBasedOnDirection(); // Move HERO to GOLD
        gameBoard.pickUpGoldCommand(); // Pick up GOLD
        gameBoard.getHero().setDirection(Hero.Direction.WEST); // Set HERO direction back to starting position
        gameBoard.moveHeroBasedOnDirection(); // Move HERO back to starting position
        assertTrue(gameBoard.isGameOver()); // Check if game over is true
        assertTrue(gameBoard.getHero().hasGold()); // Check if HERO has GOLD
    }


    @Test
    void testSerialization() throws Exception {
        // Create and set up the original GameBoard
        GameBoard originalGameBoard = new GameBoard(5);
        originalGameBoard.addElement(2, 2, GameBoard.Tile.HERO);

        // Serialize the originalGameBoard to XML
        JAXBContext jaxbContext = JAXBContext.newInstance(GameBoard.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(originalGameBoard, writer);
        String xmlContent = writer.toString();

        // Deserialize from XML to a new GameBoard object
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xmlContent);
        GameBoard deserializedGameBoard = (GameBoard) unmarshaller.unmarshal(reader);

        // Assertions to compare the state of the original and deserialized game boards
        assertEquals(originalGameBoard.getSize(), deserializedGameBoard.getSize());
        // Add more assertions as needed to verify the correctness of the deserialized object
    }
    @Test
    void testRemoveElement_ValidPosition() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.removeElement(2, 2);
        assertEquals(GameBoard.Tile.EMPTY, gameBoard.getBoard()[2][2]);
    }

    @Test
    void testRemoveElement_InvalidPosition() {
        GameBoard gameBoard = new GameBoard(5);
        // Assuming positions outside the board are invalid
        gameBoard.removeElement(5, 5);
        // Add assertions to verify that the method handles invalid positions gracefully
    }
    @Test
    void testMoveHero_TowardsWall() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        // Assuming that NORTH direction is towards the wall
        gameBoard.getHero().setDirection(Hero.Direction.NORTH);
        gameBoard.moveHeroBasedOnDirection();
        // Verify hero hasn't moved into the wall
        assertEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[1][1]);
    }
    @Test
    void testCheckForHazards_OnWumpus() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.addElement(2, 2, GameBoard.Tile.WUMPUS);
        gameBoard.checkForHazards(2, 2);
        assertTrue(gameBoard.isGameOver());
    }
    @Test
    void testPickUpGoldCommand_WithGold() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.addElement(1, 1, GameBoard.Tile.GOLD);
        gameBoard.pickUpGoldCommand();
        assertTrue(gameBoard.isGoldPickedUp());
    }
    @Test
    void testPerformPostPlacementActions_PlaceHero() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        // Verify post-placement actions
        assertTrue(gameBoard.isHeroPlaced());
    }
    @Test
    void testCheckVictoryCondition_Victory() {
        // Initialize the game board
        GameBoard gameBoard = new GameBoard(5);

        // Set the starting position for the hero
        int startingRow = 1;
        int startingCol = 1;
        gameBoard.setStartingRow(startingRow);
        gameBoard.setStartingColumn(startingCol);

        // Place the hero at the starting position
        gameBoard.addElement(startingRow, startingCol, GameBoard.Tile.HERO);

        // Place gold at a different position
        int goldRow = 2;
        int goldCol = 2;
        gameBoard.addElement(goldRow, goldCol, GameBoard.Tile.GOLD);

        // Move the hero to the position of the gold
        gameBoard.getHero().setRow(goldRow);
        gameBoard.getHero().setColumn(goldCol);
        gameBoard.pickUpGoldCommand(); // Hero picks up the gold

        // Move the hero back to the starting position
        gameBoard.getHero().setRow(startingRow);
        gameBoard.getHero().setColumn(startingCol);

        // Call the method to check victory condition
        gameBoard.checkVictoryCondition();

        // Check if the game over condition is true after picking up gold and returning to the start
        assertTrue(gameBoard.isGameOver(), "The game should be over when the hero returns with gold.");

        // Check if the hero has the gold and the game state reflects the victory
        assertTrue(gameBoard.getHero().hasGold(), "The hero should have gold upon victory.");
    }
    @Test
    void testRemoveElement_NonExistent() {
        GameBoard gameBoard = new GameBoard(5);
        // Try to remove an element that doesn't exist
        gameBoard.removeElement(1, 1);
        // Assert the state of the board is as expected after removal attempt
    }
    @Test
    void testCheckForHazards_NoHazard() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.checkForHazards(1, 1);
        // Assert the state of the game is unchanged
    }
    @Test
    void testMoveHero_Boundary() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        // Attempt to move hero towards a boundary (e.g., towards the north wall)
        gameBoard.getHero().setDirection(Hero.Direction.NORTH);
        gameBoard.moveHeroBasedOnDirection();
        // Verify hero's position is unchanged due to the wall
    }
    @Test
    void testPickUpGoldCommand_AlreadyPicked() {
        GameBoard gameBoard = new GameBoard(5);
        // Setup the scenario where the hero already picked up the gold
        // ...
        gameBoard.pickUpGoldCommand();
        // Assert that the game state reflects that gold cannot be picked up again
    }
    @Test
    void testPerformPostPlacementActions_Overlap() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.addElement(1, 1, GameBoard.Tile.GOLD); // Place GOLD where HERO already exists
        // Assert the post-placement actions are correctly handled
    }
    @Test
    void testCanPlaceElementAt_Invalid() {
        GameBoard gameBoard = new GameBoard(5);
        // Assert that you cannot place an element out of bounds
        assertFalse(gameBoard.canPlaceElementAt(5, 5, GameBoard.Tile.HERO));
    }



        @Test
        void testRemoveWumpus() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(2, 2, GameBoard.Tile.WUMPUS);
            gameBoard.removeElement(2, 2);
            assertFalse(gameBoard.getWumpusPositions().contains(new SimplePoint(2, 2)));
        }

        @Test
        void testRemovePit() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(3, 3, GameBoard.Tile.PIT);
            gameBoard.removeElement(3, 3);
            assertFalse(gameBoard.getPitPositions().contains(new SimplePoint(3, 3)));
        }

        @Test
        void testRemoveGold() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(1, 1, GameBoard.Tile.GOLD);
            gameBoard.removeElement(1, 1);
            assertFalse(gameBoard.isGoldPlaced());
        }

    @Test
    void testHeroEncounterWithPit() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.addElement(2, 3, GameBoard.Tile.PIT);
        // Assuming the hero is facing east
        gameBoard.getHero().setDirection(Hero.Direction.EAST);
        gameBoard.getHero().setArrows(3); // Set the initial arrow count
        gameBoard.moveHeroBasedOnDirection();
        // Hero encounters pit, should lose an arrow
        assertEquals(2, gameBoard.getHero().getArrows(), "Hero should have 2 arrows after encountering a pit.");
    }



    @Test
        void testVictoryConditionAfterPickingUpGold() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(0, 0, GameBoard.Tile.HERO);
            gameBoard.addElement(0, 0, GameBoard.Tile.GOLD);
            gameBoard.pickUpGoldCommand();
            gameBoard.moveHeroBasedOnDirection(); // Assume the hero moves back to the starting position
            gameBoard.checkVictoryCondition();
            assertTrue(gameBoard.isGameOver());
        }
    @Test
    public void testTileToChar() {
        GameBoard gameBoard = new GameBoard(); // Setup your game board accordingly
        assertEquals('.', gameBoard.tileToChar(GameBoard.Tile.EMPTY));
        assertEquals('#', gameBoard.tileToChar(GameBoard.Tile.WALL));
        assertEquals('O', gameBoard.tileToChar(GameBoard.Tile.PIT));
        assertEquals('W', gameBoard.tileToChar(GameBoard.Tile.WUMPUS));
        assertEquals('H', gameBoard.tileToChar(GameBoard.Tile.HERO));
        assertEquals('G', gameBoard.tileToChar(GameBoard.Tile.GOLD));

        // and so on for other tiles...
    }
    @Test
    public void testCalculateWumpusCount() {
        // Test for size resulting in 1 wumpus
        GameBoard gameBoard = new GameBoard(8);
        assertEquals(1, gameBoard.getWumpusCount());

        // Test for size resulting in 2 wumpuses
        gameBoard = new GameBoard(9); // Size greater than 8 should give us 2 wumpuses
        assertEquals(2, gameBoard.getWumpusCount());

        // Test for size resulting in 3 wumpuses
        gameBoard = new GameBoard(15); // Size greater than 14 should give us 3 wumpuses
        assertEquals(3, gameBoard.getWumpusCount());
    }

    @Test
        void testArrowShotMisses() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
            gameBoard.addElement(2, 3, GameBoard.Tile.EMPTY); // An empty tile
            boolean hit = gameBoard.processArrowShot(2, 2, Hero.Direction.EAST);
            assertFalse(hit);
        }

        @Test
        void testArrowShotHitsWall() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
            boolean hit = gameBoard.processArrowShot(2, 2, Hero.Direction.NORTH);
            assertFalse(hit); // Assuming there is a wall
        }

        @Test
        void testFindStartingPointWithHeroPlaced() {
            GameBoard gameBoard = new GameBoard(5);
            gameBoard.addElement(4, 4, GameBoard.Tile.HERO);
            gameBoard.findStartingPoint();
            assertEquals(4, gameBoard.getStartingRow());
            assertEquals(4, gameBoard.getStartingColumn());
        }

        @Test
        void testFindStartingPointNoHero() {
            GameBoard gameBoard = new GameBoard(5);
            // No hero placed
            gameBoard.findStartingPoint();
            assertNotEquals(-1, gameBoard.getStartingRow()); // -1 would indicate no starting point was found
            assertNotEquals(-1, gameBoard.getStartingColumn());
        }

    @Test
    public void testInitializeBoardCreatesCorrectWallBoundary() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.initializeBoard();
        // Check corners for WALL
        assertEquals(GameBoard.Tile.WALL, gameBoard.getBoard()[0][0]);
        assertEquals(GameBoard.Tile.WALL, gameBoard.getBoard()[0][4]);
        assertEquals(GameBoard.Tile.WALL, gameBoard.getBoard()[4][0]);
        assertEquals(GameBoard.Tile.WALL, gameBoard.getBoard()[4][4]);
        // Check center for EMPTY
        assertEquals(GameBoard.Tile.EMPTY, gameBoard.getBoard()[2][2]);
    }
    @Test
    public void testAddElementValidPositionPlacesElement() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        assertEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[1][1]);
    }

    @Test
    public void testAddElementInvalidPositionDoesNotPlaceElement() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.initializeBoard(); // Ensure the board is initialized to a known state.

        // Copy the initial state of the board.
        GameBoard.Tile[][] initialState = new GameBoard.Tile[gameBoard.getSize()][gameBoard.getSize()];
        for (int i = 0; i < gameBoard.getSize(); i++) {
            for (int j = 0; j < gameBoard.getSize(); j++) {
                initialState[i][j] = gameBoard.getBoard()[i][j];
            }
        }

        // Attempt to add an element at an invalid position.
        gameBoard.addElement(-1, -1, GameBoard.Tile.HERO);

        // Verify the board has not changed.
        for (int i = 0; i < gameBoard.getSize(); i++) {
            for (int j = 0; j < gameBoard.getSize(); j++) {
                assertEquals(initialState[i][j], gameBoard.getBoard()[i][j]);
            }
        }
    }

    @Test
    public void testRemoveElementClearsTile() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.removeElement(1, 1);
        assertEquals(GameBoard.Tile.EMPTY, gameBoard.getBoard()[1][1]);
    }
    @Test
    public void testMoveHeroNorthMovesCorrectly() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.getHero().setDirection(Hero.Direction.NORTH);
        gameBoard.moveHeroBasedOnDirection();
        assertEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[1][2]);
    }
    @Test
    public void testIsWallIdentifiesWalls() {
        GameBoard gameBoard = new GameBoard(5);
        assertTrue(gameBoard.isWall(0, 0));
        assertFalse(gameBoard.isWall(1, 1));
    }
    @Test
    public void testPickUpGoldCommandPicksUpGold() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.GOLD);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.pickUpGoldCommand();
        assertTrue(gameBoard.isGoldPickedUp());
    }

    @Test
    public void testProcessArrowShotHitsWumpus() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.WUMPUS);
        assertTrue(gameBoard.processArrowShot(2, 1, Hero.Direction.NORTH));
        assertEquals(GameBoard.Tile.EMPTY, gameBoard.getBoard()[1][1]);
    }

    @Test
    public void testProcessArrowShotMissesWumpus() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.WUMPUS);
        assertFalse(gameBoard.processArrowShot(2, 1, Hero.Direction.EAST));
        assertEquals(GameBoard.Tile.WUMPUS, gameBoard.getBoard()[1][1]);
    }
    @Test
    public void testCheckVictoryConditionDeclaresVictory() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        gameBoard.addElement(1, 1, GameBoard.Tile.GOLD);
        gameBoard.pickUpGoldCommand();
        gameBoard.setStartingRow(1);
        gameBoard.setStartingColumn(1);
        gameBoard.checkVictoryCondition();
        assertTrue(gameBoard.isGameOver());
    }
    @Test
    public void testTileAdapterMarshalling() throws Exception {
        GameBoard.TileAdapter adapter = new GameBoard().new TileAdapter();
        assertEquals("HERO", adapter.marshal(GameBoard.Tile.HERO));
    }

    @Test
    public void testTileAdapterUnmarshalling() throws Exception {
        GameBoard.TileAdapter adapter = new GameBoard().new TileAdapter();
        assertEquals(GameBoard.Tile.HERO, adapter.unmarshal("HERO"));
    }
    @Test
    public void testCheckHeroAndGoldPlacement() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(2, 2, GameBoard.Tile.HERO);
        gameBoard.addElement(3, 3, GameBoard.Tile.GOLD);
        gameBoard.checkHeroAndGoldPlacement();
        assertTrue(gameBoard.isHeroPlaced());
        assertTrue(gameBoard.isGoldPlaced());
    }
    @Test
    public void testCalculateWumpusAndPitPositions() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.WUMPUS);
        gameBoard.addElement(2, 2, GameBoard.Tile.PIT);
        gameBoard.calculateWumpusAndPitPositions();
        assertTrue(gameBoard.getWumpusPositions().contains(new SimplePoint(1, 1)));
        assertTrue(gameBoard.getPitPositions().contains(new SimplePoint(2, 2)));
    }
    @Test
    public void testRecreateHeroIfNeeded() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.setHeroPlaced(true);
        gameBoard.recreateHeroIfNeeded();
        assertNotNull(gameBoard.getHero());
    }
    @Test
    public void testProcessArrowShotWithWall() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.initializeBoard();
        assertFalse(gameBoard.processArrowShot(1, 1, Hero.Direction.NORTH));
    }
    @Test
    public void testAddElementWithValidPosition() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(1, 1, GameBoard.Tile.HERO);
        assertEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[1][1]);
    }

    @Test
    public void testAddElementWithInvalidPosition() {
        GameBoard gameBoard = new GameBoard(5);
        gameBoard.addElement(5, 5, GameBoard.Tile.HERO); // Assuming out of bounds
        assertNotEquals(GameBoard.Tile.HERO, gameBoard.getBoard()[0][0]);
    }
    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard(5);
        gameBoard.initializeBoard();
        // Set up initial conditions if necessary
    }



}



