package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
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

   String currentUser;

    @FXML
    void signup(ActionEvent event) {
        String u = username.getText(); // getting username from textfield
        String c_p = cPassword.getText();//getting password from passwordfield
        String r_p = password.getText();//getting password from re_entered passwordfield
        if ("".equals(u) || "".equals(c_p) || "".equals(r_p) || "".equals(FirstName.getText()) || "".equals(LastName.getText()) || "".equals(Email.getText()))//if any filed is empty
        {
            JOptionPane.showMessageDialog(null, "Input Field cannot be empty");
        } else {

            if (c_p.equals(r_p)) // if created password matches with re-entered password
            {
                try {
                    Main m = new Main();
                    m.out.writeUTF("1");
                    m.out.writeUTF(u);     // sending username to server
                    m.out.writeUTF(c_p);   //sending passowrd to server
                    m.out.writeUTF(FirstName.getText());   //sending firstname to server
                    m.out.writeUTF(LastName.getText());   //sending lastname to server
                    m.out.writeUTF(Email.getText());   //sending Email to server
                    m.out.writeUTF("");// sending empty string to terminate while loop in server
                    String s = m.br.readLine();// to read response from server (if registration successful)
                    if (s.equals("true")) {
                        JOptionPane.showMessageDialog(null, "Registered Successfully");
                        currentUser = u;
                        rootPane.getScene().getWindow().hide();
                        Stage homePage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
                        homePage.setScene(new Scene(root, 1280, 720));
                        homePage.show();
                    } else {
                        JOptionPane.showMessageDialog(null, "User already Exist");
                    }
                } catch (IOException ex) {
//                    Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error");

                }
            } else //if password doesn't match
            {
                password.setText("");
                cPassword.setText("");
                JOptionPane.showMessageDialog(null, " created password doesn't match with renter password");
            }
        }
    }

    @FXML
    void MoveToSLogin(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));
        System.out.println("moved to Main fxml");
        rootPane.getChildren().setAll(pane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }
}
