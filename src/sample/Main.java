package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application
{
     Socket socket;          // to create a socket b/w client and server
     static ObjectOutputStream clientOutputStream;   // to send data to server
     static ObjectInputStream clientInputStream;
     static BufferedReader br;      // to read data coming from server

    public  Main()
    {
        try
        {
            socket = new Socket("localhost", 12000);
            this.clientOutputStream = new ObjectOutputStream(socket.getOutputStream());    //sends output to socket
            this.clientInputStream = new ObjectInputStream(socket.getInputStream());       //reads data from server
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));//to read data from server
        }
        catch (IOException ex)
        {
            System.out.println("Not connected to server");
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setScene(new Scene(root, 631, 463));
        primaryStage.setTitle("Login");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }

}