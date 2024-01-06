package org.example;

import java.util.Scanner;

import static org.example.GameLauncher.dbManager;

public class GameSession {
    private final GameBoard gameBoard;
    private final String playerName;
    private final Scanner scanner;
    private GamePersistenceManager persistenceManager;

    public GameSession(GameBoard gameBoard, String playerName, GamePersistenceManager persistenceManager) {
        this.gameBoard = gameBoard;
        this.playerName = playerName;
        this.scanner = new Scanner(System.in);
        this.persistenceManager = persistenceManager;
        System.out.println("GameSession: Kapott GamePersistenceManager állapot: " + (persistenceManager != null));

    }

    public void start() {
        System.out.println("Játék indítása " + playerName + " számára...");

        gameBoard.draw();

        while (!gameBoard.isGameOver()) {
            System.out.println("Mit szeretnél tenni? (1: lép, 2: fordul jobbra, 3: fordul balra, 4: lő, 5: aranyat felszed, 6: felad, 7: játék mentése)");
            String action = scanner.nextLine();

            if (gameBoard.getHero() == null) {
                System.out.println("Nincs hős a táblán.");
                break;
            }

            processAction(action);

            gameBoard.checkForHazards(gameBoard.getHero().getRow(), gameBoard.getHero().getColumn());
            gameBoard.draw();

            checkGameStatus();
        }

        System.out.println("A játék véget ért. Köszönjük, hogy játszottál!");
    }


    private void processAction(String action) {
        switch (action) {
            case "1":
                moveHero();
                break;
            case "2":
                gameBoard.getHero().turnRight();
                break;
            case "3":
                gameBoard.getHero().turnLeft();
                break;
            case "4":
                gameBoard.getHero().shootArrow();
                break;
            case "5":
                gameBoard.pickUpGoldCommand();
                break;
            case "6":
                System.out.println("Játék feladva.");
                gameBoard.setGameOver(true);
                break;
            case "7":
                saveGameState();
                break;
            default:
                System.out.println("Érvénytelen parancs. Próbáld újra!");
        }
    }


    private void checkGameStatus() {
        Hero hero = gameBoard.getHero();
        if (gameBoard.getHero() == null) {
            System.out.println("Hiba: A hős nincs inicializálva.");
            return;
        }

        if (hero.hasGold() && hero.getRow() == gameBoard.getStartingRow() && hero.getColumn() == gameBoard.getStartingColumn()) {
            System.out.println("Gratulálunk, " + playerName + "! Sikeresen megszerezted az aranyat és visszatértél a kezdőpontra.");
            gameBoard.setGameOver(true);
            updatePlayerWinCount();
        } else if (gameBoard.isGameOver()) {
            System.out.println(playerName + ", a játékot feladtad.");
        }
    }

    private void updatePlayerWinCount() {

        dbManager.updateWinCount(playerName);
    }

    private void saveGameState() {
        System.out.println("GameSession: Mentési kísérlet, GamePersistenceManager állapot: " + (persistenceManager != null));
        if (persistenceManager != null) {
            boolean isSaved = persistenceManager.saveGameState(gameBoard, playerName);
            System.out.println("GameSession: Mentés eredménye: " + isSaved);
            if (isSaved) {
                System.out.println("Játékállapot mentve.");
            } else {
                System.out.println("Nem sikerült menteni a játékállapotot.");
            }
        } else {
            System.out.println("Nem sikerült menteni a játékállapotot, mert a persistenceManager null.");
        }
    }
    private void moveHero() {

        int currentRow = gameBoard.getHero().getRow();
        int currentCol = gameBoard.getHero().getColumn();

        gameBoard.moveHeroBasedOnDirection();

        gameBoard.draw();
    }



}


