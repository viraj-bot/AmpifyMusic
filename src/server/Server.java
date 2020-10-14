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

    public void run() {
        try {
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
             if("1".equals(a[0]))
             {
              System.out.println("Hey there");
                String p = a[2];       //to convert a string password to int type password
               String Verify_user, Verify_password;
              Verify_user = "select * from userdata where username = '" + a[1] + "'";//query to verify username
              Verify_password = "select * from userdata where password = '" + p + "'";//query to verify password
              Database_Connection ob1 = new Database_Connection();                // to load driver class and making c
                try {
                 Statement stat1 = ob1.getStat();                              //to prepare a statement for equecuting query
                    ResultSet rs_pass = stat1.executeQuery(Verify_password);//executing the query and storing in resultset
                    Database_Connection ob2 = new Database_Connection();
                    Statement stat2 = ob2.getStat();
                    ResultSet rs_user = stat2.executeQuery(Verify_user);
                if (rs_user.next() == true) // if username  matches
                {
                    System.out.println("here3");
                    if (n == 3) //
                    {
                        System.out.println("here4");
                        if (rs_pass.next())// if password matches
                        {
                            System.out.println("here4.1");
                            System.out.println("Logged In sucessfully : ");
                            dos.writeBytes("true");
                        } else {
                            System.out.println("Logged In failed password is Incorrect");
                            dos.writeBytes("false");
                        }
                    } else if (n == 6) {
                        System.out.println("User Already exists");
                        dos.writeBytes("false");
                    }

                } else if (rs_user.next() == false) {
                    if (n == 3) {
                        System.out.println("Login failed : Incorrect user name");
                        dos.writeBytes("false");
                    } else if (n == 6) {
                        String Insert_Query = "Insert into userdata values('" + a[1] + "','" + p + "' ,'" + a[3] + "','" + a[4] + "','" + a[5] + "' )";
                        Database_Connection ob3 = new Database_Connection();
                        Statement stat3 = ob3.getStat();
                        int Ex = stat3.executeUpdate(Insert_Query);
                        if (Ex == 0) {
                            System.out.println("User not registered");
                            dos.writeBytes("false");
                        } else {
                            System.out.println("User registered Successful");
                            dos.writeBytes("true");
                        }
                    }
                }
            } catch (SQLException ex) {
//                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("SQL Exception");
            }
            this.dis.close();
            this.dos.close();
        }

        // elseif()










        } catch (Exception e) {
             System.out.println("cannot read choice");
        }

    }



}






