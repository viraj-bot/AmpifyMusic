package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.scene.input.MouseEvent;
import javafx.scene.media.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.*;

public class Test2 implements Initializable {
    AudioEqualizer equalizer;
    MediaPlayer mediaPlayer;
    ObservableList<EqualizerBand> bands;
    @FXML
    private MediaView mediaView;
    @FXML
    private JFXTextField lyricshere;
    @FXML
    private JFXButton ONOFF;
    @FXML
    private JFXSlider band0, band1, band2, band3, band4, band5, band6, band7, band8, band9;

    @FXML
    void OnOff(MouseEvent event) {
        if (ONOFF.getText().equals("ON")) {
            ONOFF.setText("OFF");
            equalizer = mediaPlayer.getAudioEqualizer();
            equalizer.setEnabled(true);
            bands = equalizer.getBands();
            bands.get(0).setGain(band0.getValue() / 100 * 24 - 12);
            bands.get(1).setGain(band1.getValue() / 100 * 24 - 12);
            bands.get(2).setGain(band2.getValue() / 100 * 24 - 12);
            bands.get(3).setGain(band3.getValue() / 100 * 24 - 12);
            bands.get(4).setGain(band4.getValue() / 100 * 24 - 12);
            bands.get(5).setGain(band5.getValue() / 100 * 24 - 12);
            bands.get(6).setGain(band6.getValue() / 100 * 24 - 12);
            bands.get(7).setGain(band7.getValue() / 100 * 24 - 12);
            bands.get(8).setGain(band8.getValue() / 100 * 24 - 12);
            bands.get(9).setGain(band9.getValue() / 100 * 24 - 12);
            band0.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;

                    if ((gain = (band0.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(0).setGain(gain);

                    } else {
                        bands.get(0).setGain(gain - 12.0);
                    }
                }
            });
            band1.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;

                    if ((gain = (band1.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(1).setGain(gain);
                    } else {
                        bands.get(1).setGain(gain - 12.0);
                    }
                }
            });
            band2.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band2.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(2).setGain(gain);
                    } else {
                        bands.get(2).setGain(gain - 12.0);
                    }
                }
            });
            band3.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band3.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(3).setGain(gain);
                    } else {
                        bands.get(3).setGain(gain - 12.0);
                    }
                }
            });
            band4.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band4.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(4).setGain(gain);
                    } else {
                        bands.get(4).setGain(gain - 12.0);
                    }
                }
            });
            band5.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band5.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(5).setGain(gain);
                    } else {
                        bands.get(5).setGain(gain - 12.0);
                    }
                }
            });
            band6.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band6.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(6).setGain(gain);
                    } else {
                        bands.get(6).setGain(gain - 12.0);
                    }
                }
            });
            band7.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band7.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(7).setGain(gain);
                    } else {
                        bands.get(7).setGain(gain - 12.0);
                    }
                }
            });
            band8.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band8.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(8).setGain(gain);
                    } else {
                        bands.get(8).setGain(gain - 12.0);
                    }
                }
            });
            band9.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double gain;
                    if ((gain = (band9.getValue() / 100 * 24)) >= 12.0) {
                        gain = gain - 12.0;
                        bands.get(9).setGain(gain);
                    } else {
                        bands.get(9).setGain(gain - 12.0);
                    }
                }
            });

        } else if (ONOFF.getText().equals("OFF")) {
            ONOFF.setText("ON");
            System.out.println("Equilizer is OFF");
            band0.setValue(50);
            band1.setValue(50);
            band2.setValue(50);
            band3.setValue(50);
            band4.setValue(50);
            band5.setValue(50);
            band6.setValue(50);
            band7.setValue(50);
            band8.setValue(50);
            band9.setValue(50);
            equalizer.setEnabled(false);
            equalizer = null;
        }
    }

    private double band;
    private double centerfrq;
    private double gn;

    @FXML
    void set(MouseEvent event) {
        System.out.println("clicked");
        System.out.println("here1");
        AudioEqualizer au = mediaView.getMediaPlayer().getAudioEqualizer();
        System.out.println("here2");
        au.setEnabled(true);
        System.out.println("here3");
        System.out.println("max bands " + au.MAX_NUM_BANDS);
        ObservableList<EqualizerBand> bands = au.getBands();
        for (int i = 0; i < 4; i++) {
            System.out.println("center frequency = " + bands.get(i).getCenterFrequency());
            System.out.println("BandWidth  = " + bands.get(i).getBandwidth());
            System.out.println("Gain = " + bands.get(i).getGain());
            bands.get(i).setGain(-12);
            System.out.println("Gain = " + bands.get(0).getGain());
        }
    }


    Map<String, List<String>> lyrics = new HashMap<>();
    Map<String, Long> EndTime = new HashMap<>();
    String currentTime;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String songname = "SomethingJustLikeThis";
                try {

                    Main.dos.writeObject(songname);
                    byte[] buffer = new byte[1024];
                    int bytesRead = Main.dis.read(buffer);
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try {
                        System.out.println("here 17");
                        while (bytesRead != -1) {
                            output.write(buffer, 0, bytesRead);
                            bytesRead = Main.dis.read(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] tuneAsBytes = (byte[]) Main.dis.readObject();

                    File tempMp3 = File.createTempFile("music", ".ampify");
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(tuneAsBytes);
                    System.out.println(tempMp3.getAbsolutePath());
                    System.out.println(tempMp3.getAbsolutePath());
                    final Media media = new Media(tempMp3.toURI().toURL().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();

                    System.out.println("here 21");
                    tempMp3.deleteOnExit();
                    Main.dos.writeObject(songname);

                    byte[] lyricsAsBytes = (byte[]) Main.dis.readObject();
                    File tempMp3lyrics = File.createTempFile("music", ".tmp");
                    System.out.println("here 26");
                    FileOutputStream fos2 = new FileOutputStream(tempMp3lyrics);
                    fos2.write(lyricsAsBytes);
                    tempMp3lyrics.deleteOnExit();

                    play(tempMp3.toURI().toURL().toString());
                    lyricsToHashMap(tempMp3lyrics.toPath().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    public void lyricsToHashMap(String path) {

        try {
            File file = new File(path);
            BufferedReader bufRdr = new BufferedReader(new FileReader(file));
            String str, index;
            while ((bufRdr.readLine()) != null) {
                List<String> content = new ArrayList<String>();
                String completetime = bufRdr.readLine();
                String endtime = completetime.substring(completetime.indexOf('>') + 2, completetime.indexOf('>') + 12);
                String starttime;
                starttime = completetime.substring(0, completetime.indexOf(',') + 2);
                EndTime.put(starttime, sleepTime(starttime, endtime));
                lyrics.put(starttime, null);
                while ((str = bufRdr.readLine()).length() != 0) {
                    content.add(str);
                }
                lyrics.put(starttime, content);
            }
//            Set<Map.Entry<String, List<String>>> hashSet = lyrics.entrySet();
//            for (Map.Entry entry : hashSet) {
//
//                System.out.println("Key=" + entry.getKey() + "  ,  Value=" + entry.getValue());
//            }
            displayLyrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayLyrics() throws InterruptedException {
        Thread.sleep(1000);
        while (true) {
            try {
                if (lyrics.containsKey(currentTime)) {
                    System.out.println(currentTime);
                    String s = String.valueOf(lyrics.get(currentTime));
                    System.out.println(s);
                    lyricshere.setText(s);
                    Thread.sleep(EndTime.get(currentTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Long sleepTime(String starttime, String endtime) {
        long stime;
        Long etime;
        StringTokenizer st = new StringTokenizer(starttime, ":,");
        st.nextToken();
        String min = st.nextToken();
        String sec = st.nextToken();
        String milli = st.nextToken();
        stime = (Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(milli) * 100);
//        System.out.println("stime = " + stime);
        st = new StringTokenizer(endtime, ":,");
        st.nextToken();
        min = st.nextToken();
        sec = st.nextToken();
        milli = st.nextToken();
        etime = (Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(milli) * 100);
//        System.out.println("etime = " + etime);
        return (etime - stime);

    }


    public String getSecondsToSimpleString(double userSeconds) {

        double mins = userSeconds / 60;

        String minsStr = mins + "";

        int index = minsStr.indexOf('.');

        String str1 = minsStr.substring(0, index);

        String minsStr2 = minsStr.substring(index + 1);


        double secs = Double.parseDouble("0." + minsStr2) * 60;

        String milli = secs + "";
        int indsex2 = milli.indexOf('.');
        milli = milli.substring(indsex2 + 1, indsex2 + 2);

        String str2 = (int) secs + "";

        if (secs < 10) {
            str2 = 0 + str2;
        }
        if (mins < 10) {
            str1 = 0 + str1;
        }
        return "00" + ":" + str1 + ":" + str2 + "," + milli;
    }

    public void play(String s) {

        Media media = new Media(s);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) ->
                {
                    currentTime = getSecondsToSimpleString(newValue.toSeconds());
                }
        );
    }
}