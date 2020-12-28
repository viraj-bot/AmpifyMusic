package server;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import java.io.*;

import static java.lang.System.out;

import java.net.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.*;
import java.lang.*;
import java.util.*;

class MetaDataReader {
    static HashMap<String, ObservableMap> songMetadata = new HashMap<>();
    List<String> songs = new ArrayList<>();
    static HashSet<String> songsWithData = new HashSet<>();

    public MetaDataReader() throws SQLException {
        final JFXPanel fxPanel = new JFXPanel();
        try {
            scanSongs("D://AmpifySongs");//to scan songs available in AmpifySongs folder and add their name to database
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Database_Connection ds = new Database_Connection();
        String checkQuery = "select songname from allsongs";
        Statement statement = ds.getStat();
        ResultSet rs = statement.executeQuery(checkQuery);
        while (rs.next()) {
            songs.add(rs.getString("songname"));
            if (!songsWithData.contains(rs.getString("songname"))) {
                String deleteQuery = "delete from allsongs where songname ='" + rs.getString("songname") + "'";
                out.println(rs.getString("songname"));
                Database_Connection dc = new Database_Connection();
                Statement statement1 = dc.getStat();
                int c = statement1.executeUpdate(deleteQuery);
                if (c == 0)
                    out.println("Deletion failed");
                else {
                    out.println("Deletion sucessfull : ");
                }
            }
        }

        out.println("Songs in db : " + songs.size() + " songs in pc " + songsWithData.size());
        out.println("songs with Metadata :" + songMetadata.size());
        for (String y : songsWithData) {
            if (!songMetadata.containsKey(y)) {
                if (!songs.contains(y)) {
                    String s = "<Unknown>";
                    String insertQuery = "Insert into allsongs values('" + y + "','" + 0 + "' ,'" + s + "','" + s + "','" + s + "', CURRENT_TIMESTAMP )";
                    Statement statement1 = ds.getStat();
                    int i = statement1.executeUpdate(insertQuery);
                    if (i == 0) {
                        out.println("Insertion failed");
                    } else {
                        out.println("Insertion sucessfull");
                    }
                }
            } else {
                if (!songs.contains(y)) {
                    ObservableMap<String, Object> map = (ObservableMap<String, Object>) songMetadata.get(y);
                    String str1, str2, str4;
                    str1 = String.valueOf(map.get("artist"));
                    if (str1 == null || str1.equals("null"))
                        str1 = "<Unknown>";
                    str2 = "<Unknown>";
                    str4 = String.valueOf(map.get("genre"));
                    if (str4 == null || str4.equals("null"))
                        str4 = "<Unknown>";
                    out.println(y + " " + str1 + " " + str4);
                    int count = 0;
                    String insertQuery = "Insert into allsongs values('" + y + "','" + count + "' ,'" + str2 + "','" + str1 + "','" + str4 + "', CURRENT_TIMESTAMP )";
                    Statement statement1 = ds.getStat();
                    int i = statement1.executeUpdate(insertQuery);
                    if (i == 0) {
                        out.println("Insertion failed");
                    } else {
                        out.println("Insertion sucessfull");
                    }
                }
            }
        }
    }

    public static void scanSongs(String p) throws SQLException// scanning songs in folder and and adding their name to database
    {
        File f = new File(p);
        File l[] = f.listFiles();
        for (File x : l) {
            if (x.isHidden() || !x.canRead())
                continue;
            if (x.isDirectory())
                scanSongs(x.getPath());
            else if (x.getName().endsWith(".mp3")) {
                String path = x.toURI().toString();
                songsWithData.add(x.getName().replace(".mp3", ""));
                Media media = new Media(path);
                media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
                    songMetadata.put(x.getName().replace(".mp3", ""), media.getMetadata());

                });
            }
        }
        out.println("DONE");
    }

}

