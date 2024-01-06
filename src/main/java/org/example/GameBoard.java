package org.example;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;
@XmlRootElement
public class GameBoard {

    public List<SimplePoint> getWumpusPositions() {
        return wumpusPositions;
    }

    public List<SimplePoint> getPitPositions() {
        return pitPositions;
    }

    public int getWumpusCount() {
        return wumpusCount;
    }

    public void setWumpusCount(int wumpusCount) {
        this.wumpusCount = wumpusCount;
    }

    enum Tile { EMPTY, WALL, PIT, WUMPUS, HERO, GOLD }
    private Tile[][] board;

    private int size;
    private int wumpusCount;
    private boolean heroPlaced;
    private boolean goldPlaced;
    private int savedHeroRow;
    private int savedHeroCol;
    private Hero hero;
    private int startingRow;
    private int startingColumn;
    private boolean gameOver;
    private boolean goldPickedUp;
    private int goldRow = -1;
    private int goldCol = -1;
    private List<SimplePoint> wumpusPositions = new ArrayList<>();
    private List<SimplePoint> pitPositions = new ArrayList<>();
    private SimplePoint goldPosition;


    public GameBoard(int size) {
        this.size = size;
        this.board = new Tile[size][size];
        initializeBoard();
        calculateWumpusCount();
        this.hero = null;
        this.gameOver = false;
        this.goldPickedUp = false;
        this.draw();

    }
    public GameBoard() {
        // Ez az alapértelmezett konstruktor szükséges a JAXB szerializációhoz
    }

