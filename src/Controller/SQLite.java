package Controller;

import Model.Password;
import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SQLite {
    
    String driverURL = "jdbc:sqlite:" + "database.db";
    String logURL = "jdbc:sqlite:" + "logs.db";
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {}
    }
    
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " password BLOB NOT NULL,\n"
            + " salt BLOB NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " attemptCounter INTEGER DEFAULT 0\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {}
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {}
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, attemptCounter FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("attemptCounter") ));
            
            }
        } catch (Exception ex) {}
        return users;
    }
    
    protected byte[] getSalt(String username) throws Exception {
        String sql = "SELECT salt FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.getBytes(1);
        } catch (Exception ex) { throw ex; }
    }
    
    public boolean login(String username, String password) { 
        byte[] salt;
        
        try {
            salt = this.getSalt(username);
        } catch (Exception ex) { return false; }
        
        Password hashed = PasswordHasher.hash(password.toCharArray(), salt);
        
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        boolean result = false;
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setBytes(2, hashed.getHash());
            ResultSet rs = stmt.executeQuery();
            result = rs.getInt(1)==1;
        } catch (Exception ex) {}
        return result;
    }
    
    public boolean addUser(String username, String password) {
        Password hashed = PasswordHasher.hash(password.toCharArray());
        
        if(this.checkUsernameAvailable(username)){
            String sql = "INSERT INTO users(username,password,salt) VALUES(?,?,?)";
            
            try (Connection conn = DriverManager.getConnection(driverURL);
                PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, username);
                stmt.setBytes(2, hashed.getHash());
                stmt.setBytes(3, hashed.getSalt());
                stmt.execute();
            
//  For this activity, we would not be using prepared statements first.
//      String sql = "INSERT INTO users(username,password) VALUES(?,?)";
//      PreparedStatement pstmt = conn.prepareStatement(sql)) {
//      pstmt.setString(1, username);
//      pstmt.setString(2, password);
//      pstmt.executeUpdate();
            } catch (Exception ex) {}
            return true;
        }else
            return false;
    }
    public boolean checkUsernameAvailable(String username){
        ArrayList<User> users =this.getUsers();
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUsername().toLowerCase().equals(username.toLowerCase()))
                return false;
        }
        return true;
    }
    
    public void addUser(String username, String password, int role, int attemptCounter) {
        Password hashed = PasswordHasher.hash(password.toCharArray());
        
        String sql = "INSERT INTO users(username,password,salt,role,attemptCounter) VALUES(?,?,?,?,?)";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setBytes(2, hashed.getHash());
            stmt.setBytes(3, hashed.getSalt());
            stmt.setInt(4, role);
            stmt.setInt(5, attemptCounter);
            stmt.execute();
            
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username='" + username + "');";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User: " + username + " has been deleted.");
        } catch (Exception ex) {}
    }
    
    public void updateUser (String username){
         String sql = "UPDATE users SET role = 1 " + "WHERE username = '" +username+"'";
         
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User: " + username + " has been locked.");
        } catch (Exception ex) {}
    }
    
    public void updateAttemptCounter (String username,int attemptCounter){
         String sql = "UPDATE users SET attemptCounter =" + attemptCounter
                + " WHERE username = '" +username+"'";
         
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User: " + username + " # of attempts ->" + attemptCounter);
        } catch (Exception ex) {}
    }
    
    public void createNewLogDatabase() {
        try (Connection conn = DriverManager.getConnection(logURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database logs.db created.");
            }
        } catch (Exception ex) {}
    }
    
    public void createLogTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " message TEXT NOT NULL,\n"
            + " time TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(logURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in logs.db created.");
        } catch (Exception ex) {}
    }
    
    public void addLog(String message) {    
        String sql = "INSERT INTO logs(message,time) VALUES(?,?)";
        String time = LocalDateTime.now().toString();
        
        try (Connection conn = DriverManager.getConnection(logURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, message);
            stmt.setString(2, time);
            stmt.execute();
            
            System.out.println("Log added:" + message);
            System.out.println("Time:" + time);
            
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
}