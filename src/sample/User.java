package client;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;


    User(String userName, String password, String firstName, String lastName, String email){
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    User(String userName , String password){
        this.userName = userName;
        this.password = password;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public String  getFirstName(){
        return firstName;
    }

   public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

}
