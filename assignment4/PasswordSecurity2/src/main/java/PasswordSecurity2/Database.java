/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PasswordSecurity2;

import java.io.IOException;
import java.sql.*;

public class Database {
    
    final static class MyResult {
        private final boolean first;
        private final String second;
        
        public MyResult(boolean first, String second) {
            this.first = first;
            this.second = second;
        }
        public boolean getFirst() {
            return first;
        }
        public String getSecond() {
            return second;
        }
    }
    
    protected static MyResult add(String name, String password, String salt) throws IOException{ 
        createTable();        

        if(find(name).getFirst())
            return new MyResult(false, "Meno uz existuje");
        insertUser(name, password, salt);            
        return new MyResult(true, "");
    }

    public static MyResult find(String name){
        createTable();

        String sql = "SELECT name, password, salt "
                   + "FROM users WHERE name = ?";
 
        try (Connection conn = connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            
            pstmt.setString(1,name);
            ResultSet rs  = pstmt.executeQuery();
            
            // loop through the result set
            while (rs.next()) {
               String nameFromDb =  rs.getString("name");
               String password =  rs.getString("password"); 
               String salt =  rs.getString("salt");  
                
               return new MyResult(true,nameFromDb + ":" + password + ":" + salt);
            }
            return new MyResult(false,"Meno sa nenaslo");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new MyResult(false,"Chyba");
        }
    }


    public static void insertUser(String name, String password, String salt ) {
        String sql = "INSERT INTO users(name,password,salt) VALUES(?,?,?)";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.setString(3, salt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void createTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
        + "	name text PRIMARY KEY,\n"
        + "	password text NOT NULL,\n"
        + "	salt text NOT NULL\n"
        + ");";


      
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);            
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }         
    }

    private static Connection connect() {
        // db parameters
        String url = "jdbc:sqlite:.\\database.db";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }    
}
