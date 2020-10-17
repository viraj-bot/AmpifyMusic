package server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Test implements Initializable {

    @FXML
    private MediaView mediaView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] tuneAsBytes = streamToBytes();
                    System.out.println(tuneAsBytes);
                    File tempMp3 = File.createTempFile("music", ".amplify");
                    System.out.println("here 3");
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    System.out.println("here 5");
                    fos.write(tuneAsBytes);
                    System.out.println("here 6");
                    System.out.println("here 7");
                    System.out.println(tempMp3.getAbsolutePath());
                    System.out.println("here 8");
                    final Media media = new Media(tempMp3.toURI().toURL().toString());
                    System.out.println(tempMp3.toURI().toURL().toString());
                    System.out.println("here 9");
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    System.out.println("here 10");
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                    System.out.println("here 11");
                    tempMp3.isHidden();
//                    Process p = Runtime.getRuntime().exec("attrib + H" + tempMp3.getPath());
//                    tempMp3.setReadOnly();
                    fos.close();
                    tempMp3.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
    public byte[] streamToBytes() throws IOException {
        URL url = new URL(" http://127.0.0.1:7777/lobo.mp3");
        System.out.println("here 12");
        InputStream inputStream = null;
        System.out.println("here 13");
        try {
            inputStream = url.openStream();
            System.out.println("inputstream = " + inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Length of input stream is = " + inputStream.available());
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.out.println("here 17");
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytearr = output.toByteArray();
        System.out.println("Byte array length : " + bytearr.length);
        return bytearr;
    }
}

