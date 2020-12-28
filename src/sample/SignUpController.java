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

public class SignUpController implements Initializable {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXPasswordField password, cPassword;
    @FXML
    private JFXTextField username, FirstName, LastName, Email;
    @FXML
    private Label warningLabel;

    static String currentUser=null ;

    @FXML
    void signup(ActionEvent event) {
        String u = username.getText(); // getting username from textfield
        String c_p = password.getText();//getting password from passwordfield
        String r_p = cPassword.getText();//getting password from re_entered passwordfield
        if ("".equals(u) || "".equals(c_p) || "".equals(r_p) || "".equals(FirstName.getText()) || "".equals(LastName.getText()) || "".equals(Email.getText()))//if any filed is empty
        {
            warningLabel.setText( "Input Field cannot be empty");
        }
        else if(!Email.getText().contains("@")||!Email.getText().contains(".")||Email.getText().contains(" ")){
            warningLabel.setText("Enter a valid Email id");
        }
        else {

            if (c_p.equals(r_p)) // if created password matches with re-entered password
            {
                try {
                    User user = new User(u, c_p, FirstName.getText(), LastName.getText(), Email.getText());
                    Main.clientOutputStream.writeObject(user);
                    String replyFromServer = Main.clientInputStream.readUTF();// to read response from server (if registration successful)
                    if (replyFromServer.equals("User registered Successfully"))
                    {
                        System.out.println("sign Up successful");
                        currentUser = u;
                        rootPane.getScene().getWindow().hide();
                        Stage homePage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
                        homePage.setScene(new Scene(root, 1280, 720));
                        homePage.show();
                    } else {
                        warningLabel.setText(replyFromServer);
                    }
                } catch (IOException ex) {
                    System.out.println("Error");

                }
            } else //if password doesn't match
            {
                password.setText("");
                cPassword.setText("");
                warningLabel.setText(" created password doesn't match with renter password");
            }
        }
    }

    @FXML
    void MoveToSLogin(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));
        System.out.println("moved to Main fxml");
        rootPane.getChildren().setAll(pane);
    }

    static String getCurrentUser(){
        return currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }
}