public class Server {
    public static void main(String[] args) throws IOException, SQLException {
        new MetaDataReader();
        ServerSocket ss;
        ss = new ServerSocket(12000);
        while (true) {     // to make server run forever to accept multiple client request
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
                Thread t = new LoginHandler(s, serverInputStream, serverOutputStream);// creating new object of ClientHandling class for each new client

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
    String username;

    public LoginHandler(Socket s, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.s = s;                                                      //setting value using constructor
        this.input_Stream = inputStream;
        this.output_Stream = outputStream;
    }

    public void run() {
        try {
            int loginStatus = 0;
            while (loginStatus == 0) {// running loop until login and signup gets sucessfull
                String password, firstname, lastname, email;
                Object object;
                object = input_Stream.readObject();
                String userString = object.toString();
                StringTokenizer user = new StringTokenizer(userString, " ");  // to making tokens of user request
                String Verify_user, Verify_password;
                out.println(username = user.nextToken());
                out.println(password = user.nextToken());
                out.println(firstname = user.nextToken());
                out.println(lastname = user.nextToken());
                out.println(email = user.nextToken());
                Verify_user = "select * from userdata where username = '" + username + "'";               //query to verify username
                Verify_password = "select * from userdata where password = '" + password + "'";                  //query to verify password
                Database_Connection ob1 = new Database_Connection();                          // to load driver class and making c
                try {
                    Statement stat1 = ob1.getStat();                                               //to prepare a statement for equecuting query
                    ResultSet rs_pass = stat1.executeQuery(Verify_password);                //executing the query and storing in resultset
                    Database_Connection ob2 = new Database_Connection();
                    Statement stat2 = ob2.getStat();
                    ResultSet rs_user = stat2.executeQuery(Verify_user);
                    if (rs_user.next() == true)                                                                     // if username  matches
                    {
                        out.println("here3");

                        if (firstname.equals("null")) {
                            out.println("here4");
                            if (rs_pass.next()) // if password matches
                            {
                                out.println("Logged In sucessfully");
                                loginStatus = 1; // to end login / signup loop
                                output_Stream.writeUTF("Logged In sucessfully");
                            } else {
                                out.println("Log In failed password is Incorrect");
                                output_Stream.writeUTF("Login failed : Incorrect Password");
                            }
                        } else {
                            out.println("User Already exists");
                            output_Stream.writeUTF("User Already exists");
                        }

                    } else if (rs_user.next() == false) {
                        if (firstname.equals("null")) {
                            out.println("No account found with this username");
                            output_Stream.writeUTF("No account found with this username");
                        } else {
                            String Insert_Query = "Insert into userdata values('" + username + "','" + password + "' ,'" + firstname + "','" + lastname + "','" + email + "' )";
                            Database_Connection ob3 = new Database_Connection();
                            Statement stat3 = ob3.getStat();
                            int Ex = stat3.executeUpdate(Insert_Query);
                            if (Ex == 0) {
                                out.println("User not registered");
                                output_Stream.writeUTF("User not registered");
                            } else {
                                out.println("User registered Successfully");
                                output_Stream.writeUTF("User registered Successfully");
                                loginStatus = 1;
                            }
                        }
                    }
                    output_Stream.flush();
                } catch (SQLException ex) {
                    out.println("SQL Exception");
                }
            }

            out.println("login ended");

            while (true) {
                out.println("entered while loop");

                Object object = input_Stream.readObject();
                String completeRequset = object.toString();
                out.println("complete request : " + completeRequset);
                StringTokenizer st = new StringTokenizer(completeRequset, ".");
                String queryType = st.nextToken();
                String name1 = st.nextToken();
                String name2 = st.nextToken();

                if (queryType.equals("CreatePlaylist")) {

                    out.println("adding playlist to database");

                    String query = "insert into playlists values('" + username + "','" + name1 + "' )";
                    Database_Connection con = new Database_Connection();
                    Statement stat = con.getStat();
                    int x = 0;
                    try {
                        x = stat.executeUpdate(query);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    if (x == 0)
                        out.println("playlist not added ERROR!");
                    else
                        out.println("playlist successfully added to the database");


                } else if (queryType.equals("playSong")) {
                    try {
                        out.println("playsongs");
                        out.println(name1);
                        File songFile = new File("/D:/AmpifySongs/" + name1 + ".mp3").getCanonicalFile();
                        out.println(songFile);
                        File lyricsFile = null;
                        try {
                            lyricsFile = new File("/D:/AmpifyLyrics/" + name1 + ".srt").getCanonicalFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        out.println(lyricsFile);
                        byte[] songBytes = Files.readAllBytes(Paths.get(songFile.toURI()));
                        out.println(songBytes);
                        byte[] lyricsBytes = null;
                        try {
                            lyricsBytes = Files.readAllBytes(Paths.get(lyricsFile.toURI()));
                        } catch (NoSuchFileException e) {
                            out.println("no such  srt file found");
                        }
                        out.println(lyricsBytes);
                        out.println(songBytes);
                        output_Stream.writeObject(songBytes);
                        out.println("song sent");

                        out.println(input_Stream.readObject());

                        output_Stream.writeObject(lyricsBytes);

                        out.println("Lyrics sent");

                        String countplayedallsongs = "update allsongs set playcount = playcount + 1 where songname =  '" + name1 + "' ";
                        String check = "select * from userplayedcount where songname = '" + name1 + "'AND username = '" + username + "'";
                        String checkUserRecents = "select * from recentlyplayed where songname = '" + name1 + "' AND username = '" + username + "'";
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        int rs = stat.executeUpdate(countplayedallsongs);
                        if (rs == 0) {
                            out.println("failed to update play count in allsongs");
                        }
                        ResultSet resultSet = stat.executeQuery(check);
                        if (resultSet.next()) {
                            out.println("found");
                            String update = "update userplayedcount set playcount = playcount +1 where songname = '" + name1 + "'AND username = '" + username + "'";
                            int i = stat.executeUpdate(update);
                            if (i == 0) {
                                out.println("failed to update usercount in userplayedcount");
                            }
                        } else {
                            out.println("not found");
                            String artist = null;
                            String getArtistName = "select artist from allsongs where songname = '" + name1 + "'";
                            resultSet = stat.executeQuery(getArtistName);
                            if (resultSet.next()) {
                                artist = resultSet.getString("artist");
                            }
                            String insert = "insert into userplayedcount values('" + username + "','" + name1 + "','" + artist + "',1)";
                            int i = stat.executeUpdate(insert);
                            if (i == 0) {
                                out.println("failed to update usercount in userplayedcount");
                            }
                        }

                        resultSet = stat.executeQuery(checkUserRecents);
                        if (resultSet.next()) {
                            String updateUserHistory = "update recentlyplayed set timeplayed = CURRENT_TIMESTAMP where songname = '" + name1 + "'AND username = '" + username + "'";
                            int i = stat.executeUpdate(updateUserHistory);
                            if (i == 0) {
                                out.println("failed to update user history");
                            }
                        } else {
                            String artist = null;
                            String getArtistName = "select artist from allsongs where songname = '" + name1 + "'";
                            resultSet = stat.executeQuery(getArtistName);
                            if (resultSet.next()) {
                                artist = resultSet.getString("artist");
                            }
                            String insertIntoRecents = "insert into recentlyplayed values('" + username + "','" + name1 + "','" + artist + "', CURRENT_TIMESTAMP )";
                            int i = stat.executeUpdate(insertIntoRecents);
                            if (i == 0) {
                                out.println("failed to enter data into user history");
                            }
                        }
                        String insertIntoUserHistory = "insert into userhistory values('" + username + "','" + name1 + "', CURRENT_TIMESTAMP )";
                        int i = stat.executeUpdate(insertIntoUserHistory);
                        if (i == 0) {
                            out.println("failed to enter data into user history");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (queryType.equals("loadPlaylists")) {
                    out.println("Loading playlists from server");
                    String query = "select playlistname from playlists where username = '" + username + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending playlist names to client");
                        while (rs.next()) {
                            out.println(rs.getString("playlistname"));
                            output_Stream.writeUTF(rs.getString("playlistname"));
                        }
                        output_Stream.writeUTF("");
                        out.println("playlists sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending playlist names from server");
                    }
                } else if (queryType.equals("showPlaylist")) {

                    out.println("Loading songs in playlists from server");
                    String query = "select songname from songsinplaylists where username = '" + username + "' and playlistname = '" + name1 + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending songs in playlist to client");
                        while (rs.next()) {
                            out.println(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("songname"));
                        }
                        output_Stream.writeUTF("");
                        out.println("Songs in playlist sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending songs in playlist from server");
                    }
                } else if (queryType.equals("addSongToPlaylist")) {
                    System.out.println("adding song to playlist");
                    Database_Connection con = new Database_Connection();
                    Statement stat = con.getStat();
                    String check = "select * from songsinplaylists where username = '" + username + "' and playlistname = '" + name1 + "' and songname = '" + name2 + "'";
                    ResultSet resultSet = stat.executeQuery(check);
                    if (!resultSet.next()) {
                        String query = "insert into songsinplaylists values('" + username + "','" + name1 + "', '" + name2 + "')";
                        int x = stat.executeUpdate(query);
                        if (x == 0)
                            System.out.println("song not added ERROR!");
                        else
                            System.out.println("song successfully added to the playlist");
                    }
                } else if (queryType.equals("removeFromPlaylist")) {
                    out.println("Removing " + name1 + " from playlist " + name2);
                    String query = "delete from songsinplaylists where username = '" + username + "' and playlistname = '" + name1 + "' and songname = '" + name2 + "' ";
                    Database_Connection con = new Database_Connection();
                    Statement stat = con.getStat();
                    stat.executeUpdate(query);
                    out.println("" + name2 + " deleted succesdfully from " + name1);
                } else if (queryType.equals("deletePlaylist")) {

                    out.println("Deleting playlist from server");
                    String query = "delete from songsinplaylists where username = '" + username + "' and playlistname = '" + name1 + "'";
                    String query2 = "delete from playlists where username = '" + username + "' and playlistname = '" + name1 + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        out.println(" here ....");
                        stat.executeUpdate(query2);
                        out.println("here 2 .. .. .");
                        stat.executeUpdate(query);
                        out.println("playlist deleted successfully");
                    } catch (Exception e) {
                        out.println("Error deleting playlist from server");
                    }

                } else if (queryType.equals("getArtistData")) {

                    out.println("artist function server side");
                    out.println("Loading artists from server");
                    String query = "select distinct artist from allsongs order by artist asc ";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending artist data to client");
                        while (rs.next()) {
                            out.println(rs.getString("artist"));
                            output_Stream.writeUTF(rs.getString("artist"));
                        }
                        output_Stream.writeUTF("");
                        out.println("Artist data sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending Artist data from server");
                    }

                } else if (queryType.equals("getLanguageData")) {
                    out.println("artist function server side");
                    out.println("Loading artists from server");
                    String query = "select distinct language from allsongs order by language asc ";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending artist data to client");
                        while (rs.next()) {
                            out.println(rs.getString("language"));
                            output_Stream.writeUTF(rs.getString("language"));
                        }
                        output_Stream.writeUTF("");
                        out.println("Artist data sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending Artist data from server");
                    }
                } else if (queryType.equals("getGenreData")) {

                    out.println("artist function server side");
                    out.println("Loading artists from server");
                    String query = "select distinct genre from allsongs order by genre asc";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending genre data to client");
                        while (rs.next()) {
                            out.println(rs.getString("genre"));
                            output_Stream.writeUTF(rs.getString("genre"));
                        }
                        output_Stream.writeUTF("");
                        out.println("genre data sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending genre data from server");
                    }

                } else if (queryType.equals("getSongs")) {

                    out.println("Loading songs  from server");
                    String query = "select songname from allsongs where " + name1 + " = '" + name2 + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        out.println("Sending songs data to client");
                        while (rs.next()) {
                            output_Stream.writeUTF(rs.getString("songname"));
                        }
                        output_Stream.writeUTF("");
                        out.println("Songs  sent succesfully");
                    } catch (Exception e) {
                        out.println("Error sending songs from server");
                    }

                } else if (queryType.equals("getAllSongs")) {
                    System.out.println("Loading songs  from server");
                    String query = "select songname,artist  from allsongs";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        System.out.println("Sending songs data to client");
                        while (rs.next()) {
                            out.println(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("songname"));
                            out.println(rs.getString("artist"));
                            output_Stream.writeUTF(rs.getString("artist"));
                        }
                        output_Stream.writeUTF("");
                        System.out.println("Songs  sent succesfully");
                    } catch (Exception e) {
                        System.out.println("Error sending songs from server");
                        e.printStackTrace();
                    }
                } else if (queryType.equals("getLikedSongs")) {
                    String query = "select songname from likedsongs where username = '" + username + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        while (rs.next()) {
                            out.println(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("songname"));
                        }
                        output_Stream.writeUTF("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (queryType.equals("addToLikedSongs")) {
                    String query = "insert into likedsongs values('" + username + "','" + name1 + "')";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        int x = stat.executeUpdate(query);
                        if (x == 0) {
                            out.println("song not added to liked songs");
                        } else {
                            out.println("song added succefully to liked songs");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (queryType.equals("removeFromLikedSongs")) {
                    String query = "delete from likedsongs where username = '" + username + "' and songname = '" + name1 + "'";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        int x = stat.executeUpdate(query);
                        if (x == 0) {
                            out.println("song not removed succesfully");
                        } else {
                            out.println("song removed successfully");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (queryType.equals("getTop50Songs")) {
                    System.out.println("Loading songs  from server");
                    String query = "select songname,artist  from allsongs order by playcount desc limit 50 ";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        System.out.println("Sending songs data to client");
                        while (rs.next()) {
                            out.println(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("songname"));
                            out.println(rs.getString("artist"));
                            output_Stream.writeUTF(rs.getString("artist"));
                        }
                        output_Stream.writeUTF("");
                        System.out.println("Songs  sent succesfully");
                    } catch (Exception e) {
                        System.out.println("Error sending songs from server");
                        e.printStackTrace();
                    }
                } else if (queryType.equals("mostPlayedSongs")) {
                    String query = "SELECT * FROM userplayedcount where username ='" + username + "'  ORDER BY playcount DESC LIMIT 50";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet resultSet = stat.executeQuery(query);
                        while (resultSet.next()) {
                            out.print(resultSet.getString("songname") + "  ");
                            output_Stream.writeUTF(resultSet.getString("songname"));
                            out.print(resultSet.getString(("artist")));
                            output_Stream.writeUTF(resultSet.getString(("artist")));
                            out.println("\n");
                        }
                        output_Stream.writeUTF("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (queryType.equals("recentlyPlayed")) {
                    System.out.println("Loading songs  from server");
                    String query = "select *  from recentlyplayed where username ='" + username + "'order by timeplayed desc";
                    try {
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(query);
                        System.out.println("Sending songs data to client");
                        while (rs.next()) {
                            out.println(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("songname"));
                            out.println(rs.getString("artist"));
                            output_Stream.writeUTF(rs.getString("artist"));
                        }
                        output_Stream.writeUTF("");
                        System.out.println("Songs  sent succesfully");
                    } catch (Exception e) {
                        System.out.println("Error sending user history from server");
                        e.printStackTrace();
                    }
                } else if (queryType.equals("getnewsongs")) {
                    try {
                        String getnewsongs = "SELECT * FROM allsongs  WHERE timeadded >= ( current_timestamp() - INTERVAL 2 DAY ) order by timeadded desc";
                        Database_Connection con = new Database_Connection();
                        Statement stat = con.getStat();
                        ResultSet rs = stat.executeQuery(getnewsongs);
                        System.out.println("Sending songs data to client");
                        while (rs.next()) {
                            output_Stream.writeUTF(rs.getString("songname"));
                            output_Stream.writeUTF(rs.getString("artist"));
                        }
                        output_Stream.writeUTF("");
                        System.out.println("Songs  sent succesfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (queryType.equals("getHistory")) {
                    String query = "select * from userhistory where username = '" + username + "' order by timeplayed desc ";
                    Database_Connection con = new Database_Connection();
                    Statement stat = con.getStat();
                    ResultSet rs = stat.executeQuery(query);
                    for (int i = 0; i < 50 && rs.next(); i++) {
                        String complete = rs.getString("songname") + " @ " + rs.getString("timeplayed").substring(0, 18);
                        output_Stream.writeUTF(complete);
                    }

                    output_Stream.writeUTF("");
                } else if (queryType.equals("getrecommendation")) {
                    String day1 = "select * from userhistory where timeplayed >= ( current_timestamp() - interval  25 hour ) AND ((timeplayed <=  current_timestamp() - interval  23 hour ))AND username = '" + username + "'";
                    String day2 = "select * from userhistory where timeplayed >= ( current_timestamp() - interval  49 hour ) AND ((timeplayed <=  current_timestamp() - interval  47 hour ))AND username = '" + username + "'";
                    String day3 = "select * from userhistory where timeplayed >= ( current_timestamp() - interval  73 hour ) AND ((timeplayed <=  current_timestamp() - interval  71 hour ))AND username = '" + username + "'";
                    String day4 = "select * from userhistory where timeplayed >= ( current_timestamp() - interval  97 hour ) AND ((timeplayed <=  current_timestamp() - interval  95 hour ))AND username = '" + username + "'";

                    Database_Connection con = new Database_Connection();
                    Statement stat1 = con.getStat();
                    Statement stat2 = con.getStat();
                    Statement stat3 = con.getStat();
                    Statement stat4 = con.getStat();
                    ResultSet rs = stat1.executeQuery(day1);
                    while (rs.next()) {
                        String songname = rs.getString("songname");
                        String getArtistname = "select * from allsongs where songname ='" + songname + "'";
                        Database_Connection con2 = new Database_Connection();
                        Statement stat5 = con2.getStat();
                        ResultSet rs1 = stat5.executeQuery(getArtistname);
                        String artistname = rs1.getString("artist");
                        output_Stream.writeUTF(songname);
                        output_Stream.writeUTF(artistname);
                    }
                    rs = stat2.executeQuery(day2);
                    while (rs.next()) {
                        String songname = rs.getString("songname");
                        String getArtistname = "select * from allsongs where songname ='" + songname + "'";
                        Database_Connection con2 = new Database_Connection();
                        Statement stat5 = con2.getStat();
                        ResultSet rs1 = stat5.executeQuery(getArtistname);
                        String artistname = rs1.getString("artist");
                        output_Stream.writeUTF(songname);
                        output_Stream.writeUTF(artistname);
                    }
                    rs = stat3.executeQuery(day3);
                    while (rs.next()) {
                        String songname = rs.getString("songname");
                        String getArtistname = "select * from allsongs where songname ='" + songname + "'";
                        Database_Connection con2 = new Database_Connection();
                        Statement stat5 = con2.getStat();
                        ResultSet rs1 = stat5.executeQuery(getArtistname);
                        String artistname = rs1.getString("artist");
                        output_Stream.writeUTF(songname);
                        output_Stream.writeUTF(artistname);
                    }
                    rs = stat4.executeQuery(day4);
                    while (rs.next()) {
                        String songname = rs.getString("songname");
                        String getArtistname = "select * from allsongs where songname ='" + songname + "'";
                        Database_Connection con2 = new Database_Connection();
                        Statement stat5 = con2.getStat();
                        ResultSet rs1 = stat5.executeQuery(getArtistname);
                        String artistname = rs1.getString("artist");
                        output_Stream.writeUTF(songname);
                        output_Stream.writeUTF(artistname);
                    }
                    output_Stream.writeUTF("");
                }

                output_Stream.flush();

            }//while loop ends
        } catch (Exception e) {
            e.printStackTrace();
            out.println("cannot read choice");
        }
    }
}

