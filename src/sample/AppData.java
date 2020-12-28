package sample;

import java.io.Serializable;

public class AppData implements Serializable {
    private String name1;
    private String queryType;                     //class to send and receive data between server and client
    private String name2;
    private String type;


    AppData(String queryType) {
        this.queryType = queryType;
    }

    AppData(String queryType, String playlistName, String name) {
        this.queryType = queryType;
        this.name1 = playlistName;
        this.name2 = name;
    }

    AppData(String queryType, String name) {
        this.queryType = queryType;
        this.name1 = name;
    }

    public String toString() {
        return queryType + "." + name1 + "." + name2;
    }


    public void setName(String name) {
        this.name2 = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name2;
    }

    public String getType() {
        return this.type;
    }

    public String getQueryType() {
        return this.queryType;
    }

    public String getPlaylistName() {
        return this.name1;
    }

}