package sample;

import com.jfoenix.controls.*;
import com.sun.javafx.webkit.KeyCodeMap;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {
    String currentPlaylist, currentlyPlayingPlaylist, currentSong;
    FileChooser fileChooser = new FileChooser();
    private MediaPlayer mediaPlayer;
    Boolean status = false, flag = true, bool = true;
    private List<File> fileList;
    private List<File> fileList1;
    private List<File> fileList2;
    int currentSelectedPane = 3;
    boolean equaliserIsFront = false, lyricsPaneIsFront = false, queuePaneIsFront = false;
    private ObservableList<String> playlistNames = FXCollections.observableArrayList();   //to store the names of playlists locally
    private ObservableList<String> queue = FXCollections.observableArrayList();
    private ObservableList<String> listOfSongsInPlaylist = FXCollections.observableArrayList();
    private ObservableList<String> localSongsQueue = FXCollections.observableArrayList();
    private ObservableList<String> localVideosQueue = FXCollections.observableArrayList();
    private ObservableList<String> likedSongs = FXCollections.observableArrayList();
    private ObservableList<SongData> allLikedSongs = FXCollections.observableArrayList();
    Map<String, List<String>> lyrics = new HashMap<>();
    Map<String, Long> endTime = new HashMap<>();
    String currentTime2;
    AudioEqualizer equalizer;
    ObservableList<EqualizerBand> bands;

    @FXML
    private JFXTextField lyricshere;
    @FXML
    private JFXToggleButton ONOFF;
    @FXML
    private JFXSlider band0, band1, band2, band3, band4, band5, band6, band7, band8, band9;
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
    private AnchorPane localSongPane;
    @FXML
    private HBox selectPlaylistHbox;
    @FXML
    private HBox equaliserHBox;
    @FXML
    private JFXListView<String> playlistNameListView;
    @FXML
    private JFXButton historyButton;
    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane sidePanel, defaultPane;
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
    private JFXButton allSongsButton;
    @FXML
    private VBox playlistPaneVbox;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private JFXTextField searchField, searchField1, tableSearchField;
    @FXML
    private AnchorPane songPane;
    @FXML
    private JFXListView<String> songsListView;
    @FXML
    private JFXListView<String> playlistListView;
    @FXML
    private JFXListView<String> libraryListView;
    @FXML
    private JFXTextArea lyricsTextArea;
    @FXML
    private AnchorPane libraryPane;
    @FXML
    private VBox libraryPaneVbox;
    @FXML
    private ImageView likeButtonImage;
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
    private JFXButton likeButton;
    @FXML
    private ImageView playSongsFromCurrentPlaylistButton;
    @FXML
    private JFXListView<String> localSongsListView;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXTextField localSearchField;
    @FXML
    private JFXListView<String> queueListView;
    @FXML
    private JFXTextArea currentTimeLabel;
    @FXML
    private JFXTextArea trackLength;
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
    private JFXTextArea nowPlayingLabel;
    @FXML
    private JFXButton playButton;
    @FXML
    private ImageView playImage;
    @FXML
    private Label homePageLabel;
    @FXML
    private HBox createPlaylistHbox;
    @FXML
    private JFXButton createPlaylistcloseButton;
    @FXML
    private JFXTextField createPlaylistTextField;
    @FXML
    private JFXButton createPlaylistButton;
    @FXML
    private Label warningLabel;
    @FXML
    private TableView<SongData> songsTableView;
    @FXML
    private TableColumn<SongData, String> songNameCol;
    @FXML
    private TableColumn<SongData, String> artistNameCol;
    @FXML
    private TableColumn<SongData, JFXButton> buttonCol;
    @FXML
    private AnchorPane homePageSongsPane;

    private AnchorPane[] allPanes = new AnchorPane[13];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePagePane.toFront();
        progressBar.setValue(0);
        disableEqualiser(true);
        loadPlaylists();
        allPanes[0] = defaultPane;
        allPanes[1] = mediaViewPanel;
        allPanes[2] = localSongPane;
        allPanes[3] = homePagePane;
        allPanes[4] = playlistPane;
        allPanes[5] = lyricsPane;
        allPanes[6] = queuePane;
        allPanes[7] = sidePanel;
        allPanes[8] = rootPanel;
        allPanes[9] = songPane;
        allPanes[10] = libraryPane;
        allPanes[11] = homePageSongsPane;
        allPanes[12] = playerControl;
        shortcutKeys();

        playlistNameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //currentPlaylist = playlistNames.get(playlistNameListView.getSelectionModel().getSelectedIndex());
                showPlaylist(playlistNameListView.getSelectionModel().getSelectedIndex());
            }
        });

        songNameCol.setCellValueFactory(new PropertyValueFactory<SongData, String>("songName"));
        artistNameCol.setCellValueFactory(new PropertyValueFactory<SongData, String>("artistName"));
        buttonCol.setCellValueFactory(new PropertyValueFactory<SongData, JFXButton>("button"));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                fileList1 = new ArrayList<>();
                scanSongs("D:\\");
            }
        };
        Thread th = new Thread(r);
        th.start();

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

    public void shortcutKeys() {
        for (int i = 0; i < 13; i++) {
            allPanes[i].setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent key) {
                    System.out.println(key.getCode());
                    switch (key.getCode()) {
                        case ESCAPE -> {
                            System.exit(0);
                            break;
                        }
                        case SPACE -> {
                            playpause();
                            break;
                        }
                        case S -> {
                            stop();
                            songNameLabel.setText("");
                            break;
                        }
                        case RIGHT -> {
                            mediaPlayer.seek(Duration.seconds(progressBar.getValue() + 10));
                            break;
                        }
                        case LEFT -> {
                            mediaPlayer.seek(Duration.seconds(progressBar.getValue() - 10));
                            break;
                        }
                        case UP -> {
                            volumeSlider.setValue(volumeSlider.getValue() + 10);
                            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                            break;
                        }
                        case DOWN -> {
                            volumeSlider.setValue(volumeSlider.getValue() - 10);
                            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                            break;
                        }
                        case M -> {
                            if (!mediaPlayer.isMute()) {

                                mediaPlayer.setMute(true);
                                volumeSlider.setValue(0);
                            } else {
                                mediaPlayer.setMute(false);
                                volumeSlider.setValue(100);
                            }
                            break;
                        }
                        case E -> {
                            equaliserButtonPressed();
                            break;
                        }
                        case L -> {
                            lyricsButtonPressed();
                            break;
                        }
                    }
                }
            });
        }
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

    public void equaliserButtonPressed() {
        if (equaliserIsFront) {
            equaliserHBox.toBack();
            equaliserIsFront = false;
        } else {
            equaliserHBox.toFront();
            equaliserIsFront = true;
        }
    }

    public void equaliserCloseButtonPressed() {
        equaliserHBox.toBack();
    }

    private void disableEqualiser(Boolean bool) {
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

    public void equaliserOnOff() {
        if (ONOFF.isSelected()) {
            disableEqualiser(false);
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


    public void nextButtonPressed() {
        if (currentlyPlayingPlaylist == "local") {
            System.out.println(currentSong);
            int trackno = queue.indexOf(currentSong) + 1;
            if (trackno == queue.size()) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno, fileList);
            queueListView.getSelectionModel().select(trackno);
        } else if (currentlyPlayingPlaylist == "localSongs") {
            System.out.println(currentSong);
            int trackno = localSongsQueue.indexOf(currentSong) + 1;
            if (trackno == localSongsQueue.size()) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno, fileList1);
            songsListView.getSelectionModel().select(trackno);
        } else if (currentlyPlayingPlaylist == "localVideos") {
            System.out.println(currentSong);
            int trackno = localVideosQueue.indexOf(currentSong) + 1;
            if (trackno == localVideosQueue.size()) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno, fileList2);
            songsListView.getSelectionModel().select(trackno);
        } else {
            int trackno = queue.indexOf(currentSong) + 1;
            if (trackno == queue.size()) {
                trackno = 0;
            }
            System.out.println("trackno " + trackno + queue.get(trackno));
            queueListView.getSelectionModel().select(trackno);
            handlePlay(queue.get(trackno));
        }
    }

    public void previousButtonPressed() {
        if (currentlyPlayingPlaylist == "local") {
            System.out.println(currentSong);
            int trackno = queue.lastIndexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            System.out.println(trackno);
            jumpTrack(trackno, fileList);
            queueListView.getSelectionModel().select(trackno);
        } else if (currentlyPlayingPlaylist == "localSongs") {
            int trackno = localSongsQueue.lastIndexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            songsListView.getSelectionModel().select(trackno);
            jumpTrack(trackno, fileList1);
        } else if (currentlyPlayingPlaylist == "localVideos") {
            int trackno = localVideosQueue.lastIndexOf(currentSong) - 1;
            if (trackno < 0) {
                trackno = 0;
            }
            songsListView.getSelectionModel().select(trackno);
            jumpTrack(trackno, fileList2);
        } else {
            int trackno = queue.lastIndexOf(currentSong) - 1;
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
        lyricsTextArea.clear();
        flag = false;
        lyrics.clear();
        endTime.clear();
        status = false;
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

    @FXML
    void localVideosButtonPressed(MouseEvent event) {
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

    public void openSong() {
        if (fileList != null) {
            if (!fileList.isEmpty()) {
                File existDirectory = fileList.get(0).getParentFile();
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
                jumpTrack(queueListView.getSelectionModel().getSelectedIndex(), fileList);
            }
        });
    }//openFile() method closed here

    void playLocalSong(File f) {
        ONOFF.setSelected(false);
        disableEqualiser(true);
        String fileName, path, fileExtension;
        path = f.toURI().toString();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        likeButton.setVisible(false);
        likeButton.setDisable(true);
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

    public void jumpTrack(int index, List<File> listOfFiles) {
        File file = null;
        System.out.println(index);
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
    }   //jumpTrack() method closed here

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


    public void handlePlay(String songName) {
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
        currentSong = songName;
        AppData playSong = new AppData("playSong", songName);
        try {
            lyrics = new HashMap<>();
            endTime = new HashMap<>();
            Main.clientOutputStream.writeObject(playSong);
            byte[] tuneAsBytes = (byte[]) Main.clientInputStream.readObject();
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
                byte[] lyricsAsBytes = (byte[]) Main.clientInputStream.readObject();
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

    public void lyricsButtonPressed() {
        if (lyricsPaneIsFront) {
            lyricsPane.toBack();
            lyricsPaneIsFront = false;

        } else {
            lyricsPane.toFront();
            lyricsPaneIsFront = true;
        }
    }


    public void lyricsToHashMap(String path) {
        try {
            File file = new File(path);
            BufferedReader bufRdr = new BufferedReader(new FileReader(file));
            String str;
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

        } catch (Exception e) {
            e.printStackTrace();
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
        st = new StringTokenizer(endtime, ":,");
        st.nextToken();
        min = st.nextToken();
        sec = st.nextToken();
        milli = st.nextToken();
        etime = (Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(milli) * 100);
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

    public void displayLyrics() {
        if (lyrics != null) {
            while (flag && bool) {
                try {

                        System.out.println("1");
                    if (lyrics.containsKey(currentTime2)) {
                        System.out.print("2");
//                        String s = String.valueOf(lyrics.get(currentTime2));
                        System.out.print("3");
//                        System.out.println(s);
//                        System.out.print("4");
                        lyricsTextArea.setText(String.valueOf(lyrics.get(currentTime2)));
                        System.out.print("5");
                        Thread.sleep(endTime.get(currentTime2));
                        lyricsTextArea.setText("");
                        System.out.print("6");
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

    Thread t = null;

    public void play(String s, String songName) {
        System.out.println("entered play method");

        if (status) {
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

        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) ->
        {
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
                        AppData createPlaylistData = new AppData("CreatePlaylist", createPlaylistTextField.getText());
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
            AppData loadPlaylistData = new AppData("loadPlaylists");
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

        currentPlaylist = playlistNames.get(i);
        playlistNameLabel.setText(currentPlaylist);
        try {
            AppData showPlaylistData = new AppData("showPlaylist", currentPlaylist);
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

    public void deletePlaylist() {
        try {
            AppData deletePlaylist = new AppData("deletePlaylist", currentPlaylist);
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

    public void OnCreatePlaylistcloseButtonPressed() {
        createPlaylistHbox.toBack();
    }

    public void onAddPlaylistcloseButtonPressed() {
        selectPlaylistHbox.toBack();
    }

    public void addToPlaylistMenuButtonPressed2() {
        addToPlaylistMenuButtonPressed(songsListView);
    }

    public void addToPlaylistMenuButtonPressed1() {
        addToPlaylistMenuButtonPressed(playlistListView);
    }

    public void addToPlaylistMenuButtonPressed(JFXListView<String> listview) {
        selectPlaylistHbox.toFront();
        selectPlaylistToAdd.setItems(playlistNames);
        selectPlaylistToAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(playlistListView.getSelectionModel().getSelectedItem());
                System.out.println(selectPlaylistToAdd.getSelectionModel().getSelectedItem());
                try {
                    AppData addSongToPlaylist = new AppData("addSongToPlaylist", selectPlaylistToAdd.getSelectionModel().getSelectedItem(), listview.getSelectionModel().getSelectedItem());
                    Main.clientOutputStream.writeObject(addSongToPlaylist);
                    selectPlaylistHbox.toBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void tableAddToPlaylistButtonPressed() {
        selectPlaylistHbox.toFront();
        selectPlaylistToAdd.setItems(playlistNames);
        selectPlaylistToAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(songsTableView.getSelectionModel().getSelectedItem().getSongName());
                System.out.println(selectPlaylistToAdd.getSelectionModel().getSelectedItem());
                try {
                    AppData addSongToPlaylist = new AppData("addSongToPlaylist", selectPlaylistToAdd.getSelectionModel().getSelectedItem(), songsTableView.getSelectionModel().getSelectedItem().getSongName());
                    Main.clientOutputStream.writeObject(addSongToPlaylist);
                    selectPlaylistHbox.toBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeFromPlaylistButtonPressed() throws IOException {
        String songToRemove = playlistListView.getSelectionModel().getSelectedItem();
        AppData removeFromPlaylist = new AppData("removeFromPlaylist", playlistNameListView.getSelectionModel().getSelectedItem(), songToRemove);
        Main.clientOutputStream.writeObject(removeFromPlaylist);
        listOfSongsInPlaylist.remove(songToRemove);
    }

    public void queueButtonPressed() {
        if (queuePaneIsFront) {
            queuePane.toBack();
            queuePaneIsFront = false;
        } else {
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


    public void addToQueueButtonPressedLibrary(ActionEvent actionEvent) {
        queue.add(songsListView.getSelectionModel().getSelectedItem());
    }

    public void addToQueueButtonPressedPlaylist(ActionEvent actionEvent) {
        queue.add(playlistListView.getSelectionModel().getSelectedItem());
    }

    public void tableAddToQueueButtonPressed() {
        queue.add(songsTableView.getSelectionModel().getSelectedItem().getSongName());
    }

    public void clearQueueButtonPressed() {
        queue.clear();
        stop();
        fileList.clear();
    }

    public void menuPlayButtonPressed() {
        handlePlay(playlistListView.getSelectionModel().getSelectedItem());
    }

    public void recentlyPlayedButtonPressed() throws IOException {
        currentPlaylist = "recents";
        libraryPane.toFront();
        libraryNameLabel.setText("Recently Played");
        ObservableList<SongData> songs = FXCollections.observableArrayList();
        getLikedSongs();
        AppData recentlyPlayed = new AppData("recentlyPlayed");
        Main.clientOutputStream.writeObject(recentlyPlayed);
        System.out.println("Reading list of song  from server");
        String str1, str2, str3;
        while (!"".equals(str1 = Main.clientInputStream.readUTF())) {
            str2 = Main.clientInputStream.readUTF();
            if (likedSongs.contains(str1)) {
                str3 = "Liked";
                allLikedSongs.add(new SongData(str1, str2, str3));
            } else {
                str3 = "Unliked";
            }
            System.out.println("Song Name : " + str1);
            System.out.println("Artist name: " + str2);
            System.out.println(str3);
            songs.add(new SongData(str1, str2, str3));
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
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                    currentlyPlayingPlaylist = "recents";
                    queue.clear();
                    queue.add(currentSong);
                    handlePlay(currentSong);
                }
            }
        });
    }

    public void historyButtonPressed() throws IOException {
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
        currentPlaylist = "library";
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
            e.printStackTrace();
            System.out.println("Error getting genre data");
        }
    }

    public void languagesButtonPressed() {
        searchListView = null;
        currentPlaylist = "library";
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

    public void getSongs(String name, String type) {
        searchListView = null;
        currentPlaylist = "library";
        ObservableList<String> songList = FXCollections.observableArrayList();
        songPane.toFront();
        songNameLabel.setText(name);
        try {
            AppData getSongs = new AppData("getSongs", type, name);
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
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
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


    public void likedSongsButtonPressed() {
        currentPlaylist = "library";
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
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    currentSong = songsTableView.getSelectionModel().getSelectedItem().getSongName();
                    currentlyPlayingPlaylist = "library";
                    queue.clear();
                    queue.add(currentSong);
                    handlePlay(currentSong);
                }
            }
        });
    }


    public void getLikedSongs() {
        try {
            likedSongs.clear();
            AppData getLikedSongs = new AppData("getLikedSongs");
            Main.clientOutputStream.writeObject(getLikedSongs);
            String str;
            while (!"".equals(str = Main.clientInputStream.readUTF())) {
                System.out.println(str);
                likedSongs.add(str);
            }
        } catch (Exception e) {
            System.out.println("Error getting liked songs");
            e.printStackTrace();
        }
    }

    public void likeButtonPressed() {
        getLikedSongs();
        if (likedSongs.contains(currentSong)) {
            System.out.println("Unliked" + currentSong);
            try {
                likeButtonImage.setImage(new Image(new FileInputStream("src/sample/Icons/heart.png")));
                AppData removeFromLikedSongs = new AppData("removeFromLikedSongs", currentSong);
                Main.clientOutputStream.writeObject(removeFromLikedSongs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("liked" + currentSong);
            try {
                likeButtonImage.setImage(new Image(new FileInputStream("src/sample/Icons/heartSelected.png")));
                AppData addToLikedSongs = new AppData("addToLikedSongs", currentSong);
                Main.clientOutputStream.writeObject(addToLikedSongs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    JFXListView searchListView;


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

    public void homeButtonPressed() {
        homePagePane.toFront();
    }

    public void allSongsButtonPressed() {
        try {
            ObservableList<SongData> songs = FXCollections.observableArrayList();
            getLikedSongs();
            AppData getAllSongs = new AppData("getAllSongs");
            Main.clientOutputStream.writeObject(getAllSongs);
            System.out.println("Reading list of song  from server");
            String str1, str2, str3;
            while (!"".equals(str1 = Main.clientInputStream.readUTF())) {
                str2 = Main.clientInputStream.readUTF();
                if (likedSongs.contains(str1)) {
                    str3 = "Liked";
                    allLikedSongs.add(new SongData(str1, str2, str3));
                } else {
                    str3 = "Unliked";
                }
                System.out.println("Song Name : " + str1);
                System.out.println("Artist name: " + str2);
                System.out.println(str3);
                songs.add(new SongData(str1, str2, str3));
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
            System.out.println("Unable to get songs");
        }
    }

    public void top50SongsButtonPressed() {
        ObservableList<SongData> songs = FXCollections.observableArrayList();
        AppData top50Songs = new AppData("getTop50Songs");
        try {
            getLikedSongs();
            Main.clientOutputStream.writeObject(top50Songs);
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
            homePageLabel.setText("Top 50 Songs");
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
            System.out.println("cannot send request to server ");
        }
    }

    public void newlyAddedButtonPressed() {
        try {
            ObservableList<SongData> songs = FXCollections.observableArrayList();
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

    public void mostPlayedButtonPressed() {
        ObservableList<SongData> songs = FXCollections.observableArrayList();
        AppData mostPlayedSongs = new AppData("mostPlayedSongs");
        try {
            getLikedSongs();
            Main.clientOutputStream.writeObject(mostPlayedSongs);
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
            homePageLabel.setText("Most Played Songs");
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
            System.out.println("cannot send request to server ");
        }
    }

}





