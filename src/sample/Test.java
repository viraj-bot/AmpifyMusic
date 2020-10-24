package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                String songname = "Circles";
                try {

                    Main.clientOutputStream.writeObject(songname);
                    byte[] buffer = new byte[1024];
                    int bytesRead = Main.clientInputStream.read(buffer);
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try {
                        System.out.println("here 17");
                        while (bytesRead != -1)
                        {
                            output.write(buffer, 0, bytesRead);
                            bytesRead = Main.clientInputStream.read(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] tuneAsBytes = output.toByteArray();
                    File tempMp3 = File.createTempFile("music", ".amplify");
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(tuneAsBytes);
                    System.out.println(tempMp3.getAbsolutePath());
                    System.out.println(tempMp3.getAbsolutePath());
                    final Media media = new Media(tempMp3.toURI().toURL().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
}