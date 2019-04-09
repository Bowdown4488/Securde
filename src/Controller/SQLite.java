package Controller;

import Model.History;
import Model.Logs;
import Model.Password;
import Model.Product;
import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLite {
    
    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
     
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
     
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT UNIQUE NOT NULL COLLATE NOCASE,\n"
            + " password BLOB NOT NULL,\n"
            + " salt BLOB NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " locked INTEGER DEFAULT 0\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {/* Log: Log exception */}
    }
    
    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + timestamp + "')";
        boolean result = false;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            result = stmt.executeUpdate() > 0;
        } catch (Exception ex) {/* Log: Log exception */}
        
        if (result) {
            // Log: User added successfully
        } else {
            // Log: User was not added
        }
        
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {/* Log: Log exception */}
    }
    

    
    public boolean addUser(String username, String password) {
        Password hashed = PasswordHasher.hash(password.toCharArray());
        
        String sql = "INSERT INTO users(username,password,salt) VALUES(?,?,?)";
        boolean result = false;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setBytes(2, hashed.getHash());
            stmt.setBytes(3, hashed.getSalt());
            result = stmt.executeUpdate() > 0;
        } catch (Exception ex) { /* Log: Log exception */ }
        
        if (result) {
            // Log: User added successfully
        } else {
            // Log: User was not added
        }
        return result;
    }
    
    
    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {}
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {}
        return products;
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, locked FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("locked")));
            }
        } catch (Exception ex) {}
        return users;
    }
    
    public boolean addUser(String username, String password, int role, int locked) {
        Password hashed = PasswordHasher.hash(password.toCharArray());
        
        String sql = "INSERT INTO users(username,password,salt,role,locked) VALUES(?,?,?,?,?)";
        boolean result = false;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setBytes(2, hashed.getHash());
            stmt.setBytes(3, hashed.getSalt());
            stmt.setInt(4, role);
            stmt.setInt(5, locked);
            result = stmt.executeUpdate() > 0;
            
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result) {
            // Log: User added successfully
        } else {
            // Log: User was not added
        }
        return result;
    }
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        int result =0;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            result = 1;
        }catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("User " + username + " has been deleted."); // Debug
            // Log: User attempt counter set to N
        }
        
        
    }
  
    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES('" + name + "','" + stock + "','" + price + "')";
        boolean result = false;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            result = stmt.executeUpdate() > 0;
        } catch (Exception ex) {}
        if (result == true) {
            System.out.println("Product " + name + " has been added."); // Debug
            // Log: User attempt counter set to N
        }
    }
    
    public void removeProduct(String productname) {
        String sql = "DELETE from product WHERE name = ?";
        int result =0;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productname);
            stmt.executeUpdate();
            result = 1;
        }catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("Product " + productname + " has been deleted."); // Debug
            // Log: User attempt counter set to N
        }
        
        
    }
    
    public void updateProduct (String productName, String newName, int stock, double price){
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE name = ?";
        int result = 0;
        System.out.println("test");
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, stock);
            stmt.setDouble(3, price);
            stmt.setString(4, productName);
            stmt.executeUpdate();
            
            result = 1;
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("Product: " + productName + " changed values to name->" + newName+" ,stock->"+stock+",price->"+price); // Debug     
            // Log: User attempt counter set to N
        }
    }
    public Product getProduct(String name){
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            product = new Product(rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price"));
        } catch (Exception ex) {}
        return product;
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
        } catch (Exception ex) {
            // Log: Login failed
            return false;
        }
        
        Password hashed = PasswordHasher.hash(password.toCharArray(), salt);
        
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        boolean result = false;
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setBytes(2, hashed.getHash());
            ResultSet rs = stmt.executeQuery();
            result = rs.getInt(1) > 0;
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result) {
            // Log: Login successful
        } else {
            // Log: Login failed
        }
        return result;
    }
    
    public void updateUser (String username){
        String sql = "UPDATE users SET role = 1 WHERE username = ?";
//        String sql = "UPDATE users SET role = 1 " + "WHERE username = '" +username+"'";
         
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("User " + username + " locked"); // Debug
            // Log: User locked
        } catch (Exception ex) { /* Log: Log exception */ }
    }
    
    public void updateAttemptCounter (String username, int attemptCounter){
        String sql = "UPDATE users SET locked = ? WHERE username = ?";
        int result = 0;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptCounter);
            stmt.setString(2, username);
            stmt.executeUpdate();
            result = 1;
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("User: " + username + " # of attempts ->" + attemptCounter); // Debug
            // Log: User attempt counter set to N
        }
    }
    
    public void updateRole (String username, int role){
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        int result = 0;
        System.out.println("test");
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, role);
            stmt.setString(2, username);
            stmt.executeUpdate();
            result = 1;
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("User: " + username + " changed role to ->" + role); // Debug
            // Log: User attempt counter set to N
        }
    }
    
    public void lockAccount (String username, int lock){
        String sql = "UPDATE users SET locked = ? WHERE username = ?";
        int result = 0;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lock);
            stmt.setString(2, username);
            stmt.executeUpdate();
            result = 1;
        } catch (Exception ex) { /* Log: Log exception */ }
        if (result == 1) {
            System.out.println("User: " + username + " was assigned " + lock); // Debug
            // Log: User attempt counter set to N
        }
    }
    
    public void updatePassword(String username, String password) {
        //Sorry Im not sure how to update hashed query password :((
//        Password hashed = PasswordHasher.hash(password.toCharArray());
//        
////        String sql = "INSERT INTO users(username,password,salt) VALUES(?,?,?)";
//        String sql = "UPDATE users SET password = ?, salt = ? WHERE username = ?"; 
////        String sql = "UPDATE users SET password = PASSWORD(?) WHERE username = ?";
//        int result = 0;
//        
//        System.out.println("test: " + username + " test: " + password);
//        System.out.println("hashed: " + hashed);
//        
//        try (Connection conn = DriverManager.getConnection(driverURL);
//            PreparedStatement stmt = conn.prepareStatement(sql)){
//            stmt.setBytes(1, hashed.getHash());
//            stmt.setBytes(2, hashed.getSalt());
//            stmt.setString(2, username);
//            stmt.executeUpdate();
//            result = 1;
//        } catch (Exception ex) { /* Log: Log exception */ }
//        
//        if (result == 1) {
//            System.out.println("User: " + username + " password changed to: " + password);
//            // Log: User added successfully
//        } else {
//            // Log: User was not added
//        }
    }
}