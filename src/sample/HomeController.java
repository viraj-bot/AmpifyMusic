package client;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {
    private String currentPlaylist, currentlyPlayingPlaylist, currentSong, currentTime2;
    private FileChooser fileChooser = new FileChooser();
    private MediaPlayer mediaPlayer;
    private Boolean status = false , flag=true , bool = true , isRepeatOn = false , isShuffleOn = false, equaliserIsFront = false , lyricsPaneIsFront = false , queuePaneIsFront = false ;
    private List<File> fileList , fileList2 , fileList1;
    private ObservableList<String> playlistNames = FXCollections.observableArrayList();   //to store the names of playlists locally
    private ObservableList<String> queue = FXCollections.observableArrayList();
    private ObservableList<String> listOfSongsInPlaylist = FXCollections.observableArrayList();
    private ObservableList<String> localSongsQueue = FXCollections.observableArrayList();
    private ObservableList<String> localVideosQueue = FXCollections.observableArrayList();
    private ObservableList<String> likedSongs = FXCollections.observableArrayList();
    private ObservableList<SongData> allLikedSongs = FXCollections.observableArrayList();
    private ObservableList<SongData> songs = FXCollections.observableArrayList();
    private ObservableList<String> songList = FXCollections.observableArrayList();
    private ObservableList<EqualizerBand> bands;
    private  Map<String, List<String>> lyrics = new HashMap<>();
    private Map<String, Long> endTime = new HashMap<>();
    private AudioEqualizer equalizer;


    @FXML
    private  JFXButton songsPanePlayButton, createPlaylistButton, likeButton;
    @FXML
    private JFXToggleButton ONOFF;
    @FXML
    private JFXSlider band0, band1, band2, band3, band4, band5, band6, band7, band8, band9, progressBar, volumeSlider;
    @FXML
    private AnchorPane mediaViewPanel, homePageSongsPane, localSongPane, homePagePane, playlistPane, lyricsPane, queuePane, songPane, playerControl, libraryPane, mainPane;
    @FXML
    private Label totalSongNumber, libraryNameLabel, playlistNameLabel, songNameLabel ,homePageLabel, warningLabel;
    @FXML
    private JFXTextField searchField , searchField1 , tableSearchField, createPlaylistTextField, localSearchField;
    @FXML
    private JFXTextArea lyricsTextArea, nowPlayingLabel, trackLength, currentTimeLabel;
    @FXML
    private MediaView mediaView;
    @FXML
    private HBox selectPlaylistHbox , equaliserHBox, createPlaylistHbox;
    @FXML
    private JFXListView<String> playlistNameListView , selectPlaylistToAdd, songsListView, playlistListView, libraryListView, queueListView, localSongsListView;
    @FXML
    private ImageView likeButtonImage, playImage , shuffleButtonImage , repeatButtonImage;
    @FXML
    private  TableView<SongData> songsTableView;
    @FXML
    private TableColumn<SongData,String> songNameCol ;
    @FXML
    private TableColumn<SongData,String> artistNameCol ;
    @FXML
    private  TableColumn<SongData,JFXButton> buttonCol;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePagePane.toFront();          // to bring the home page to front
        progressBar.setValue(0);        //to set media player seek bar to zero
        disableEqualiser(true);     //to disable the equaliser
        loadPlaylists();                   //to display all the playlists created by the user

        //setting up all the columns of the table view
        songNameCol.setCellValueFactory(new PropertyValueFactory<SongData , String>("songName"));
        artistNameCol.setCellValueFactory(new PropertyValueFactory<SongData,String>("artistName"));
        buttonCol.setCellValueFactory(new PropertyValueFactory<SongData,JFXButton>("button"));

        //calling scan function to scan users D drive to get all the local songs
        Runnable r = new Runnable() {
            @Override
            public void run() {
                fileList1 = new ArrayList<>();
                scanSongs("D:\\");
            }
        };
        Thread th = new Thread(r);
        th.start();

        //calling scan function to scan users D drive to get all the local videos
        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                fileList2 = new ArrayList<>();
                scanVideos("D:\\");
            }
        };
        Thread t = new Thread(r3);
        t.start();
    }

    //playpause function to perform action as soon as user presses the play button in media controller
    public void playpause() {
        if (status) {               //checking status if music is already playing
            mediaPlayer.pause();
            status = false;
            try {
                playImage.setImage(new Image(new FileInputStream("src/client/Icons/playIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.play();
            System.out.println("playing song in play pause method");
            status = true;
            try {
                playImage.setImage(new Image(new FileInputStream("src/client/Icons/pauseIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //to turn on shuffle mode (get randomised order of songs in the currently playing playlist)
    public void shuffleButtonPressed() throws FileNotFoundException {
        if(isShuffleOn){
            System.out.println("turned off shuffle");
            shuffleButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/shuffle.png")));
            isShuffleOn = false;
        }
        else {
            System.out.println("turned on shuffle");
            shuffleButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/shuffleGreen.png")));
            isShuffleOn = true;
        }
    }

    //to turn on the repeat mode to run a song on loop
    public void repeatButtonPressed() throws FileNotFoundException {
        if(isRepeatOn){
            repeatButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/repeat.png")));
            isRepeatOn = false;
        }
        else {
            repeatButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/repeatGreen.png")));
            isRepeatOn = true;
        }
    }

    //to bring equaliser to the front
    public void equaliserButtonPressed(){
        if(equaliserIsFront){
            equaliserHBox.toBack();
            equaliserIsFront = false;
        }
        else{
            equaliserHBox.toFront();
            equaliserIsFront = true;
        }
    }

    //to close equaliser i.e. sending it to the back
    public void equaliserCloseButtonPressed(){
        equaliserHBox.toBack();
    }

    //to disable equaliser
    private  void disableEqualiser(Boolean bool){
        band0.setDisable(bool);
        band1.setDisable(bool);
        band2.setDisable(bool);
        band3.setDisable(bool);
        band4.setDisable(bool);
        band5.setDisable(bool);
        band6.setDisable(bool);
        band7.setDisable(bool);
        band8.setDisable(bool);
        band9.setDisable(bool);
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
    }

    //functionality for equaliser as soon as user enables it
    public void equaliserOnOff() {
        if (ONOFF.isSelected()) {
            disableEqualiser(false);            //to enable equalizer as soon as it is enabled
            System.out.println("Equilizer is ON");
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

            //setting up listeners for all the frequency bands
            band0.setOnMouseClicked(Event -> bands.get(0).setGain(band0.getValue() / 100 * 24 - 12));
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
                band1.setOnMouseClicked(Event -> bands.get(1).setGain(band0.getValue() / 100 * 24 - 12));
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
                band2.setOnMouseClicked(Event -> bands.get(2).setGain(band0.getValue() / 100 * 24 - 12));
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
                band3.setOnMouseClicked(Event -> bands.get(3).setGain(band0.getValue() / 100 * 24 - 12));
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
                band4.setOnMouseClicked(Event -> bands.get(4).setGain(band0.getValue() / 100 * 24 - 12));
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
                band5.setOnMouseClicked(Event -> bands.get(5).setGain(band0.getValue() / 100 * 24 - 12));
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
                band6.setOnMouseClicked(Event -> bands.get(6).setGain(band0.getValue() / 100 * 24 - 12));
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
                band7.setOnMouseClicked(Event -> bands.get(7).setGain(band0.getValue() / 100 * 24 - 12));
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
                band8.setOnMouseClicked(Event -> bands.get(8).setGain(band0.getValue() / 100 * 24 - 12));
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
                band9.setOnMouseClicked(Event -> bands.get(9).setGain(band0.getValue() / 100 * 24 - 12));
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
            } else {
                System.out.println("Equilizer is OFF");
                disableEqualiser(true);
                equalizer.setEnabled(false);
                equalizer = null;
            }
        }

        //As soon as repeat is turned on this function becomes active and checks that the song is played on loop
        public  void repeat(){
        int trackno;
           if(currentlyPlayingPlaylist == "local"){
               trackno = queue.indexOf(currentSong);
               jumpTrack(trackno , fileList);
           }
           else if(currentlyPlayingPlaylist == "localSongs"){
               trackno = localSongsQueue.indexOf(currentSong);
               jumpTrack(trackno , fileList1);
           }
           else if(currentlyPlayingPlaylist == "localVideos"){
               trackno = localVideosQueue.indexOf(currentSong);
               jumpTrack(trackno , fileList2);
           }
           else {
               trackno = queue.indexOf(currentSong);
               handlePlay(queue.get(trackno));
           }
        }


    //this method is to set up the functionality of next button on media controller in case of different playlists
    // as well as for the shuffle mode
    public void nextButtonPressed(){
        if(currentlyPlayingPlaylist == "local")
        {
            int trackno=0 ;
            System.out.println(currentSong);
            if(isShuffleOn){
                Random rand = new Random();
                System.out.println("size of queue" + queue.size());
                trackno = rand.nextInt(queue.size());
                System.out.println("random integer:" + trackno);
            }
            else{
                trackno = queue.lastIndexOf(currentSong) + 1;
            }
            if(trackno == queue.size())
            {  trackno = 0;  }
            System.out.println(trackno);
            jumpTrack(trackno , fileList);
        }
        else if(currentlyPlayingPlaylist == "localSongs")
        {
            int trackno = 0 ;
            System.out.println(currentSong);
           if(isShuffleOn){
                Random rand = new Random();
                System.out.println("size of queue" + localSongsQueue.size());
               trackno = rand.nextInt(localSongsQueue.size());
               System.out.println("random integer:" + trackno);
            }
           else{
                trackno = localSongsQueue.lastIndexOf(currentSong) + 1;
            }
           if(trackno == localSongsQueue.size())
            {  trackno = 0;  }
            System.out.println("track no . : " + trackno);
            jumpTrack(trackno , fileList1);
        }
        else if(currentlyPlayingPlaylist == "localVideos")
        {   int trackno;
            System.out.println(currentSong);
            if(isShuffleOn){
                Random rand = new Random();
                System.out.println("size of queue" + localVideosQueue.size());
                trackno = rand.nextInt(localVideosQueue.size());
                System.out.println("random integer:" + trackno);
            }
            else{
                trackno = localVideosQueue.lastIndexOf(currentSong) + 1;
            }
            if(trackno == localVideosQueue.size())
            {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno , fileList2);
        }
        else{
            int trackno;
            if(isShuffleOn) {
                Random rand = new Random();
                System.out.println("size of queue" + queue.size());
                trackno = rand.nextInt(queue.size());
                System.out.println("random integer:" + trackno);
            }
           else{
                trackno = queue.lastIndexOf(currentSong) + 1;
            }
            if(trackno == queue.size())
            {
                trackno = 0;
            }
            System.out.println("trackno " +trackno + queue.get(trackno));
            handlePlay(queue.get(trackno));
        }
    }

    //This method is to set up the funtionality for previous button on the media contoller in case of different playlists
    public void previousButtonPressed(){
        if(currentlyPlayingPlaylist == "local"){
            System.out.println(currentSong);
            int trackno = queue.lastIndexOf(currentSong) - 1;
            if(trackno<0)
            {  trackno = 0;  }
            System.out.println(trackno);
            jumpTrack(trackno , fileList);
        }
        else if(currentlyPlayingPlaylist == "localSongs") {
            int trackno = localSongsQueue.lastIndexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            jumpTrack(trackno, fileList1);
        }
        else if(currentlyPlayingPlaylist == "localVideos") {
            int trackno = localVideosQueue.lastIndexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            jumpTrack(trackno, fileList2);
        }
        else{
            int trackno = queue.lastIndexOf(currentSong) - 1;
            if(trackno < 0){
                trackno = 0;
            }
            handlePlay(queue.get(trackno));
        }
    }

    //it sets up functionality for the stop button on media controller
    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        lyricsTextArea.clear();
        flag = false;       //this is a flag variable to stop the while loop in display lyrics function as soon as stop button is pressed
        lyrics.clear();
        endTime.clear();
        status = false;        //this is to set playing status to false
        try {
            playImage.setImage(new Image(new FileInputStream("src/sample/Icons/playIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        nowPlayingLabel.setText("");
        trackLength.setText("");
        currentTimeLabel.setText("");
        if (currentlyPlayingPlaylist == "local" || currentlyPlayingPlaylist == "localVideos")
            mediaViewPanel.toBack();
    }

    //this method converts seconds to minutes:seconds format
    public String getSecondsToSimpleString(double userSeconds) {
        double mins = userSeconds / 60;
        String minsStr = mins + "";
        int index = minsStr.indexOf('.');
        String str1 = minsStr.substring(0, index);
        String minsStr2 = minsStr.substring(index + 1);
        double secs = Double.parseDouble("0." + minsStr2) * 60;
        String str2 = (int) secs + "";
        if (secs < 10)
            str2 = 0 + str2;
        return str1 + ":" + str2;
    }

    //this method is used to display list of all the local songs
    public void localSongsButtonPressed(MouseEvent mouseEvent) {
        searchListView = null;
        localSongPane.toFront();
        localSongsListView.setItems(localSongsQueue);
        localSongsListView.getSelectionModel().select(0);
        searchListView = localSongsListView;
        final int[] index = new int[1];
        localSongsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                currentlyPlayingPlaylist = "localSongs";
                System.out.println(localSongsListView.getSelectionModel().getSelectedItem());
                for (File x : fileList1) {
                    if (x.toPath().toString().contains(localSongsListView.getSelectionModel().getSelectedItem())) {
                        index[0] = fileList1.indexOf(x);
                    }
                }
                jumpTrack(index[0], fileList1);
            }
        });
    }

    //this function is called at initialisation and it scans and stores users local songs to filelist1
    public void scanSongs(String p) {
        File f = new File(p);
        File[] fl = f.listFiles();
        try {
            for (File x : fl) {
                if (x == null) {
                    return;
                }
                if (x.isHidden() || !x.canRead()) {
                    continue;
                }
                if (x.isDirectory()) {
                    scanSongs(x.getPath());
                } else if (x.getName().endsWith(".mp3") || x.getName().endsWith(".m4a") || x.getName().endsWith(".wav") || x.getName().endsWith(".aif") || x.getName().endsWith(".aiff")) {
                    localSongsQueue.add(x.getName());
                    fileList1.add(x);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //this method is called to display list of all scanned local videos
    public void localVideosButtonPressed(MouseEvent event) {
        searchListView = null;
        localSongPane.toFront();
        localSongsListView.setItems(localVideosQueue);
        localSongsListView.getSelectionModel().select(0);
        searchListView = localSongsListView;
        final int[] index = new int[1];
        localSongsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                currentlyPlayingPlaylist = "localVideos";
                System.out.println(localSongsListView.getSelectionModel().getSelectedItem());
                for (File x : fileList2) {
                    if (x.toPath().toString().contains(localSongsListView.getSelectionModel().getSelectedItem())) {
                        index[0] = fileList2.indexOf(x);
                    }
                }
                jumpTrack(index[0], fileList2);
            }
        });
    }

    //this function is called at initialisation and it scans and stores users local videos to filelist2
    private void scanVideos(String s) {
        File f = new File(s);
        File[] fl = f.listFiles();
        try {
            for (File x : fl) {
                if (x == null) {
                    return;
                }
                if (x.isHidden() || !x.canRead()) {
                    continue;
                }
                if (x.isDirectory()) {
                    scanVideos(x.getPath());
                } else if (x.getName().endsWith(".mp4") || x.getName().endsWith(".m4v") || x.getName().endsWith(".fxm") || x.getName().endsWith(".flv")) {
                    localVideosQueue.add(x.getName());
                    fileList2.add(x);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this method allows user to open filechooser and select any local song or video to play
    public void openSong() {
        if (fileList != null) {
            if (!fileList.isEmpty()) {
                File existDirectory = fileList.get(0).getParentFile();   // to open filechooser from previous directory if any
                fileChooser.setInitialDirectory(existDirectory);
            }
        }
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select files", "*.mp3", "*.mp4", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
        queue.clear();
        fileList = fileChooser.showOpenMultipleDialog(null);
        for (File value : fileList) {
            queue.add(value.getName());
        }
        queuePane.toFront();
        queueListView.setItems(queue);
        queueListView.getSelectionModel().select(0);
        queueListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                currentlyPlayingPlaylist = "local";
                jumpTrack(queueListView.getSelectionModel().getSelectedIndex() , fileList);
            }
        });
    }//openFile() method closed here


    //this method is used to play any local song or video
    void playLocalSong(File f) {
        ONOFF.setSelected(false);   //to disable equaliser
        disableEqualiser(true);
        String fileName, path, fileExtension;
        path = f.toURI().toString();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        likeButton.setVisible(false);
        likeButton.setDisable(true);
        try {
            playImage.setImage(new Image(new FileInputStream("src/client/Icons/pauseIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = true;
        fileName = f.getName();
        nowPlayingLabel.setText("Now Playing-\n" + fileName);
        progressBar.setValue(0.0);
        fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, f.getName().length());
        if (fileExtension.equals("mp4")) {
            mediaView.setMediaPlayer(mediaPlayer);
            DoubleProperty widthProp = mediaView.fitWidthProperty();
            DoubleProperty heightProp = mediaView.fitHeightProperty();
            //creating bindings
            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            mediaView.setPreserveRatio(true);
            mediaViewPanel.toFront();
            playerControl.toFront();
        } else if (fileExtension.equals("mp3") || fileExtension.equals("wav") && status) {
            mediaViewPanel.toBack();
        }
        setPlayer(media);
    }//playLocalSong method ends here

    //this method is used to jump from one local song to other
    public void jumpTrack(int index , List<File> listOfFiles) {
        File file = null;
        System.out.println("index from jump track : " + index);
        if (status) {
            stop();
        }
        try {
            file = listOfFiles.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            playLocalSong(file);
            currentSong = file.getName();
        }
    }//jumpTrack() method closed here


    // this method is used to set listeners to all the ui components of the media controller
    void setPlayer(Media media) {
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);       //set volume to 100
        volumeSlider.valueProperty().addListener(new InvalidationListener() {   // to check if volume slider is moved
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {    //to set the seek bar moving while media is playing
                    progressBar.setValue(newValue.toSeconds());
                    currentTimeLabel.setText("" + getSecondsToSimpleString(newValue.toSeconds()));
                }
        );

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {    //to seek if seek bar  is pressed at any point
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {   //to seek when seekbar is dragged
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {   //to set track length to seek bar when media is ready
            @Override
            public void run() {
                Duration total = media.getDuration();
                trackLength.setText(getSecondsToSimpleString(total.toSeconds()));
                progressBar.setMax(total.toSeconds());
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {   //to call the nextButtonPressed or repeat method at end of media
            @Override
            public void run() {
                if(isRepeatOn){
                    repeat();
                }
                else {
                    nextButtonPressed();
                }
            }
        });
    }//setPlayer method ends here


    //This method is used to recieve songs from server and sends it to play method
        public void handlePlay(String songName)
        {
            ONOFF.setSelected(false);
            disableEqualiser(true);
            currentSong = songName;
            getLikedSongs();
            likeButton.setVisible(true);
            likeButton.setDisable(false);
            if (likedSongs.contains(songName)) {
                try {
                    likeButtonImage.setImage(new Image(new FileInputStream("src/sample/Icons/heartSelected.png")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    likeButtonImage.setImage(new Image(new FileInputStream("src/sample/Icons/heart.png")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentSong = songName;                     //as sson as a new song is played the value of current song is changed
            AppData playSong = new AppData("playSong", songName);
            try {
                lyrics = new HashMap<>();
                endTime = new HashMap<>();
                Main.clientOutputStream.writeObject(playSong);
                byte[] tuneAsBytes = (byte[]) Main.clientInputStream.readObject();           //getting the song from server in bytes format
                File tempMp3 = File.createTempFile("music", ".ampify");
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(tuneAsBytes);
                System.out.println(tempMp3.getAbsolutePath());
                System.out.println(tempMp3.toURI().toString());
                play(tempMp3.toURI().toString(), songName);
                System.out.println("here 21");
                tempMp3.deleteOnExit();
                System.out.println(("moving to play method"));

                File tempMp3lyrics = null;
                Main.clientOutputStream.writeObject(songName);
                try {
                    byte[] lyricsAsBytes = (byte[]) Main.clientInputStream.readObject();  //getting the lyrics from server in byte format
                    tempMp3lyrics = File.createTempFile("music", ".tmp");
                    System.out.println("here 26");
                    FileOutputStream fos2 = new FileOutputStream(tempMp3lyrics);
                    fos2.write(lyricsAsBytes);
                    System.out.println(tempMp3lyrics.getAbsolutePath());
                    lyricsToHashMap(tempMp3lyrics.toPath().toString());
                } catch (Exception e) {
                    System.out.println("NULL pointer exceprion found in handleplay");
                    lyricsTextArea.setText("    No  Lyrics  Available  For  This  Song ");
                    tempMp3lyrics.delete();
                    lyrics = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    //to bring the lyrics text area to front when show lyrics button is pressed
    public void lyricsButtonPressed() {
        if (lyricsPaneIsFront) {
            lyricsPane.toBack();
            lyricsPaneIsFront = false;
        }
        else if(!lyricsPaneIsFront){
            lyricsPane.toFront();
            lyricsPaneIsFront = true;
        }
    }

    //this method reads the lyrics frfom the temporary file and stores it ti a hashmap where key is the start timestamp
    public void lyricsToHashMap(String path) {
        try {
            File file = new File(path);
            BufferedReader bufRdr = new BufferedReader(new FileReader(file));
            String str, index;
            while ((bufRdr.readLine()) != null) {
                List<String> content = new ArrayList<>();
                String completetime = bufRdr.readLine();
                String endtime = completetime.substring(completetime.indexOf('>') + 2, completetime.indexOf('>') + 12);
                String starttime;
                starttime = completetime.substring(0, completetime.indexOf(',') + 2);
                endTime.put(starttime, sleepTime(starttime, endtime));
                lyrics.put(starttime, null);
                while ((str = bufRdr.readLine()).length() != 0) {
                    content.add(str);
                }
                lyrics.put(starttime, content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this method sleeps the display lyrics funtion tiil the time music plays
    private Long sleepTime(String starttime, String endtime) {
        long stime;
        Long etime;
        StringTokenizer st = new StringTokenizer(starttime, ":,");
        st.nextToken();
        String min = st.nextToken();
        String sec = st.nextToken();
        String milli = st.nextToken();
        stime = (Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(milli) * 100);
        st = new StringTokenizer(endtime, ":,");
        st.nextToken();
        min = st.nextToken();
        sec = st.nextToken();
        milli = st.nextToken();
        etime = (Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(milli) * 100);
        return (etime - stime);
    }

    //this method converts seconds to minute:seconds format
    public String getSecondsToString(double userSeconds) {
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

    //this method displays lyrics from the hashmap
  public void displayLyrics(){
        if (lyrics != null) {
            while (flag && bool) {
                try {
                    System.out.println("here1");
                    if (lyrics.containsKey(currentTime2)) {
                        System.out.print("here2");
                        String s = String.valueOf(lyrics.get(currentTime2));
                        System.out.print("here3");
                        System.out.println(s);
                        System.out.print("here4");
                        lyricsTextArea.setText(s);
                        System.out.print("here5");
                        Thread.sleep(endTime.get(currentTime2));
                        System.out.print("here6");
                    }
                } catch (Exception e) {
                    System.out.println("here7");
                    e.printStackTrace();
                    System.out.println("here8");
                }
            }
        }
        System.out.println("OUT");
        flag = true;
    }

    //this method plays the song recieved from the server
    Thread t;
    public void play(String s, String songName) {
        System.out.println("entered play method");
        if (status) {   //to check if song is already playing
            stop();
            flag = false;
            lyrics.clear();
            endTime.clear();
            lyricsTextArea.clear();
        }
        currentSong = songName;
        nowPlayingLabel.setText("Now Playing-\n" + songName);
        Media media = new Media(s);
        mediaPlayer = new MediaPlayer(media);
        System.out.println("playing song");
        playpause();
        status = true;
        setPlayer(media);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    currentTime2 = getSecondsToString(newValue.toSeconds());
                }
        );

        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) ->   //manipulating the values of flag and bool to stop
        {                                                                               //the while loop in the display lyrics method
            System.out.println("mediaplayer status " + mediaPlayer.getStatus());
          try {
                switch (mediaPlayer.getStatus()) {
                    case PLAYING: {
                        bool = true;
                        flag = true;

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                displayLyrics();
                            }
                        };
                        t = new Thread(r);
                        t.setDaemon(true);
                        t.start();
                    }
                    break;
                    default: {
                        bool = false;
                    }
                }
            } catch (NullPointerException e) {

        }

        });
    }

   //this method creates a new playlist and adds it to server
    public void createPlaylist(){
      createPlaylistHbox.toFront();
      createPlaylistTextField.setText(null);
      warningLabel.setText(null);
      createPlaylistButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (createPlaylistTextField.getText().isEmpty() || createPlaylistTextField.getText().isBlank()) {
                    warningLabel.setText("Playlist name cannot be empty");
                }
                else if (playlistNames.contains(createPlaylistTextField.getText())) {
                    warningLabel.setText("Playlist name already taken");
                }
                else {
                    playlistNames.add(createPlaylistTextField.getText());
                   try{
                       AppData createPlaylistData = new AppData("CreatePlaylist" ,  createPlaylistTextField.getText());
                       Main.clientOutputStream.writeObject(createPlaylistData);
                   }catch(Exception e){
                       e.printStackTrace();
                       System.out.println("Playlist not created ERROR!");
                   }
                   playlistListView.setItems(null);
                   currentPlaylist = createPlaylistTextField.getText();
                    createPlaylistHbox.toBack();
                    playlistPane.toFront();
                    playlistNameLabel.setText(createPlaylistTextField.getText());
                    totalSongNumber.setText("it is lonely here!");
                    playlistNameListView.setItems(playlistNames);
                }
            }
      });
    }//CreatePlaylist method ends here


    //this method is called at intialisation ti load all the user playlists from the server
    public void loadPlaylists(){
        try{
            System.out.println("Sending username to server to get playlist names");
            AppData loadPlaylistData = new AppData("loadPlaylists");
            Main.clientOutputStream.writeObject(loadPlaylistData);
            System.out.println("reading playlist names from server");
            String playlistName;
            while (!"".equals(playlistName = Main.clientInputStream.readUTF())){
                System.out.println("playlist Name : "+ playlistName);
                playlistNames.add(playlistName);
            }
            playlistNameListView.setItems(playlistNames);
            playlistNameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    showPlaylist(playlistNameListView.getSelectionModel().getSelectedIndex());
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error loading playlists");
        }

    }//loadPlaylists method ends here

    //this method shows the songs in playlist
    public void showPlaylist(int i){
        listOfSongsInPlaylist.clear();
        playlistListView.setDisable(false);
        playlistPane.toFront();
        currentPlaylist = playlistNames.get(i);
        playlistNameLabel.setText(currentPlaylist);
        try {
            AppData showPlaylistData = new AppData("showPlaylist", currentPlaylist);
            Main.clientOutputStream.writeObject(showPlaylistData);
            System.out.println("Reading list of songs in playlist from server");
            String songName;
            while (!"".equals(songName = Main.clientInputStream.readUTF())){
                System.out.println("Song Name : "+ songName);
                listOfSongsInPlaylist.add(songName);
            }
            if(listOfSongsInPlaylist.size()>0)
                totalSongNumber.setText("Number of songs : " + listOfSongsInPlaylist.size());
            else
                totalSongNumber.setText("It is lonely here!");
            playlistListView.setItems(listOfSongsInPlaylist);
            playlistListView.getSelectionModel().select(0);
            playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                            currentlyPlayingPlaylist = currentPlaylist ;
                            queue.clear();
                            currentSong = playlistListView.getSelectionModel().getSelectedItem();
                            queue.add(currentSong);
                            handlePlay(currentSong);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to open playlist");
        }
    }//showPlaylists method ends here

    //this method is used to delete a playlist from client and server side both
    public void deletePlaylist(){
        try {
            AppData deletePlaylist = new AppData("deletePlaylist", currentPlaylist);
            Main.clientOutputStream.writeObject(deletePlaylist);
            System.out.println("delete data sent successfully");
            playlistNames.remove(currentPlaylist);
            homePagePane.toFront();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("error deleting playlist");
        }
    }

    //this method is used to play the complete playlist
    public void playlistPlayButtonPressed(){
        System.out.println("list of songs" + listOfSongsInPlaylist);
        queue.clear();
        queue.addAll(listOfSongsInPlaylist) ;
        currentlyPlayingPlaylist = currentPlaylist ;
        handlePlay(queue.get(0));
        System.out.println("queue:"+ queue);
    }

    //this method is used to play all the library songs
    public void libraryPlayButtonPressed(){
        queue.clear();
        queue.addAll(songList);
        currentlyPlayingPlaylist = "queue" ;
        handlePlay(queue.get(0));
    }

    //this method is used to play all songs at any home page playlist
    public  void homePagePlayButtonPressed(){
        queue.clear();
        int i;
        for(i=0; i<songs.size();i++)
        {
            queue.add(songs.get(i).getSongName());
        }
        currentlyPlayingPlaylist = "queue";
        handlePlay(queue.get(0));
    }


    public void OnCreatePlaylistcloseButtonPressed(){
        createPlaylistHbox.toBack();
    }

    public void onAddPlaylistcloseButtonPressed(){
        selectPlaylistHbox.toBack();
    }

    public void addToPlaylistMenuButtonPressed2(){
        addToPlaylistMenuButtonPressed(songsListView);
    }

    public void addToPlaylistMenuButtonPressed1(){
        addToPlaylistMenuButtonPressed(playlistListView);
    }

   //this button adds the listview song to selected playlist
    public void addToPlaylistMenuButtonPressed(JFXListView<String> listview){
        selectPlaylistHbox.toFront();
        selectPlaylistToAdd.setItems(playlistNames);
        selectPlaylistToAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(playlistListView.getSelectionModel().getSelectedItem());
                System.out.println(selectPlaylistToAdd.getSelectionModel().getSelectedItem());
                try{
                    AppData addSongToPlaylist = new AppData("addSongToPlaylist",  selectPlaylistToAdd.getSelectionModel().getSelectedItem() , listview.getSelectionModel().getSelectedItem())  ;
                    Main.clientOutputStream.writeObject(addSongToPlaylist);
                    selectPlaylistHbox.toBack();
                }catch(Exception e ){
                    e.printStackTrace();
                }
            }
        });
    }

    //this method also adds the song from the tableview to selected playlist
    public void tableAddToPlaylistButtonPressed(){
        selectPlaylistHbox.toFront();
        selectPlaylistToAdd.setItems(playlistNames);
        selectPlaylistToAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(songsTableView.getSelectionModel().getSelectedItem().getSongName());
                System.out.println(selectPlaylistToAdd.getSelectionModel().getSelectedItem());
                try{
                    AppData addSongToPlaylist = new AppData("addSongToPlaylist",  selectPlaylistToAdd.getSelectionModel().getSelectedItem() , songsTableView.getSelectionModel().getSelectedItem().getSongName()) ;
                    Main.clientOutputStream.writeObject(addSongToPlaylist);
                    selectPlaylistHbox.toBack();
                }catch(Exception e ){
                    e.printStackTrace();
                }
            }
        });
    }

    //this method removes the particular song from playlist
    public void removeFromPlaylistButtonPressed() throws IOException {
        String songToRemove = playlistListView.getSelectionModel().getSelectedItem();
        AppData removeFromPlaylist = new AppData("removeFromPlaylist" , playlistNameListView.getSelectionModel().getSelectedItem() , songToRemove );
        Main.clientOutputStream.writeObject(removeFromPlaylist);
        listOfSongsInPlaylist.remove(songToRemove);
    }

    //this method opens the playing queue
    public void queueButtonPressed(){
        if(queuePaneIsFront){
            queuePane.toBack();
            queuePaneIsFront = false;
        }
        else {
            queuePane.toFront();
            queuePaneIsFront = true;
            queueListView.setItems(queue);
            currentPlaylist = "queue";
            queueListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentlyPlayingPlaylist = "queue";
                        currentSong = queueListView.getSelectionModel().getSelectedItem();
                        handlePlay(currentSong);
                    }

                }
            });
        }
    }

    //this method is used to add song to queue from library list  view
    public void addToQueueButtonPressedLibrary(ActionEvent actionEvent){
        queue.add(songsListView.getSelectionModel().getSelectedItem());
    }

    public void  addToQueueButtonPressedPlaylist(ActionEvent actionEvent){
        queue.add(playlistListView.getSelectionModel().getSelectedItem());
    }

    //this method adds the song to queue from home page table view
    public void tableAddToQueueButtonPressed(){
        queue.add(songsTableView.getSelectionModel().getSelectedItem().getSongName());
    }

    //this method clears the queue
    public void clearQueueButtonPressed(){
        queue.clear();
        stop();
        fileList.clear();
    }

    //this method is used to play a particular song in playlist by clicking the right button over it
    public void menuPlayButtonPressed(){
        handlePlay(playlistListView.getSelectionModel().getSelectedItem());
    }

    //this method displays the recently played songs from the server
    public void recentlyPlayedButtonPressed() throws IOException {
        songs.clear();
        currentPlaylist = "recents";
        libraryPane.toFront();
        libraryNameLabel.setText("Recently Played");
        getLikedSongs();
        AppData recentlyPlayed = new AppData("recentlyPlayed");
        Main.clientOutputStream.writeObject(recentlyPlayed);
        System.out.println("Reading list of song  from server");
        String str1 , str2 , str3 ;
        while (!"".equals(str1 = Main.clientInputStream.readUTF())){
            str2 = Main.clientInputStream.readUTF();
            if(likedSongs.contains(str1)){
                str3 = "Liked";
            }
            else{
                str3 = "Unliked";
            }
            System.out.println("Song Name : "+ str1);
            System.out.println("Artist name: " + str2);
            System.out.println(str3);
            songs.add(new SongData(str1 , str2 , str3));
        }
        songsTableView.setItems(songs);
        searchedTableView = null;
        searchedTableView = songsTableView;
        homePageSongsPane.toFront();
        homePageLabel.setText("Recently Played");
        songsTableView.getSelectionModel().select(0);
        songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                    currentlyPlayingPlaylist = "recents";
                    queue.clear();
                    queue.add(currentSong);
                    handlePlay(currentSong);
                }
            }
        });
 }

 //this method displays the play history from the server
    public void historyButtonPressed() throws IOException {
        songsPanePlayButton.setVisible(false);
        songsPanePlayButton.setDisable(true);
        AppData history = new AppData("getHistory");
        Main.clientOutputStream.writeObject(history);
        currentPlaylist = "userHistory";
        songPane.toFront();
        songNameLabel.setText("User History");
        ObservableList<String> songname = FXCollections.observableArrayList();
        String Name;
        while (!"".equals(Name = Main.clientInputStream.readUTF())) {
            System.out.println(Name);
            songname.add(Name);
        }
        songsListView.setItems(songname);
        searchListView = songsListView;
        songsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String str = songsListView.getSelectionModel().getSelectedItem();
                str = str.substring(0, str.indexOf(" @ "));
                currentSong = str;
                handlePlay(str);
            }
        });
    }

     //this method is a filter to display all artists from the server
    public void artistButtonPressed() {
        searchListView = null;
        System.out.println("entered artist fuction");
        currentPlaylist = "library";
        ObservableList<String> artists = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Artists");
        System.out.println("sending request to get artist data");
        try {
            AppData artistData = new AppData("getArtistData");
            Main.clientOutputStream.writeObject(artistData);
            String artistName;
            while (!"".equals(artistName = Main.clientInputStream.readUTF())) {
                System.out.println("Artist name: " + artistName);
                artists.add(artistName);
            }
            libraryListView.setItems(artists);
            searchListView = libraryListView;
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem() , "artist");
                }
            });
        }catch(Exception e){
            System.out.println("Error getting artist data");
        }
    }
    //this method is a filter to display all genres from the server
    public void genreButtonPressed(){
        searchListView = null;
        currentPlaylist ="library";
        ObservableList<String> genres = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Genres");
        System.out.println("sending request to get genre data");
        try {
            AppData languageData = new AppData("getGenreData");
            Main.clientOutputStream.writeObject(languageData);
            String genre;
            while (!"".equals(genre = Main.clientInputStream.readUTF())) {
                System.out.println("genre : " + genre);
                genres.add(genre);
            }
            libraryListView.setItems(genres);
            searchListView = libraryListView;
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem() , "genre");
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting genre data");
        }
    }

    //this method is a filter to display all languages from the server
    public void languagesButtonPressed(){
        searchListView = null;
        currentPlaylist ="library";
        ObservableList<String> languages = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Languages");
        System.out.println("sending request to get language data");
        try {
            AppData languageData = new AppData("getLanguageData");
            Main.clientOutputStream.writeObject(languageData);
            String language;
            while (!"".equals(language = Main.clientInputStream.readUTF())) {
                System.out.println("Language : " + language);
                languages.add(language);
            }
            libraryListView.setItems(languages);
            searchListView = libraryListView;
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem() , "language");
                }
            });
        }catch(Exception e){
            System.out.println("Error getting language data");
        }
    }

    //this method displays all the songs of the selected artist, genre or language froml the server
    public void getSongs(String name , String type){
        songsPanePlayButton.setVisible(true);
        songsPanePlayButton.setDisable(false);
        searchListView = null;
        songList.clear();
        currentPlaylist ="library";
        songPane.toFront();
        songNameLabel.setText(name);
        try {
            AppData getSongs = new AppData("getSongs", type , name);
            Main.clientOutputStream.writeObject(getSongs);
            System.out.println("Reading list of song  from server");
            String songName ;
            while (!"".equals(songName = Main.clientInputStream.readUTF())){
                System.out.println("Song Name : "+ songName);
                songList.add(songName);
            }
            songsListView.setItems(songList);
            searchListView = songsListView;
            songsListView.getSelectionModel().select(0);
            songsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentSong = songsListView.getSelectionModel().getSelectedItem();
                        currentlyPlayingPlaylist = "library";
                        queue.clear();
                        queue.add(currentSong);
                        handlePlay(currentSong);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to get songs");
        }
    }

    //this method displays the liked songs of the user from the server
    public void likedSongsButtonPressed(){
        currentPlaylist ="library";
        homePageSongsPane.toFront();
        allLikedSongs.clear();
        allSongsButtonPressed();
        homePageLabel.setText("Liked Songs");
        songsTableView.setItems(allLikedSongs);
        searchedTableView = null;
        searchedTableView = songsTableView;
        songsTableView.getSelectionModel().select(0);
        songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                    currentlyPlayingPlaylist = "library";
                    queue.clear();
                    queue.add(currentSong);
                    handlePlay(currentSong);
                }
            }
        });
    }


    //this method is used to get all the liked songs from the server to set the like button as enabled or disabled in the table view
    public void getLikedSongs() {
        try {
            likedSongs.clear();
            AppData getLikedSongs = new AppData("getLikedSongs");
            Main.clientOutputStream.writeObject(getLikedSongs);
            String str ;
            while (!"".equals(str = Main.clientInputStream.readUTF())) {
                System.out.println(str);
                likedSongs.add(str);
            }
        } catch (Exception e) {
            System.out.println("Error getting liked songs");
            e.printStackTrace();
        }
    }

    //this method adds the song to server as soon as the like button is pressed
    public void likeButtonPressed(){
        getLikedSongs();
        if (likedSongs.contains(currentSong)) {
            System.out.println("Unliked" + currentSong);
            try {
                likeButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/heart.png")));
                AppData removeFromLikedSongs = new AppData("removeFromLikedSongs", currentSong);
                Main.clientOutputStream.writeObject(removeFromLikedSongs);
            }catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("liked" + currentSong);
            try {
                likeButtonImage.setImage(new Image(new FileInputStream("src/client/Icons/heartSelected.png")));
                AppData addToLikedSongs = new AppData("addToLikedSongs", currentSong);
                Main.clientOutputStream.writeObject(addToLikedSongs);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    JFXListView searchListView;

    //this method is to search any song  from the library section
    public void searchFunction(MouseEvent event) {
        ObservableList<String> toSearch = searchListView.getItems();
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                ObservableList<String> searchedList = FXCollections.observableArrayList();
                String searching = null;
                searching = searchField.getText();
                for (int i = 0; i < toSearch.size(); i++) {
                    if (containsIgnoreCase(toSearch.get(i), searching)) {
                        System.out.println(toSearch.get(i));
                        searchedList.add(toSearch.get(i));
                    }
                }
                songsListView.setItems(searchedList);
            }
        });
    }

    TableView<SongData> searchedTableView = null;

    //this method is used to search any artist or song form the table view
    public void tableSearchFunction(MouseEvent mouseEvent) {
        ObservableList<SongData> toSearch = searchedTableView.getItems();
        getLikedSongs();
        ObservableList<String> songList = FXCollections.observableArrayList();
        ObservableList<String> artistList = FXCollections.observableArrayList();
        ObservableList<String> likedsongsList = FXCollections.observableArrayList();
        for (SongData x : toSearch) {
            songList.add(x.getSongName());
            artistList.add(x.getArtistName());
            if (likedSongs.contains(x.getSongName())) {
                likedsongsList.add("Liked");
            } else {
                likedsongsList.add("Unliked");
            }
        }
        tableSearchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                ObservableList<SongData> searchedTable = FXCollections.observableArrayList();
                String searching = null;
                searching = tableSearchField.getText();
                for (int i = 0; i < toSearch.size(); i++) {
                    if (containsIgnoreCase(songList.get(i), searching)) {
                        searchedTable.add(new SongData(songList.get(i), artistList.get(i), likedsongsList.get(i)));
                    } else if (containsIgnoreCase(artistList.get(i), searching)) {
                        searchedTable.add(new SongData(songList.get(i), artistList.get(i), likedsongsList.get(i)));
                    }

                }
                songsTableView.setItems(searchedTable);
            }
        });
    }

    //this is a method to ignore case while searching
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    //this method is to search any artist , genre , language in library section
    public void searchFunction1(MouseEvent event) {
        ObservableList<String> toSearch = searchListView.getItems();
        searchField1.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                ObservableList<String> searchedList = FXCollections.observableArrayList();
                String searching = null;
                searching = searchField1.getText();
                for (int i = 0; i < toSearch.size(); i++) {
                    if (containsIgnoreCase(toSearch.get(i), searching)) {
                        System.out.println(toSearch.get(i));
                        searchedList.add(toSearch.get(i));
                    }
                }
                libraryListView.setItems(searchedList);
            }
        });
    }

    //this method is to search any local song or videos
    public void localSearchFunction(MouseEvent mouseEvent) {
        ObservableList<String> toSearch = searchListView.getItems();
        localSearchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                ObservableList<String> searchedList = FXCollections.observableArrayList();
                String searching = null;
                searching = localSearchField.getText();
                for (int i = 0; i < toSearch.size(); i++) {
                    if (containsIgnoreCase(toSearch.get(i), searching)) {
                        System.out.println(toSearch.get(i));
                        searchedList.add(toSearch.get(i));
                    }
                }
                localSongsListView.setItems(searchedList);
            }
        });
    }

    //this method brings the home page to front on pressed
    public void homeButtonPressed(){
        homePagePane.toFront();
    }

    //this method gets all the songs in the server
    public void allSongsButtonPressed(){
        try{
            songs.clear();
            getLikedSongs();
            AppData getAllSongs = new AppData("getAllSongs");
            Main.clientOutputStream.writeObject(getAllSongs);
            System.out.println("Reading list of song  from server");
            String str1 , str2 , str3 ;
            while (!"".equals(str1 = Main.clientInputStream.readUTF())){
                 str2 = Main.clientInputStream.readUTF();
                 if(likedSongs.contains(str1)){
                     str3 = "Liked";
                     allLikedSongs.add(new SongData(str1 , str2 , str3));
                 }
                 else{
                     str3 = "Unliked";
                 }
                 System.out.println("Song Name : "+ str1);
                 System.out.println("Artist name: " + str2);
                 System.out.println(str3);
                 songs.add(new SongData(str1 , str2 , str3));
            }
            songsTableView.setItems(songs);
            searchedTableView = null;
            searchedTableView = songsTableView;
            homePageSongsPane.toFront();
            homePageLabel.setText("All Songs");
            songsTableView.getSelectionModel().select(0);
            songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                    currentlyPlayingPlaylist = "library";
                    queue.clear();
                    queue.add(currentSong);
                    handlePlay(currentSong);
                }
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to get songs");
        }
    }


    //this method gets the top50 most played songs amongst all users
    public void top50SongsButtonPressed(){
        songs.clear();
        AppData top50Songs = new AppData("getTop50Songs");
        try{
            getLikedSongs();
            Main.clientOutputStream.writeObject(top50Songs);
            String str , str1 , str2;
            while(!"".equals(str = Main.clientInputStream.readUTF())){
                str1 = Main.clientInputStream.readUTF();
                if(likedSongs.contains(str))
                {
                    str2 = "Liked";
                }
                else{
                    str2 = "Unliked";
                }
                System.out.println("Song Name : "+ str);
                System.out.println("Artist name: " + str1);
                System.out.println(str2);
                songs.add(new SongData(str , str1 , str2));
            }
            homePageSongsPane.toFront();
            songsTableView.setItems(songs);
            searchedTableView = null;
            searchedTableView = songsTableView;
            homePageLabel.setText("Top 50 Songs");
            songsTableView.getSelectionModel().select(0);
            songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                        currentlyPlayingPlaylist = "library";
                        queue.clear();
                        queue.add(currentSong);
                        handlePlay(currentSong);
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("cannot send request to server ");
        }
    }

    //this method displays newly added songs to the server
    public void newlyAddedButtonPressed(){
        try {
            songs.clear();
            getLikedSongs();
            AppData getNewSongs = new AppData("getnewsongs");
            Main.clientOutputStream.writeObject(getNewSongs);
            String str, str1, str2;
            while (!"".equals(str = Main.clientInputStream.readUTF())) {
                str1 = Main.clientInputStream.readUTF();
                if (likedSongs.contains(str)) {
                    str2 = "Liked";
                } else {
                    str2 = "Unliked";
                }
                System.out.println("Song Name : " + str);
                System.out.println("Artist name: " + str1);
                System.out.println(str2);
                songs.add(new SongData(str, str1, str2));
            }
            homePageSongsPane.toFront();
            songsTableView.setItems(songs);
            searchedTableView = null;
            searchedTableView = songsTableView;
            homePageLabel.setText("New Songs");
            songsTableView.getSelectionModel().select(0);
            songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                        currentlyPlayingPlaylist = "library";
                        queue.clear();
                        queue.add(currentSong);
                        handlePlay(currentSong);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this method gets the most played songs by the current user
    public void mostPlayedButtonPressed()  {
        songs.clear();
        AppData mostPlayedSongs = new AppData("mostPlayedSongs");
        try{
            getLikedSongs();
            Main.clientOutputStream.writeObject(mostPlayedSongs);
            String str , str1 , str2;
            while(!"".equals(str = Main.clientInputStream.readUTF())){
                str1 = Main.clientInputStream.readUTF();
                if(likedSongs.contains(str))
                {
                    str2 = "Liked";
                }
                else{
                    str2 = "Unliked";
                }
                System.out.println("Song Name : "+ str);
                System.out.println("Artist name: " + str1);
                System.out.println(str2);
                songs.add(new SongData(str , str1 , str2));
            }
            homePageSongsPane.toFront();
            songsTableView.setItems(songs);
            searchedTableView = null;
            searchedTableView = songsTableView;
            homePageLabel.setText("Most Played Songs");
            songsTableView.getSelectionModel().select(0);
            songsTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                        currentlyPlayingPlaylist = "library";
                        queue.clear();
                        queue.add(currentSong);
                        handlePlay(currentSong);
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("cannot send request to server ");
        }
    }

    //this method gets recommendations based on the songs played at a particular time of the day
    public void getRecommendedSongs(){
        AppData getRecommendedSongs = new AppData("getRecommendedSongs");
        try{
            Main.clientOutputStream.writeObject(getRecommendedSongs);
            String str ;
            while(!"".equals(str = Main.clientInputStream.readUTF())){
                if(!songList.contains(str)){
                    songList.add(str);
                }
            }
            songPane.toFront();
            songsListView.setItems(songList);
            searchListView = null;
            searchListView = songsListView;
            songNameLabel.setText("Recommended Songs");
            songsListView.getSelectionModel().select(0);
            songsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                        currentSong = songsListView.getSelectionModel().getSelectedItem();
                        currentlyPlayingPlaylist = "library";
                        queue.clear();
                        queue.add(currentSong);
                        handlePlay(currentSong);
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("cannot send request to server ");
        }
    }

    //this is to sign out current user
    public void signOutButtonPressed() throws IOException {
        if(mediaPlayer!=null)
        {
            mediaPlayer.dispose();
        }
        Stage loginPage = new Stage();
        loginPage.setTitle("Login");
        mainPane.getScene().getWindow().hide();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        loginPage.setScene(new Scene(root, 631, 463));
        loginPage.show();
        AppData signOut = new AppData("signOut");
        Main.clientOutputStream.writeObject(signOut);
        Main m = new Main();
    }
}





