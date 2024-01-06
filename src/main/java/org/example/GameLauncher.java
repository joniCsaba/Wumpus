package org.example;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

public class GameLauncher {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GamePersistenceManager persistenceManager = new GamePersistenceManager();
    static final DatabaseManager dbManager = new DatabaseManager();

    public static void main(String[] args) {

        setupDatabase();
        String playerName = getPlayerName();
        boolean exit = false;
        while (!exit) {
            exit = processMainMenu(playerName);
        }

        scanner.close();
    }

    private static void setupDatabase() {
        dbManager.createNewDatabase();
        dbManager.createTables();
    }

    private static String getPlayerName() {
        System.out.println("Add meg a játékos nevét:");
        String playerName = scanner.nextLine();
        if (!dbManager.playerExists(playerName)) {
            dbManager.insertPlayer(playerName, 0);
        }
        System.out.println("Üdvözöllek a játékban, " + playerName + "!");
        return playerName;
    }

    private static boolean processMainMenu(String playerName) {
        printMainMenu();
        int choice = getUserInput("Válassz egy opciót:", 1, 4);

        switch (choice) {
            case 1:
                MapEditor mapEditor = new MapEditor();
                mapEditor.setPlayerName(playerName);
                mapEditor.setPersistenceManager(persistenceManager);
                mapEditor.runEditor();
                break;
            case 2:
                GameBoard gameBoard = handleGameLoading();
                if (gameBoard != null) {
                    launchGame(gameBoard, playerName, persistenceManager);
                } else {
                    System.out.println("Nem sikerült betölteni a játékot.");
                }
                break;


            case 3:
                printHighScores();
                break;
            case 4:
                System.out.println("Kilépés...");
                return true;
        }
        return false;
    }
    private static void printMainMenu() {
        System.out.println("\n--- Főmenü ---");
        System.out.println("1. Pályakészítő");
        System.out.println("2. Játék betöltése");
        System.out.println("3. Ranglista");
        System.out.println("4. Kilépés");
    }

    private static GameBoard handleGameLoading() {
        File dir = new File("."); // Jelenlegi könyvtár
        FilenameFilter filter = (dir1, name) -> name.endsWith("_gameState.xml");
        File[] files = dir.listFiles(filter);

        if (files == null || files.length == 0) {
            System.out.println("Nincsenek elérhető mentések.");
            return null;
        }

        System.out.println("Elérhető mentések:");
        for (int i = 0; i < files.length; i++) {

            System.out.println((i + 1) + ": " + files[i].getName());
        }


        int fileIndex = getUserInput("Válassz egy mentést a betöltéshez (szám): ", 1, files.length) - 1;
        if (fileIndex >= 0 && fileIndex < files.length) {
            String filename = files[fileIndex].getName();

            filename = filename.substring(0, filename.indexOf("_gameState.xml"));


            GameBoard loadedGameBoard = persistenceManager.loadGameState(filename);

            if (loadedGameBoard != null) {
                loadedGameBoard.postLoadInitialization();
                loadedGameBoard.draw();
                System.out.println("Játék betöltve.");
            } else {
                System.out.println("Nem sikerült betölteni a játékállapotot.");
            }

            return loadedGameBoard;
        } else {
            System.out.println("Érvénytelen fájlindex.");
            return null;
        }
    }




    private static void printHighScores() {
        dbManager.printHighScores();
    }

    private static int getUserInput(String prompt, int min, int max) {
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


    public static void launchGame(GameBoard gameBoard, String playerName, GamePersistenceManager persistenceManager) {
        if (gameBoard == null) {
            System.out.println("Érvénytelen indítás");
            return;
        }

        System.out.println("Játék indítása " + playerName + " számára...");
        gameBoard.draw(); // Hozzáadva a tábla kirajzolása
        GameSession session = new GameSession(gameBoard, playerName, persistenceManager);
        session.start();
    }


}