    void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = (i == 0 || i == size - 1 || j == 0 || j == size - 1) ? Tile.WALL : Tile.EMPTY;
            }
        }
    }
    public void draw() {
        System.out.println("Drawing the game board:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(tileToChar(board[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("Game board drawn.");
    }

    char tileToChar(Tile tile) {
        switch (tile) {
            case EMPTY:
                return '.';
            case WALL:
                return '#';
            case PIT:
                return 'O';
            case WUMPUS:
                return 'W';
            case HERO:
                return 'H';
            case GOLD:
                return 'G';
            default:
                return ' ';
        }
    }

    void calculateWumpusCount() {
        wumpusCount = (size <= 8) ? 1 : (size <= 14) ? 2 : 3;
    }
    public void addElement(int row, int col, Tile element) {
        if (row < 0 || col < 0 || row >= size || col >= size) {
            handleInvalidPosition(row, col);
            return;
        }

        if (!canPlaceElementAt(row, col, element)) {
            handleElementPlacementError(row, col, element);
            return;
        }

        placeElement(row, col, element);
    }

    boolean canPlaceElementAt(int row, int col, Tile element) {
        // Check if the position is within the bounds of the board array
        if (row < 0 || col < 0 || row >= board.length || col >= board[row].length) {
            return false;
        }
        // Existing logic to determine if the placement is valid based on the type of tile
        return !(board[row][col] == Tile.WALL && element != Tile.HERO && element != Tile.GOLD);
    }


    private void placeElement(int row, int col, Tile element) {
        board[row][col] = element;
        performPostPlacementActions(element, row, col);
    }

    private void performPostPlacementActions(Tile element, int row, int col) {
        switch (element) {
            case HERO:
                setHero(row, col);
                break;
            case GOLD:
                setGold(row, col);
                break;
            case WUMPUS:
                wumpusPositions.add(new SimplePoint(row, col));
                break;
            case PIT:
                pitPositions.add(new SimplePoint(row, col));
                break;
            // Consider adding a default case for unexpected elements
        }
    }

    private void setHero(int row, int col) {
        heroPlaced = true;
        hero = new Hero(this, row, col, wumpusCount);
        startingRow = row;
        startingColumn = col;
    }

    private void setGold(int row, int col) {
        goldPlaced = true;
        goldRow = row;
        goldCol = col;
        goldPosition = new SimplePoint(row, col);
    }

    private void handleInvalidPosition(int row, int col) {

        System.err.println("Invalid position: [" + row + ", " + col + "]");
    }

    private void handleElementPlacementError(int row, int col, Tile element) {

        System.err.println("Cannot place " + element + " at position: [" + row + ", " + col + "]");
    }



    public void removeElement(int row, int col) {
        if (!isValidPosition(row, col)) {
            return;
        }
        Tile currentTile = board[row][col];
        board[row][col] = Tile.EMPTY;

        SimplePoint pointToRemove = new SimplePoint(row, col);

        if (currentTile == Tile.WUMPUS) {
            wumpusPositions.remove(pointToRemove);
        } else if (currentTile == Tile.PIT) {
            pitPositions.remove(pointToRemove);
        } else if (currentTile == Tile.GOLD) {
            if (goldPosition != null && goldPosition.equals(pointToRemove)) {
                goldPosition = null;
                goldRow = -1;
                goldCol = -1;
                goldPlaced = false;
            }
        } else if (currentTile == Tile.HERO) {
            heroPlaced = false;
            hero = null;
        }
    }






    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }


    public void moveHeroBasedOnDirection() {
        if (hero == null) {
            return;
        }
        int newRow = hero.getRow();
        int newCol = hero.getColumn();
        switch (hero.getDirection()) {
            case NORTH: newRow--; break;
            case EAST:  newCol++; break;
            case SOUTH: newRow++; break;
            case WEST:  newCol--; break;
        }
        if (isValidPosition(newRow, newCol) && !isWall(newRow, newCol)) {
            updateBoardAfterHeroMove(hero.getRow(), hero.getColumn(), newRow, newCol);
            hero.setRow(newRow);
            hero.setColumn(newCol);
            checkForHazards(newRow, newCol);
        } else {
            System.out.println("Nem lehet oda lépni! (You can't move there!)");
        }
    }

    boolean isWall(int row, int col) {
        return board[row][col] == Tile.WALL;
    }


    private void updateBoardAfterHeroMove(int oldRow, int oldCol, int newRow, int newCol) {

        if (board[oldRow][oldCol] != Tile.WUMPUS && board[oldRow][oldCol] != Tile.PIT) {
            board[oldRow][oldCol] = Tile.EMPTY;
        }
        board[newRow][newCol] = Tile.HERO;
    }

    void checkForHazards(int row, int col) {
        SimplePoint heroPos = new SimplePoint(row, col);
        if (wumpusPositions.contains(heroPos)) {
            System.out.println("A hős wumpuszra lépett és meghalt!");
            gameOver = true;
        } else if (pitPositions.contains(heroPos)) {
            System.out.println("A hős beleesett egy verembe és elveszített egy nyilat.");
            hero.setArrows(hero.getArrows() - 1);
        } else if (goldPosition != null && goldPosition.equals(heroPos) && !goldPickedUp) {
            System.out.println("A hős rálépett az aranyra. Használd az 'aranyat felszed' parancsot a felvételéhez.");
        }

        if (goldPickedUp && heroPos.equals(new SimplePoint(startingRow, startingColumn))) {
            System.out.println("Gratulálunk! Megnyerted a játékot, visszatértél az arannyal a kezdőpontba!");
            gameOver = true;
        }
    }

    public void pickUpGoldCommand() {
        if (goldPlaced && hero.getRow() == goldRow && hero.getColumn() == goldCol && !goldPickedUp) {
            hero.pickUpGold();
            goldPickedUp = true;
            goldPlaced = false;
            System.out.println("Arany felvéve.");
        } else {
            System.out.println("Itt nincs arany.");
        }
    }




    public boolean processArrowShot(int row, int col, Hero.Direction direction) {
        while (true) {
            switch (direction) {
                case NORTH: row--; break;
                case EAST:  col++; break;
                case SOUTH: row++; break;
                case WEST:  col--; break;
            }

            if (!isValidPosition(row, col) || board[row][col] == Tile.WALL) {
                System.out.println("A nyíl falba ütközött vagy elhagyta a táblát.");
                return false;
            }

            if (board[row][col] == Tile.WUMPUS) {
                System.out.println("Egy wumpusz meghalt.");
                board[row][col] = Tile.EMPTY;
                wumpusPositions.remove(new SimplePoint(row, col));
                return true;
            }
        }
    }




    public void postLoadInitialization() {
        calculateWumpusAndPitPositions();
        checkHeroAndGoldPlacement();
        recreateHeroIfNeeded();
        checkVictoryCondition();


        findStartingPoint();
    }

    void findStartingPoint() {
        if (heroPlaced && hero != null) {
            startingRow = hero.getRow();
            startingColumn = hero.getColumn();
        } else {

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] != Tile.WALL) {
                        startingRow = i;
                        startingColumn = j;
                        return;
                    }
                }
            }
        }
    }

    void recreateHeroIfNeeded() {

        if (heroPlaced && hero == null) {
            hero = new Hero(this, savedHeroRow, savedHeroCol, wumpusCount);
        }
    }

    void checkVictoryCondition() {

        if (goldPickedUp && hero != null && hero.getRow() == startingRow && hero.getColumn() == startingColumn) {
            System.out.println("Gratulálunk! Megnyerted a játékot, visszatértél az arannyal a kezdőpontba!");
            gameOver = true;
        }
    }


    void checkHeroAndGoldPlacement() {
        heroPlaced = false;
        goldPlaced = false;
        if (board != null && size > 0) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == Tile.HERO) {
                        heroPlaced = true;
                        if (hero == null) {
                            hero = new Hero(this, i, j, wumpusCount);
                        }
                        System.out.println("Hős helyezve: [" + i + "," + j + "]");
                    }
                    if (board[i][j] == Tile.GOLD) {
                        goldPlaced = true;
                        goldPosition = new SimplePoint(i, j);
                        goldRow = i;
                        goldCol = j;
                        System.out.println("Arany helyezve: [" + i + "," + j + "]");
                    }
                }
            }
        }
    }
    void calculateWumpusAndPitPositions() {
        wumpusPositions.clear();
        pitPositions.clear();
        if (board != null && size > 0) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == Tile.WUMPUS) {
                        wumpusPositions.add(new SimplePoint(i, j));
                    } else if (board[i][j] == Tile.PIT) {
                        pitPositions.add(new SimplePoint(i, j));
                    }
                }
            }
        }
    }





    public boolean isGameOver() {
        return gameOver;
    }




    public boolean isHeroPlaced() {
        return heroPlaced;
    }
    public Tile[][] getBoard() {
        return board;
    }



    public boolean isGoldPlaced() {
        return goldPlaced;
    }



    public Hero getHero() {
        return hero;
    }



    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }



    @XmlElement(name = "board")

    public void setBoard(Tile[][] board) {
        this.board = board;
    }
    @XmlElement(name = "startingRow")
    public int getStartingRow() {
        return startingRow;
    }

    public void setGoldPlaced(boolean goldPlaced) {
        this.goldPlaced = goldPlaced;
    }

    public void setStartingRow(int startingRow) {
        this.startingRow = startingRow;
    }

    @XmlElement(name = "startingColumn")
    public int getStartingColumn() {
        return startingColumn;
    }

    public void setStartingColumn(int startingColumn) {
        this.startingColumn = startingColumn;
    }

    @XmlElement(name = "goldPickedUp")
    public boolean isGoldPickedUp() {
        return goldPickedUp;
    }

    public void setGoldPickedUp(boolean goldPickedUp) {
        this.goldPickedUp = goldPickedUp;
    }




    @XmlElement(name = "size")
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setHeroPlaced(boolean heroPlaced) {
        this.heroPlaced = heroPlaced;
    }

    public class TileAdapter extends XmlAdapter<String, Tile> {
        @Override
        public Tile unmarshal(String v) throws Exception {
            return Tile.valueOf(v);
        }

        @Override
        public String marshal(Tile v) throws Exception {
            if (v == null) {
                return null;
            }
            return v.toString();
        }
    }
}
