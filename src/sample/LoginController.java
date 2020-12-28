package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{

    @FXML
    private Label warningLabel;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField username;

    static String currentUser = null;

    @FXML
    public void login(ActionEvent event) throws IOException
    {
        System.out.println(username.getText());
        System.out.println(password.getText());
        if (username.getText().isEmpty() || password.getText().isEmpty() || username.getText().isBlank() || password.getText().isBlank()) {
            warningLabel.setText("Input field cannot be empty");                                    //to validate the username and password
        } else {
            try {
                User user = new User(username.getText() , password.getText());
                Main.clientOutputStream.writeObject(user);
                System.out.println("ready to read data");
                String replyFromServer = Main.clientInputStream.readUTF() ;
                System.out.println(replyFromServer);
                if (replyFromServer.equals("Logged In sucessfully"))
                {
                    System.out.println("ready to display homepage");
                    currentUser = username.getText();
                    rootPane.getScene().getWindow().hide();
                    Stage homePage = new Stage();
                    homePage.setTitle("Ampify");
                    Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
                    homePage.setScene(new Scene(root, 1280, 720));
                    homePage.show();
                }
                else
                {
                    warningLabel.setText(replyFromServer);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error 2");
            }
        }

    }

    @FXML
    void MoveToSignup(ActionEvent event) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Signup.fxml"));
        System.out.println("clicked fxml");
        rootPane.getChildren().setAll(pane);
    }

    static String getCurrentUser(){
        return currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}