package sample;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
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
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {
    String currentPlaylist, currentlyPlayingPlaylist, currentSong, currentUser;
    FileChooser fileChooser = new FileChooser();
    private MediaPlayer mediaPlayer;
    Boolean status = false, flag = true;
    //private ObservableList<String> list = FXCollections.observableArrayList();
    private List<File> fileList;
    // private HashMap<String , List<File>> playlists = new HashMap<>();
    int currentSelectedPane = 3;
    private ObservableList<String> playlistNames = FXCollections.observableArrayList();   //to store the names of playlists locally
    private ObservableList<String> queue = FXCollections.observableArrayList();
    private ObservableList<String> listOfSongsInPlaylist = FXCollections.observableArrayList();
    Map<String, List<String>> lyrics = new HashMap<>();
    Map<String, Long> endTime = new HashMap<>();
    String currentTime2;


    @FXML
    private AnchorPane mediaViewPanel;
    @FXML
    private Label totalSongNumber;
    @FXML
    private MenuItem menuPlayButton;
    @FXML
    private JFXButton addButton;
    @FXML
    private Label libraryNameLabel;
    @FXML
    private HBox selectPlaylistHbox;
    @FXML
    private JFXListView<String> playlistNameListView;
    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane sidePanel;
    @FXML
    private AnchorPane rootPanel;
    @FXML
    private JFXListView<String> selectPlaylistToAdd;
    @FXML
    private AnchorPane homePagePane;
    @FXML
    private AnchorPane playlistPane;
    @FXML
    private AnchorPane lyricsPane;
    @FXML
    private AnchorPane queuePane;
    @FXML
    private VBox playlistPaneVbox;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private AnchorPane songPane;
    @FXML
    private JFXListView<String> songsListView;
    @FXML
    private JFXListView<String> playlistListView;
    @FXML
    private JFXListView<String> libraryListView;
    @FXML
    private JFXTextField lyricsTextField;
    @FXML
    private AnchorPane libraryPane;
    @FXML
    private VBox libraryPaneVbox;
    @FXML
    private ScrollPane playlistsScrollPane;
    @FXML
    private JFXMasonryPane playlistsMasonryPane;
    @FXML
    private AnchorPane playerControl;
    @FXML
    private JFXSlider progressBar;
    @FXML
    private ImageView importButton;
    @FXML
    private JFXButton deletePlaylistButton;
    @FXML
    private ImageView playSongsFromCurrentPlaylistButton;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXListView<String> queueListView;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label trackLength;
    @FXML
    private ImageView volumeImage;
    @FXML
    private JFXButton openButton;
    @FXML
    private JFXButton stopButton;
    @FXML
    private JFXButton playlistPlayButton;
    @FXML
    private ContextMenu libraryContextMenu;
    @FXML
    private Label nowPlayingLabel;
    @FXML
    private JFXButton playButton;
    @FXML
    private ImageView playImage;
    @FXML
    private HBox createPlaylistHbox;
    @FXML
    private JFXButton createPlaylistcloseButton;
    @FXML
    private JFXTextField createPlaylistTextField, searchField, searchField1;
    @FXML
    private JFXButton createPlaylistButton;
    @FXML
    private Label warningLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePagePane.toFront();
        progressBar.setValue(0);
        if (LoginController.getCurrentUser().equals(null))
            currentUser = SignUpController.getCurrentUser();
        else
            currentUser = LoginController.getCurrentUser();
        loadPlaylists();
        playlistNameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //currentPlaylist = playlistNames.get(playlistNameListView.getSelectionModel().getSelectedIndex());
                showPlaylist(playlistNameListView.getSelectionModel().getSelectedIndex());
            }
        });
    }


    public void playpause() {
        if (status) {
            mediaPlayer.pause();
            status = false;
            try {
                playImage.setImage(new Image(new FileInputStream("src/sample/Icons/playIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.play();
            System.out.println("playing song in play pause method");
            status = true;
            try {
                playImage.setImage(new Image(new FileInputStream("src/sample/Icons/pauseIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void equalizer() {

    }

    public void nextButtonPressed() {
        if (currentlyPlayingPlaylist == "local") {
            System.out.println(currentSong);
            int trackno = queue.indexOf(currentSong) + 1;
            if (trackno == queue.size()) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno);
            queueListView.getSelectionModel().select(trackno);
        } else {
            int trackno = queue.indexOf(currentSong);
            if (trackno == queue.size()) {
                trackno = 0;
            }
            handlePlay(queue.get(trackno));
            queueListView.getSelectionModel().select(trackno);
        }
    }

    public void previousButtonPressed() {
        if (currentlyPlayingPlaylist == "local") {
            System.out.println(currentSong);
            int trackno = queue.indexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno);
            queueListView.getSelectionModel().select(trackno);
        } else {
            int trackno = queue.indexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            handlePlay(queue.get(trackno));
            queueListView.getSelectionModel().select(trackno);
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        status = false;
        try {
            playImage.setImage(new Image(new FileInputStream("src/sample/Icons/playIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        nowPlayingLabel.setText("");
        trackLength.setText("");
        currentTimeLabel.setText("");
        mediaViewPanel.toBack();
    }

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

    public void openSong() {
        if (fileList != null) {
            if (!fileList.isEmpty()) {
                File existDirectory = fileList.get(0).getParentFile();
                fileChooser.setInitialDirectory(existDirectory);
            }
        }
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select files", ".mp3", ".mp4", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
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
                jumpTrack(queueListView.getSelectionModel().getSelectedIndex());
            }
        });
    }//openFile() method closed here

    void playLocalSong(File f) {
        String fileName, path, fileExtension;
        path = f.toURI().toString();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        try {
            playImage.setImage(new Image(new FileInputStream("src/sample/Icons/pauseIcon.png")));
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

    void setPlayer(Media media) {
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressBar.setValue(newValue.toSeconds());
                    currentTimeLabel.setText("" + getSecondsToSimpleString(newValue.toSeconds()));
                }
        );

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = media.getDuration();
                trackLength.setText(getSecondsToSimpleString(total.toSeconds()));
                progressBar.setMax(total.toSeconds());

            }
        });
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                nextButtonPressed();
            }
        });
    }//setPlayer method ends here


    public void jumpTrack(int index) {
        File file = null;
        System.out.println(index);
        if (status) {
            mediaPlayer.stop();
        }
        try {
            file = fileList.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            playLocalSong(file);
            currentlyPlayingPlaylist = "local";
            currentSong = file.getName();
        }
    }//jumpTrack() method closed here


    public void handlePlay(String songName) {
        currentSong = songName;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                AppData playSong = new AppData("playSong");
                try {
                    Main.clientOutputStream.writeObject(playSong);
                    Main.clientOutputStream.writeObject(songName);
                    byte[] tuneAsBytes = (byte[]) Main.clientInputStream.readObject();
                    File tempMp3 = File.createTempFile("music", ".ampify");
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(tuneAsBytes);
                    System.out.println(tempMp3.getAbsolutePath());
                    System.out.println("here 21");
                    tempMp3.deleteOnExit();
                    System.out.println(("moving to play method"));
                    play(tempMp3.toURI().toString(), songName);

                    Main.clientOutputStream.writeObject(songName);
                    try {
                        byte[] lyricsAsBytes = (byte[]) Main.clientInputStream.readObject();
                        File tempMp3lyrics = File.createTempFile("music", ".tmp");
                        System.out.println("here 26");
                        FileOutputStream fos2 = new FileOutputStream(tempMp3lyrics);
                        fos2.write(lyricsAsBytes);
                        tempMp3lyrics.deleteOnExit();
                        System.out.println(tempMp3.toURI().toString());
                        lyricsToHashMap(tempMp3lyrics.toPath().toString());
                    } catch (Exception e) {
                        System.out.println("NULL pointer exceprion found in handleplay");
                    }
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
                endTime.put(starttime, sleepTime(starttime, endtime));
                lyrics.put(starttime, null);
                while ((str = bufRdr.readLine()).length() != 0) {
                    content.add(str);
                }
                lyrics.put(starttime, content);
            }
            displayLyrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayLyrics() throws InterruptedException {
        Thread.sleep(1000);
        while (flag) {
            try {
                if (lyrics.containsKey(currentTime2)) {
                    System.out.println(currentTime2);
                    String s = String.valueOf(lyrics.get(currentTime2));
                    s = s.substring(1, s.length() - 1);
                    System.out.println(s);
                    lyricsTextField.setText(s);
                    Thread.sleep(endTime.get(currentTime2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        flag = true;
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

    public void play(String s, String songName) {
        System.out.println("entered play method");
        if (status) {
            stop();
            flag = false;
            lyrics.clear();
            endTime.clear();
            lyricsTextField.setText("");
        }
        currentSong = songName;
        nowPlayingLabel.setText("Now Playing-\n" + songName);
        Media media = new Media(s);
        mediaPlayer = new MediaPlayer(media);
        queue.clear();
        queue.add(songName);
        System.out.println("playing song");
        playpause();
        status = true;
        setPlayer(media);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) ->
                {
                    currentTime2 = getSecondsToString(newValue.toSeconds());
                }
        );
    }


    public void createPlaylist() {
        createPlaylistHbox.toFront();
        createPlaylistTextField.setText(null);
        warningLabel.setText(null);
        createPlaylistButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (createPlaylistTextField.getText().isEmpty() || createPlaylistTextField.getText().isBlank()) {
                    warningLabel.setText("Playlist name cannot be empty");
                } else if (playlistNames.contains(createPlaylistTextField.getText())) {
                    warningLabel.setText("Playlist name already taken");
                } else {
                    playlistNames.add(createPlaylistTextField.getText());
                    try {
                        AppData createPlaylistData = new AppData("CreatePlaylist", currentUser, createPlaylistTextField.getText());
                        Main.clientOutputStream.writeObject(createPlaylistData);
                    } catch (Exception e) {
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

    public void loadPlaylists() {
        try {
            System.out.println("Sending username to server to get playlist names");
            AppData loadPlaylistData = new AppData("loadPlaylists", currentUser);
            Main.clientOutputStream.writeObject(loadPlaylistData);
            System.out.println("reading playlist names from server");
            String playlistName;
            while (!"".equals(playlistName = Main.clientInputStream.readUTF())) {
                System.out.println("playlist Name : " + playlistName);
                playlistNames.add(playlistName);
            }
            playlistNameListView.setItems(playlistNames);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading playlists");
        }

    }//loadPlaylists method ends here

    public void showPlaylist(int i) {
        listOfSongsInPlaylist.clear();
        playlistListView.setDisable(false);
        playlistPane.toFront();
        if (currentPlaylist != playlistNames.get(i)) {
            currentPlaylist = playlistNames.get(i);
            playlistNameLabel.setText(currentPlaylist);
            try {
                AppData showPlaylistData = new AppData("showPlaylist", currentUser, currentPlaylist);
                Main.clientOutputStream.writeObject(showPlaylistData);
                System.out.println("Reading list of songs in playlist from server");
                String songName;
                while (!"".equals(songName = Main.clientInputStream.readUTF())) {
                    System.out.println("Song Name : " + songName);
                    listOfSongsInPlaylist.add(songName);
                }
                if (listOfSongsInPlaylist.size() > 0)
                    totalSongNumber.setText("Number of songs : " + listOfSongsInPlaylist.size());
                else
                    totalSongNumber.setText("It is lonely here!");
                playlistListView.setItems(listOfSongsInPlaylist);
                playlistListView.getSelectionModel().select(0);
                playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            currentlyPlayingPlaylist = currentPlaylist;
                            handlePlay(playlistListView.getSelectionModel().getSelectedItem());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to open playlist");
            }
        }
    }//showPlaylists method ends here

    public void deletePlaylist() {
        try {
            AppData deletePlaylist = new AppData("deletePlaylist", currentUser, currentPlaylist);
            Main.clientOutputStream.writeObject(deletePlaylist);
            System.out.println("delete data sent successfully");
            playlistNames.remove(currentPlaylist);
            homePagePane.toFront();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error deleting playlist");
        }
    }

    public void playlistPlayButtonPressed() {
        System.out.println("list of songs" + listOfSongsInPlaylist);
        queue.clear();
        queue.addAll(listOfSongsInPlaylist);
        currentlyPlayingPlaylist = currentPlaylist;
        playlistListView.getSelectionModel().select(0);
        handlePlay(playlistListView.getSelectionModel().getSelectedItem());
        System.out.println("queue:" + queue);
    }


    public void clearQueueButtonPressed() {
        queue.clear();
        stop();
        // fileList.clear();
    }


    public void OnCreatePlaylistcloseButtonPressed() {
        createPlaylistHbox.toBack();
    }

    public void onAddPlaylistcloseButtonPressed() {
        selectPlaylistHbox.toBack();
    }

    public void addToPlaylistMenuButtonPressed2() {
        addToPlaylistMenuButtonPressed(songsListView);
    }

    public void addToPlaylistMenuButtonPressed3() {
        addToPlaylistMenuButtonPressed(queueListView);
    }

    public void addToPlaylistMenuButtonPressed1() {
        addToPlaylistMenuButtonPressed(playlistListView);
    }

    public void addToPlaylistMenuButtonPressed(JFXListView<String> listview) {
        selectPlaylistHbox.toFront();
        ObservableList<String> playlistToAdd = FXCollections.observableArrayList();
        playlistToAdd = playlistNames;
        selectPlaylistToAdd.setItems(playlistToAdd);
        selectPlaylistToAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(playlistListView.getSelectionModel().getSelectedItem());
                System.out.println(selectPlaylistToAdd.getSelectionModel().getSelectedItem());
                try {
//                    AppData addSongToPlaylist = new AppData("addSongToPlaylist", currentUser, selectPlaylistToAdd.getSelectionModel().getSelectedItem(), listview.getSelectionModel().getSelectedItem());
//                    Main.clientOutputStream.writeObject(addSongToPlaylist);
                    selectPlaylistHbox.toBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void menuPlayButtonPressed() {
        handlePlay(playlistListView.getSelectionModel().getSelectedItem());
    }


    public void homeButtonPressed() {
        homePagePane.toFront();
    }

    public void queueButtonPressed() {
        queuePane.toFront();
        queueListView.setItems(queue);

    }

    public void artistButtonPressed() {
        searchListView = null;
        System.out.println("entered artist fuction");
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
                    if (mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem(), "artist");
                }
            });
        } catch (Exception e) {
            System.out.println("Error getting artist data");
        }
    }


    public void genreButtonPressed() {
        searchListView = null;
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
                    if (mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem(), "genre");
                }
            });
        } catch (Exception e) {
            System.out.println("Error getting genre data");
        }
    }

    public void languagesButtonPressed() {
        searchListView = null;
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
                    if (mouseEvent.getButton() == MouseButton.PRIMARY)
                        getSongs(libraryListView.getSelectionModel().getSelectedItem(), "language");
                }
            });
        } catch (Exception e) {
            System.out.println("Error getting language data");
        }
    }

    public void likedSongsButtonPressed() {
        libraryPane.toFront();
        libraryNameLabel.setText("Liked Songs");
        libraryListView.setItems(null);
    }

    public void getSongs(String name, String type) {
        searchListView = null;
        ObservableList<String> songList = FXCollections.observableArrayList();
        songPane.toFront();
        songNameLabel.setText(name);
        try {
            AppData getSongs = new AppData("getSongs");
            getSongs.setName(name);
            getSongs.setType(type);
            Main.clientOutputStream.writeObject(getSongs);
            System.out.println("Reading list of song  from server");
            String songName;
            while (!"".equals(songName = Main.clientInputStream.readUTF())) {
                System.out.println("Song Name : " + songName);
                songList.add(songName);
            }
            songsListView.setItems(songList);
            searchListView = songsListView;
            songsListView.getSelectionModel().select(0);
            songsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY)
                        currentlyPlayingPlaylist = "library";
                    handlePlay(songsListView.getSelectionModel().getSelectedItem());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to get songs");
        }
    }


    public void historyButtonPressed() {
        libraryPane.toFront();
        libraryNameLabel.setText("Recently Played");
    }

    public void lyricsButtonPressed() {
        lyricsPane.toFront();

    }

    private void switchPane(int paneNo) {
        // 1 : home 2 : Library, 3: playlists
        if (currentSelectedPane != paneNo) {
            if (paneNo == 1) {
                // homePane.toFront();
            } else if (paneNo == 2) {
                rootPanel.toFront();
                libraryPane.toFront();

            } else if (paneNo == 3) {
                rootPanel.toFront();
                playlistPane.toFront();
            }
            currentSelectedPane = paneNo;
        }
    }

    public void addToPlaylistMenuButtonPressed(ActionEvent actionEvent) {

    }



    JFXListView searchListView;

    @FXML
    void s(MouseEvent event) {
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

    @FXML
    void s1(MouseEvent event) {
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
}
