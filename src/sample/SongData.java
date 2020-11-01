package client;

import java.awt.*;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.Period;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SongData extends RecursiveTreeObject<SongData> {

    private SimpleStringProperty songName, artistName;
    private JFXButton button;
    private boolean buttonIsSelected;

    public SongData(String songName, String artistName ,String str) {
        this.songName = new SimpleStringProperty(songName);
        this.artistName = new SimpleStringProperty(artistName);                            //this class is to set elements to table view
        try {
            Image playI = new Image(new FileInputStream("src/client/Icons/heart.png"));
            ImageView iv1 = new ImageView(playI);
            iv1.setFitHeight(16);
            iv1.setFitWidth(16);
            Image playI2 = new Image(new FileInputStream("src/client/Icons/heartSelected.png"));
            ImageView iv2 = new ImageView(playI2);
            iv2.setFitHeight(16);
            iv2.setFitWidth(16);
            if(str.equals("Unliked")){
                button = new JFXButton("", iv1);
                buttonIsSelected = false;
            }
            else if(str.equals("Liked")){
                button = new JFXButton("", iv2);
                buttonIsSelected = true;
            }
            button.setRipplerFill(new Color (0,0,0,0));
            button.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (buttonIsSelected) {
                        button.setGraphic(iv1);
                        System.out.println("Unliked" + songName);
                        try {                                                         //remove songs from liked when like button is pressed
                            HomeController obj = new HomeController();
                            AppData removeFromLikedSongs = new AppData("removeFromLikedSongs", songName);
                            Main.clientOutputStream.writeObject(removeFromLikedSongs);
                            buttonIsSelected=false;
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        button.setGraphic(iv2);
                        System.out.println("Liked" + songName);                         //add songs to liked when like button is pressed
                        try {
                            HomeController obj = new HomeController();
                            AppData addToLikedSongs = new AppData("addToLikedSongs", songName);
                            Main.clientOutputStream.writeObject(addToLikedSongs);
                            buttonIsSelected=true;
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getSongName() {
        return songName.get();
    }

    public SimpleStringProperty getsongName(){
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = new SimpleStringProperty(songName);
    }

    public String getArtistName() {
        return artistName.get();
    }

    public  SimpleStringProperty getartistName(){
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = new SimpleStringProperty(artistName);
    }

    public JFXButton getButton(){
        return button;
    }

}