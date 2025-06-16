package com.library.service;

import com.library.dao.DatabaseManager;
import com.library.model.User;
import java.security.*;
import java.sql.*;

/**
 * handles user registration, authentication, and removal of user.
 * It hashes passwords using SHA-256 and deals w db interactions to DatabaseManager.
 */
public class AuthService {
    //regisers user with username and hashed password. asks whether admin or not
    //throws sql sql exception if db error and nsa error if sha is not available
    public static boolean registerUser(String username, String password, boolean isAdmin)
            throws SQLException, NoSuchAlgorithmException {
        String sql = "INSERT INTO users(username,password_hash,is_admin) VALUES(?,?,?)"; //save input to sql format
        try (PreparedStatement p = DatabaseManager.getConnection().prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, hash(password));//password is saved after hashing in 64 bytes
            p.setInt(3, isAdmin ? 1 : 0);
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            //username alr exists
            return false;
        }
    }
    //authenticate user with username and hashed password
    //return user obj if correct, null otherwise
    //throws sqlexception if db error occurs and nsa exception if sha is not available
    public static User authenticate(String username, String password)
            throws SQLException, NoSuchAlgorithmException {
        String sql = "SELECT id,is_admin FROM users WHERE username=? AND password_hash=?";
        try (PreparedStatement p = DatabaseManager.getConnection().prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, hash(password));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), username, rs.getInt("is_admin")==1);
                }
            }
        }
        return null;
    }

    //deletes user record from database by deleting username
    //returns true if record deleted, false if user doesnt exist
    public static boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username=?";
        try (PreparedStatement p = DatabaseManager.getConnection().prepareStatement(sql)) {
            p.setString(1, username);
            return p.executeUpdate()>0;
        }
    }

    //hashes string using sha256 and returns token
    private static String hash(String in) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(in.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b: d) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
