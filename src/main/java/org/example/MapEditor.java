package org.example;

import java.util.Scanner;

public class MapEditor {
    private String playerName;
    private boolean isLoadedGame = false;
    private Scanner scanner;
    private GameBoard gameBoard;
    private GamePersistenceManager persistenceManager;




    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public MapEditor() {
        this.scanner = new Scanner(System.in);
    }


    public void runEditor() {
        if (!isLoadedGame) {
            int size = getUserInput("Add meg a tábla méretét (6 és 20 között): ", 6, 20);
            this.gameBoard = new GameBoard(size);
        }

        gameBoard.initializeBoard();

        boolean editing = true;
        while (editing) {
            System.out.println("\n--- Pályaszerkesztő ---");
            System.out.println("1. Elem hozzáadása");
            System.out.println("2. Elem eltávolítása");
            System.out.println("3. Játék indítása");
            System.out.println("4. Vissza a főmenübe");
            System.out.print("Válassz egy opciót: ");

            int choice = getUserInput("Válassz egy opciót (1-4): ", 1, 4);
            switch (choice) {
                case 1:
                    addElement();
                    gameBoard.draw();
                    break;
                case 2:
                    removeElement();
                    gameBoard.draw();
                    break;
                case 3:
                    if (gameBoard.isHeroPlaced() && gameBoard.isGoldPlaced()) {
                        GameLauncher.launchGame(gameBoard, playerName, persistenceManager);
                        gameBoard.draw();
                        editing = false;
                    } else {
                        System.out.println("A játék indításához először helyezz el hőst és aranyat a táblán.");
                    }
                    break;

                case 4:
                    editing = false;
                    break;
                default:
                    System.out.println("Érvénytelen opció. Próbáld újra!");
            }
        }
    }


    private void addElement() {
        int row = getUserInput("Add meg a sor koordinátát: ", 1, gameBoard.getSize() - 2);
        int col = getUserInput("Add meg az oszlop koordinátát: ", 1, gameBoard.getSize() - 2);

        System.out.print("Válassz egy elemet (FAL, VEREM, WUMPUS, HŐS, ARANY): ");
        String elementStr = scanner.nextLine().toUpperCase();
        GameBoard.Tile element;

        switch (elementStr) {
            case "FAL":
                element = GameBoard.Tile.WALL;
                break;
            case "VEREM":
                element = GameBoard.Tile.PIT;
                break;
            case "WUMPUS":
                element = GameBoard.Tile.WUMPUS;
                break;
            case "HŐS":
                element = GameBoard.Tile.HERO;
                break;
            case "ARANY":
                element = GameBoard.Tile.GOLD;
                break;
            default:
                System.out.println("Érvénytelen elem. Próbáld újra!");
                return;
        }

        gameBoard.addElement(row, col, element);
    }

    private void removeElement() {
        int row = getUserInput("Add meg a sor koordinátát: ", 1, gameBoard.getSize() - 2);
        int col = getUserInput("Add meg az oszlop koordinátát: ", 1, gameBoard.getSize() - 2);
        gameBoard.removeElement(row, col);
    }

    private int getUserInput(String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Kérlek adj meg egy számot " + min + " és " + max + " között!");
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen bemenet, kérlek adj meg egy számot!");
            }
        }
    }
    public void setPersistenceManager(GamePersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
