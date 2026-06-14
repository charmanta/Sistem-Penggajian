package com.mycompany.sistempenggajian;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection mysqlconfig;
    
    public static Connection configDB() throws SQLException {
        try {
            String url = "jdbc:mysql://localhost:3307/db_penggajian"; 
            String user = "root"; 
            String pass = "";     
            
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            mysqlconfig = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Koneksi Database Gagal!");
        }
        return mysqlconfig;
    }
}