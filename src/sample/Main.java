package  sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main extends Application
{
    Socket socket;          // to create a socket b/w client and server
    DataOutputStream out;   // to send data to server
    BufferedReader br;      // to read data coming from server
    public Main()
    {
        try
        {
            this.socket = new Socket("localhost", 12000);
            out = new DataOutputStream(socket.getOutputStream());                            //sends output to socket
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));//to read data from server
        }
        catch (IOException ex)
        {
//            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Not connected to server");
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setScene(new Scene(root, 631, 463));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}