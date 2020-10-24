package client;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.List;

public class AppData implements Serializable
{
    private String userName;
    private String playlistName;
    private String queryType;

    AppData(String queryType){
       this.queryType=queryType;
    }

    AppData(String queryType , String userName, String playlistName)
    {
        this.queryType =queryType;
        this.userName = userName;
        this.playlistName = playlistName;
    }

    AppData(String queryType,String userName)
    {
        this.queryType = queryType;
        this.userName = userName;
    }



    public String getUserName()
    {
        return userName;
    }

    public String getQueryType(){return queryType;}

    public String getPlaylistName()
    {
        return playlistName;
    }

}
