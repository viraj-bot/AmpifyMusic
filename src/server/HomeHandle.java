package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Handler;

import static java.lang.Integer.parseInt;

public class HomeHandle {
    static DataOutputStream dos;
    static DataInputStream dis;
    static String username, playlistname;

    public HomeHandle(DataInputStream dis, DataOutputStream dos, String username) {
        this.dis = dis;
        this.dos = dos;
        this.username = username;
    }

    public static void main(String[] args) throws IOException, SQLException {
        int res;
        while (true) {
            res = parseInt(dis.readUTF());
            switch (res) {
                case 1:
                    playlistinsertion();
                    break;
                case 2:
                    playListLoader();       //on 3
                    break;
                case 3:
                    playlistsongsLoader();  //on 4
                    break;
                case 4:
                    playlistdelete();       //on 5
                    break;
                default:
                    System.out.println("NULL ");
                    break;
            }

        }
    }

    public static void playlistinsertion() throws SQLException {
        System.out.println("adding playlist to database");
        String query = "insert into playlists values('" + username + "','" + playlistname + "' )";
        Database_Connection con = new Database_Connection();
        Statement stat = con.getStat();
        int x = stat.executeUpdate(query);
        if (x == 0)
            System.out.println("playlist not added ERROR!");
        else
            System.out.println("playlist successfully added to the database");
    }


    private static void playListLoader() {

        System.out.println("Loading playlists from server");
        String query = "select playlistname from playlists where username = '" + username + "'";
        try {
            Database_Connection con = new Database_Connection();
            Statement stat = con.getStat();
            ResultSet rs = stat.executeQuery(query);
            System.out.println("Sending playlist names to client");
            while (rs.next()) {
                System.out.println(rs.getString("playlistname"));
                dos.writeUTF(rs.getString("playlistname"));
            }
            dos.writeUTF("Stop");
            System.out.println("playlists sent succesfully");
        } catch (Exception e) {
            System.out.println("Error sending playlist names from server");
        }

    }

    public static void playlistsongsLoader() {
        System.out.println("Loading songs in playlists from server");
        String query = "select songname from songsinplaylists where username = '" + username + "' and playlistname = '" + playlistname + "'";
        try {
            Database_Connection con = new Database_Connection();
            Statement stat = con.getStat();
            ResultSet rs = stat.executeQuery(query);
            System.out.println("Sending songs in playlist to client");
            while (rs.next()) {
                System.out.println(rs.getString("songname"));
                dos.writeUTF(rs.getString("songname"));
            }
            dos.writeUTF("Stop");
            System.out.println("Songs in playlist sent succesfully");
        } catch (Exception e) {
            System.out.println("Error sending songs in playlist from server");
        }
    }

    public static void playlistdelete() {
        System.out.println("Deleting playlist from server");
        String query = "delete from songsinplaylists where username = '" + username + "' and playlistname = '" + playlistname + "'";
        String query2 = "delete from playlists where username = '" + username + "' and playlistname = '" + playlistname + "'";
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

}

