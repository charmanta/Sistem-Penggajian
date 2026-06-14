/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistempenggajian;

/**
 *
 * @author Lenovo
 */
import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;

public class DBconnection {
    private static Connection koneksi;

    public static Connection getKoneksi() {
        // cek apakah koneksi sudah ada
        if (koneksi == null) {
            try {
                String url = "jdbc:mysql://127.0.0.1:3306/penggajian";
                String user = "root";
                String password = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi Berhasil!");
            } catch (SQLException t) {
                System.out.println("Error Membuat Koneksi: " + t.getMessage());
            } catch (ClassNotFoundException ex) {
                System.getLogger(DBconnection.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return koneksi;
    }
}
    

