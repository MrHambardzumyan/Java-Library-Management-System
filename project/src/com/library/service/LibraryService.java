package com.library.service;

import com.library.dao.DatabaseManager;
import com.library.model.Book;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibraryService {
    private static final int    LOAN_DAYS    = 14;
    private static final double FINE_PER_DAY = 0.50;

    //Search book by title or author (case insensitive)
    public static List<Book> searchBooks(String kw) throws SQLException {
        List<Book> out = new ArrayList<>();
        String sql = "SELECT isbn,title,author,is_available FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";
        //pre prepare the string before execute
        try (PreparedStatement p=DatabaseManager.getConnection().prepareStatement(sql)) {
            String k = "%" + kw.toLowerCase() + "%";
            p.setString(1, k);
            p.setString(2, k);
            //get string
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    out.add(new Book(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("is_available")==1
                    ));
                }
            }
        }
        return out;
    }

    //add books to catalog, inserts new row at the bottom
    public static boolean addBook(Book b) throws SQLException {
        String sql="INSERT INTO books(isbn,title,author,is_available) VALUES(?,?,?,1)";
        try (PreparedStatement p=DatabaseManager.getConnection().prepareStatement(sql)) {
            p.setString(1,b.getIsbn());
            p.setString(2,b.getTitle());
            p.setString(3,b.getAuthor());
            return p.executeUpdate()>0;
        }
    }

    //borrow book, check if unavailable
    public static LocalDate borrowBook(int userId, String isbn) throws SQLException {
        Connection c = DatabaseManager.getConnection();
        //check availability
        try (PreparedStatement p = c.prepareStatement(
                "SELECT is_available FROM books WHERE isbn=?")) {
            p.setString(1, isbn);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next() || rs.getInt("is_available") == 0) {
                    return null;
                }
            }
        }
        //mark checked out after borrow
        try (PreparedStatement p = c.prepareStatement(
                "UPDATE books SET is_available=0 WHERE isbn=?")) {
            p.setString(1, isbn);
            p.executeUpdate();
        }
        //record loan date and set due date
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate    = borrowDate.plusDays(LOAN_DAYS);
        String sql = "INSERT INTO loans(user_id,isbn,borrow_date,due_date) VALUES(?,?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            p.setString(2, isbn);
            p.setString(3, borrowDate.toString());
            p.setString(4, dueDate.toString());
            p.executeUpdate();
        }
        return dueDate;
    }

    //return book, and set fine depending on due date. mark book as available again
    public static double returnBook(int userId, String isbn) throws SQLException {
        Connection c = DatabaseManager.getConnection();
        // find open loan
        int loanId;
        LocalDate dueDate;
        String find = "SELECT loan_id,due_date FROM loans "
                    + "WHERE user_id=? AND isbn=? AND return_date IS NULL";
        try (PreparedStatement p = c.prepareStatement(find)) {
            p.setInt(1, userId);
            p.setString(2, isbn);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
                loanId  = rs.getInt("loan_id");
                dueDate = LocalDate.parse(rs.getString("due_date"));
            }
        }
        //compute fine based on due date
        LocalDate returnDate = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        double fine = daysLate > 0 ? daysLate * FINE_PER_DAY : 0.0;
        //update loan on db
        String updLoan = "UPDATE loans SET return_date=?,fine=? WHERE loan_id=?";
        try (PreparedStatement p = c.prepareStatement(updLoan)) {
            p.setString(1, returnDate.toString());
            p.setDouble(2, fine);
            p.setInt(3, loanId);
            p.executeUpdate();
        }
        //mark available agian
        try (PreparedStatement p = c.prepareStatement(
                "UPDATE books SET is_available=1 WHERE isbn=?")) {
            p.setString(1, isbn);
            p.executeUpdate();
        }
        return fine;
    }

    //calculate total fine
    public static double getOutstandingFines(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(fine),0) FROM loans WHERE user_id=? AND fine>0";
        try (PreparedStatement p = DatabaseManager.getConnection().prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    //pay fine and zero out the fine
    public static double payFines(int userId) throws SQLException {
        double total = getOutstandingFines(userId);
        if (total > 0) {
            String sql = "UPDATE loans SET fine=0 WHERE user_id=? AND fine>0";
            try (PreparedStatement p = DatabaseManager.getConnection().prepareStatement(sql)) {
                p.setInt(1, userId);
                p.executeUpdate();
            }
        }
        return total;
    }

    //list all books in an array list
    public static List<Book> listBooks() throws SQLException {
        List<Book> out = new ArrayList<>();
        String sql = "SELECT isbn,title,author,is_available FROM books";
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("is_available") == 1
                ));
            }
        }
        return out;
    }

    
}

