package server;
import client.AppData;
import client.User;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.Database_Connection;

import java.io.*;

import static java.lang.Integer.parseInt;
import static java.lang.System.in;
import static java.lang.System.out;

import java.net.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                ObjectInputStream serverInputStream = new ObjectInputStream(s.getInputStream());// to read data from client
                ObjectOutputStream serverOutputStream = new ObjectOutputStream(s.getOutputStream()); // to send data to client

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new LoginHandler(s, serverInputStream, serverOutputStream );// creating new object of ClientHandling class for each new client

                // Invoking the start() method
                t.start();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}

class LoginHandler extends Thread                                                   // handling each new client
{

    final ObjectInputStream input_Stream;
    final ObjectOutputStream output_Stream;
    final Socket s;

    public LoginHandler(Socket s, ObjectInputStream inputStream, ObjectOutputStream outputStream )
    {
        this.s = s;                                                      //setting value using constructor
        this.input_Stream = inputStream;
        this.output_Stream = outputStream;
    }

    public void run()
    {
        try {
                int loginStatus = 0;
                while(loginStatus==0)
                {
                    System.out.println("Hey there");
                    User user;
                    user = (User) input_Stream.readObject();
                    String Verify_user, Verify_password;
                    Verify_user = "select * from userdata where username = '" + user.getUserName() + "'";               //query to verify username
                    Verify_password = "select * from userdata where password = '" + user.getPassword() + "'";                  //query to verify password
                    Database_Connection ob1 = new Database_Connection();                          // to load driver class and making c
                    try {
                        Statement stat1 = ob1.getStat();                                               //to prepare a statement for equecuting query
                        ResultSet rs_pass = stat1.executeQuery(Verify_password);                //executing the query and storing in resultset
                        Database_Connection ob2 = new Database_Connection();
                        Statement stat2 = ob2.getStat();
                        ResultSet rs_user = stat2.executeQuery(Verify_user);
                        if (rs_user.next() == true)                                                                     // if username  matches
                        {
                            System.out.println("here3");
                            if (user.getFirstName() == null)
                            {
                                System.out.println("here4");
                                if (rs_pass.next())                                             // if password matches
                                {
                                    System.out.println("here4.1");
                                    System.out.println("Logged In sucessfully");
                                    loginStatus=1;
                                    output_Stream.writeUTF("Logged In sucessfully");
                                } else
                                    {
                                    System.out.println("Log In failed password is Incorrect");
                                    output_Stream.writeUTF("Login failed : Incorrect Password");
                                }
                            } else
                                {
                                System.out.println("User Already exists");
                                output_Stream.writeUTF("User Already exists");
                            }

                        }
                        else if (rs_user.next() == false)
                        {
                            if (user.getFirstName() == null)
                            {
                                System.out.println("No account found with this username");
                                output_Stream.writeUTF("No account found with this username");
                            } else
                                {
                                String Insert_Query = "Insert into userdata values('" + user.getUserName() + "','" + user.getPassword() + "' ,'" + user.getFirstName() + "','" + user.getLastName() + "','" + user.getEmail() + "' )";
                                Database_Connection ob3 = new Database_Connection();
                                Statement stat3 = ob3.getStat();
                                int Ex = stat3.executeUpdate(Insert_Query);
                                if (Ex == 0)
                                {
                                    System.out.println("User not registered");
                                    output_Stream.writeUTF("User not registered");
                                } else
                                    {
                                    System.out.println("User registered Successfully");
                                    output_Stream.writeUTF("User registered Successfully");
                                    loginStatus=1;
                                }
                            }
                        }
                        output_Stream.flush();
                    } catch (SQLException ex) {
                        System.out.println("SQL Exception");
                    }
               }

                System.out.println("login ended");

          while(true) {

               System.out.println("entered while loop");
               AppData appData ;
               appData = (AppData)input_Stream.readObject();
               System.out.println(appData.getQueryType());
               //ObservableList<String> listOfData = FXCollections.observableArrayList();

              if (appData.getQueryType().equals("CreatePlaylist")) {
                   System.out.println("adding playlist to database");

                   String query = "insert into playlists values('" + appData.getUserName() + "','" + appData.getPlaylistName() + "' )";
                   Database_Connection con = new Database_Connection();
                   Statement stat = con.getStat();
                   int x = stat.executeUpdate(query);
                   if (x == 0)
                       System.out.println("playlist not added ERROR!");
                   else
                       System.out.println("playlist successfully added to the database");
               }

              else if(appData.getQueryType().equals("playSong")){
                  out.println("entered playSong");
                  String songname =  appData.getUserName();
                  File mp3 = new File("/D:/Ampify/" + songname + ".mp3").getCanonicalFile();
                  byte[] arr = Files.readAllBytes(Paths.get(mp3.toURI()));
                  output_Stream.write(arr);
              }

               else if (appData.getQueryType().equals("loadPlaylists")) {
                   System.out.println("Loading playlists from server");
                   String query = "select playlistname from playlists where username = '" + appData.getUserName() + "'";
                   try {
                       Database_Connection con = new Database_Connection();
                       Statement stat = con.getStat();
                       ResultSet rs = stat.executeQuery(query);
                       System.out.println("Sending playlist names to client");
                       while (rs.next()) {
                           System.out.println(rs.getString("playlistname"));
                           output_Stream.writeUTF(rs.getString("playlistname"));
                       }
                       output_Stream.writeUTF("");
                       System.out.println("playlists sent succesfully");
                   } catch (Exception e) {
                       System.out.println("Error sending playlist names from server");
                   }
               }

               else if (appData.getQueryType().equals("showPlaylist")) {
                   System.out.println("Loading songs in playlists from server");
                   String query = "select songname from songsinplaylists where username = '" + appData.getUserName() + "' and playlistname = '" + appData.getPlaylistName() + "'";
                   try {
                       Database_Connection con = new Database_Connection();
                       Statement stat = con.getStat();
                       ResultSet rs = stat.executeQuery(query);
                       System.out.println("Sending songs in playlist to client");
                       while (rs.next()) {
                           System.out.println(rs.getString("songname"));
                           output_Stream.writeUTF(rs.getString("songname"));
                       }
                       output_Stream.writeUTF("");
                       System.out.println("Songs in playlist sent succesfully");
                   } catch (Exception e) {
                       System.out.println("Error sending songs in playlist from server");
                   }
               }

               else if (appData.getQueryType().equals("deletePlaylist")) {
                   System.out.println("Deleting playlist from server");
                   String query = "delete from songsinplaylists where username = '" + appData.getUserName() + "' and playlistname = '" + appData.getPlaylistName() + "'";
                   String query2 = "delete from playlists where username = '" + appData.getUserName() + "' and playlistname = '" + appData.getPlaylistName() + "'";
                   try {
                       Database_Connection con = new Database_Connection();
                       Statement stat = con.getStat();
                       System.out.println(" here ....");
                       stat.executeUpdate(query2);
                       System.out.println("here 2 .. .. .");
                       stat.executeUpdate(query);
                       System.out.println("playlist deleted successfully");
                   } catch (Exception e) {
                       System.out.println("Error deleting playlist from server");
                   }
               }

                else if(appData.getQueryType().equals("getArtistData")){
                  System.out.println("artist function server side");
                  System.out.println("Loading artists from server");
                  String query = "select distinct artist from allsongs order by artist asc ";
                  try {
                      Database_Connection con = new Database_Connection();
                      Statement stat = con.getStat();
                      ResultSet rs = stat.executeQuery(query);
                      System.out.println("Sending artist data to client");
                      while (rs.next()) {
                          System.out.println(rs.getString("artist"));
                          output_Stream.writeUTF(rs.getString("artist"));
                      }
                      output_Stream.writeUTF("");
                      System.out.println("Artist data sent succesfully");
                  } catch (Exception e) {
                      System.out.println("Error sending Artist data from server");
                  }
              }

              else if(appData.getQueryType().equals("getLanguageData")){
                  System.out.println("artist function server side");
                  System.out.println("Loading artists from server");
                  String query = "select distinct language from allsongs order by language asc ";
                  try {
                      Database_Connection con = new Database_Connection();
                      Statement stat = con.getStat();
                      ResultSet rs = stat.executeQuery(query);
                      System.out.println("Sending artist data to client");
                      while (rs.next()) {
                          System.out.println(rs.getString("language"));
                          output_Stream.writeUTF(rs.getString("language"));
                      }
                      output_Stream.writeUTF("");
                      System.out.println("Artist data sent succesfully");
                  } catch (Exception e) {
                      System.out.println("Error sending Artist data from server");
                  }
              }

              else if(appData.getQueryType().equals("getGenreData")){
                  System.out.println("artist function server side");
                  System.out.println("Loading artists from server");
                  String query = "select distinct genre from allsongs order by genre asc ";
                  try {
                      Database_Connection con = new Database_Connection();
                      Statement stat = con.getStat();
                      ResultSet rs = stat.executeQuery(query);
                      System.out.println("Sending artist data to client");
                      while (rs.next()) {
                          System.out.println(rs.getString("genre"));
                          output_Stream.writeUTF(rs.getString("genre"));
                      }
                      output_Stream.writeUTF("");
                      System.out.println("Artist data sent succesfully");
                  } catch (Exception e) {
                      System.out.println("Error sending Artist data from server");
                  }
              }



              else if(appData.getQueryType().equals("getSongs")){
                  System.out.println("Loading songs  from server");
                  out.println(appData.getPlaylistName());
                  out.println(appData.getUserName());
                  String query = "select songname from allsongs where "+ appData.getPlaylistName() +" = '"+ appData.getUserName() +"'";
                  try {
                      Database_Connection con = new Database_Connection();
                      Statement stat = con.getStat();
                      ResultSet rs = stat.executeQuery(query);
                      System.out.println("Sending songs data to client");
                      while (rs.next()) {
                          System.out.println(rs.getString("songname"));
                          output_Stream.writeUTF(rs.getString("songname"));
                      }
                      output_Stream.writeUTF("");
                      System.out.println("Songs  sent succesfully");
                  } catch (Exception e) {
                      System.out.println("Error sending songs from server");
                  }
              }

                output_Stream.flush();

           }//while loop ends
              // this.dis.close();
              // this.dos.close();

        } catch (Exception e) {
            e.printStackTrace();
             System.out.println("cannot read choice");
        }

    }
}






