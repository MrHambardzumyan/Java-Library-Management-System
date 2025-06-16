package com.library.dao;

import java.sql.*;

public class DatabaseManager {
    private static final String URL  = "jdbc:sqlite:library.db";
    private static Connection conn;

    //initialize db
    public static void connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            initDatabase();
        }
    }

    //function to give classes access to db
    public static Connection getConnection() {
        return conn;
    }

    //db format, put availability etc in sql instead of linked list or arraylist
    private static void initDatabase() throws SQLException {
        String ddlUsers = """
          CREATE TABLE IF NOT EXISTS users (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            username      TEXT UNIQUE,
            password_hash TEXT,
            is_admin      INTEGER
          );
          """;

        String ddlBooks = """
          CREATE TABLE IF NOT EXISTS books (
            isbn         TEXT PRIMARY KEY,
            title        TEXT,
            author       TEXT,
            is_available INTEGER
          );
          """;

        String ddlLoans = """
          CREATE TABLE IF NOT EXISTS loans (
            loan_id     INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id     INTEGER,
            isbn        TEXT,
            borrow_date TEXT,
            due_date    TEXT,
            return_date TEXT,
            fine        REAL,
            FOREIGN KEY(user_id) REFERENCES users(id),
            FOREIGN KEY(isbn)    REFERENCES books(isbn)
          );
          """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(ddlUsers);
            stmt.execute(ddlBooks);
            stmt.execute(ddlLoans);
        }
    }
}
