package server;

import server.Database_Connection;

import java.io.*;

import static java.lang.Integer.parseInt;
import static java.lang.System.in;

import java.net.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.lang.*;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket ss;
        ss = new ServerSocket(12000);
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests
                System.out.println("waiting for client");
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());// to read data from client
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); // to send data to client

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new LoginHandler(s, dis, dos);// creating new object of ClientHandling class for each new client

                // Invoking the start() method
                t.start();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}

class LoginHandler extends Thread // handling each new client
{

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    public LoginHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;           //setting value using constructor
        this.dis = dis;
        this.dos = dos;
    }

    public void run()
    {
        int n = 0;                  //to check how many data input data string is coming from client
        String[] a = new String[10]; // making an array of string of size 5
        String str;                 //a string that will  take data from inputstream and check is it an empty string
        try {
            while (!"".equals(str = dis.readUTF())) // running loop until str gets ""
            {
                a[n] = str;
                n++;
                System.out.println(" n = " + n);
                System.out.println(str);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Hey there");
        String p = a[1];       //to convert a string password to int type password
        String Verify_user, Verify_password;
        Verify_user = "select * from user where username = '" + a[0] + "'";//query to verify username
        Verify_password = "select * from user where password = '" + p + "'";//query to verify password
        Database_Connection ob1 = new Database_Connection();                // to load driver class and making c
        try {
            System.out.println("here1");
            Statement stat1 = ob1.getStat();                              //to prepare a statement for equecuting query
            System.out.println("here2");
            ResultSet rs_pass = stat1.executeQuery(Verify_password);//executing the query and storing in resultset
            System.out.println("here3");
            Database_Connection ob2 = new Database_Connection();
            System.out.println("here4");
            Statement stat2 = ob2.getStat();
            System.out.println("here5");
            ResultSet rs_user = stat2.executeQuery(Verify_user);
            System.out.println("here6");
            if (rs_user.next() == true) // if username  matches
            {
                System.out.println("here3");
                if (n == 2) //
                {
                    System.out.println("here4");
                    if (rs_pass.next())// if password matches
                    {
                        System.out.println("here4.1");
                        System.out.println("Logged In sucessfully : ");
                        dos.writeUTF("true");
                        HomeHandle hm = new HomeHandle(this.dis, this.dos, a[0]);
                    } else {
                        System.out.println("Logged In failed password is Incorrect");
                        dos.writeUTF("false");
                    }
                } else if (n == 5) {
                    System.out.println("User Already exists");
                    dos.writeBytes("false");
                }

            } else if (rs_user.next() == false) {
                if (n == 2) {
                    System.out.println("Login failed : Incorrect user name");
                    dos.writeBytes("false");
                } else if (n == 5) {
                    String Insert_Query = "Insert into userdata values('" + a[0] + "','" + p + "' ,'" + a[2] + "','" + a[3] + "','" + a[4] + "' )";
                    Database_Connection ob3 = new Database_Connection();
                    Statement stat3 = ob3.getStat();
                    int Ex = stat3.executeUpdate(Insert_Query);
                    if (Ex == 0) {
                        System.out.println("User not registered");
                        dos.writeBytes("false");
                    } else {
                        System.out.println("User registered Successful");
                        dos.writeBytes("true");
                        HomeHandle hm = new HomeHandle(this.dis, this.dos, a[0]);
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            System.out.println("SQL Exception");
        }
    }
}






