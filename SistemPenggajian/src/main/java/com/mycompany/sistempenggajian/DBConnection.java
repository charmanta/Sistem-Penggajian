package com.mycompany.sistempenggajian;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DBConnection {
    static Connection koneksi;  
 
    public static Connection getKoneksi() {
        try {
        if (koneksi == null || koneksi.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/penggajian";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            koneksi = DriverManager.getConnection(url, user, password);
        }
    } catch (SQLException t) {
        System.out.println("Error Membuat Koneksi");
    } catch (ClassNotFoundException ex) {
        System.getLogger(DBConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
    }
    return koneksi;
    }
}