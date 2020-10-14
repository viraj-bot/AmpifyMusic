package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.application.LauncherImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{


    @FXML
    AnchorPane rootPane;

    @FXML
    JFXPasswordField password;

    @FXML
    JFXTextField username;

    String currentUser;

    @FXML
    public void login(ActionEvent event) throws IOException
    {
        System.out.println(username.getText());
        System.out.println(password.getText());
        if (username.getText().isEmpty() || password.getText().isEmpty() || username.getText().isBlank() || password.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Input field cannot be empty");
        } else {
            try {
                Main m = new Main();
                m.out.writeUTF("1");
                m.out.writeUTF(username.getText()); // to send username from username textfield
                m.out.writeUTF(password.getText()); // to send password from textfield
                m.out.writeUTF("");                 // sends empty string to terminate while loop in server
                String s = m.br.readLine();   // to get response from server (is login done)
                if (s.equals("true"))
                {
                    JOptionPane.showMessageDialog(null, "Login Sucessfull");
                    currentUser = username.getText();
                    rootPane.getScene().getWindow().hide();
                    Stage homePage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
                    homePage.setScene(new Scene(root, 1280, 720));
                    homePage.show();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Login Failed");
                }
            } catch (Exception ex) {

                System.out.println("Error 2");
            }
        }

    }

    public String getCurrentUser(){
        return currentUser;
    }
    @FXML
    void MoveToSignup(ActionEvent event) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        System.out.println("clicked fxml");
        rootPane.getChildren().setAll(pane);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }


}