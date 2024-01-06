package org.example;

import javax.sql.DataSource;
import java.sql.*;

public class DatabaseManager {

    // Kapcsolat létrehozása az adatbázissal
    private Connection connect() {

        String url = "jdbc:sqlite:the_path_to_your_db.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewDatabase() {
        try (Connection conn = this.connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("A driver neve: " + meta.getDriverName());
                System.out.println("Új adatbázis létrehozva.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void createTables() {
        // SQL utasítás a tábla létrehozásához
        String sql = "CREATE TABLE IF NOT EXISTS scores ("
                + " name text NOT NULL,"
                + " wins integer DEFAULT 0"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            // Tábla létrehozása
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Játékos hozzáadása az adatbázishoz
    public void insertPlayer(String name, int wins) {
        String sql = "INSERT INTO scores(name, wins) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, wins);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Ellenőrzi, hogy a játékos létezik-e az adatbázisban
    public boolean playerExists(String name) {
        String sql = "SELECT count(*) FROM scores WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Frissíti a játékos győzelmeinek számát az adatbázisban
    public void updateWinCount(String name) {
        String sql = "UPDATE scores SET wins = wins + 1 WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Kiírja a legmagasabb pontszámokat
    public void printHighScores() {
        String sql = "SELECT name, wins FROM scores ORDER BY wins DESC";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t" +
                        rs.getInt("wins"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

