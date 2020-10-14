package sample;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List ;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    // HashMap<String,ArrayList<HashMap<String,Object>>> cachedPlaylist = new HashMap<>();
    String currentPlaylist = "";
    FileChooser fileChooser = new FileChooser();
    private MediaPlayer mediaPlayer;
    int trackNo = 0;
    Boolean status = false;
    private File file;
    private ObservableList<String> list = FXCollections.observableArrayList();
    private List<File> fileList;
    private HashMap<String , List<File>> playlists = new HashMap<>();
    private ObservableList<String> listOfSongsInPlaylist = FXCollections.observableArrayList();
    int currentSelectedPane=3;
    private ObservableList<String> playlistNames = FXCollections.observableArrayList();

    @FXML
    private AnchorPane mediaViewPanel;
    @FXML
    private Label totalSongNumber;
    @FXML
    private JFXListView<String> playlistNameListView;
    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane sidePanel;
    @FXML
    private AnchorPane rootPanel;
    @FXML
    private AnchorPane playlistPane;
    @FXML
    private VBox playlistPaneVbox;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private JFXListView<String> playlistListView;
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
    private Label currentTime;
    @FXML
    private Label trackLength;
    @FXML
    private ImageView volumeImage;
    @FXML
    private JFXButton openButton;
    @FXML
    private JFXButton stopButton;
    @FXML
    private Label songName;
    @FXML
    private JFXButton playButton;
    @FXML
    private ImageView playImage;
    @FXML
    private HBox createPlaylistHbox;
    @FXML
    private JFXButton createPlaylistcloseButton;
    @FXML
    private JFXTextField createPlaylistTextField;
    @FXML
    private JFXButton createPlaylistButton ;
    @FXML
    private Label warningLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlistNameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playlistNames.get(playlistNameListView.getSelectionModel().getSelectedIndex());
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
            status = true;
            try {
                playImage.setImage(new Image(new FileInputStream("src/sample/Icons/pauseIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        songName.setText("");
        trackLength.setText("");
        currentTime.setText("");
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


    void handlePlay(File f) {
        String fileName, path, fileExtension;
        path = f.toURI().toString();
        Media media = new Media(path);
        System.out.println(""+path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        try {
            playImage.setImage(new Image(new FileInputStream("src/sample/Icons/pauseIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        status=true;
        fileName = f.getName();
        songName.setText("Now Playing-\n" + fileName);
        progressBar.setValue(0.0);
        fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());
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
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setValue(newValue.toSeconds());
            currentTime.setText("" + getSecondsToSimpleString(newValue.toSeconds()));
        }
        );

        progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));

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
    }//handlePlay method ends here

    public void jumpTrack(int index) {
        if (status) {
            mediaPlayer.stop();
        }
        try {
            trackNo = index;
            file = fileList.get(trackNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            handlePlay(file);
        }
    }//jumpTrack() method closed here


    public void openSong() {
        if (fileList != null) {
            if (!fileList.isEmpty()) {
                File existDirectory = fileList.get(0).getParentFile();
                fileChooser.setInitialDirectory(existDirectory);
            }
        }
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select files", "*.mp3", "*.mp4", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
        fileList = fileChooser.showOpenMultipleDialog(null);
        list.clear();
        for (File value : fileList) {
            list.add(value.getName());
        }
        playlistListView.setItems(list);
        playlistListView.getSelectionModel().select(0);
        playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                jumpTrack(playlistListView.getSelectionModel().getSelectedIndex());
            }
        });
    }//openFile() method closed here

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
                else if (playlists.containsKey(createPlaylistTextField.getText())) {
                    warningLabel.setText("Playlist name already taken");
                }
                else {
                    playlists.put(createPlaylistTextField.getText(), null);
                    playlistNames.add(createPlaylistTextField.getText());
                    createPlaylistHbox.toBack();
                    playlistPane.toFront();
                    playlistNameLabel.setText(createPlaylistTextField.getText());
                    totalSongNumber.setText("it is lonely here");
                    playlistNameListView.setItems(playlistNames);
                }
            }
      });
    }

    public void loadPlaylists(){

    }


    public void OnCreatePlaylistcloseButtonPressed(){
        createPlaylistHbox.toBack();
    }


    public void showPlaylist(int i){
        List<File> playlistFiles ;
        currentPlaylist = playlistNames.get(i);
        playlistNameLabel.setText(currentPlaylist);
        playlistFiles = playlists.get(currentPlaylist) ;
        if(playlistFiles==null)
        {
            totalSongNumber.setText("It is lonely here");
        }
        else {
            for (File value : playlistFiles) {
                listOfSongsInPlaylist.add(value.getName());
            }
            totalSongNumber.setText("" + playlistFiles.size());
            playlistListView.setItems(listOfSongsInPlaylist);
            playlistListView.getSelectionModel().select(0);
            playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    jumpTrack(playlistListView.getSelectionModel().getSelectedIndex());
                }
            });
        }
    }


    private void switchPane(int paneNo)
    {
        // 1 : home 2 : Library, 3: playlists
        if(currentSelectedPane!=paneNo)
        {
            if(paneNo == 1)
            {
               // homePane.toFront();
            }
            else if(paneNo == 2)
            {
                rootPanel.toFront();
                libraryPane.toFront();

            }
            else if(paneNo == 3)
            {
                rootPanel.toFront();
                playlistPane.toFront();
            }
            currentSelectedPane = paneNo;
        }
    }
}



