package server;


import java.sql.*;

public class Database_Connection {

    private Connection con;
    private Statement stat;

    public Database_Connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
            //below statement
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "Ds7727956757");
            System.out.println("connection elsatblished");
            stat = con.createStatement();

        }
        catch (Exception ex)
        {
            System.out.println("connection not elsatblished");
//            ex.printStackTrace();
        }
    }

    public Statement getStat() {
        System.out.println(stat);
        return stat;
    }
}
