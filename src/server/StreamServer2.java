package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamServer2 {
//    static ObjectOutputStream dos;
//    static String sonngname;

//    public StreamServer2(ObjectInputStream dis, ObjectOutputStream ois) throws IOException {
//        this.dos = dos;
//    }

    public static void main(String [] args ) throws IOException, ClassNotFoundException {
        ServerSocket ss;
        ss = new ServerSocket(12000);
        Socket s = ss.accept();
        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());// to read data from client
        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream()); // to send data to client
        String songname = (String) dis.readObject();
        File mp3 = new File("/D:/AmplifySongs/" + songname + ".mp3").getCanonicalFile();
        byte[] arr = Files.readAllBytes(Paths.get(mp3.toURI()));
        dos.write(arr);
    }
}
