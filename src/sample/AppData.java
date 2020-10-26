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
    private String name;
    private String type;


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

    public void setName(String name){
        this.name = name;
    }

    public void setType(String type){
        this.type = type;
    }

    public String  getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }



    public String getUserName()
    {
        return this.userName;
    }

    public String getQueryType(){return this.queryType;}

    public String getPlaylistName()
    {
        return this.playlistName;
    }

}
